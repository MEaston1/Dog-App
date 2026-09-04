package com.measton.dogapp.di

import com.measton.dogapp.DogRepository
import com.measton.dogapp.HomeRepository
import com.measton.dogapp.network.networkModule
import com.measton.dogapp.ui.DogViewModel
import com.measton.dogapp.ui.SharedPetViewModel
import com.measton.dogapp.ui.breeddetail.BreedDetailViewModel
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

val appModule = module {
    includes(networkModule, repositoryModule, viewModelModule)
}