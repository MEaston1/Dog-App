package com.example.dog.network

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class HeaderInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
        request.addHeader("x-api-key", "live_QRgtqyX7S5fd1d3JND27ibTsNFpvwYyu6o82YIWPqYIohrzTPXaAyS89raO88qFs")
        return chain.proceed(request.build())
    }
}
