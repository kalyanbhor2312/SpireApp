package com.udacity.project.spire.ui.statistics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.udacity.project.spire.data.repository.BuildingRepository
import com.udacity.project.spire.domain.model.BuildingStatistics
import com.udacity.project.spire.ui.common.ErrorEvent
import com.udacity.project.spire.ui.common.Event
import kotlinx.coroutines.launch

/**
 * ViewModel for StatisticsFragment.
 * Loads and displays aggregated building statistics.
 */
class StatisticsViewModel(
    private val repository: BuildingRepository
) : ViewModel() {

    private val _statistics = MutableLiveData<BuildingStatistics>()
    val statistics: LiveData<BuildingStatistics> = _statistics

    private val _errorEvent = MutableLiveData<Event<ErrorEvent>>()
    val errorEvent: LiveData<Event<ErrorEvent>> = _errorEvent

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadStatistics()
    }

    /**
     * Loads statistics from repository with loading state and error handling.
     */
    fun loadStatistics() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val stats = repository.getStatistics()
                _statistics.value = stats
            } catch (exception: Exception) {
                _errorEvent.value = Event(
                    ErrorEvent(
                        message = exception.message ?: "Failed to load statistics",
                        throwable = exception
                    )
                )
                // Set default statistics on error to avoid null pointer issues in UI
                _statistics.value = BuildingStatistics(
                    totalBuildings = 0,
                    visitedCount = 0,
                    bucketListCount = 0,
                    totalMetersClimbed = 0,
                    countriesExplored = 0
                )
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class StatisticsViewModelFactory(
    private val repository: BuildingRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatisticsViewModel::class.java)) {
            return StatisticsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
