package com.example.dog.network

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class HeaderInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
        request.addHeader("x-api-key", "live_S51JT7UdAEZTYZXwesxvzCbXhZCBj7HODuTycsOUY9kTPH3GeZfn6lraWKrH3xqR")
        return chain.proceed(request.build())
    }
}
