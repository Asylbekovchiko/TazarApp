package com.io.tazarapp.modules

import com.io.tazarapp.data.api.AuthApi
import com.io.tazarapp.data.api.CitizenApi
import com.io.tazarapp.data.api.RecyclerApi
import com.io.tazarapp.data.api.SharedApi
import com.io.tazarapp.data.api.MapApi
import org.koin.dsl.module
import retrofit2.Retrofit


private val retrofit: Retrofit =
    createNetworkClient()
private val retrofitMap: Retrofit =
    createNetworkClientMap()

private val AUTH_API: AuthApi = retrofit.create(AuthApi::class.java)
private val CITIZEN_API: CitizenApi = retrofit.create(CitizenApi::class.java)
private val RECYCLER_API: RecyclerApi = retrofit.create(RecyclerApi::class.java)
private val SHARED_API: SharedApi = retrofit.create(SharedApi::class.java)
private val MAP: MapApi = retrofitMap.create(
    MapApi::class.java)

val networkModule = module {
    single { AUTH_API }
    single { CITIZEN_API }
    single { RECYCLER_API }
    single { SHARED_API }
    single { MAP }
}