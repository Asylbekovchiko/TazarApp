package com.io.tazarapp.modules
import com.io.tazarapp.App.Companion.context
import com.io.tazarapp.BuildConfig
import com.io.tazarapp.utils.LanguagePref
import com.io.tazarapp.utils.SharedPrefModule
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


private val sLogLevel =
    if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE

private const val baseUrl = "http://tazar.sunrisetest.online/api/v1/"
private const val prodIP = "http://45.135.132.254/api/v1/"
private const val baseUrlMap = "http://www.mapquestapi.com/directions/v2/"
private const val localUrl = "http://192.168.88.123:8001/api/v1/"

private var currentUrl = baseUrl

fun createNetworkClient() = retrofitClient(
    currentUrl,
    okHttpClient(true)
)

fun createNetworkClientMap() = retrofitClient(
    baseUrlMap,
    okHttpClient(false)
)

private fun getLogInterceptor() = HttpLoggingInterceptor().apply { level = sLogLevel }

private fun okHttpClient(addAuthHeader: Boolean) = OkHttpClient.Builder()
    .addInterceptor(getLogInterceptor()).apply {
        setTimeOutToOkHttpClient(
            this
        )
    }
    .addInterceptor(headersInterceptor(addAuthHeader)).build()

private fun retrofitClient(baseUrl: String, httpClient: OkHttpClient): Retrofit =
    Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

fun headersInterceptor(addAuthHeader: Boolean) = Interceptor { chain ->
    chain.proceed(
        chain.request().newBuilder()
            .addHeader("Content-Type", "application/json")
            .also {
                if (addAuthHeader) {
                    if (SharedPrefModule(context).TokenModule().getToken() != null) {
                        it.addHeader(
                            "Authorization",
                            "Token " + SharedPrefModule(context).TokenModule().getToken()?.token
                        )
                    }
                    if (LanguagePref.LanguageModule().getLanguage(context) != "empty") {
                        it.addHeader(
                            "Accept-Language",
                                LanguagePref.LanguageModule().getLanguage(context)
                            )
                    }
                }
            }
            .build()
    )
}

private fun setTimeOutToOkHttpClient(okHttpClientBuilder: OkHttpClient.Builder) =
    okHttpClientBuilder.apply {
        readTimeout(30L, TimeUnit.SECONDS)
        connectTimeout(30L, TimeUnit.SECONDS)
        writeTimeout(30L, TimeUnit.SECONDS)
    }