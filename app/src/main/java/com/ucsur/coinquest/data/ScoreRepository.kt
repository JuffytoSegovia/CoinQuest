package com.ucsur.coinquest.data

import android.util.Log
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ucsur.coinquest.model.Score
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class ScoreRepository {
    private val firestore = Firebase.firestore
    private val scoresCollection = firestore.collection("scores")

    fun getTopScores(): Flow<List<Score>> = flow {
        try {
            val snapshot = scoresCollection
                .orderBy("score", Query.Direction.DESCENDING)
                .get()
                .await()

            val scores = snapshot.toObjects(Score::class.java)
                .groupBy { it.level }
                .mapValues { (_, levelScores) ->
                    levelScores
                        .sortedWith(
                            compareByDescending<Score> { it.score }
                                .thenBy { it.timeElapsed }
                        )
                        .take(5)
                }
                .values
                .flatten()

            emit(scores)
        } catch (e: Exception) {
            Log.e("ScoreRepository", "Error obteniendo scores", e)
            emit(emptyList())
        }
    }

    suspend fun saveScore(score: Score) {
        try {
            Log.d("ScoreRepository", "Intentando guardar score: $score")
            scoresCollection.add(score)
                .addOnSuccessListener { documentReference ->
                    Log.d("ScoreRepository", "Score guardado con ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.e("ScoreRepository", "Error al guardar score", e)
                }
                .await()
        } catch (e: Exception) {
            Log.e("ScoreRepository", "Error en saveScore", e)
        }
    }
}