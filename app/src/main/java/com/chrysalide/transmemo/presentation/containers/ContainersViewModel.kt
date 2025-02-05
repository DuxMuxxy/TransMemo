package com.chrysalide.transmemo.presentation.containers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chrysalide.transmemo.domain.boundary.DatabaseRepository
import com.chrysalide.transmemo.domain.model.Container
import com.chrysalide.transmemo.presentation.containers.ContainersUiState.Containers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class ContainersViewModel(
    private val databaseRepository: DatabaseRepository
) : ViewModel() {
    val containersUiState: StateFlow<ContainersUiState> = databaseRepository
        .getAllContainers()
        .map(::Containers)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
            initialValue = ContainersUiState.Loading
        )

    fun deleteContainer(container: Container) {
        viewModelScope.launch {
            databaseRepository.deleteContainer(container)
        }
    }
}

sealed interface ContainersUiState {
    data object Loading : ContainersUiState

    data class Containers(
        val containers: List<Container>
    ) : ContainersUiState
}
