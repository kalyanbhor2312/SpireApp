package com.udacity.project.spire.data.remote.dto

/**
 * The root response object from the /api/buildings endpoint.
 * TODO #23: Implement BuildingsResponse DTO
 */
data class BuildingsResponse(
    val buildings: List<BuildingDto>,
    val pagination: PaginationMetadata?
)
