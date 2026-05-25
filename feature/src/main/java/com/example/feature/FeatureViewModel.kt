package com.example.feature

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FeatureViewModel @Inject constructor() : ViewModel() {
    val message = "Hello from injected ViewModel"
}
