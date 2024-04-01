package com.app.varzybos

import com.app.varzybos.MainViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.scope.get
import org.koin.dsl.module

val appModule = module {
    single { MainViewModel(androidApplication()) }
    viewModel { ApplicationViewModel(androidApplication())}
}