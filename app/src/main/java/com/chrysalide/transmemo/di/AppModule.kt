package com.chrysalide.transmemo.di

import androidx.biometric.BiometricManager
import com.chrysalide.transmemo.data.AndroidBiometricRepository
import com.chrysalide.transmemo.data.usecase.ComputeNextIntakeForProductUseCase
import com.chrysalide.transmemo.data.usecase.CreateIntakeForProductUseCase
import com.chrysalide.transmemo.data.usecase.DoIntakeForProductUseCase
import com.chrysalide.transmemo.data.usecase.GetNextCalendarEventsUseCase
import com.chrysalide.transmemo.domain.boundary.BiometricRepository
import com.chrysalide.transmemo.presentation.MainActivityViewModel
import com.chrysalide.transmemo.presentation.calendar.CalendarViewModel
import com.chrysalide.transmemo.presentation.calendar.dointake.DoIntakeViewModel
import com.chrysalide.transmemo.presentation.intakes.IntakesViewModel
import com.chrysalide.transmemo.presentation.inventory.InventoryViewModel
import com.chrysalide.transmemo.presentation.products.ProductsViewModel
import com.chrysalide.transmemo.presentation.products.add.AddProductViewModel
import com.chrysalide.transmemo.presentation.settings.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

private val viewModelModule = module {
    viewModelOf(::MainActivityViewModel)
    viewModelOf(::CalendarViewModel)
    viewModelOf(::IntakesViewModel)
    viewModelOf(::InventoryViewModel)
    viewModelOf(::ProductsViewModel)
    viewModelOf(::AddProductViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::DoIntakeViewModel)
}

private val repositoryModule = module {
    single { BiometricManager.from(androidContext()) }
    singleOf(::AndroidBiometricRepository) bind BiometricRepository::class
}

private val useCaseModule = module {
    singleOf(::ComputeNextIntakeForProductUseCase)
    singleOf(::GetNextCalendarEventsUseCase)
    singleOf(::CreateIntakeForProductUseCase)
    singleOf(::DoIntakeForProductUseCase)
}

val appModule = viewModelModule + repositoryModule + useCaseModule
