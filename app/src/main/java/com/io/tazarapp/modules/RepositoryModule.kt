package com.io.tazarapp.modules

import com.io.tazarapp.data.model.CityDataModel
import com.io.tazarapp.data.model.ad.AdModel
import com.io.tazarapp.data.model.ad.AdFile
import com.io.tazarapp.data.model.collectionplace.CollectionPointModel
import com.io.tazarapp.data.model.cp.CpFile
import com.io.tazarapp.data.repository.LoginRepository
import com.io.tazarapp.data.repository.NewsRepository
import com.io.tazarapp.data.repository.VersionCheckRepository
import com.io.tazarapp.data.repository.citizen.*
import com.io.tazarapp.data.repository.recycler.MapRecyclerRepository
import com.io.tazarapp.data.repository.citizen.MyAdvertisementRepository
import com.io.tazarapp.data.repository.citizen.ProfileRepository
import com.io.tazarapp.data.repository.filter.FilterRepository
import com.io.tazarapp.ui.recycler.schedule.ScheduleRepository
import com.io.tazarapp.data.repository.recycler.FoundRepository
import com.io.tazarapp.ui.recycler.map.mapQuest.MapQuestRepo
import com.io.tazarapp.data.repository.recycler.ModerationCheckRepository
import com.io.tazarapp.data.repository.recycler.HistoryRepository
import org.koin.dsl.module


val repositoryModule = module {
    single { MainRepository(get()) }
    single { LoginRepository(get()) }
    single { MapRecyclerRepository(get()) }
    single { AdRepository(get(),get()) }
    single { FilterRepository(get()) }
    single { ProfileRepository(get()) }
    single { NewsRepository(get()) }

    //Data for creating advertise
    single { AdModel() }
    single { AdFile() }
    single { CpFile() }

    //myEdits
    single { MyAdvertisementRepository(get()) }
    single { CollectionPointModel() }
    single { FoundRepository(get()) }
    single { ScheduleRepository(get(),get()) }
    single { HistoryRepository(get()) }
    single { ModerationCheckRepository(get()) }

    //MapQuest
    single { MapQuestRepo(get()) }

    single { VersionCheckRepository(get()) }
    single { MapCitizenRepository(get()) }

    single { CityDataModel() }

}