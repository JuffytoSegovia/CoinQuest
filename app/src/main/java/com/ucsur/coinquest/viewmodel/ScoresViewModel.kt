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
    private val _scores = MutableStateFlow<List<Score>>(emptyList())
    val scores = _scores.asStateFlow()

    init {
        loadTopScores()
    }

    private fun loadTopScores() {
        viewModelScope.launch {
            repository.getTopScores().collect { scores ->
                _scores.value = scores
            }
        }
    }

    fun saveScore(score: Score) {
        viewModelScope.launch {
            repository.saveScore(score)
        }
    }
}