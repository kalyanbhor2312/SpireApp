package com.udacity.project.spire.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.udacity.project.spire.data.local.entity.BuildingEntity
import com.udacity.project.spire.data.local.entity.BuildingWithDetails
import com.udacity.project.spire.data.local.entity.VisitStatusEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Building entity.
 * Defines database operations for the buildings table.
 */
@Dao
interface BuildingDao {

    /**
     * Get all buildings with their city and country details.
     */
    @Transaction
    @Query("SELECT * FROM buildings ORDER BY id ASC")
    fun getAllBuildings(): Flow<List<BuildingWithDetails>>

    /**
     * Get all buildings as a PagingSource for Paging 3.
     */
    @Transaction
    @Query("SELECT * FROM buildings ORDER BY id ASC")
    fun getBuildingsPagingSource(): PagingSource<Int, BuildingWithDetails>

    /**
     * Get a specific building by ID with city and country details.
     */
    @Transaction
    @Query("SELECT * FROM buildings WHERE id = :id")
    fun getBuildingById(id: Int): Flow<BuildingWithDetails?>

    /**
     * Get all buildings in a specific country by country name.
     */
    @Transaction
    @Query(
        """
        SELECT b.* FROM buildings AS b
        INNER JOIN cities AS c ON b.cityId = c.id
        INNER JOIN countries AS co ON c.countryId = co.id
        WHERE co.name = :countryName
        ORDER BY b.id ASC
        """
    )
    fun getBuildingsByCountry(countryName: String): Flow<List<BuildingWithDetails>>

    /**
     * Get all buildings with a specific visit status.
     */
    @Transaction
    @Query("SELECT * FROM buildings WHERE visitStatus = :status ORDER BY id ASC")
    fun getBuildingsByVisitStatus(status: VisitStatusEntity): Flow<List<BuildingWithDetails>>

    /**
     * Insert multiple buildings, replacing on conflict.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBuildings(buildings: List<BuildingEntity>)

    /**
     * Update an existing building.
     */
    @Update
    suspend fun updateBuilding(building: BuildingEntity)

    /**
     * Clear all buildings from the database.
     */
    @Query("DELETE FROM buildings")
    suspend fun clearBuildings()

    /**
     * Get the total count of buildings.
     */
    @Query("SELECT COUNT(*) FROM buildings")
    suspend fun getBuildingCount(): Int

    /**
     * Get the count of buildings with a specific visit status.
     */
    @Query("SELECT COUNT(*) FROM buildings WHERE visitStatus = :status")
    suspend fun getCountByStatus(status: VisitStatusEntity): Int

    /**
     * Get the count of unique countries where buildings have been visited.
     */
    @Query(
        """
        SELECT COUNT(DISTINCT co.id) FROM countries AS co
        INNER JOIN cities AS c ON co.id = c.countryId
        INNER JOIN buildings AS b ON c.id = b.cityId
        WHERE b.visitStatus = :status
        """
    )
    suspend fun getVisitedCountriesCount(status: VisitStatusEntity = VisitStatusEntity.VISITED): Int

    /**
     * Get the total height of all buildings with a specific visit status.
     */
    @Query("SELECT SUM(heightMeters) FROM buildings WHERE visitStatus = :status")
    suspend fun getTotalMetersClimbed(status: VisitStatusEntity = VisitStatusEntity.VISITED): Int?
}
