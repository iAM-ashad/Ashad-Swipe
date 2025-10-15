package com.iamashad.ashad_swipe.domain.repo

import android.net.Uri
import com.iamashad.ashad_swipe.domain.model.Product
import kotlinx.coroutines.flow.Flow

// Represents the state of a resource being loaded or fetched
sealed interface Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>        // Data loaded successfully
    data class Error(val t: Throwable) : Resource<Nothing>  // Error occurred
    data object Loading : Resource<Nothing>                 // Operation in progress
}

// Repository interface defining all product-related operations
interface SwipeRepository {

    // Observes all products with their loading state
    fun observeProducts(): Flow<Resource<List<Product>>>

    // Fetches latest products from the API and updates local cache
    suspend fun refresh(): Resource<Unit>

    // Adds a product directly to the server (online mode)
    suspend fun addOnline(
        name: String,
        type: String,
        price: String,
        tax: String,
        imageUris: List<Uri>
    ): Resource<Unit>

    // Saves a product locally for later upload (offline mode)
    suspend fun enqueueOffline(
        name: String,
        type: String,
        price: String,
        tax: String,
        imageUris: List<Uri>
    )

    // Uploads any locally stored pending products when back online
    suspend fun processPending()
}
