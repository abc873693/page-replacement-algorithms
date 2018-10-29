package com.rainvisitor.homework.pageReplacementAlgorithms.models

import android.graphics.Color

class FIFO(numberOfFrames: Int) : PageReplacement(numberOfFrames) {
    override var label = "FIFO"

    init {
        color = Color.BLUE
    }

    override fun execute(referenceStrings: List<Page>) {
        //依序讀取Reference Strings
        referenceStrings.forEach { page ->
            //尋找page是否存在於frame中
            val index = findPage(page)
            if (index == -1) {
                //若dirtyBit 等於 1 Disk I/O +1
                if (frames[firstIndex].dirtyBit) writeDisk++
                //若dirtyBit 從 1 變成 0 則發生 中斷
                if (frames[firstIndex].dirtyBit && !page.dirtyBit) interrupt++
                //將page取代
                frames[firstIndex] = page
                pageFaults++
                //位移到下一個索引 若超過frame大小則歸零
                firstIndex++
                if (firstIndex == frames.size) firstIndex = 0
            }
            //Log.e("execute", "$frames ${frames.size}")
        }
    }
}