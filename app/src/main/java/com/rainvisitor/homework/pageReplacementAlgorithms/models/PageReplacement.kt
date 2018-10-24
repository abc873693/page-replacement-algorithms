package com.rainvisitor.homework.pageReplacementAlgorithms.models

import android.graphics.Color
import java.util.*

open class PageReplacement(val numberOfFrames: Int) {
    var frames: ArrayList<Page> = ArrayList()
    var pageFaults = 0
    var writeDisk = 0
    var firstIndex = 0
    open var label = ""
    var color = Color.BLUE
    var random: Random = Random()

    init {
        for (i in 1..numberOfFrames)
            frames.add(Page("", false))
    }

    open fun execute(referenceStrings: List<Page>) {}

    fun findPage(page: Page): Int {
        for (i in 0 until frames.size)
            if (page.name == frames[i].name) return i
        return -1
    }
}