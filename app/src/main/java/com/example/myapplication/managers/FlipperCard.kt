package com.example.myapplication.managers

import android.view.View
import android.widget.FrameLayout
import androidx.core.animation.doOnEnd
import com.example.myapplication.constants.Constants
import com.example.myapplication.models.GameMemoryItem
import com.example.myapplication.models.Level
import com.example.myapplication.QueueAnalyzer
import com.example.myapplication.events.GameOverListener
import com.example.myapplication.events.MatchCardsListener
import com.example.myapplication.events.QueueTapedListener
import com.example.myapplication.events.QueueUnMatchListener
import com.example.myapplication.models.Box
import java.util.function.Consumer

class FlipperCard(val level: Level, private val queueAnalyzer: QueueAnalyzer = QueueAnalyzer() ) {

     var matches: MutableSet<Int> = mutableSetOf()
     private set

    var onGameOverListener: GameOverListener? = null

    var onMatchCardsListener: MatchCardsListener? = null

    init {
        queueAnalyzer.onTaped = taped()

        queueAnalyzer.onUnMatch = tapUnMatch()

        queueAnalyzer.onMatch = tapMatch()
    }

    private fun taped() = object : QueueTapedListener {
        override fun tap(box: Box) {
            val frontViewCard = box.view.findViewById<FrameLayout>(Constants.cardFrontResourceId)
            val backViewCard = box.view.findViewById<FrameLayout>(Constants.cardBackResourceId)

            frontViewCard.visibility = View.VISIBLE
            backViewCard.visibility = View.GONE
        }
    }

    private fun tapUnMatch() = object : QueueUnMatchListener {
        override fun reverse(pair: List<Box>) {

            flipForHideCard(pair[0])
            flipForHideCard(pair[1])
        }
    }

    private fun  tapMatch() = object : MatchCardsListener {
        override fun onMatch(resourceId: Int) {
            matches.add(resourceId)
            onMatchCardsListener?.onMatch(resourceId)

            if(matches.size == level.getCartCount()/2){
                onGameOverListener?.onEnd()
            }
        }
    }

    fun flipForShowCard(box: Box) {

        if( !box.item.backIsVisible ) return

        queueAnalyzer.add(box)

        val mCardBackLayout = box.view.findViewById<FrameLayout>(Constants.cardBackResourceId)
        val  mCardFrontLayout = box.view.findViewById<FrameLayout>(Constants.cardFrontResourceId)

        box.item.mSetRightOut.setTarget(mCardBackLayout)
        box.item.mSetLeftIn.setTarget(mCardFrontLayout)

        mCardBackLayout.visibility = View.VISIBLE
        mCardFrontLayout.visibility = View.VISIBLE

        val doEndAll = AfterAll({
            queueAnalyzer.extract()
        }, 2)

        box.item.mSetRightOut.doOnEnd {
            run {
                doEndAll.end(box.item)
            }
        }

        box.item.mSetLeftIn.doOnEnd {
            run {
                doEndAll.end(box.item)
            }
        }

        flipCard(box)
    }

    private fun flipForHideCard(box: Box) {

         if( box.item.backIsVisible ) return

        val mCardBackLayout = box.view.findViewById<FrameLayout>(Constants.cardBackResourceId)
        val mCardFrontLayout = box.view.findViewById<FrameLayout>(Constants.cardFrontResourceId)

        box.item.mSetRightOut.setTarget(mCardFrontLayout)
        box.item.mSetLeftIn.setTarget(mCardBackLayout)

        mCardBackLayout.visibility = View.VISIBLE
        mCardFrontLayout.visibility = View.VISIBLE

        flipCard(box)
    }

    private fun flipCard(box: Box) {

        val mCardBackLayout = box.view.findViewById<FrameLayout>(Constants.cardBackResourceId)
        val mCardFrontLayout = box.view.findViewById<FrameLayout>(Constants.cardFrontResourceId)

        box.item.mSetRightOut.start()
        box.item.mSetLeftIn.start()

        box.item.backIsVisible = !box.item.backIsVisible

        val distance = 8000
        val scale = box.view.resources.displayMetrics.density * distance

        mCardBackLayout.cameraDistance = scale
        mCardFrontLayout.cameraDistance = scale
    }

    companion object {

         class AfterAll(private val callBack: Consumer<GameMemoryItem>, private val limitFinish: Int ){
            private   var finishCount = 0

            fun end(item: GameMemoryItem) {
                if(++finishCount == limitFinish) {
                    callBack.accept(item)
                    finishCount = 0
                }
               }
            }
        }
    }
