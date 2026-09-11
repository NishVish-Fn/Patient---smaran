package net.kibotu.geofencerelay.features.ai.history

import android.content.Context
import net.kibotu.geofencerelay.features.ai.model.CpsAssessmentResult
import net.kibotu.geofencerelay.features.ai.model.SubDomainScores
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class DailyScorecardItem(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val dateFormatted: String,
    val dayKey: String,
    val gameType: String,
    val difficulty: String, // Selected by AI
    val cpsScore: Double,
    val accuracy: Double,
    val durationMs: Long,
    val attempts: Int,
    val errors: Int,
    val memoryRetention: Double,
    val reactionLatency: Double,
    val executiveFunction: Double,
    val patientMessage: String,
    val caregiverSummary: String,
    val anomalyDetected: Boolean
)

/**
 * Lifetime Cognitive History & Daily Scorecard Manager.
 * Persistently stores all cognitive assessments, multi-domain telemetry, and daily scorecards
 * in SharedPreferences for the entire lifetime of the installation.
 */
object CognitiveHistoryManager {

    private const val PREFS_NAME = "smaran_lifetime_cognitive_prefs"
    private const val KEY_ASSESSMENT = "key_lifetime_assessment_json"
    private const val KEY_SCORECARDS = "key_lifetime_scorecards_json"

    fun getLatestAssessment(context: Context): CpsAssessmentResult? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonStr = prefs.getString(KEY_ASSESSMENT, null) ?: return null
        return try {
            deserializeAssessment(JSONObject(jsonStr))
        } catch (e: Exception) {
            null
        }
    }

    fun saveAssessment(context: Context, assessment: CpsAssessmentResult) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        try {
            val json = serializeAssessment(assessment)
            prefs.edit().putString(KEY_ASSESSMENT, json.toString()).apply()
        } catch (_: Exception) {}
    }

    fun recordScorecard(context: Context, scorecard: DailyScorecardItem) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val rawJson = prefs.getString(KEY_SCORECARDS, "[]") ?: "[]"
        try {
            val array = JSONArray(rawJson)
            val itemObj = JSONObject().apply {
                put("id", scorecard.id)
                put("timestamp", scorecard.timestamp)
                put("dateFormatted", scorecard.dateFormatted)
                put("dayKey", scorecard.dayKey)
                put("gameType", scorecard.gameType)
                put("difficulty", scorecard.difficulty)
                put("cpsScore", scorecard.cpsScore)
                put("accuracy", scorecard.accuracy)
                put("durationMs", scorecard.durationMs)
                put("attempts", scorecard.attempts)
                put("errors", scorecard.errors)
                put("memoryRetention", scorecard.memoryRetention)
                put("reactionLatency", scorecard.reactionLatency)
                put("executiveFunction", scorecard.executiveFunction)
                put("patientMessage", scorecard.patientMessage)
                put("caregiverSummary", scorecard.caregiverSummary)
                put("anomalyDetected", scorecard.anomalyDetected)
            }
            // Prepend newest first
            val newArray = JSONArray()
            newArray.put(itemObj)
            for (i in 0 until array.length()) {
                newArray.put(array.get(i))
            }
            prefs.edit().putString(KEY_SCORECARDS, newArray.toString()).apply()
        } catch (_: Exception) {}
    }

    fun getAllScorecards(context: Context): List<DailyScorecardItem> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val rawJson = prefs.getString(KEY_SCORECARDS, "[]") ?: "[]"
        val list = mutableListOf<DailyScorecardItem>()
        try {
            val array = JSONArray(rawJson)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    DailyScorecardItem(
                        id = obj.optString("id", UUID.randomUUID().toString()),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                        dateFormatted = obj.optString("dateFormatted", "Today"),
                        dayKey = obj.optString("dayKey", ""),
                        gameType = obj.optString("gameType", "Cognitive Game"),
                        difficulty = obj.optString("difficulty", "Medium"),
                        cpsScore = obj.optDouble("cpsScore", 75.0),
                        accuracy = obj.optDouble("accuracy", 0.8),
                        durationMs = obj.optLong("durationMs", 30000L),
                        attempts = obj.optInt("attempts", 10),
                        errors = obj.optInt("errors", 0),
                        memoryRetention = obj.optDouble("memoryRetention", 75.0),
                        reactionLatency = obj.optDouble("reactionLatency", 75.0),
                        executiveFunction = obj.optDouble("executiveFunction", 75.0),
                        patientMessage = obj.optString("patientMessage", ""),
                        caregiverSummary = obj.optString("caregiverSummary", ""),
                        anomalyDetected = obj.optBoolean("anomalyDetected", false)
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    private fun serializeAssessment(a: CpsAssessmentResult): JSONObject {
        return JSONObject().apply {
            put("cpsScore", a.cpsScore)
            put("functionalCognitiveAge", a.functionalCognitiveAge)
            put("biologicalAge", a.biologicalAge)
            put("motorJitterIndex", a.motorJitterIndex)
            put("motorDiagnostic", a.motorDiagnostic)
            put("speechHesitationScore", a.speechHesitationScore)
            put("speechDiagnostic", a.speechDiagnostic)
            put("hiddenDifficulty", a.hiddenDifficulty)
            put("fatigueIndex", a.fatigueIndex)
            put("avgReactionPerAttemptMs", a.avgReactionPerAttemptMs)
            put("circadianRisk", a.circadianRisk)
            put("optimalExerciseWindow", a.optimalExerciseWindow)
            put("projectedCps30Days", a.projectedCps30Days)
            put("projectedCps90Days", a.projectedCps90Days)
            put("trajectoryStatus", a.trajectoryStatus)
            put("caregiverReminiscencePlan", a.caregiverReminiscencePlan)
            put("encouragementPrompt", a.encouragementPrompt)
            put("subScores", JSONObject().apply {
                put("autobiographicalReminiscence", a.subScores.autobiographicalReminiscence)
                put("memoryRetentionIndex", a.subScores.memoryRetentionIndex)
                put("reactionLatencyScore", a.subScores.reactionLatencyScore)
                put("executiveFunctionIndex", a.subScores.executiveFunctionIndex)
                put("errorRecoveryRate", a.subScores.errorRecoveryRate)
            })
        }
    }

    private fun deserializeAssessment(obj: JSONObject): CpsAssessmentResult {
        val subObj = obj.optJSONObject("subScores") ?: JSONObject()
        val subScores = SubDomainScores(
            autobiographicalReminiscence = subObj.optDouble("autobiographicalReminiscence", 75.0),
            memoryRetentionIndex = subObj.optDouble("memoryRetentionIndex", 75.0),
            reactionLatencyScore = subObj.optDouble("reactionLatencyScore", 75.0),
            executiveFunctionIndex = subObj.optDouble("executiveFunctionIndex", 75.0),
            errorRecoveryRate = subObj.optDouble("errorRecoveryRate", 75.0)
        )
        return CpsAssessmentResult(
            cpsScore = obj.optDouble("cpsScore", 75.0),
            functionalCognitiveAge = 0.0,
            biologicalAge = 0,
            subScores = subScores,
            motorJitterIndex = obj.optDouble("motorJitterIndex", 25.0),
            motorDiagnostic = obj.optString("motorDiagnostic", "Normal Motor Control"),
            speechHesitationScore = obj.optDouble("speechHesitationScore", 20.0),
            speechDiagnostic = obj.optString("speechDiagnostic", "Fluent Speech"),
            hiddenDifficulty = obj.optString("hiddenDifficulty", "medium"),
            fatigueIndex = obj.optDouble("fatigueIndex", 0.15),
            avgReactionPerAttemptMs = obj.optDouble("avgReactionPerAttemptMs", 2200.0),
            circadianRisk = obj.optString("circadianRisk", "Low"),
            optimalExerciseWindow = obj.optString("optimalExerciseWindow", "Morning (08:00 - 12:00)"),
            projectedCps30Days = obj.optDouble("projectedCps30Days", 80.0),
            projectedCps90Days = obj.optDouble("projectedCps90Days", 85.0),
            trajectoryStatus = obj.optString("trajectoryStatus", "Stable Memory Retention"),
            caregiverReminiscencePlan = obj.optString("caregiverReminiscencePlan", "Continue daily exercises."),
            encouragementPrompt = obj.optString("encouragementPrompt", "Great effort!")
        )
    }
}