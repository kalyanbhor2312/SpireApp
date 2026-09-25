package com.udacity.project.spire.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.udacity.project.spire.domain.model.Building
import com.udacity.project.spire.domain.model.VisitStatus

/**
 * Main building data from the API.
 * TODO #27: Implement BuildingDto
 */
data class BuildingDto(
    val id: Int,
    val name: String,
    val city: CityDto,
    val country: CountryDto,

    @SerializedName("height_m")
    val heightMeters: Int,

    val floors: Int,

    @SerializedName("year_completed")
    val yearCompleted: Int,

    @SerializedName("architectural_style")
    val architecturalStyle: String,

    @SerializedName("image_url")
    val imageUrl: String,

    val description: String
)

/**
 * Convert BuildingDto to domain Building model.
 * Used by the repository to transform API data to domain data.
 */
fun BuildingDto.toDomainModel(): Building {
    return Building(
        id = this.id,
        name = this.name,
        city = this.city.name,
        country = this.country.name,
        heightMeters = this.heightMeters,
        floors = this.floors,
        yearCompleted = this.yearCompleted,
        architecturalStyle = this.architecturalStyle,
        imageUrl = this.imageUrl,
        description = this.description,
        visitStatus = VisitStatus.NOT_VISITED  // Default for new buildings
    )
}
