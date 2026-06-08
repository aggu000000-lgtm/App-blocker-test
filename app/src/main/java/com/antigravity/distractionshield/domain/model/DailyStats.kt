package com.antigravity.distractionshield.domain.model

data class DailyStats(
    val dateStr: String, // format: "yyyy-MM-dd"
    val focusDurationMs: Long,
    val blockedAttempts: Int,
    val rapidSwitches: Int
) {
    val distractionScore: Int
        get() = blockedAttempts * 5 + rapidSwitches * 2
}
