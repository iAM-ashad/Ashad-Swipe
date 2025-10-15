package com.iamashad.ashad_swipe.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

// Retrofit interface for interacting with Swipe's backend APIs
interface SwipeApi {

    // Fetches the list of products from the server
    @GET("public/get")
    suspend fun getProducts(): List<ProductDto>

    // Uploads a new product with optional image files
    @Multipart
    @POST("public/add")
    suspend fun addProduct(
        @Part("product_name") name: RequestBody,
        @Part("product_type") type: RequestBody,
        @Part("price") price: RequestBody,
        @Part("tax") tax: RequestBody,
        @Part files: List<MultipartBody.Part> = emptyList() // Optional image(s)
    ): AddProductResponse
}

// Response model for product creation
@Serializable
data class AddProductResponse(
    val message: String? = null,   // API response message
    val productId: String? = null  // ID of the created product
)

// DTO representing a product received from the API
@Serializable
data class ProductDto(
    val image: String? = null,
    val price: Double,
    @SerialName("product_name") val productName: String,
    @SerialName("product_type") val productType: String,
    val tax: Double
)
