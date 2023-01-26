package com.jimmy.common.compose


sealed class UIState{
    object Success : UIState()
    data class Error(val message: UiText) : UIState()
    object Loading : UIState()
    object Empty : UIState()
}
