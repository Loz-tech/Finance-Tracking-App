package com.financetracker.domain.util

import java.time.LocalDate

interface TimeProvider {
    fun today(): LocalDate
}

class SystemTimeProvider : TimeProvider {
    override fun today(): LocalDate = LocalDate.now()
}
