package com.iamashad.ashad_swipe.work

import android.content.Context
import androidx.work.*
import com.iamashad.ashad_swipe.data.db.PendingUploadDao
import com.iamashad.ashad_swipe.domain.repo.SwipeRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

/**
 * Background worker that uploads any pending offline products
 * once the device is connected to the internet.
 *
 * - Runs periodically (every 15 min) via [schedule]
 * - Can also be triggered immediately via [kickOnce]
 */
class UploadPendingWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params), KoinComponent {

    // Inject dependencies from Koin
    private val repo: SwipeRepository by inject()
    private val pendingDao: PendingUploadDao by inject()

    /**
     * Uploads pending products and reports how many were successfully synced.
     */
    override suspend fun doWork(): Result {
        val before = pendingDao.getAll().size
        repo.processPending()
        val after = pendingDao.getAll().size
        val uploadedCount = (before - after).coerceAtLeast(0)

        return Result.success(workDataOf("uploaded_count" to uploadedCount))
    }

    companion object {
        const val TAG = "pending_upload"

        /**
         * Schedules periodic sync every 15 minutes (network required).
         * Used for background retry when app is idle.
         */
        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<UploadPendingWorker>(
                15, TimeUnit.MINUTES
            )
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .addTag(TAG)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "pending_uploader_periodic",
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
        }

        /**
         * Triggers an immediate one-time upload.
         * Called when user saves a product offline.
         */
        fun kickOnce(context: Context) {
            val request = OneTimeWorkRequestBuilder<UploadPendingWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    10, TimeUnit.SECONDS
                )
                .addTag(TAG)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "pending_uploader_once",
                ExistingWorkPolicy.REPLACE,
                request
            )
        }
    }
}
