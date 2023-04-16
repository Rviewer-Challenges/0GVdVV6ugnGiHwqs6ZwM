package com.example.myapplication

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import com.example.myapplication.constants.Constants
import com.example.myapplication.managers.CartSelector
import com.example.myapplication.managers.MemoryGame
import com.example.myapplication.models.Level
import com.example.myapplication.selectors.SelectorIndex
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

@RunWith(MockitoJUnitRunner::class)
class MemoryGameTest {

    init  {
        CartSelector.selector = object : SelectorIndex {
            override fun selectIndex(list: List<Int>, position: Int) = 0
        }
    }

    private var level = Level.LOW
    @Mock
    private  var mockAnimatorSet: AnimatorSet = mock()

    @Mock
    private  var mockLayoutInflater: LayoutInflater = mock()

    @Mock
    private var mockView: LinearLayout = mock()

    @Mock
    private var mockFrameLayout: FrameLayout = mock()

    @Mock
    private var mockContext: Context = mock()

    private var mockedImageViewConstructor =  mockConstruction(ImageView::class.java )

    private lateinit var memoryGame: MemoryGame

    private lateinit var mockStaticLayoutInflater: MockedStatic<LayoutInflater>

    private lateinit var mockStaticAnimatorInflater: MockedStatic<AnimatorInflater>


    @Before
    fun setUp(){
     mockStaticLayoutInflater =  mockStatic(LayoutInflater::class.java)
     mockStaticAnimatorInflater =  mockStatic(AnimatorInflater::class.java)

     `when`(mockView.findViewById<FrameLayout>(Constants.cardFrontResourceId)).doReturn(mockFrameLayout)

     `when`(mockLayoutInflater.inflate(anyInt(), any(), anyBoolean()))
          .doReturn(mockView)

    mockStaticAnimatorInflater.`when`<Animator> {

        AnimatorInflater.loadAnimator(
            any(Context::class.java),
            anyInt()
        )
    }
        .thenReturn(mockAnimatorSet)

       mockStaticLayoutInflater.`when`<LayoutInflater> { LayoutInflater.from(any(Context::class.java)) }
           .thenReturn(mockLayoutInflater)
    }

    @After
    fun after() {
       mockStaticAnimatorInflater.close()
       mockStaticLayoutInflater.close()
    }

    @Test
    fun checkStart() {

        mockedImageViewConstructor.use {

            memoryGame = MemoryGame( mockContext , level )

            assertEquals(memoryGame.board.size, level.getRowCount())

            assertEquals(memoryGame.board[0].size, level.getColumnCount())

            assertEquals(memoryGame.getMatches().size, 0)

            assertFalse(MemoryGame.isGaming)

            assertAllCardVisibility(true)
        }
    }

    @Test
    fun checkRevealAllCards() {

        `when`(mockView.findViewById<FrameLayout>(Constants.cardBackResourceId))
            .doReturn(mockFrameLayout)

         mockedImageViewConstructor.use {
            memoryGame = MemoryGame(mockContext, level)

            memoryGame.revealAllCards()

            assertAllCardVisibility(true)

        }
    }


    private fun assertAllCardVisibility(isBackVisible: Boolean)  {

        for( row in memoryGame.board) {
            for (column in row) {
                assertEquals(column.backIsVisible, isBackVisible)
            }
        }
    }
}