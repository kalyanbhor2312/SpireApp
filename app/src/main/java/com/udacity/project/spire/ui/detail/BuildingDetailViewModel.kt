package com.udacity.project.spire.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.udacity.project.spire.data.repository.BuildingRepository
import com.udacity.project.spire.domain.model.Building
import com.udacity.project.spire.domain.model.VisitStatus
import com.udacity.project.spire.ui.common.ErrorEvent
import com.udacity.project.spire.ui.common.Event
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * ViewModel for BuildingDetailFragment.
 * Loads a specific building by ID and handles visit status updates.
 *
 * TODO #39: Implement BuildingDetailViewModel
  */
class BuildingDetailViewModel(
    private val repository: BuildingRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    // Get buildingId from navigation arguments via SavedStateHandle
    private val buildingId =
        BuildingDetailFragmentArgs.fromSavedStateHandle(savedStateHandle).buildingId

    init {
        Log.d("BuildingDetailViewModel", "buildingId: $buildingId")
    }

    /**
     * TODO #39a: Initialize building property
     */
    val building: LiveData<Building?> = repository.getBuildingById(buildingId)
        .onEach { Log.d("BuildingDetailViewModel", "Building loaded: ${it?.name}") }
        .asLiveData()

    // Error state exposed to UI
    private val _errorEvent = MutableLiveData<Event<ErrorEvent>>()
    val errorEvent: LiveData<Event<ErrorEvent>> = _errorEvent

    // Success feedback
    private val _updateSuccess = MutableLiveData<Event<String>>()
    val updateSuccess: LiveData<Event<String>> = _updateSuccess

    /**
     * TODO #39b: Implement updateVisitStatus() method
     */
    fun updateVisitStatus(status: VisitStatus) {
        if (buildingId == -1) return

        viewModelScope.launch {
            repository.updateBuildingVisitStatus(buildingId, status)
                .onSuccess {
                    val message = when (status) {
                        VisitStatus.VISITED -> "Marked as visited!"
                        VisitStatus.BUCKET_LIST -> "Added to bucket list!"
                        VisitStatus.NOT_VISITED -> "Status updated!"
                    }
                    _updateSuccess.value = Event(message)
                }
                .onFailure { exception ->
                    _errorEvent.value = Event(
                        ErrorEvent(
                            message = exception.message ?: "Failed to update visit status",
                            throwable = exception
                        )
                    )
                }
        }
    }
}

/**
 * ViewModelFactory for BuildingDetailViewModel.
 * Uses SavedStateHandle to access navigation arguments.
 */
class BuildingDetailViewModelFactory(
    private val repository: BuildingRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(BuildingDetailViewModel::class.java)) {
            val savedStateHandle = extras.createSavedStateHandle()
            return BuildingDetailViewModel(repository, savedStateHandle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
