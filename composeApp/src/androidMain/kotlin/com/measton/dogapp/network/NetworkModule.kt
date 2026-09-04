package com.measton.dogapp.network

import com.measton.dogapp.BuildConfig
import com.measton.dogapp.DOG_API_KEY
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
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

val networkModule = module {


    single {
        HttpClient(OkHttp) {
            expectSuccess = true

            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }

            if(BuildConfig.DEBUG){
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