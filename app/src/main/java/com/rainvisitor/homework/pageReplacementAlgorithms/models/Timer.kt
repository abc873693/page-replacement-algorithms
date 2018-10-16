package com.rainvisitor.homework.pageReplacementAlgorithms.models

import android.util.Log
import java.util.*

class Timer(private val TAG: String) {
    private val start = Date()
    private lateinit var end: Date
    val duration
        get() = Date(end.time - start.time).seconds

    init {

    }

    fun stop() {
        end = Date()
        Log.e(TAG, "duration = $duration seconds")
    }
}