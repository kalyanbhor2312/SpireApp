package com.udacity.project.spire.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.udacity.project.spire.data.local.dao.BuildingDao
import com.udacity.project.spire.data.local.dao.CityDao
import com.udacity.project.spire.data.local.dao.CountryDao
import com.udacity.project.spire.data.local.database.SpireDatabase
import com.udacity.project.spire.data.local.entity.BuildingEntity
import com.udacity.project.spire.data.local.entity.CityEntity
import com.udacity.project.spire.data.local.entity.CountryEntity
import com.udacity.project.spire.data.local.entity.toDomainModel
import com.udacity.project.spire.data.local.entity.toEntity
import com.udacity.project.spire.data.paging.BuildingRemoteMediator
import com.udacity.project.spire.data.remote.api.BuildingApiService
import com.udacity.project.spire.data.remote.dto.BuildingDto
import com.udacity.project.spire.data.remote.dto.PaginationMetadata
import com.udacity.project.spire.domain.model.Building
import com.udacity.project.spire.domain.model.BuildingStatistics
import com.udacity.project.spire.domain.model.VisitStatus
import com.udacity.project.spire.data.local.entity.VisitStatusEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Repository interface for building data operations.
 */
interface BuildingRepository {
    companion object {
        const val DEFAULT_PAGE_SIZE = 10
    }

    fun getBuildings(): Flow<PagingData<Building>>
    fun getBuildingById(id: Int): Flow<Building?>
    suspend fun refreshBuildings(): Result<Unit>
    suspend fun refreshBuildingsPaginated(page: Int, limit: Int = DEFAULT_PAGE_SIZE): Result<PaginationMetadata?>
    suspend fun updateBuildingVisitStatus(buildingId: Int, status: VisitStatus): Result<Unit>
    fun getBuildingsByCountry(country: String): Flow<List<Building>>
    fun getBuildingsByVisitStatus(status: VisitStatus): Flow<List<Building>>
    fun getAllCountries(): Flow<List<String>>
    suspend fun getStatistics(): BuildingStatistics
}

/**
 * Implementation of BuildingRepository.
 * Manages data operations between local database and remote API.
 */
@OptIn(ExperimentalPagingApi::class)
class DefaultBuildingRepository(
    private val database: SpireDatabase,
    private val apiService: BuildingApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BuildingRepository {

    private val buildingDao = database.buildingDao()
    private val cityDao = database.cityDao()
    private val countryDao = database.countryDao()

    private suspend fun getOrCreateCountry(countryName: String, code: String = ""): Int {
        val existingCountry = countryDao.getCountryByName(countryName)
        return if (existingCountry != null) {
            existingCountry.id
        } else {
            val newCountry = CountryEntity(name = countryName, code = code)
            countryDao.insertCountry(newCountry).toInt()
        }
    }

    private suspend fun getOrCreateCity(cityName: String, countryId: Int): Int {
        val existingCity = cityDao.getCityByNameAndCountry(cityName, countryId)
        return if (existingCity != null) {
            existingCity.id
        } else {
            val newCity = CityEntity(name = cityName, countryId = countryId)
            cityDao.insertCity(newCity).toInt()
        }
    }

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
            visitStatus = VisitStatusEntity.NOT_VISITED,
            cityId = cityId
        )
    }

    override fun getBuildings(): Flow<PagingData<Building>> {
        return Pager(
            config = PagingConfig(
                pageSize = BuildingRepository.DEFAULT_PAGE_SIZE,
                enablePlaceholders = false
            ),
            remoteMediator = BuildingRemoteMediator(apiService, database),
            pagingSourceFactory = { buildingDao.getBuildingsPagingSource() }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomainModel() }
        }
    }

    override fun getBuildingById(id: Int): Flow<Building?> {
        return buildingDao.getBuildingById(id).map { it?.toDomainModel() }
    }

    override suspend fun refreshBuildings(): Result<Unit> {
        return withContext(ioDispatcher) {
            try {
                val response = apiService.getBuildingsPaginated(1, 10)
                val entities = response.buildings.map { buildingDtoToEntity(it) }
                buildingDao.insertBuildings(entities)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun refreshBuildingsPaginated(
        page: Int,
        limit: Int
    ): Result<PaginationMetadata?> {
        return withContext(ioDispatcher) {
            try {
                val response = apiService.getBuildingsPaginated(page, limit)
                val entities = response.buildings.map { buildingDtoToEntity(it) }
                buildingDao.insertBuildings(entities)
                Result.success(response.pagination)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun updateBuildingVisitStatus(
        buildingId: Int,
        status: VisitStatus
    ): Result<Unit> {
        return withContext(ioDispatcher) {
            try {
                val buildingWithDetails = buildingDao.getBuildingById(buildingId).first()
                if (buildingWithDetails != null) {
                    val updatedBuilding = buildingWithDetails.building.copy(
                        visitStatus = status.toEntity()
                    )
                    buildingDao.updateBuilding(updatedBuilding)
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Building not found"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override fun getBuildingsByCountry(country: String): Flow<List<Building>> {
        return buildingDao.getBuildingsByCountry(country).map { list ->
            list.map { it.toDomainModel() }
        }
    }

    override fun getBuildingsByVisitStatus(status: VisitStatus): Flow<List<Building>> {
        return buildingDao.getBuildingsByVisitStatus(status.toEntity()).map { list ->
            list.map { it.toDomainModel() }
        }
    }

    override fun getAllCountries(): Flow<List<String>> {
        return countryDao.getAllCountries().map { list ->
            list.map { it.name }
        }
    }

    override suspend fun getStatistics(): BuildingStatistics {
        return withContext(ioDispatcher) {
            val totalBuildings = buildingDao.getBuildingCount()
            val visitedCount = buildingDao.getCountByStatus(VisitStatusEntity.VISITED)
            val bucketListCount = buildingDao.getCountByStatus(VisitStatusEntity.BUCKET_LIST)
            val totalMetersClimbed = buildingDao.getTotalMetersClimbed(VisitStatusEntity.VISITED) ?: 0
            val countriesExplored = buildingDao.getVisitedCountriesCount(VisitStatusEntity.VISITED)
            
            BuildingStatistics(
                totalBuildings = totalBuildings,
                visitedCount = visitedCount,
                bucketListCount = bucketListCount,
                totalMetersClimbed = totalMetersClimbed,
                countriesExplored = countriesExplored
            )
        }
    }
}
