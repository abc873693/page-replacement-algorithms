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
        //建立是否frame還有空的判斷變數
        var isEmpty = true
        //依序讀取Reference Strings
        referenceStrings.forEachIndexed { order, page ->
            //Log.e("execute", "order = $order")
            if (isEmpty) {
                //若還有空的時 則照FIFO依序取代 並找到其page下一次出現的index
                if (frames[firstIndex].dirtyBit) writeDisk++
                frames[firstIndex] = page
                framesNext[firstIndex] = findNextIndex(order, page, referenceStrings)
                firstIndex++
                pageFaults++
                //若firstIndex等於frame的大小代表全部都已填滿則isEmpty設為false
                if (firstIndex == frames.size) {
                    isEmpty = false
                    firstIndex = 0
                }
            } else {
                //尋找page是否存在於frame中
                val find = findPage(page)
                //每次尋找下一次的位址代表有發生interrupt
                interrupt++
                if (find != -1) {
                    framesNext[find] = findNextIndex(order, page, referenceStrings)
                } else {
                    //找到最晚會出現的page的索引
                    findMaxIndex(referenceStrings)
                    //將page取代並檢查Disk I/O
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

    //找到page下一次出現的index
    private fun findNextIndex(currentIndex: Int, page: Page, referenceStrings: List<Page>): Int {
        for (i in currentIndex + 1 until referenceStrings.size) {
            if (page.name == referenceStrings[i].name)
                return i
        }
        return referenceStrings.size
    }

    //找到frame中最晚出現page給定 下次要替換的index
    private fun findMaxIndex(referenceStrings: List<Page>) {
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