package com.rainvisitor.homework.pageReplacementAlgorithms.models

import android.graphics.Color
import java.util.*

class Optimal(numberOfFrames: Int) : PageReplacement(numberOfFrames) {
    override var label = "Optimal"

    var framesNext: ArrayList<Int> = ArrayList()
    var empty = true

    init {
        label = "Optimal"
        color = Color.GRAY
        for (i in 1..numberOfFrames)
            framesNext.add(0)
    }

    override fun execute(referenceStrings: List<Page>) {
        referenceStrings.forEachIndexed { order, page ->
            //Log.e("execute", "order = $order")
            if (empty) {
                if (frames[firstIndex].dirtyBit) writeDisk++
                frames[firstIndex] = page
                framesNext[firstIndex] = findNextIndex(order, referenceStrings)
                firstIndex++
                pageFaults++
                if (firstIndex == frames.size) {
                    empty = false
                    firstIndex = 0
                    findMaxIndex(page, referenceStrings)
                }
            } else {
                val find = findPage(page)
                if (find != -1) {
                    framesNext[find] = findNextIndex(order, referenceStrings)
                } else {
                    if (frames[firstIndex].dirtyBit) writeDisk++
                    frames[firstIndex] = page
                    framesNext[firstIndex] = findNextIndex(order, referenceStrings)
                    pageFaults++
                    //Log.e("execute", "Page Faults")
                }
                findMaxIndex(page, referenceStrings)
            }
           /* Log.e("execute", "$frames page = $page")
            Log.e("execute", "$framesNext firstIndex = $firstIndex")
            Log.e("execute", "-----------------------------------")*/
        }
    }

    private fun findNextIndex(currentIndex: Int, referenceStrings: List<Page>): Int {
        for (i in currentIndex + 1 until referenceStrings.size) {
            if (frames[firstIndex].name == referenceStrings[i].name)
                return i
        }
        return referenceStrings.size
    }

    private fun findMaxIndex(page: Page, referenceStrings: List<Page>) {
        if (framesNext[firstIndex] != referenceStrings.size)
            for (i in 0 until frames.size) {
                if (framesNext[i] > framesNext[firstIndex])
                    if (frames[i].name != page.name) firstIndex = i
            }
    }
}