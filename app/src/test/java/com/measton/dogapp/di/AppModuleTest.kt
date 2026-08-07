package com.measton.dogapp.di

import com.measton.dogapp.HomeRepository
import com.measton.dogapp.network.TheDogApi
import com.measton.dogapp.network.networkModule
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertNotNull
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.koinApplication
import org.koin.test.verify.verify

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
            assertNotNull(koin.get<TheDogApi>())
            assertNotNull(koin.get<HomeRepository>())
        } finally {
            koin.close()
        }
    }
}