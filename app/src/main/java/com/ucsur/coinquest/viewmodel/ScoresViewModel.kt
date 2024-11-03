package com.ucsur.coinquest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucsur.coinquest.data.ScoreRepository
import com.ucsur.coinquest.model.Score
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ScoresViewModel(
    private val repository: ScoreRepository
) : ViewModel() {
    // Separamos los flujos de datos por nivel
    private val _level1Scores = MutableStateFlow<List<Score>>(emptyList())
    private val _level2Scores = MutableStateFlow<List<Score>>(emptyList())

    val level1Scores = _level1Scores.asStateFlow()
    val level2Scores = _level2Scores.asStateFlow()

    // Estado para la pestaña seleccionada
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab = _selectedTab.asStateFlow()

    init {
        loadScoresByLevel()
    }

    private fun loadScoresByLevel() {
        viewModelScope.launch {
            repository.getTopScores().collect { allScores ->
                // Filtrar y ordenar scores por nivel
                _level1Scores.value = allScores
                    .filter { it.level == 1 }
                    .sortedByDescending { it.score }
                    .take(5) // Top 5

                _level2Scores.value = allScores
                    .filter { it.level == 2 }
                    .sortedByDescending { it.score }
                    .take(5) // Top 5
            }
        }
    }

    fun onTabSelected(index: Int) {
        _selectedTab.value = index
    }
}