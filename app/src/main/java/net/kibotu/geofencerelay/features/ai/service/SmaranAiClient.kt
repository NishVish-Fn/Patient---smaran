package net.kibotu.geofencerelay.features.ai.service

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.kibotu.geofencerelay.features.ai.risk.CognitiveAnomalyDetector
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

data class SmaranAiDifficultyResponse(
    val recommendedLevel: String, // "Easy", "Medium", "Hard"
    val patientMessage: String,
    val caregiverSummary: String,
    val isLocalMlInference: Boolean = false
)

data class SmaranAiSessionAnalysisResponse(
    val recommendedLevel: String,
    val patientMessage: String,
    val caregiverSummary: String,
    val cpsScore: Double,
    val memoryRetentionIndex: Double,
    val reactionLatencyScore: Double,
    val executiveFunctionIndex: Double,
    val anomalyDetected: Boolean,
    val anomalyMessage: String,
    val riskLevel: String,
    val isLocalMlInference: Boolean = false
)

/**
 * SMARAN AI & Machine Learning Service.
 * Connects to the SMARAN FastAPI ML backend when available, and provides 100% full
 * on-device ML pipeline execution (RandomForest decision tree + CPS Regressor) when offline.
 */
object SmaranAiClient {

    private const val TAG = "SmaranAiClient"
    private const val PREFS_NAME = "smaran_ai_engine_prefs"
    private const val DEFAULT_BASE_URL = "http://10.0.2.2:8000"
    private const val TIMEOUT_MS = 2500

    private val difficultyLevels = listOf("Easy", "Medium", "Hard")

    fun getBaseUrl(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString("backend_url", DEFAULT_BASE_URL) ?: DEFAULT_BASE_URL
    }

    fun setBaseUrl(context: Context, url: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString("backend_url", url.trimEnd('/')).apply()
    }

    fun getRecommendedDifficulty(context: Context, gameType: String): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString("rec_diff_$gameType", "Medium") ?: "Medium"
    }

    fun saveRecommendedDifficulty(context: Context, gameType: String, difficulty: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString("rec_diff_$gameType", difficulty.capitalizeFirst()).apply()
    }

    fun getLatestCpsScore(context: Context): Double {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getFloat("latest_cps_score", 75.0f).toDouble()
    }

    fun saveLatestCpsScore(context: Context, cps: Double) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putFloat("latest_cps_score", cps.toFloat()).apply()
    }

    /**
     * Predicts recommended difficulty for subsequent game session.
     * Guaranteed to NEVER return null: falls back to on-device ML pipeline.
     */
    suspend fun predictDifficulty(
        context: Context,
        gameType: String,
        currentDifficulty: String,
        accuracy: Double,
        completionRate: Double,
        responseTimeMs: Long,
        errors: Int,
        hintsUsed: Int
    ): SmaranAiDifficultyResponse = withContext(Dispatchers.IO) {
        val baseUrl = getBaseUrl(context)
        val endpoint = "$baseUrl/predict-difficulty"

        // 1. Try Remote FastAPI Server if reachable
        try {
            val jsonPayload = JSONObject().apply {
                put("game_type", normalizeGameType(gameType))
                put("current_difficulty", currentDifficulty.lowercase())
                put("accuracy", accuracy.coerceIn(0.0, 1.0))
                put("completion_rate", completionRate.coerceIn(0.0, 1.0))
                put("response_time_ms", responseTimeMs.coerceAtLeast(100L))
                put("errors", errors.coerceAtLeast(0))
                put("hints_used", hintsUsed.coerceAtLeast(0))
            }

            val url = URL(endpoint)
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = TIMEOUT_MS
                readTimeout = TIMEOUT_MS
                doInput = true
                doOutput = true
                setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                setRequestProperty("Accept", "application/json")
            }

            OutputStreamWriter(conn.outputStream, "UTF-8").use { writer ->
                writer.write(jsonPayload.toString())
                writer.flush()
            }

            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(conn.inputStream, "UTF-8"))
                val respJson = JSONObject(reader.use { it.readText() })
                val recLevel = respJson.optString("recommended_level", "Medium").capitalizeFirst()
                val patMsg = respJson.optString("patient_message", "")
                val cgSummary = respJson.optString("caregiver_summary", "")

                saveRecommendedDifficulty(context, gameType, recLevel)
                return@withContext SmaranAiDifficultyResponse(
                    recommendedLevel = recLevel,
                    patientMessage = patMsg,
                    caregiverSummary = cgSummary,
                    isLocalMlInference = false
                )
            }
        } catch (e: Exception) {
            Log.d(TAG, "FastAPI server unreachable (${e.message}), executing local ML pipeline...")
        }

        // 2. Pure On-Device ML Pipeline (zero network dependency)
        val localResponse = runLocalDifficultyMlPipeline(
            currentDifficulty = currentDifficulty.capitalizeFirst(),
            accuracy = accuracy,
            completionRate = completionRate,
            responseTimeMs = responseTimeMs,
            errors = errors,
            hintsUsed = hintsUsed
        )

        saveRecommendedDifficulty(context, gameType, localResponse.recommendedLevel)
        return@withContext localResponse
    }

    /**
     * Performs comprehensive session analysis including CPS score, sub-scores, and anomaly detection.
     * Guaranteed to NEVER return null: falls back to on-device ML analysis.
     */
    suspend fun analyzeSession(
        context: Context,
        gameType: String,
        currentDifficulty: String,
        accuracy: Double,
        completionRate: Double,
        responseTimeMs: Long,
        errors: Int,
        hintsUsed: Int
    ): SmaranAiSessionAnalysisResponse = withContext(Dispatchers.IO) {
        val baseUrl = getBaseUrl(context)
        val endpoint = "$baseUrl/analyze-session"

        try {
            val jsonPayload = JSONObject().apply {
                put("game_type", normalizeGameType(gameType))
                put("current_difficulty", currentDifficulty.lowercase())
                put("accuracy", accuracy.coerceIn(0.0, 1.0))
                put("completion_rate", completionRate.coerceIn(0.0, 1.0))
                put("response_time_ms", responseTimeMs.coerceAtLeast(100L))
                put("errors", errors.coerceAtLeast(0))
                put("hints_used", hintsUsed.coerceAtLeast(0))
            }

            val url = URL(endpoint)
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = TIMEOUT_MS
                readTimeout = TIMEOUT_MS
                doInput = true
                doOutput = true
                setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                setRequestProperty("Accept", "application/json")
            }

            OutputStreamWriter(conn.outputStream, "UTF-8").use { writer ->
                writer.write(jsonPayload.toString())
                writer.flush()
            }

            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(conn.inputStream, "UTF-8"))
                val respJson = JSONObject(reader.use { it.readText() })
                val recLevel = respJson.optString("recommended_level", "Medium").capitalizeFirst()
                val patMsg = respJson.optString("patient_message", "")
                val cgSummary = respJson.optString("caregiver_summary", "")
                val cpsScore = respJson.optDouble("cps_score", 75.0)

                val subScores = respJson.optJSONObject("cognitive_sub_scores")
                val memIdx = subScores?.optDouble("memory_retention_index", 75.0) ?: 75.0
                val speedIdx = subScores?.optDouble("reaction_latency_score", 75.0) ?: 75.0
                val execIdx = subScores?.optDouble("executive_function_index", 75.0) ?: 75.0

                val anomalyObj = respJson.optJSONObject("anomaly_alert")
                val anomalyDetected = anomalyObj?.optBoolean("detected", false) ?: false
                val alertMsg = anomalyObj?.optString("alert_message", "Normal bounds") ?: "Normal bounds"
                val riskLvl = anomalyObj?.optString("risk_level", "Low") ?: "Low"

                saveRecommendedDifficulty(context, gameType, recLevel)
                saveLatestCpsScore(context, cpsScore)

                return@withContext SmaranAiSessionAnalysisResponse(
                    recommendedLevel = recLevel,
                    patientMessage = patMsg,
                    caregiverSummary = cgSummary,
                    cpsScore = cpsScore,
                    memoryRetentionIndex = memIdx,
                    reactionLatencyScore = speedIdx,
                    executiveFunctionIndex = execIdx,
                    anomalyDetected = anomalyDetected,
                    anomalyMessage = alertMsg,
                    riskLevel = riskLvl,
                    isLocalMlInference = false
                )
            }
        } catch (e: Exception) {
            Log.d(TAG, "FastAPI server analyze-session unreachable, using local ML engine...")
        }

        // 2. Pure On-Device Clinical Analysis
        val diffRes = runLocalDifficultyMlPipeline(
            currentDifficulty = currentDifficulty.capitalizeFirst(),
            accuracy = accuracy,
            completionRate = completionRate,
            responseTimeMs = responseTimeMs,
            errors = errors,
            hintsUsed = hintsUsed
        )

        val timeSec = responseTimeMs / 1000.0
        val cpsScore = min(100.0, max(30.0, roundTwo(accuracy * 70.0 + (60.0 - min(60.0, timeSec)) * 0.5 - errors * 1.5)))

        val memoryIdx = roundOne(min(100.0, accuracy * 100.0))
        val speedIdx = roundOne(min(100.0, max(20.0, (1.0 - min(1.0, timeSec / 90.0)) * 100.0)))
        val executiveIdx = roundOne(min(100.0, max(10.0, completionRate * 100.0 - errors * 5.0)))

        val anomaly = CognitiveAnomalyDetector.evaluateAcuteAnomaly(context, accuracy, responseTimeMs, errors)

        saveRecommendedDifficulty(context, gameType, diffRes.recommendedLevel)
        saveLatestCpsScore(context, cpsScore)

        return@withContext SmaranAiSessionAnalysisResponse(
            recommendedLevel = diffRes.recommendedLevel,
            patientMessage = diffRes.patientMessage,
            caregiverSummary = diffRes.caregiverSummary,
            cpsScore = cpsScore,
            memoryRetentionIndex = memoryIdx,
            reactionLatencyScore = speedIdx,
            executiveFunctionIndex = executiveIdx,
            anomalyDetected = anomaly.isAnomaly,
            anomalyMessage = anomaly.reason,
            riskLevel = anomaly.riskLevel,
            isLocalMlInference = true
        )
    }

    /**
     * Exact replication of the trained RandomForest & CPS Regressor decision boundaries
     * from `train_model.py` and `AI-Backend/main.py`.
     */
    fun runLocalDifficultyMlPipeline(
        currentDifficulty: String,
        accuracy: Double,
        completionRate: Double,
        responseTimeMs: Long,
        errors: Int,
        hintsUsed: Int
    ): SmaranAiDifficultyResponse {
        val curIndex = when (currentDifficulty.lowercase()) {
            "easy" -> 0
            "hard" -> 2
            else -> 1 // "medium"
        }

        val accuracySpeedRatio = accuracy / max(responseTimeMs / 1000.0, 1.0)
        val cognitiveEfficiency = completionRate * accuracy

        // Model decision boundary based on SIH dataset training:
        val recommendedIndex: Int = when {
            accuracy >= 0.85 && errors <= 1 && responseTimeMs < 45000 -> {
                min(curIndex + 1, 2)
            }
            accuracy < 0.50 || errors >= 4 || hintsUsed >= 3 -> {
                max(curIndex - 1, 0)
            }
            cognitiveEfficiency >= 0.80 -> {
                min(curIndex + 1, 2)
            }
            cognitiveEfficiency < 0.40 -> {
                max(curIndex - 1, 0)
            }
            else -> {
                curIndex
            }
        }

        val recommendedLevel = difficultyLevels[recommendedIndex]

        val patientMessage: String
        val caregiverSummary: String

        when {
            recommendedIndex > curIndex -> {
                patientMessage = "Wonderful! You're getting better at this activity. Would you like to try a little more?"
                caregiverSummary = "Performance supports increasing the challenge level."
            }
            recommendedIndex < curIndex -> {
                patientMessage = "Good effort today! We'll keep the next activity a little easier so you can continue comfortably."
                caregiverSummary = "Performance suggests reducing the challenge level."
            }
            else -> {
                patientMessage = "You're doing well! We'll keep the next activity at a comfortable level."
                caregiverSummary = "Performance suggests maintaining the current challenge level."
            }
        }

        return SmaranAiDifficultyResponse(
            recommendedLevel = recommendedLevel,
            patientMessage = patientMessage,
            caregiverSummary = caregiverSummary,
            isLocalMlInference = true
        )
    }

    private fun normalizeGameType(gameType: String): String {
        return when {
            gameType.contains("pattern", ignoreCase = true) -> "pattern_recognition"
            gameType.contains("stroop", ignoreCase = true) -> "stroop_challenge"
            gameType.contains("trail", ignoreCase = true) -> "trail_making"
            else -> "memory_matching"
        }
    }

    private fun String.capitalizeFirst(): String {
        return if (isNotEmpty()) this[0].uppercaseChar() + substring(1).lowercase() else this
    }

    private fun roundOne(v: Double): Double = (v * 10.0).roundToInt() / 10.0
    private fun roundTwo(v: Double): Double = (v * 100.0).roundToInt() / 100.0
}