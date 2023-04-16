package com.example.myapplication.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.myapplication.models.Level

class LevelArrayAdapter(context: Context) :
    ArrayAdapter<Level>(context, android.R.layout.simple_spinner_item, Level.values().toList()) {

    @SuppressLint("ViewHolder", "InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
     val view = LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_item, null, false)

     val textView = view.findViewById<TextView>(android.R.id.text1)

     textView.text = getItem(position)?.label() ?: ""

      return view
    }
}