package com.udacity.project.spire.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.udacity.project.spire.data.local.entity.CountryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Country entity.
 * Provides essential database operations for countries.
 *
 * TODO #7: Implement DAO methods with Room annotations
 */
@Dao
interface CountryDao {

    /**
     * Get all countries.
     * @return Flow of all countries sorted alphabetically by name
     */
    @Query("SELECT * FROM countries ORDER BY name ASC")
    fun getAllCountries(): Flow<List<CountryEntity>>

    /**
     * Find a country by name.
     * Used to check if country exists before inserting.
     * @param name The country name
     * @return CountryEntity if found, null otherwise
     */
    @Query("SELECT * FROM countries WHERE name = :name")
    suspend fun getCountryByName(name: String): CountryEntity?

    /**
     * Insert a country, replacing on conflict.
     * @param country The country to insert
     * @return The row ID of the inserted country
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCountry(country: CountryEntity): Long
}
