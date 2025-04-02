package com.noam.happybirthday.di

import com.noam.happybirthday.data_layer.BabyImageRepository
import com.noam.happybirthday.data_layer.BabyImageRepositoryImpl
import com.noam.happybirthday.data_layer.BirthdayRepository
import com.noam.happybirthday.data_layer.BirthdayRepositoryImpl
import com.noam.happybirthday.remote.WebSocketClient
import com.noam.happybirthday.ui.view.Navigator
import com.noam.happybirthday.view_model.BirthdayViewModel
import com.noam.happybirthday.view_model.ImageViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    singleOf(::BirthdayRepositoryImpl) { bind<BirthdayRepository>()}
    single{ BabyImageRepositoryImpl(androidContext()) } bind BabyImageRepository::class
    viewModelOf(::BirthdayViewModel)
    viewModel { ImageViewModel(Dispatchers.Default, get()) }
    single { WebSocketClient() }
    single { Navigator() }
}
