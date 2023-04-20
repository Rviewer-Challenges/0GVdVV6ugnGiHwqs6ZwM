package com.example.myapplication.managers

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import com.example.myapplication.models.GameMemoryItem
import com.example.myapplication.constants.GamePictures
import com.example.myapplication.models.Level
import com.example.myapplication.selectors.RandomSelectorIndex
import com.example.myapplication.selectors.SelectorIndex
import java.util.stream.Collectors

class CartSelector(private val context: Context, private val outAnimationResource:Int, private val inAnimationResource:Int ) {
    fun buildCartSelection(level: Level): List<List<GameMemoryItem>> {
      val carts = selectCarts(level)
      val list = ArrayList<List<GameMemoryItem>>()
        for ( row in 1..level.getRowCount()){
            val start = (row - 1) * level.getColumnCount()
            val end = row* level.getColumnCount()
            list.add(carts.subList( start, end ).map { id -> GameMemoryItem(
                id,
                AnimatorInflater.loadAnimator(context,outAnimationResource) as AnimatorSet,
                AnimatorInflater.loadAnimator(context, inAnimationResource) as AnimatorSet
            )
            })
        }
        return list
    }
    private fun selectCarts(level: Level):List<Int>{

        val  map =  GamePictures.pictures.stream().collect(Collectors.toMap({ s -> s},{ 2}))

        val list = ArrayList(GamePictures.pictures)

       val carts = ArrayList<Int>()

        var cartsSelected = 0

        for(i in  1 .. level.getCartCount()) {

            val selectedIndex = selector.selectIndex(list, i)

            val k = list[selectedIndex]

            carts.add(k)

            map[k] = map[k]!! - 1

            if (map[k] == 1) {
                if(++cartsSelected == level.getCartCount()/2) {
                    list.removeAll( map.filter { (_,v) -> v == 2 }.keys)
                }
                continue
            }
            list.remove(k)

        }

        return carts

    }

    companion object {
        var selector: SelectorIndex = RandomSelectorIndex()
    }
}