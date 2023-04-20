package com.example.myapplication.models

enum class Level {
    LOW{
        override fun getColumnCount() = 4
        override fun label() = "Low"
       },
    MEDIUM {
        override fun label() = "Medium"
    },
    HIGH{
        override fun getRowCount() = 5
        override fun label() = "High"
    };

    open fun getRowCount() = 4
    open fun getColumnCount() = 6


    abstract fun label():String

    fun getCartCount() = getColumnCount()*getRowCount()

}