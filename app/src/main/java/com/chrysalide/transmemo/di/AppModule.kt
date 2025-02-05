package com.chrysalide.transmemo.di

import com.chrysalide.transmemo.presentation.MainActivityViewModel
import com.chrysalide.transmemo.presentation.calendar.CalendarViewModel
import com.chrysalide.transmemo.presentation.containers.ContainersViewModel
import com.chrysalide.transmemo.presentation.products.ProductsViewModel
import com.chrysalide.transmemo.presentation.products.add.AddProductViewModel
import com.chrysalide.transmemo.presentation.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

private val viewModelModule = module {
    viewModelOf(::MainActivityViewModel)
    viewModelOf(::CalendarViewModel)
    viewModelOf(::ContainersViewModel)
    viewModelOf(::ProductsViewModel)
    viewModelOf(::AddProductViewModel)
    viewModelOf(::SettingsViewModel)
}

val appModule = viewModelModule
