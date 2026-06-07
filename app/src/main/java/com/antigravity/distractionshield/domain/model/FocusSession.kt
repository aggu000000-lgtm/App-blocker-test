package com.antigravity.distractionshield.domain.model

data class FocusSession(
    val isActive: Boolean,
    val endTime: Long,
    val totalDuration: Long
)
