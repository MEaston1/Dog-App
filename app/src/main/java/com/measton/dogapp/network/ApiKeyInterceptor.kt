package com.measton.dogapp.network

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import com.measton.dogapp.BuildConfig

class HeaderInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
        request.addHeader("x-api-key", BuildConfig.DOG_API_KEY)
        return chain.proceed(request.build())
    }
}
