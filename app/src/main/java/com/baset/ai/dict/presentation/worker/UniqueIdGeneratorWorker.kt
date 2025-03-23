package com.baset.ai.dict.presentation.worker

import android.content.Context
import android.os.Build
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.baset.ai.dict.common.Constants
import com.baset.ai.dict.domain.PreferenceRepository
import kotlinx.coroutines.flow.first


class UniqueIdGeneratorWorker(
    appContext: Context,
    params: WorkerParameters,
    private val preferenceRepository: PreferenceRepository
) : CoroutineWorker(appContext, params) {

    companion object {
        private const val TAG = "UniqueIdGeneratorWorker"

        @JvmStatic
        fun enqueueWorker(context: Context) {
            val request = OneTimeWorkRequestBuilder<UniqueIdGeneratorWorker>()
                .addTag(TAG)
                .build()
            WorkManager
                .getInstance(context)
                .enqueueUniqueWork(
                    TAG,
                    ExistingWorkPolicy.REPLACE,
                    request
                )
        }
    }

    override suspend fun doWork(): Result {
        if (preferenceRepository
                .getPreference(
                    key = Constants.PreferencesKey.keyUniqueId,
                    defaultValue = ""
                )
                .first()
                .isEmpty()
        ) {
            val uniqueId = buildString {
                append(Build.BOARD)
                append(Constants.UNDERLINE)
                append(Build.BRAND)
                append(Constants.UNDERLINE)
                append(Build.DEVICE)
                append(Constants.UNDERLINE)
                append(Build.MANUFACTURER)
                append(Constants.UNDERLINE)
                append(Build.MODEL)
                append(Constants.UNDERLINE)
                append(Build.PRODUCT)
            }
            preferenceRepository.putPreference(
                Constants.PreferencesKey.keyUniqueId,
                uniqueId
            )
        }
        return Result.success()
    }
}
