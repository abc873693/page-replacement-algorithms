package com.rainvisitor.homework.pageReplacementAlgorithms.models

import android.util.Log
import java.util.*

class Timer {
    val start = Date()
    lateinit var end: Date
    val duration
        get() = Date(end.time - start.time).seconds

    init {

    }

    fun stop() {
        end = Date()
        Log.e("duration","$duration")
    }
}