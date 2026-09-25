package com.udacity.project.spire

import android.app.Application
import com.udacity.project.spire.data.local.database.SpireDatabase
import com.udacity.project.spire.data.remote.api.ApiServiceProvider
import com.udacity.project.spire.data.repository.BuildingRepository
import com.udacity.project.spire.data.repository.DefaultBuildingRepository

class SpireApplication : Application() {

    lateinit var buildingRepository: BuildingRepository
        private set

    override fun onCreate() {
        super.onCreate()
        initializeRepository()
    }

    private fun initializeRepository() {
        // TODO #47: Replace MockBuildingRepository with DefaultBuildingRepository
        val database = SpireDatabase.getInstance(this)
        val apiService = ApiServiceProvider.apiService
        buildingRepository = DefaultBuildingRepository(database, apiService)
    }
}
