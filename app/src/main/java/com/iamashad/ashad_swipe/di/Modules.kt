package com.iamashad.ashad_swipe.di

import androidx.room.Room
import com.iamashad.ashad_swipe.data.db.SwipeDb
import com.iamashad.ashad_swipe.data.remote.SwipeApi
import com.iamashad.ashad_swipe.domain.repo.SwipeRepository
import com.iamashad.ashad_swipe.domain.repo.SwipeRepositoryImpl
import com.iamashad.ashad_swipe.userinterface.add.AddProductViewModel
import com.iamashad.ashad_swipe.userinterface.list.ProductListViewModel
import com.iamashad.ashad_swipe.work.UploadPendingWorker
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

// Provides network-related dependencies
val networkModule = module {

    // Singleton JSON serializer
    single {
        Json {
            ignoreUnknownKeys = true
            explicitNulls = false
            isLenient = true
        }
    }

    // OkHttp client with logging
    single {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            .build()
    }

    // Retrofit setup using Kotlinx serialization
    single {
        val contentType = "application/json".toMediaType()
        Retrofit.Builder()
            .baseUrl("https://app.getswipe.in/api/")
            .client(get())
            .addConverterFactory(get<Json>().asConverterFactory(contentType))
            .build()
            .create(SwipeApi::class.java)
    }
}

// Provides Room database and DAOs
val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            SwipeDb::class.java,
            "swipe.db"
        )
            .fallbackToDestructiveMigration(true) // Explicitly allow destructive migration
            .build()
    }
    single { get<SwipeDb>().productDao() }
    single { get<SwipeDb>().pendingDao() }
}

// Provides repository implementation
val repositoryModule = module {
    singleOf(::SwipeRepositoryImpl) bind SwipeRepository::class
}

// Provides view models (uses new viewModel DSL)
val viewModelModule = module {
    viewModelOf(::ProductListViewModel)
    viewModelOf(::AddProductViewModel)
}

// Provides background workers
val workModule = module {
    worker { UploadPendingWorker(get(), get()) }
}
