package com.rainvisitor.homework.pageReplacementAlgorithms.models

class Page(var name: String, var dirtyBit: Boolean) {
    override fun toString(): String {
        return name
    }
}