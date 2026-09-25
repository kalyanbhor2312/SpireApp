package com.udacity.project.spire.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.udacity.project.spire.data.local.entity.CityEntity

/**
 * Data Access Object for City entity.
 * Provides essential database operations for cities.
 *
 * TODO #8: Implement CityDao queries with Room annotations
 */
@Dao
interface CityDao {

    /**
     * Find a city by name and country ID.
     * Used to check if city exists before inserting.
     * @param name The city name
     * @param countryId The country ID this city belongs to
     * @return CityEntity if found, null otherwise
     */
    @Query("SELECT * FROM cities WHERE name = :name AND countryId = :countryId")
    suspend fun getCityByNameAndCountry(name: String, countryId: Int): CityEntity?

    /**
     * Insert a city, replacing on conflict.
     * @param city The city to insert
     * @return The row ID of the inserted city
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCity(city: CityEntity): Long
}
