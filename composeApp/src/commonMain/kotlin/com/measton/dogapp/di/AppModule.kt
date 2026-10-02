package com.measton.dogapp.di

import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import com.measton.dogapp.DogRepository
import com.measton.dogapp.HomeRepository
import com.measton.dogapp.network.networkModule
import com.measton.dogapp.ui.DogViewModel
import com.measton.dogapp.ui.SharedPetViewModel
import com.measton.dogapp.ui.breeddetail.BreedDetailViewModel
import okio.FileSystem
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::HomeRepository) { bind<DogRepository>() }
}

val viewModelModule = module {
    viewModelOf(::DogViewModel)
    viewModelOf(::SharedPetViewModel)
    viewModelOf(::BreedDetailViewModel)
}

val imageModule = module {
    // A factory rather than an ImageLoader: Coil hands over the PlatformContext when it first
    // needs the loader. Network fetching comes from coil-network-ktor3, which registers itself
    // with its own HttpClient - the API client is not reused, so the x-api-key header never
    // reaches the image CDN.
    single {
        SingletonImageLoader.Factory { context ->
            ImageLoader.Builder(context)
                .memoryCache {
                    MemoryCache.Builder()
                        .maxSizePercent(context, 0.25)
                        .build()
                }
                .diskCache {
                    DiskCache.Builder()
                        .directory(FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "image_cache")
                        .maxSizePercent(0.02)
                        .build()
                }
                .build()
        }
    }
}

val appModule = module {
    includes(networkModule, repositoryModule, viewModelModule, imageModule)
}
