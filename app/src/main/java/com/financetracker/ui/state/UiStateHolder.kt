package com.financetracker.ui.state

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Composable state holder wrapping a `MutableStateFlow<S>` plus a cancellable `load(flow)`.
 * Used by ViewModels that stream data from a use case (e.g. AnalyticsViewModel).
 *
 * Composition, not inheritance — does not violate AGENTS.md §16 "No base ViewModel". The
 * `*UiState` payload type holds domain/loading fields; this holder owns only the
 * cancel-and-collect plumbing.
 */
class UiStateHolder<S>(initial: S, private val scope: CoroutineScope) {
    private val _state: MutableStateFlow<S> = MutableStateFlow(initial)
    val state: StateFlow<S> = _state.asStateFlow()

    private var loadJob: Job? = null

    fun load(flow: Flow<S>) {
        loadJob?.cancel()
        loadJob = scope.launch { flow.collect { _state.value = it } }
    }
}
