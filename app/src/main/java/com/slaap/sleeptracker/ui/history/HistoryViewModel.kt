package com.slaap.sleeptracker.ui.history

import androidx.lifecycle.ViewModel
import com.slaap.sleeptracker.repository.SleepRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: SleepRepository
) : ViewModel() {

    val allSessions = repository.allCompletedSessions
}
