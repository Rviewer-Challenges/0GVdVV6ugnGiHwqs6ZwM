package com.example.myapplication

import com.example.myapplication.events.MatchCardsListener
import com.example.myapplication.events.QueueTapedListener
import com.example.myapplication.events.QueueUnMatchListener
import com.example.myapplication.models.Box
import java.util.*


class QueueAnalyzer {

    private val queue: Queue<Box> = ArrayDeque()

    private var pair: MutableList<Box> = mutableListOf()

    fun add(box: Box) = queue.add(box)

    lateinit var onUnMatch: QueueUnMatchListener

    lateinit var onMatch: MatchCardsListener

    lateinit var onTaped: QueueTapedListener

     fun extract() {

        val next = queue.poll() ?: return

        onTaped.tap(next)

        pair.add(next)

        checkPair()
    }

    private fun checkPair()   {
        if(pair.size == 2) {
            if (pair[0].item.resourceId != pair[1].item.resourceId) {
                onUnMatch.reverse(ArrayList(pair))
            }
            else onMatch.onMatch(pair[0].item.resourceId)

            pair = mutableListOf()
        }

    }

}