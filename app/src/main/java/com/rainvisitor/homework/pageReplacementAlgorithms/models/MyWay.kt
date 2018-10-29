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
        //依序讀取Reference Strings
        referenceStrings.forEachIndexed { order, page ->
            //Log.e("execute", "order = ${order + 1}")
            //尋找page是否存在於frame中
            val index = findPage(page)
            if (index == -1) {
                //宣告紀錄是否走一圈frame用的count
                var count = 0
                //每次計算第一次出現page時 要替換的page與其他page的差 若大於frame的大小則替換該page
                var num = if (frames[firstIndex].name == "") 0 else frames[firstIndex].name.toInt()
                while (Math.abs(num - page.name.toInt()) < frames.size) {
                    //位移到下一個索引 若超過frame大小則歸零
                    firstIndex++
                    if (firstIndex == frames.size) firstIndex = 0
                    num = if (frames[firstIndex].name == "") 0 else frames[firstIndex].name.toInt()
                    count++
                    //代表循環一次 需直接替換原本的page
                    if (count == frames.size) {
                        //並將所有page寫回硬碟
                        frames.forEachIndexed { i, _ ->
                            modifyBits[i] = 0
                            writeDisk++
                        }
                        interrupt++
                        break
                    }
                }
                //將page取代並檢查Disk I/O和Interrupt
                if (modifyBits[firstIndex] == 1) {
                    writeDisk++
                    interrupt++
                }
                frames[firstIndex] = page
                modifyBits[firstIndex] = if (page.dirtyBit) 1 else 0
                pageFaults++
                //Log.e("execute", "pageFaults")
            }
            /*Log.e("execute", "$frames page = ${page.name}")
            Log.e("execute", "$modifyBits firstIndex = $firstIndex")
            Log.e("execute", "-----------------------------------")*/
        }
    }
}