package com.rainvisitor.homework.pageReplacementAlgorithms.models

import android.graphics.Color
import java.util.*

class SecondChance(numberOfFrames: Int) : PageReplacement(numberOfFrames) {
    var referenceBits: ArrayList<Int> = ArrayList()

    init {
        for (i in 1..numberOfFrames)
            referenceBits.add(0)
        label = "Second Chance"
        color = Color.GREEN
    }

    override fun execute(referenceStrings: List<String>) {
        referenceStrings.forEachIndexed { order, page ->
            //Log.e("execute", "order = ${order + 1}")
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
                    var count = 0
                    while (referenceBits[firstIndex] == 1) {
                        if (count == frames.size) {
                            for (i in 0 until referenceBits.size)
                                referenceBits[i] = 0
                            frames[firstIndex] = page
                            referenceBits[firstIndex] = 1
                            firstIndex++
                            if (firstIndex == frames.size) firstIndex = 0
                            break
                        }
                        firstIndex++
                        if (firstIndex == frames.size) firstIndex = 0
                        if (referenceBits[firstIndex] == 0) {
                            frames[firstIndex] = page
                            referenceBits[firstIndex] = 1
                            firstIndex++
                            if (firstIndex == frames.size) firstIndex = 0
                            break
                        }
                        else count++
                    }
                }
                pageFaults++
            } else referenceBits[index] = 1
            /*Log.e("execute", "$frames page = $page")
            Log.e("execute", "$referenceBits firstIndex = $firstIndex")
            Log.e("execute", "-----------------------------------")*/
        }
    }
}