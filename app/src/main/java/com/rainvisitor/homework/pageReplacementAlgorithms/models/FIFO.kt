package com.rainvisitor.homework.pageReplacementAlgorithms.models

import android.graphics.Color

class FIFO(numberOfFrames: Int) : PageReplacement(numberOfFrames) {
    init {
        label = "FIFO"
        color = Color.BLUE
    }

    override fun execute(referenceStrings: List<String>) {
        referenceStrings.forEach { page ->
            if (frames.find { it == page } == null) {
                frames[firstIndex] = page
                pageFaults++
                firstIndex++
                if (firstIndex == frames.size) firstIndex = 0
            }
            //Log.e("execute", "$frames ${frames.size}")
        }
    }
}