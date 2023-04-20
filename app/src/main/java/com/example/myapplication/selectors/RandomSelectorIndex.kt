package com.example.myapplication.selectors

class RandomSelectorIndex : SelectorIndex {
    override fun selectIndex(list: List<Int>, position: Int) =
        (100 * Math.random()).toInt() % list.size

}