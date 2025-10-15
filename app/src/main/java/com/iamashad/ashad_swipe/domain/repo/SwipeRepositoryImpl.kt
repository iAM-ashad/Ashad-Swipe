package com.iamashad.ashad_swipe.domain.repo

import android.content.Context
import android.net.Uri
import com.iamashad.ashad_swipe.data.remote.ProductDto
import com.iamashad.ashad_swipe.data.remote.SwipeApi
import com.iamashad.ashad_swipe.data.db.PendingUploadDao
import com.iamashad.ashad_swipe.data.db.PendingUploadEntity
import com.iamashad.ashad_swipe.data.db.ProductDao
import com.iamashad.ashad_swipe.data.db.ProductEntity
import com.iamashad.ashad_swipe.data.db.toDomain
import com.iamashad.ashad_swipe.domain.model.Product
import com.iamashad.ashad_swipe.util.normalizeImageUrl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class SwipeRepositoryImpl(
    private val context: Context,
    private val api: SwipeApi,
    private val productDao: ProductDao,
    private val pendingDao: PendingUploadDao
) : SwipeRepository {

    override fun observeProducts(): Flow<Resource<List<Product>>> =
        productDao.observeAll()
            .map { rows -> Resource.Success(rows.map { it.toDomain() }) as Resource<List<Product>> }
            .onStart {
                emit(Resource.Loading)
                refresh() // Pulls data before emitting DB results
            }
            .catch { e -> emit(Resource.Error(e)) }

    override suspend fun refresh(): Resource<Unit> = runCatching {
        val remoteProducts = api.getProducts().map { it.toDomain() }

        // Keep pending uploads intact; replace only confirmed server data
        productDao.clearNonPending()

        // Insert fresh data from server
        productDao.insertAll(
            remoteProducts.map {
                ProductEntity(
                    image = it.image,
                    price = it.price,
                    name = it.name,
                    type = it.type,
                    tax = it.tax,
                    localThumb = null,
                    isPending = false
                )
            }
        )

        // If any pending item now exists on the server, remove its local copy
        for (pending in productDao.getAllPending()) {
            val existsOnServer = remoteProducts.any { r ->
                r.name == pending.name &&
                        r.type == pending.type &&
                        r.price == pending.price &&
                        r.tax == pending.tax
            }
            if (existsOnServer) productDao.deleteById(pending.id)
        }
    }.fold(
        onSuccess = { Resource.Success(Unit) },
        onFailure = { Resource.Error(it) }
    )

    override suspend fun addOnline(
        name: String,
        type: String,
        price: String,
        tax: String,
        imageUris: List<Uri>
    ): Resource<Unit> = runCatching {
        val textType = "text/plain".toMediaType()

        // Convert first image URI (if any) into a multipart part
        val fileParts = imageUris.firstOrNull()?.let { uri ->
            val media = "image/*".toMediaType()
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: ByteArray(0)
            val body = bytes.toRequestBody(media)
            listOf(MultipartBody.Part.createFormData("files[]", "product.jpg", body))
        } ?: emptyList()

        // Upload to server
        api.addProduct(
            name.toRequestBody(textType),
            type.toRequestBody(textType),
            price.toRequestBody(textType),
            tax.toRequestBody(textType),
            fileParts
        )

        // Refresh to ensure local DB matches server state
        refresh()
    }.fold(
        onSuccess = { Resource.Success(Unit) },
        onFailure = { Resource.Error(it) }
    )

    override suspend fun enqueueOffline(
        name: String,
        type: String,
        price: String,
        tax: String,
        imageUris: List<Uri>
    ) {
        // Store product locally with pending flag
        val localId = productDao.insert(
            ProductEntity(
                image = null,
                price = price.toDoubleOrNull() ?: 0.0,
                name = name,
                type = type,
                tax = tax.toDoubleOrNull() ?: 0.0,
                localThumb = imageUris.firstOrNull()?.toString(),
                isPending = true
            )
        )

        // Add to pending upload queue
        pendingDao.insert(
            PendingUploadEntity(
                name = name,
                type = type,
                price = price,
                tax = tax,
                imageUri = imageUris.firstOrNull()?.toString(),
                localProductId = localId
            )
        )
    }

    override suspend fun processPending() {
        for (item in pendingDao.getAll()) {
            val success = runCatching {
                val uri = item.imageUri?.let(Uri::parse)
                addOnline(item.name, item.type, item.price, item.tax, listOfNotNull(uri))
            }.isSuccess

            if (success) {
                // Remove uploaded product from local pending cache
                item.localProductId?.let { productDao.deleteById(it) }
                pendingDao.delete(item)
                // addOnline() already triggers refresh()
            }
        }
    }
}

// Maps a DTO from API into a domain-level Product
private fun ProductDto.toDomain() = Product(
    image = normalizeImageUrl(image),
    price = price,
    name = productName,
    type = productType,
    tax = tax
)
