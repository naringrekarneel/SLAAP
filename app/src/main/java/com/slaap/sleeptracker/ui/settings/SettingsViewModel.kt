package com.slaap.sleeptracker.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slaap.sleeptracker.repository.SleepRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SleepRepository
) : ViewModel() {

    fun deleteAllData() {
        viewModelScope.launch {
            repository.deleteAllData()
        }
    }

    fun exportData(context: android.content.Context, uri: android.net.Uri) {
        viewModelScope.launch {
            try {
                val sessions = repository.getAllSessionsSync()
                val json = com.google.gson.Gson().toJson(sessions)
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(json.toByteArray())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun importData(context: android.content.Context, uri: android.net.Uri) {
        viewModelScope.launch {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val json = inputStream.bufferedReader().use { it.readText() }
                    val type = object : com.google.gson.reflect.TypeToken<List<com.slaap.sleeptracker.data.SleepSession>>() {}.type
                    val sessions: List<com.slaap.sleeptracker.data.SleepSession> = com.google.gson.Gson().fromJson(json, type)
                    repository.insertSessions(sessions)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
