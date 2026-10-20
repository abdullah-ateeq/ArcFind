package com.example.arcfind.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.arcfind.model.ArcFindHistory
import com.example.arcfind.model.LengthUnit
import com.example.arcfind.model.SolveType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class HistoryRepository(private val prefs: SharedPreferences) {

    constructor(context: Context) : this(
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    )

    private val _historyFlow = MutableStateFlow<List<ArcFindHistory>>(emptyList())
    val historyFlow: StateFlow<List<ArcFindHistory>> = _historyFlow.asStateFlow()

    init {
        _historyFlow.value = loadHistory()
    }

    fun getHistory(): List<ArcFindHistory> = _historyFlow.value

    /**
     * Saves a calculation.
     * Prevents consecutive identical saves.
     * Keeps maximum 10 entries (removes oldest when 11th is added).
     * Returns true if saved, false if consecutive duplicate.
     */
    fun saveCalculation(entry: ArcFindHistory): Boolean {
        val current = _historyFlow.value.toMutableList()

        if (current.isNotEmpty()) {
            val latest = current.first()
            if (isConsecutiveDuplicate(latest, entry)) {
                return false
            }
        }

        // Insert newest at the beginning
        current.add(0, entry)

        // Maintain maximum 10 entries
        while (current.size > MAX_HISTORY_ITEMS) {
            current.removeAt(current.lastIndex)
        }

        saveToPrefs(current)
        _historyFlow.value = current
        return true
    }

    fun deleteEntry(id: String) {
        val current = _historyFlow.value.toMutableList()
        val removed = current.removeAll { it.id == id }
        if (removed) {
            saveToPrefs(current)
            _historyFlow.value = current
        }
    }

    fun clearAll() {
        saveToPrefs(emptyList())
        _historyFlow.value = emptyList()
    }

    private fun isConsecutiveDuplicate(a: ArcFindHistory, b: ArcFindHistory): Boolean {
        return a.solveType == b.solveType &&
                a.radius == b.radius &&
                a.radiusUnit == b.radiusUnit &&
                a.angle == b.angle &&
                a.arcLength == b.arcLength &&
                a.arcLengthUnit == b.arcLengthUnit &&
                a.resultUnit == b.resultUnit
    }

    private fun saveToPrefs(items: List<ArcFindHistory>) {
        val array = JSONArray()
        for (item in items) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("solveType", item.solveType.name)
                put("radius", item.radius)
                put("radiusUnit", item.radiusUnit.name)
                put("angle", item.angle)
                put("arcLength", item.arcLength)
                put("arcLengthUnit", item.arcLengthUnit.name)
                put("resultUnit", item.resultUnit.name)
                put("resultValue", item.resultValue)
                put("savedAt", item.savedAt)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_HISTORY, array.toString()).apply()
    }

    private fun loadHistory(): List<ArcFindHistory> {
        val json = prefs.getString(KEY_HISTORY, null) ?: return emptyList()
        val list = mutableListOf<ArcFindHistory>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val item = ArcFindHistory(
                    id = obj.getString("id"),
                    solveType = SolveType.valueOf(obj.getString("solveType")),
                    radius = obj.getString("radius"),
                    radiusUnit = LengthUnit.valueOf(obj.getString("radiusUnit")),
                    angle = obj.getString("angle"),
                    arcLength = obj.getString("arcLength"),
                    arcLengthUnit = LengthUnit.valueOf(obj.getString("arcLengthUnit")),
                    resultUnit = LengthUnit.valueOf(obj.getString("resultUnit")),
                    resultValue = obj.getString("resultValue"),
                    savedAt = obj.getLong("savedAt")
                )
                list.add(item)
            }
        } catch (e: Exception) {
            // Return what could be parsed or empty
        }
        return list
    }

    companion object {
        private const val PREFS_NAME = "arcfind_history"
        private const val KEY_HISTORY = "key_history_items"
        const val MAX_HISTORY_ITEMS = 10
    }
}
