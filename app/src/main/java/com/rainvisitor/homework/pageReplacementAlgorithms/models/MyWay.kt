package com.rainvisitor.homework.pageReplacementAlgorithms.models

import android.graphics.Color
import java.util.*

class MyWay(numberOfFrames: Int) : PageReplacement(numberOfFrames) {
    override var label = "My way"
    var referenceBits: ArrayList<Int> = ArrayList()
    var modifyBits: ArrayList<Int> = ArrayList()

    init {
        for (i in 1..numberOfFrames) {
            referenceBits.add(0)
            modifyBits.add(0)
        }
        color = Color.RED
    }

    override fun execute(referenceStrings: List<Page>) {
        referenceStrings.forEachIndexed { order, page ->
            //Log.e("execute", "order = ${order + 1}")
            val index = findPage(page)
            if (index == -1) {
                var count = 0
                var num = if (frames[firstIndex].name == "") 0 else frames[firstIndex].name.toInt()
                while (Math.abs(num - page.name.toInt()) < frames.size) {
                    firstIndex++
                    if (firstIndex == frames.size) firstIndex = 0
                    num = if (frames[firstIndex].name == "") 0 else frames[firstIndex].name.toInt()
                    count++
                    if (count == frames.size) {
                        frames.forEachIndexed { i, _ ->
                            modifyBits[i] = 0
                            writeDisk++
                        }
                        interrupt++
                        break
                    }
                }
                if (modifyBits[firstIndex] == 1) {
                    writeDisk++
                    interrupt++
                }
                frames[firstIndex] = page
                modifyBits[firstIndex] = if (page.dirtyBit) 1 else 0
                pageFaults++
                //Log.e("execute", "pageFaults")
            } else referenceBits[index] = 0
            /*Log.e("execute", "$frames page = ${page.name}")
            Log.e("execute", "$modifyBits firstIndex = $firstIndex")
            Log.e("execute", "-----------------------------------")*/
        }
    }
}