package com.example.myapplication.events

import com.example.myapplication.models.Box

interface QueueTapedListener {

    fun tap(box: Box)

}