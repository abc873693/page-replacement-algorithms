package com.rainvisitor.homework.pageReplacementAlgorithms.models

import android.graphics.Color
import java.util.*

class EnhancesSecondChance(numberOfFrames: Int) : PageReplacement(numberOfFrames) {
    override var label = "ESC"
    var referenceBits: ArrayList<Int> = ArrayList()
    var modifyBits: ArrayList<Int> = ArrayList()

    init {
        for (i in 1..numberOfFrames) {
            referenceBits.add(0)
            modifyBits.add(0)
        }
        color = Color.GREEN
    }

    override fun execute(referenceStrings: List<Page>) {
        referenceStrings.forEachIndexed { order, page ->
            //Log.e("execute", "order = ${order + 1}")
            val index = findPage(page)
            if (index == -1) {
                var find = true
                while (find) {
                    if (referenceBits[firstIndex] == 0) {
                        if (modifyBits[firstIndex] == 0) {
                            modifyBits[firstIndex] = random.nextInt(2)
                            frames[firstIndex] = page
                            referenceBits[firstIndex] = 1
                            find = false
                        } else {
                            if (frames[firstIndex].dirtyBit) writeDisk++
                            modifyBits[firstIndex] = 0
                        }
                    } else {
                        referenceBits[firstIndex] = 0
                    }
                    firstIndex++
                    if (firstIndex == frames.size) firstIndex = 0
                }
                pageFaults++
            } else referenceBits[index] = 1
            /*Log.e("execute", "$frames page = $page")
            Log.e("execute", "$referenceBits firstIndex = $firstIndex")
            Log.e("execute", "-----------------------------------")*/
        }
    }
}