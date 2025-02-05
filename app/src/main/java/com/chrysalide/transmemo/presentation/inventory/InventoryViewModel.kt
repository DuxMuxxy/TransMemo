package com.chrysalide.transmemo.presentation.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chrysalide.transmemo.domain.boundary.DatabaseRepository
import com.chrysalide.transmemo.domain.model.Container
import com.chrysalide.transmemo.domain.model.ContainerState
import com.chrysalide.transmemo.presentation.inventory.InventoryUiState.Containers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class ContainersViewModel(
    private val databaseRepository: DatabaseRepository
) : ViewModel() {
    val inventoryUiState: StateFlow<InventoryUiState> = databaseRepository
        .getAllContainers()
        .map {
            it.filter { container -> container.product.inUse && container.state == ContainerState.OPEN }
        }.map(::Containers)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
            initialValue = InventoryUiState.Loading
        )

    fun recycleContainer(container: Container) {
        viewModelScope.launch {
            databaseRepository.recycleContainer(container)
        }
    }
}

sealed interface InventoryUiState {
    data object Loading : InventoryUiState

    data class Containers(
        val containers: List<Container>
    ) : InventoryUiState
}
