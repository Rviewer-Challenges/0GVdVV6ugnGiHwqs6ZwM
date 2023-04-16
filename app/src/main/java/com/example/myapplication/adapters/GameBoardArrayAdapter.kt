package com.example.myapplication.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.myapplication.models.GameMemoryItem
import com.example.myapplication.managers.MemoryGame
import com.example.myapplication.R


class GameBoardArrayAdapter(context: Context, private val memoryGame: MemoryGame)
    : ArrayAdapter<List<GameMemoryItem>>(context, R.layout.gameboard_row, memoryGame.board) {

    @SuppressLint("ViewHolder", "InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return memoryGame.views[position]
    }

}