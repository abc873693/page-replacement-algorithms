package com.rainvisitor.homework.pageReplacementAlgorithms.models

import android.graphics.Color

class FIFO(numberOfFrames: Int) : PageReplacement(numberOfFrames) {
    override var label = "FIFO"

    init {
        color = Color.BLUE
    }

    override fun execute(referenceStrings: List<Page>) {
        referenceStrings.forEach { page ->
            val index = findPage(page)
            if (index == -1) {
                if (frames[firstIndex].dirtyBit) writeDisk++
                frames[firstIndex] = page
                pageFaults++
                firstIndex++
                if (firstIndex == frames.size) firstIndex = 0
            }
            //Log.e("execute", "$frames ${frames.size}")
        }
    }
}