package com.example.core.customization

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ThemeHistoryManager(initialTheme: ThemeConfig) {
    private val undoStack = mutableListOf<ThemeConfig>()
    private val redoStack = mutableListOf<ThemeConfig>()
    val initialSnapshot: ThemeConfig = initialTheme

    private val _canUndo = MutableStateFlow(false)
    val canUndo: StateFlow<Boolean> = _canUndo.asStateFlow()

    private val _canRedo = MutableStateFlow(false)
    val canRedo: StateFlow<Boolean> = _canRedo.asStateFlow()

    private val _isLocked = MutableStateFlow(false)
    val isLocked: StateFlow<Boolean> = _isLocked.asStateFlow()

    fun pushState(theme: ThemeConfig) {
        if (_isLocked.value) return
        undoStack.add(theme)
        if (undoStack.size > 25) undoStack.removeAt(0)
        redoStack.clear()
        _canUndo.value = undoStack.isNotEmpty()
        _canRedo.value = false
    }

    fun undo(currentTheme: ThemeConfig): ThemeConfig? {
        if (undoStack.isEmpty() || _isLocked.value) return null
        redoStack.add(currentTheme)
        val prev = undoStack.removeAt(undoStack.size - 1)
        _canUndo.value = undoStack.isNotEmpty()
        _canRedo.value = true
        return prev
    }

    fun redo(currentTheme: ThemeConfig): ThemeConfig? {
        if (redoStack.isEmpty() || _isLocked.value) return null
        undoStack.add(currentTheme)
        val next = redoStack.removeAt(redoStack.size - 1)
        _canUndo.value = true
        _canRedo.value = redoStack.isNotEmpty()
        return next
    }

    fun toggleLock(): Boolean {
        _isLocked.value = !_isLocked.value
        return _isLocked.value
    }
}
