package com.iamashad.ashad_swipe.domain.model

// Represents a product in the app's domain layer
data class Product(
    val image: String?,             // URL of the image from the server
    val price: Double,
    val name: String,
    val type: String,
    val tax: Double,
    val localThumb: String? = null, // URI of the locally selected image (if not uploaded yet)
    val isPending: Boolean = false  // True if the product is not yet synced to the server
)
