package com.udacity.project.spire.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Remote keys entity for building pagination.
 * Stores page number metadata for each building to enable proper pagination.
 *
 * Following the RemoteKeys pattern from:
 * https://www.bornfight.com/blog/android-paging-3-library-with-page-and-limit-parameters/
 *
 * Key Concept: All buildings on page 4 will have prevKey=3, nextKey=5
 * This enables RemoteMediator to know which page to load next.
 *
 * TODO #6: Add Room annotations for RemoteKeys entity
 *  1. Mark this class as @Entity with tableName = "building_remote_keys"
 *  2. Mark 'buildingId' as @PrimaryKey (NOT auto-generated)
 *     - Each building has exactly one set of remote keys (1:1 mapping)
 */
@Entity(tableName = "building_remote_keys")
data class BuildingRemoteKeys(
    /**
     * Primary key is the building ID.
     * Maps 1:1 with BuildingEntity.
     */
    @PrimaryKey
    val buildingId: Int,

    /**
     * Previous page number.
     * Null for items on the first page.
     */
    val prevKey: Int?,

    /**
     * Next page number.
     * Null when there are no more pages (endOfPaginationReached).
     */
    val nextKey: Int?
)
