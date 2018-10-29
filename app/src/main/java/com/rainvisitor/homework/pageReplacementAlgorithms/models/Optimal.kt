package com.rainvisitor.homework.pageReplacementAlgorithms.models

import android.graphics.Color
import java.util.*

class Optimal(numberOfFrames: Int) : PageReplacement(numberOfFrames) {
    override var label = "Optimal"

    var framesNext: ArrayList<Int> = ArrayList()

    init {
        label = "Optimal"
        color = Color.GRAY
        for (i in 1..numberOfFrames)
            framesNext.add(0)
    }

    override fun execute(referenceStrings: List<Page>) {
        var empty = true
        referenceStrings.forEachIndexed { order, page ->
            //Log.e("execute", "order = $order")
            if (empty) {
                if (frames[firstIndex].dirtyBit) writeDisk++
                frames[firstIndex] = page
                framesNext[firstIndex] = findNextIndex(order, page, referenceStrings)
                firstIndex++
                //pageFaults++
                if (firstIndex == frames.size) {
                    empty = false
                    firstIndex = 0
                }
            } else {
                val find = findPage(page)
                interrupt++
                if (find != -1) {
                    framesNext[find] = findNextIndex(order, page, referenceStrings)
                } else {
                    findMaxIndex(page, referenceStrings)
                    if (frames[firstIndex].dirtyBit) writeDisk++
                    frames[firstIndex] = page
                    framesNext[firstIndex] = findNextIndex(order, page, referenceStrings)
                    pageFaults++
                    //Log.e("execute", "Page Faults")
                }
            }
            /* Log.e("execute", "$frames page = $page")
             Log.e("execute", "$framesNext firstIndex = $firstIndex")
             Log.e("execute", "-----------------------------------")*/
        }
    }

    private fun findNextIndex(currentIndex: Int, page: Page, referenceStrings: List<Page>): Int {
        for (i in currentIndex + 1 until referenceStrings.size) {
            if (page.name == referenceStrings[i].name)
                return i
        }
        return referenceStrings.size
    }

    private fun findMaxIndex(page: Page, referenceStrings: List<Page>) {
        if (framesNext[firstIndex] != referenceStrings.size)
            for (i in 0 until frames.size) {
                if (framesNext[i] > framesNext[firstIndex])
                    firstIndex = i
                if (framesNext[i] == frames.size) {
                    return
                }
            }
    }
}