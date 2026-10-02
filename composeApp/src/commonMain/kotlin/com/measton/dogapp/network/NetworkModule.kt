package com.measton.dogapp.network

import com.measton.dogapp.DOG_API_KEY
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module


private const val BASE_URL = "https://api.thedogapi.com/v1/"

/**
 * Koin property each platform's startKoin sets to say whether this is a debug build. Only the
 * application module knows that (a KMP library has no build types), so it is passed in rather
 * than read through expect/actual. Defaults to false when unset.
 */
const val DEBUG_BUILD_PROPERTY = "isDebugBuild"

val networkModule = module {


    single {
        val isDebugBuild = getProperty(DEBUG_BUILD_PROPERTY, false)
        HttpClient {
            expectSuccess = true

            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }

            if(isDebugBuild){
                install(Logging) { level = LogLevel.BODY}
            }

            defaultRequest {
                url(BASE_URL)
                header("x-api-key", DOG_API_KEY)
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 60_000
                connectTimeoutMillis = 60_000
            }

        }
    }

    factoryOf(::DogApiClient)
}