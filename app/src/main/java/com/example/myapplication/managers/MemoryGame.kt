package com.example.myapplication.managers


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import com.example.myapplication.constants.Constants
import com.example.myapplication.models.GameMemoryItem
import com.example.myapplication.models.Level
import com.example.myapplication.events.GameOverListener
import com.example.myapplication.events.MatchCardsListener
import com.example.myapplication.models.Box


class MemoryGame(private val context: Context, level: Level) {

   var board: List<List<GameMemoryItem>>
     private set

    private var revealed:Boolean = false

    lateinit var  views:List<View>
     private set

    private val flipperCard: FlipperCard

    var onGameOverListener: GameOverListener?
     get() = flipperCard.onGameOverListener
     set(value) {
         flipperCard.onGameOverListener = value
     }

    var onMatchCardsListener: MatchCardsListener?
      get() = flipperCard.onMatchCardsListener
      set(value) {
          flipperCard.onMatchCardsListener = value
      }

    init {
          val cartSelector = CartSelector(context,
              Constants.outAnimationResourceId,
              Constants.inAnimationResourceId
          )
          board = cartSelector.buildCartSelection(level)

          buildViews()

         flipperCard = FlipperCard(level)
    }

    fun revealAllCards() {

        views = mutableListOf()

        revealed = true

        buildViews()

        isGaming = false
    }

    private fun buildViews() {

        val list = mutableListOf<View>()

        for ( row in board) {
           list.add( buildSingleRowView(row))
        }

        views = list
    }

    private fun buildSingleRowView( row: List<GameMemoryItem> ): View {

        val view  = LayoutInflater.from(context).inflate(Constants.containerResourceId, null, false) as LinearLayout

       attachImageToContainer(view, row)

        return view
    }

    private fun attachImageToContainer(container:LinearLayout, row : List<GameMemoryItem>) {

        val weight = container.weightSum/board[0].size

        for( column in row ) {

            val view = LayoutInflater.from(context).inflate(Constants.cardResourceId, container, false)

            val cardFrontView = view.findViewById<FrameLayout>(Constants.cardFrontResourceId)

            val imageView = ImageView(context)

            imageView.setImageResource(column.resourceId)

            cardFrontView.addView(imageView)

            if(revealed) {
                cardFrontView.visibility = View.VISIBLE
                val bgResource = if(flipperCard.matches.contains(column.resourceId)) Constants.cardMatchedResourceId
                    else Constants.cardUnmatchedResourceId
                cardFrontView.setBackgroundResource (bgResource)
                view.findViewById<FrameLayout>(Constants.cardBackResourceId).visibility = View.GONE
            }
            else {
                view.setOnClickListener { v -> revealCard(Box(column, v)) }
            }

            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, weight )

            view.layoutParams = params

            container.addView(view)
        }
    }

    private fun revealCard(box: Box) {

        if(!isGaming) return

        flipperCard.flipForShowCard(box)

    }

    fun getMatches(): Set<Int> = flipperCard.matches

    companion object {
       var isGaming = false
    }
}