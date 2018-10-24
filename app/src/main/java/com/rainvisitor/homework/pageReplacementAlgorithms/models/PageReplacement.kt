package com.rainvisitor.homework.pageReplacementAlgorithms.models

import android.graphics.Color
import java.util.*

open class PageReplacement(val numberOfFrames: Int) {
    var frames: ArrayList<String> = ArrayList()
    var pageFaults = 0
    var writeDisk = 0
    var firstIndex = 0
    open var label = ""
    var color = Color.BLUE
    var random: Random = Random()

    init {
        for (i in 1..numberOfFrames)
            frames.add("")
    }

    open fun execute(referenceStrings: List<String>) {}
}