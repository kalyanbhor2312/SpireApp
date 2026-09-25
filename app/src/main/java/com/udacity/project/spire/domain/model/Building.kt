package com.udacity.project.spire.domain.model

/**
 * Domain model representing a tall building/skyscraper.
 * This is the core business object used throughout the app.
 */
data class Building(
    val id: Int,
    val name: String,
    val city: String,
    val country: String,
    val heightMeters: Int,
    val floors: Int,
    val yearCompleted: Int,
    val architecturalStyle: String,
    val imageUrl: String,
    val description: String,
    val visitStatus: VisitStatus
)
