package com.rainvisitor.homework.pageReplacementAlgorithms.models

import android.graphics.Color
import android.util.Log
import java.util.*

class Optimal(numberOfFrames: Int) : PageReplacement(numberOfFrames) {
    var framesNext: ArrayList<Int> = ArrayList()

    init {
        label = "Optimal"
        color = Color.GRAY
        for (i in 1..numberOfFrames)
            framesNext.add(0)
    }

    override fun execute(referenceStrings: List<String>) {
        referenceStrings.forEachIndexed { order, page ->
            if (frames.find { it == page } == null) {
                frames[firstIndex] = page
                framesNext[firstIndex] = -1
                for (i in order + 1 until referenceStrings.size) {
                    if (frames[firstIndex] == referenceStrings[i]) {
                        framesNext[firstIndex] = i
                        break
                    }
                }
                pageFaults++
                val index = frames.indexOf("")
                if (index != -1)
                    firstIndex = index
                else
                    for (i in 0 until frames.size) {
                        if (framesNext[i] == -1 && frames[i] != "") {
                            firstIndex = i
                            break
                        } else if (framesNext[i] > framesNext[firstIndex]) firstIndex = i
                    }
                Log.e("execute", "Page Faults")
            }
            Log.e("execute", "$frames page = $page")
            Log.e("execute", "$framesNext firstIndex = $firstIndex")
            Log.e("execute", "-----------------------------------")
        }
    }
}