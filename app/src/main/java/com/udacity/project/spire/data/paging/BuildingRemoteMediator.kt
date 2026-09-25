package com.udacity.project.spire.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.udacity.project.spire.data.local.database.SpireDatabase
import com.udacity.project.spire.data.local.entity.BuildingEntity
import com.udacity.project.spire.data.local.entity.BuildingRemoteKeys
import com.udacity.project.spire.data.local.entity.BuildingWithDetails
import com.udacity.project.spire.data.local.entity.CityEntity
import com.udacity.project.spire.data.local.entity.CountryEntity
import com.udacity.project.spire.data.local.entity.toEntity
import com.udacity.project.spire.data.remote.api.BuildingApiService
import com.udacity.project.spire.data.remote.dto.BuildingDto
import com.udacity.project.spire.domain.model.VisitStatus
import java.io.IOException
import retrofit2.HttpException

/**
 * RemoteMediator for Building data.
 * Manages fetching data from network and saving to local database.
 * Database is the single source of truth, RemoteMediator handles synchronization.
 *
 * Following the RemoteKeys pattern from:
 * https://www.bornfight.com/blog/android-paging-3-library-with-page-and-limit-parameters/
 */
@OptIn(ExperimentalPagingApi::class)
class BuildingRemoteMediator(
    private val apiService: BuildingApiService,
    private val database: SpireDatabase,
    private val initialPage: Int = 1
) : RemoteMediator<Int, BuildingWithDetails>() {

    private val buildingDao = database.buildingDao()
    private val cityDao = database.cityDao()
    private val countryDao = database.countryDao()
    private val remoteKeysDao = database.buildingRemoteKeysDao()

    /**
     * TODO #28: Implement RemoteMediator.load() for Paging3
     */
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, BuildingWithDetails>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: initialPage
            }
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            val response = apiService.getBuildingsPaginated(page, state.config.pageSize)
            val buildings = response.buildings
            val endOfPaginationReached = buildings.isEmpty() || response.pagination?.hasNext == false

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    remoteKeysDao.clearRemoteKeys()
                    buildingDao.clearBuildings()
                }

                val prevKey = if (page == initialPage) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = buildings.map {
                    BuildingRemoteKeys(buildingId = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                
                remoteKeysDao.insertAll(keys)
                
                val entities = buildings.map { buildingDtoToEntity(it) }
                buildingDao.insertBuildings(entities)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    /**
     * Convert BuildingDto to BuildingEntity.
     * Creates or gets the necessary Country and City entities.
     */
    private suspend fun buildingDtoToEntity(dto: BuildingDto): BuildingEntity {
        val countryId = getOrCreateCountry(dto.country.name, dto.country.code)
        val cityId = getOrCreateCity(dto.city.name, countryId)

        return BuildingEntity(
            id = dto.id,
            name = dto.name,
            imageUrl = dto.imageUrl,
            heightMeters = dto.heightMeters,
            floors = dto.floors,
            yearCompleted = dto.yearCompleted,
            architecturalStyle = dto.architecturalStyle,
            description = dto.description,
            visitStatus = VisitStatus.NOT_VISITED.toEntity(),
            cityId = cityId
        )
    }

    /**
     * Get or create a country entity.
     * @return The ID of the country entity
     */
    private suspend fun getOrCreateCountry(countryName: String, code: String): Int {
        val existingCountry = countryDao.getCountryByName(countryName)
        return if (existingCountry != null) {
            existingCountry.id
        } else {
            val newCountry = CountryEntity(name = countryName, code = code)
            countryDao.insertCountry(newCountry).toInt()
        }
    }

    /**
     * Get or create a city entity.
     * @return The ID of the city entity
     */
    private suspend fun getOrCreateCity(cityName: String, countryId: Int): Int {
        val existingCity = cityDao.getCityByNameAndCountry(cityName, countryId)
        return if (existingCity != null) {
            existingCity.id
        } else {
            val newCity = CityEntity(name = cityName, countryId = countryId)
            cityDao.insertCity(newCity).toInt()
        }
    }

    /**
     * Get remote keys for the item closest to current scroll position.
     * Used during REFRESH to restore pagination position.
     */
    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, BuildingWithDetails>
    ): BuildingRemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.building?.id?.let { buildingId ->
                remoteKeysDao.remoteKeysByBuildingId(buildingId)
            }
        }
    }

    /**
     * Get remote keys for the last item in the list.
     * Used during APPEND to determine next page to load.
     */
    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, BuildingWithDetails>
    ): BuildingRemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { building ->
                remoteKeysDao.remoteKeysByBuildingId(building.building.id)
            }
    }

    companion object {
        private const val TAG = "BuildingRemoteMediator"
    }
}
