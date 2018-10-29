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
        //依序讀取Reference Strings
        referenceStrings.forEachIndexed { order, page ->
            //Log.e("execute", "order = ${order + 1}")
            //尋找page是否存在於frame中
            val index = findPage(page)
            if (index == -1) {
                //建立是否有找到的變數 找到時跳出
                var find = false
                while (!find) {
                    //判斷Reference Bits是否為1 是的話則給與該page一次機會 否則繼續判斷
                    if (referenceBits[firstIndex] == 0) {
                        //判斷Modify Bits是否為0 是代表為最佳替換頁 將其替換 並檢查Interrupt
                        if (modifyBits[firstIndex] == 0) {
                            modifyBits[firstIndex] = if (frames[firstIndex].dirtyBit) 1 else 0
                            if (frames[firstIndex].dirtyBit && !page.dirtyBit) interrupt++
                            frames[firstIndex] = page
                            referenceBits[firstIndex] = 1
                            find = true
                        } else if (random.nextBoolean()) {
                            //隨機將ModifyBits設為0並將 資料寫回Disk
                            if (frames[firstIndex].dirtyBit) writeDisk++
                            modifyBits[firstIndex] = 0
                        }
                    } else {
                        //給定一次機會後 就將Reference Bits設定為 0
                        referenceBits[firstIndex] = 0
                    }
                    firstIndex++
                    if (firstIndex == frames.size) firstIndex = 0
                }
                pageFaults++
            }
            //若有找到則給予該page一次機會
            else referenceBits[index] = 1
            /*Log.e("execute", "$frames page = $page")
            Log.e("execute", "$referenceBits firstIndex = $firstIndex")
            Log.e("execute", "-----------------------------------")*/
        }
    }
}