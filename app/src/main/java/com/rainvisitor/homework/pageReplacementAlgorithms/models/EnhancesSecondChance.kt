package com.rainvisitor.homework.pageReplacementAlgorithms.models

import android.graphics.Color
import java.util.*

class EnhancesSecondChance(numberOfFrames: Int) : PageReplacement(numberOfFrames) {
    override var label = "Enhances Second Chance"
    var referenceBits: ArrayList<Int> = ArrayList()
    var modifyBits: ArrayList<Int> = ArrayList()

    init {
        for (i in 1..numberOfFrames) {
            referenceBits.add(0)
            modifyBits.add(0)
        }
        color = Color.GREEN
    }

    override fun execute(referenceStrings: List<String>) {
        referenceStrings.forEachIndexed { order, page ->
            //Log.e("execute", "order = ${order + 1}")
            val index = frames.indexOf(page)
            if (index == -1) {
                if (referenceBits[firstIndex] == 0 && modifyBits[firstIndex] == 0) {
                    if (frames[firstIndex] != "") modifyBits[firstIndex] = 1
                    frames[firstIndex] = page
                    referenceBits[firstIndex] = 1
                    firstIndex++
                    if (firstIndex == frames.size) firstIndex = 0
                } else {
                    while (referenceBits[firstIndex] == 1 && modifyBits[firstIndex] == 1) {
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
            } else referenceBits[index] = 1
            /*Log.e("execute", "$frames page = $page")
            Log.e("execute", "$referenceBits firstIndex = $firstIndex")
            Log.e("execute", "-----------------------------------")*/
        }
    }
}