package com.rainvisitor.homework.pageReplacementAlgorithms.models

import android.graphics.Color
import android.util.Log
import java.util.*

@Deprecated("Fuck")
class SecondChance(numberOfFrames: Int) : PageReplacement(numberOfFrames) {
    override var label = "Second Chance"
    var referenceBits: ArrayList<Int> = ArrayList()

    init {
        for (i in 1..numberOfFrames)
            referenceBits.add(0)
        label = "Second Chance"
        color = Color.GREEN
    }

    override fun execute(referenceStrings: List<Page>) {
        referenceStrings.forEachIndexed { order, page ->
            Log.e("execute", "order = ${order + 1}")
            /*val find = referenceBits.indexOf(0)
            if (find == -1) {
                referenceBits.forEachIndexed { i, _ ->
                    referenceBits[i] = 0
                }
            }*/
            val index = frames.indexOf(page)
            if (index == -1) {
                if (referenceBits[firstIndex] == 0) {
                    frames[firstIndex] = page
                    referenceBits[firstIndex] = 1
                    firstIndex++
                    if (firstIndex == frames.size) firstIndex = 0
                } else {
                    while (referenceBits[firstIndex] == 1) {
                        referenceBits[firstIndex] = 0
                        firstIndex++
                        if (firstIndex == frames.size) firstIndex = 0
                        if (referenceBits[firstIndex] == 0) {
                            frames[firstIndex] = page
                            referenceBits[firstIndex] = 1
                            firstIndex++
                            if (firstIndex == frames.size) firstIndex = 0
                            break
                        }
                    }
                }
                pageFaults++
                Log.e("execute", "Page Faults")
            } else referenceBits[index] = 1
            Log.e("execute", "$frames page = $page")
            Log.e("execute", "$referenceBits firstIndex = $firstIndex")
            Log.e("execute", "-----------------------------------")
        }
    }
}