package com.udacity.project.spire.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.udacity.project.spire.domain.model.Building
import com.udacity.project.spire.domain.model.VisitStatus

/**
 * Room entity representing a building in the local database.
 * A building belongs to a city, which belongs to a country.
 *
 * TODO #3: Add Room annotations for the Building entity
 */
@Entity(
    tableName = "buildings",
    foreignKeys = [
        ForeignKey(
            entity = CityEntity::class,
            parentColumns = ["id"],
            childColumns = ["cityId"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["cityId"]),
        Index(value = ["name"])
    ]
)
data class BuildingEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val imageUrl: String,
    val heightMeters: Int,
    val floors: Int,
    val yearCompleted: Int,
    val architecturalStyle: String,
    val description: String,
    val visitStatus: VisitStatusEntity,
    val cityId: Int
)

/**
 * Data class representing a building with its city and country details.
 * Used for JOIN queries to get complete building information.
 *
 * TODO #4: Add Room relation annotations
 */
data class BuildingWithDetails(
    @Embedded
    val building: BuildingEntity,
    
    @Relation(
        entity = CityEntity::class,
        parentColumn = "cityId",
        entityColumn = "id"
    )
    val city: CityWithCountry
)

/**
 * Data class representing a city with its country details.
 *
 * TODO #5: Add Room relation annotations
 */
data class CityWithCountry(
    @Embedded
    val city: CityEntity,
    
    @Relation(
        parentColumn = "countryId",
        entityColumn = "id"
    )
    val country: CountryEntity
)

/**
 * Extension function to convert BuildingWithDetails to domain Building model.
 */
fun BuildingWithDetails.toDomainModel(): Building {
    return Building(
        id = building.id,
        name = building.name,
        city = city.city.name,
        country = city.country.name,
        heightMeters = building.heightMeters,
        floors = building.floors,
        yearCompleted = building.yearCompleted,
        architecturalStyle = building.architecturalStyle,
        imageUrl = building.imageUrl,
        description = building.description,
        visitStatus = building.visitStatus.toDomainModel()
    )
}

/**
 * Extension function to convert VisitStatusEntity to domain VisitStatus model.
 */
fun VisitStatusEntity.toDomainModel(): VisitStatus {
    return when (this) {
        VisitStatusEntity.NOT_VISITED -> VisitStatus.NOT_VISITED
        VisitStatusEntity.BUCKET_LIST -> VisitStatus.BUCKET_LIST
        VisitStatusEntity.VISITED -> VisitStatus.VISITED
    }
}

/**
 * Extension function to convert domain VisitStatus to VisitStatusEntity.
 */
fun VisitStatus.toEntity(): VisitStatusEntity {
    return when (this) {
        VisitStatus.NOT_VISITED -> VisitStatusEntity.NOT_VISITED
        VisitStatus.BUCKET_LIST -> VisitStatusEntity.BUCKET_LIST
        VisitStatus.VISITED -> VisitStatusEntity.VISITED
    }
}
