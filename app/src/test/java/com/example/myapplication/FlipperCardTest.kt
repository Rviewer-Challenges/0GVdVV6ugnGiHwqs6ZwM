package com.example.myapplication

import android.animation.AnimatorSet
import android.content.res.Resources
import android.util.DisplayMetrics
import android.widget.FrameLayout
import com.example.myapplication.constants.GamePictures
import com.example.myapplication.events.GameOverListener
import com.example.myapplication.events.MatchCardsListener
import com.example.myapplication.models.Box
import com.example.myapplication.managers.FlipperCard
import com.example.myapplication.models.GameMemoryItem
import com.example.myapplication.models.Level
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock

@RunWith(MockitoJUnitRunner::class)
class FlipperCardTest {

    private lateinit var flipperCard: FlipperCard

    @Mock
    private var mockView: FrameLayout = mock()

    @Mock
    private var mockResources: Resources = mock()

    @Mock
    private var mockDisplayMetrics:DisplayMetrics = mock()

    @Mock
    private lateinit var mockAnimatorSet: AnimatorSet

    @Mock
    private lateinit var mockOnGameOverListener: GameOverListener

    private lateinit var mockOnMatchCardsListener: MatchCardsListener


     @Before
    fun setup() {

       `when`(mockView.findViewById<FrameLayout>(anyInt())).thenReturn(mockView)
       `when`(mockView.resources).thenReturn(mockResources)
       `when`(mockResources.displayMetrics).thenReturn(mockDisplayMetrics)

        mockAnimatorSet = mock()
    }

    @Test
    fun singleTapTest() {

        val queueAnalyzer = QueueAnalyzer()

        val box = fetchBox(0)

       flipperCard = FlipperCard(Level.LOW, queueAnalyzer)

       flipperCard.flipForShowCard(box)

       assertFalse(box.item.backIsVisible)

    }

    @Test
    fun unMatchTapTest()  {

        val queueAnalyzer = QueueAnalyzer()

        flipperCard = FlipperCard(Level.LOW, queueAnalyzer)

        val box1 = fetchBox(0, false)

        queueAnalyzer.add(box1)

        queueAnalyzer.extract()

        val box2 = fetchBox(1)

        flipperCard.flipForShowCard(box2)
        queueAnalyzer.extract()

        assertTrue(box1.item.backIsVisible)
        assertTrue(box2.item.backIsVisible)
    }

    @Test
    fun matchTapTest() {

        mockOnMatchCardsListener = mock()

        val queueAnalyzer = QueueAnalyzer()

        flipperCard = FlipperCard(Level.LOW, queueAnalyzer)

        flipperCard.onMatchCardsListener = mockOnMatchCardsListener

        val box1 = fetchBox(0, false)

        queueAnalyzer.add(box1)

        queueAnalyzer.extract()

        val box2 = fetchBox(0)

        flipperCard.flipForShowCard(box2)
        queueAnalyzer.extract()

        assertFalse(box1.item.backIsVisible)
        assertFalse(box2.item.backIsVisible)
        assertEquals(flipperCard.matches.size, 1)
        assertEquals(flipperCard.matches.iterator().next(), box1.item.resourceId)
        verify(mockOnMatchCardsListener).onMatch(box1.item.resourceId)
    }

    @Test
    fun tapLastMatchTest() {

        mockOnGameOverListener = mock()

        val queueAnalyzer = QueueAnalyzer()

        flipperCard = FlipperCard(Level.LOW, queueAnalyzer)

        flipperCard.onGameOverListener = mockOnGameOverListener

        val long = (Level.LOW.getCartCount() - 2) / 2

        val box1 = fetchBox( long, false )

        queueAnalyzer.add(box1)

        queueAnalyzer.extract()

        val box2 = fetchBox(long)

        for (i in 0 until  long ) {
            flipperCard.matches.add(GamePictures.pictures[i])
        }

        flipperCard.flipForShowCard(box2)
        queueAnalyzer.extract()

        verify(mockOnGameOverListener).onEnd()

    }

    private fun fetchBox(pos:Int, backIsVisible: Boolean = true): Box {

        val mg =  GameMemoryItem(GamePictures.pictures[pos], mockAnimatorSet, mockAnimatorSet )
        mg.backIsVisible = backIsVisible
        return Box(mg, mockView)
    }



}