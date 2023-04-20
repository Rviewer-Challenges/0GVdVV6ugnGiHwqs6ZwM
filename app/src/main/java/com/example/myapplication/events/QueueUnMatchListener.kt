package com.example.myapplication.events

import com.example.myapplication.models.Box

interface QueueUnMatchListener {
    fun reverse(pair: List<Box>)
}