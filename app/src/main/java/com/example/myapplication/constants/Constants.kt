package com.example.myapplication.constants

import com.example.myapplication.R

class Constants {
    companion object {
        const val containerResourceId = R.layout.gameboard_row
        const val cardResourceId = R.layout.fragment_flip
        const val cardFrontResourceId = R.id.card_front
        const val cardBackResourceId = R.id.card_back
        const val cardMatchedResourceId = R.drawable.border_matched
        const val cardUnmatchedResourceId = R.drawable.border_no_matched
        const val outAnimationResourceId = R.animator.out_animation
        const val inAnimationResourceId = R.animator.in_animation
    }
}