package com.udacity.project.spire.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.udacity.project.spire.data.local.converter.VisitStatusConverter
import com.udacity.project.spire.data.local.dao.BuildingDao
import com.udacity.project.spire.data.local.dao.BuildingRemoteKeysDao
import com.udacity.project.spire.data.local.dao.CityDao
import com.udacity.project.spire.data.local.dao.CountryDao
import com.udacity.project.spire.data.local.entity.BuildingEntity
import com.udacity.project.spire.data.local.entity.BuildingRemoteKeys
import com.udacity.project.spire.data.local.entity.CityEntity
import com.udacity.project.spire.data.local.entity.CountryEntity

/**
 * Room Database for the Spire application.
 * Manages local persistence of building, city, and country data.
 *
 * TODO #22: Configure Room Database with annotations
 */
@Database(
    entities = [
        BuildingEntity::class,
        CityEntity::class,
        CountryEntity::class,
        BuildingRemoteKeys::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(VisitStatusConverter::class)
abstract class SpireDatabase : RoomDatabase() {

    abstract fun buildingDao(): BuildingDao
    abstract fun cityDao(): CityDao
    abstract fun countryDao(): CountryDao
    abstract fun buildingRemoteKeysDao(): BuildingRemoteKeysDao

    companion object Companion {
        private const val DATABASE_NAME = "spire_database"

        @Volatile
        private var INSTANCE: SpireDatabase? = null

        /**
         * Get the singleton instance of BuildingDatabase.
         * Uses double-check locking pattern to ensure thread safety.
         */
        fun getInstance(context: Context): SpireDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SpireDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
