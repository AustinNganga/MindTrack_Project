package com.mindtrack.model

import org.json.JSONArray
import org.json.JSONObject

/**
 * On-device MindTrack depression-risk model (random forest exported from notebooks/Pipeline.ipynb).
 *
 * Usage:
 *   // put app/mindtrack_model.json in src/main/assets/
 *   val model = MindTrackModel.fromJson(context.assets.open("mindtrack_model.json").bufferedReader().readText())
 *   val result = model.predict(mapOf("TMD_Total" to 37.0, "Age" to 21.0, "STAI_item_21" to 2.0, ...))
 *   result.probability  // 0..1 risk that Beck total >= 11
 *   result.level        // "Low" or "Elevated"
 *
 * The input map must contain every name in [features] (see app/app_input_fields.csv):
 *  - STAI items: the code the student taps (1-4), NOT reverse-scored.
 *  - TMD_Total: sum of the 22 Test of Mobile Dependence answers (each 0-4), range 0-88.
 *  - Background fields: the codebook integer codes (e.g. Department 1=Law, 2=Economics, 3=Engineering, 4=Health).
 *
 * This is a screening estimate, not a diagnosis. Always show [disclaimer] and a link to support services.
 */
class MindTrackModel private constructor(
    val features: List<String>,
    private val trees: List<Tree>,
    val elevatedThreshold: Double,
    val disclaimer: String,
) {
    private class Tree(
        val feature: IntArray, val threshold: DoubleArray,
        val left: IntArray, val right: IntArray, val value: DoubleArray,
    ) {
        fun predict(x: DoubleArray): Double {
            var n = 0
            while (left[n] != -1) {
                n = if (x[feature[n]] <= threshold[n]) left[n] else right[n]
            }
            return value[n]
        }
    }

    data class Result(val probability: Double, val level: String)

    fun predict(inputs: Map<String, Double>): Result {
        val missing = features.filter { it !in inputs }
        require(missing.isEmpty()) { "Missing inputs: $missing" }
        val x = DoubleArray(features.size) { inputs.getValue(features[it]) }
        val p = trees.sumOf { it.predict(x) } / trees.size
        return Result(p, if (p >= elevatedThreshold) "Elevated" else "Low")
    }

    companion object {
        fun fromJson(json: String): MindTrackModel {
            val root = JSONObject(json)
            val feats = root.getJSONArray("features").let { a -> List(a.length()) { a.getString(it) } }
            val treesJson = root.getJSONArray("trees")
            val trees = List(treesJson.length()) { i ->
                val t = treesJson.getJSONObject(i)
                Tree(t.ints("feature"), t.doubles("threshold"), t.ints("left"), t.ints("right"), t.doubles("value"))
            }
            return MindTrackModel(
                feats, trees,
                root.getJSONObject("thresholds").getDouble("elevated"),
                root.optString("disclaimer"),
            )
        }

        private fun JSONObject.ints(k: String): IntArray = getJSONArray(k).let { a -> IntArray(a.length()) { a.getInt(it) } }
        private fun JSONObject.doubles(k: String): DoubleArray = getJSONArray(k).let { a -> DoubleArray(a.length()) { a.getDouble(it) } }

        /** TMD total from the 22 answers (each 0-4). */
        fun tmdTotal(answers: List<Int>): Double {
            require(answers.size == 22 && answers.all { it in 0..4 }) { "Need 22 TMD answers in 0..4" }
            return answers.sum().toDouble()
        }
    }
}

/*
 * Parity unit test (src/test/...): put app/test_vectors.json in test resources and check
 *   abs(model.predict(vector.inputs).probability - vector.expected_probability) < 1e-6
 * for every vector. This proves the phone gives the same answer as the Python notebook.
 */
