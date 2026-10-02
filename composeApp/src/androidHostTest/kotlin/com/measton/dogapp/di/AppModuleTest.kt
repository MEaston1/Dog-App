package com.measton.dogapp.di

import com.measton.dogapp.DogRepository
import com.measton.dogapp.HomeRepository
import com.measton.dogapp.network.DogApiClient
import com.measton.dogapp.network.networkModule
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.koinApplication
import org.koin.test.verify.verify
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertNotNull

@OptIn(KoinExperimentalAPI::class)
class AppModuleTest {

    @Test
    fun `koin config is complete`(){
        appModule.verify()
    }

    @Test
    fun `network graph instantiates`() {
        val koin = koinApplication { modules(networkModule, repositoryModule) }.koin
        try {
            assertNotNull(koin.get<DogApiClient>())
            assertIs<HomeRepository>(koin.get<DogRepository>())
        } finally {
            koin.close()
        }
    }
}