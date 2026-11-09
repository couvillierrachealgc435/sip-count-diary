package com.sipcount.diary.presentation.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sipcount.diary.core.navigation.Screen
import com.sipcount.diary.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppViewModel(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _screen = MutableStateFlow<Screen>(Screen.Splash)
    val screen: StateFlow<Screen> = _screen.asStateFlow()

    private val _canGoBack = MutableStateFlow(false)
    val canGoBack: StateFlow<Boolean> = _canGoBack.asStateFlow()

    private val _darkTheme = MutableStateFlow(false)
    val darkTheme: StateFlow<Boolean> = _darkTheme.asStateFlow()

    private val backStack = ArrayDeque<Screen>()

    init {
        viewModelScope.launch {
            preferencesRepository.preferences.collect { preferences ->
                _darkTheme.value = preferences.darkTheme
            }
        }
    }

    fun navigate(target: Screen) {
        val current = _screen.value
        if (current == target) return
        if (current != Screen.Splash) backStack.addLast(current)
        _screen.value = target
        _canGoBack.value = backStack.isNotEmpty()
    }

    fun replaceWith(target: Screen) {
        backStack.clear()
        _screen.value = target
        _canGoBack.value = false
    }

    fun back() {
        val previous = backStack.removeLastOrNull() ?: return
        _screen.value = previous
        _canGoBack.value = backStack.isNotEmpty()
    }

    fun backToMenu() {
        backStack.clear()
        _screen.value = Screen.Menu
        _canGoBack.value = false
    }
}
