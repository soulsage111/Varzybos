package com.app.varzybos

import androidx.compose.material3.ExperimentalMaterial3Api
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@OptIn(ExperimentalMaterial3Api::class)
val appModule = module {
    single { MainActivity() }
    viewModel { MainViewModel(androidApplication()) }
}