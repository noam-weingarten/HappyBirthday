package com.noam.happybirthday.di

import com.noam.happybirthday.data_layer.BirthdayRepository
import com.noam.happybirthday.data_layer.BirthdayRepositoryImpl
import com.noam.happybirthday.remote.WebSocketClient
import com.noam.happybirthday.view_model.BirthdayViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::BirthdayRepositoryImpl) { bind<BirthdayRepository>()}
    viewModelOf(::BirthdayViewModel)
    viewModel { BirthdayViewModel(get())}
    single { WebSocketClient() }
}
