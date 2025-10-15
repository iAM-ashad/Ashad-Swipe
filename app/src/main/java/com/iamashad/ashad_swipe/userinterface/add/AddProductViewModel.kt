package com.iamashad.ashad_swipe.userinterface.add

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.iamashad.ashad_swipe.domain.repo.Resource
import com.iamashad.ashad_swipe.domain.repo.SwipeRepository
import com.iamashad.ashad_swipe.work.UploadPendingWorker.Companion.kickOnce

// Handles product creation logic for the "Add Product" screen
class AddProductViewModel(private val repo: SwipeRepository) : ViewModel() {

    // Form fields
    var image: Uri? by mutableStateOf(null)
    var name by mutableStateOf("")
    var type by mutableStateOf<String?>(null) // Either "Product" or "Service"
    var price by mutableStateOf("")
    var tax by mutableStateOf("")

    // UI state flag for submission progress
    var submitting by mutableStateOf(false)

    // Resets form inputs after submission or cancel
    fun clear() {
        image = null
        name = ""
        type = null
        price = ""
        tax = ""
    }

    // Validates inputs and submits product (online or offline)
    suspend fun submit(context: Context): Pair<Boolean, String> {
        val isValid = name.isNotBlank() &&
                (type == "Product" || type == "Service") &&
                price.toDoubleOrNull() != null &&
                tax.toDoubleOrNull() != null
        if (!isValid) return false to "Please fix the highlighted fields."

        submitting = true
        val images = listOfNotNull(image)

        val result = when (repo.addOnline(name, type!!, price, tax, images)) {
            is Resource.Success -> {
                submitting = false
                true to "Product added successfully!"
            }

            is Resource.Error -> {
                // Save locally and schedule background sync
                repo.enqueueOffline(name, type!!, price, tax, images)
                kickOnce(context)
                submitting = false
                true to "Saved offline. Will upload when online."
            }

            else -> {
                submitting = false
                false to "Something went wrong."
            }
        }

        return result
    }
}