package com.udacity.project.spire.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.udacity.project.spire.data.local.entity.BuildingRemoteKeys

/**
 * Data Access Object for BuildingRemoteKeys.
 * Manages pagination state for buildings.
 *
 * TODO #9: Implement RemoteKeysDao for Paging3 with Room annotations
 */
@Dao
interface BuildingRemoteKeysDao {

    /**
     * Insert or replace remote keys for multiple buildings.
     * Called after fetching a page from the API.
     *
     * @param remoteKeys List of remote keys to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKeys: List<BuildingRemoteKeys>)

    /**
     * Get remote keys for a specific building.
     * Used to determine which page to load next.
     *
     * @param buildingId The building ID
     * @return RemoteKeys for that building, or null if not found
     */
    @Query("SELECT * FROM building_remote_keys WHERE buildingId = :buildingId")
    suspend fun remoteKeysByBuildingId(buildingId: Int): BuildingRemoteKeys?

    /**
     * Clear all remote keys.
     * Called during REFRESH to reset pagination state.
     */
    @Query("DELETE FROM building_remote_keys")
    suspend fun clearRemoteKeys()
}
