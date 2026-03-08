package dev.testify.sample.paparazzi.data

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val category: String,
    val price: Double,
    val rating: Float,
    val imageUrl: String
)

data class Review(
    val reviewerName: String,
    val comment: String,
    val rating: Float
)
