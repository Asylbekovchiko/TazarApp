package com.io.tazarapp.modules


import com.io.tazarapp.ui.auth.changeNumber.ChangeNumViewModel
import com.io.tazarapp.ui.recycler.schedule.ScheduleViewModel
import com.io.tazarapp.ui.auth.login.city.CityViewModel
import com.io.tazarapp.ui.auth.login.signIn.SignInViewModel
import com.io.tazarapp.ui.citizen.ad.adCreate.viewModel.AdCreateViewModel
import com.io.tazarapp.ui.auth.login.signUp.SignUpViewModel
import com.io.tazarapp.ui.auth.version_check.VersionCheckViewModel
import com.io.tazarapp.ui.citizen.ad.adEdit.viewModel.AdEditViewModel
import com.io.tazarapp.ui.citizen.filter.viewmodel.FilterCategoryViewModel
import com.io.tazarapp.ui.citizen.handbook.HandBookViewModel
import com.io.tazarapp.ui.citizen.map.MapCitizenViewModel
import com.io.tazarapp.ui.citizen.news.NewsViewModel
import com.io.tazarapp.ui.citizen.partners.PartnersViewModel
import com.io.tazarapp.ui.recycler.map.MapRecyclerViewModel
import com.io.tazarapp.ui.citizen.profile.ProfileMainViewModel
import com.io.tazarapp.ui.citizen.profile.item_profile.about_info.AboutAppViewModel
import com.io.tazarapp.ui.citizen.profile.item_profile.my_advertisements.viewmodel.AdvertisementFragmentViewModel
import com.io.tazarapp.ui.citizen.profile.item_profile.my_advertisements.viewmodel.ScoringHistoryFragmentViewModel
import com.io.tazarapp.ui.citizen.profile.item_profile.profile_info.ProfileViewModel
import com.io.tazarapp.ui.recycler.qrcode.found.FoundViewModel
import com.io.tazarapp.ui.citizen.profile.item_profile.qr_info.QrViewModel
import com.io.tazarapp.ui.citizen.profile.item_profile.statistic_info.StatisticsViewModel
import com.io.tazarapp.ui.recycler.map.mapQuest.MapQuestViewModel
import com.io.tazarapp.ui.recycler.profile.item_profile.profile_info.ResProfileViewModel
import com.io.tazarapp.ui.recycler.history.HistoryViewModel
import com.io.tazarapp.ui.recycler.qrcode.ModerationCheckViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel { PartnersViewModel(get()) }
    viewModel { CityViewModel(get()) }
    viewModel { SignUpViewModel(get()) }
    viewModel { MapRecyclerViewModel(get()) }
    viewModel { AdCreateViewModel(get()) }
    viewModel { FilterCategoryViewModel(get()) }
    viewModel { AboutAppViewModel(get()) }
    viewModel { HandBookViewModel(get()) }
    viewModel { ProfileMainViewModel(get()) }
    viewModel { SignInViewModel(get()) }
    viewModel { ProfileViewModel(get()) }
    viewModel { AdvertisementFragmentViewModel(get()) }
    viewModel { AdEditViewModel(get()) }
    viewModel { FoundViewModel(get()) }
    viewModel { StatisticsViewModel(get()) }
    viewModel { QrViewModel(get()) }
    viewModel { ChangeNumViewModel(get()) }
    viewModel { ScheduleViewModel(get()) }
    viewModel { HistoryViewModel(get()) }
    viewModel { ScoringHistoryFragmentViewModel(get()) }
    viewModel { ModerationCheckViewModel(get()) }
    viewModel { MapQuestViewModel(get()) }
    viewModel { ResProfileViewModel(get()) }
    viewModel { VersionCheckViewModel(get()) }
    viewModel { MapCitizenViewModel(get()) }
    viewModel { NewsViewModel(get()) }
}