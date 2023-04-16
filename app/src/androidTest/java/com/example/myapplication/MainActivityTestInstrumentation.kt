package com.example.myapplication

import android.view.View
import android.widget.*
import androidx.core.view.size
import androidx.test.espresso.*
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.myapplication.adapters.GameBoardArrayAdapter
import com.example.myapplication.managers.CartSelector
import com.example.myapplication.managers.MemoryGame
import com.example.myapplication.models.Level
import com.example.myapplication.selectors.SelectorIndex
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTestInstrumentation {

    init  {
        CartSelector.selector = object : SelectorIndex {
            override fun selectIndex(list: List<Int>, position: Int) = 0
        }
    }

      @get:Rule
       val rule = activityScenarioRule<MainActivity>()

    @After
    fun finishTest(){
        MemoryGame.isGaming = false
    }

    @Test
    fun ensureInitIsWell() {
        val scenario = rule.scenario

        scenario.onActivity { activity ->
            run {

                val spinner = activity.findViewById<Spinner>(R.id.level)
                assertEquals(spinner.selectedItem as Level, Level.LOW)

                val containerWinner = activity.findViewById<LinearLayout>(R.id.container_won)
                assertEquals(containerWinner.visibility, View.GONE)

                val timer = activity.findViewById<TextView>(R.id.tv_time)
                assertEquals(timer.text, "")

                val startButton = activity.findViewById<TextView>(R.id.restart_level)

                assertEquals(startButton.visibility, View.VISIBLE)
                assertEquals(startButton.text, activity.resources.getString(R.string.start))

                val matcherContainer = activity.findViewById<LinearLayout>(R.id.match_container)
                assertEquals(matcherContainer.visibility, View.GONE)

                val matchTv = activity.findViewById<TextView>(R.id.matches_counter)
                assertEquals(matchTv.text, activity.resources.getString(R.string.zerostring))

                val gameBoard = activity.findViewById<ListView>(R.id.game_board)
                val adapter = gameBoard.adapter as GameBoardArrayAdapter
                assertEquals(gameBoard.size, Level.LOW.getRowCount())
                assertEquals(adapter.getItem(0)!!.size, Level.LOW.getColumnCount())
                assertFalse(MemoryGame.isGaming)


                assertAllCards(gameBoard, true, Level.LOW)
            }
        }


    }

    @Test
    fun pressStartBehaviorTest(): Unit = runBlocking {

        val scenario = rule.scenario

          scenario.onActivity {

            val startButton = it.findViewById<TextView>(R.id.restart_level)

            startButton.callOnClick()

            assertEquals(startButton.visibility, View.GONE)

            val matcherContainer = it.findViewById<LinearLayout>(R.id.match_container)
            assertEquals(matcherContainer.visibility, View.VISIBLE)


            assertTrue(MemoryGame.isGaming)

              launch {
                  onView(isRoot())
                      .perform(waitFor(1000L))
                      .check { view, _ ->
                          run {
                              val timer = view.findViewById<TextView>(R.id.tv_time)
                               assertEquals(timer.text, "59")
                          }
                      }
              }

          }
    }

    @Test
    fun finishTimeTest(): Unit = runBlocking {

        val scenario = rule.scenario

        scenario.onActivity {

            val startButton = it.findViewById<TextView>(R.id.restart_level)

            MainActivity.duration = 0L

            startButton.callOnClick()

            MainActivity.duration = 61 * 1000L

            launch {
                onView(isRoot())
                    .perform(waitFor( 1000L))
                    .check { view, _ ->  run {

                        val containerWon = view.findViewById<LinearLayout>(R.id.container_won)
                        assertEquals(containerWon.visibility, View.VISIBLE)

                        val tvWin = view.findViewById<TextView>(R.id.tv_win)
                        assertEquals(tvWin.text, view.resources.getString(R.string.game_lost))

                        assertEquals(startButton.text, view.resources.getString(R.string.restart_level))

                        val lv = view.findViewById<ListView>(R.id.game_board)

                        assertAllCards(lv, false, Level.LOW)
                    }}
            }
        }
    }

    @Test
    fun matchTest() : Unit = runBlocking {
        val scenario = rule.scenario

        scenario.onActivity {

            val startButton = it.findViewById<TextView>(R.id.restart_level)

            val lv = it.findViewById<ListView>(R.id.game_board)

            startButton.callOnClick()

            val firstRow = lv.getChildAt(0) as LinearLayout

            val firstCard = firstRow.getChildAt(0)

            val secondCard = firstRow.getChildAt(1)

            launch {
                onView(isRoot())
                    .perform(
                        waitFor(100L),
                        clickOnView(firstCard),
                        waitFor(100L),
                        clickOnView(secondCard),
                        waitFor(1000L)
                        )
                    .check { view, _ -> run {

                      val matchesCounter = view.findViewById<TextView>(R.id.matches_counter)
                      assertEquals(matchesCounter.text, "1")

                        val cardFrontFirst = firstCard.findViewById<FrameLayout>(R.id.card_front)

                        assertEquals(cardFrontFirst.visibility, View.VISIBLE)

                        val cardFrontSecond = firstCard.findViewById<FrameLayout>(R.id.card_front)

                        assertEquals(cardFrontSecond.visibility, View.VISIBLE)

                    }}

            }
        }

    }

    @Test
    fun changeLevelTest() : Unit = runBlocking {

        val scenario = rule.scenario

        scenario.onActivity {

            val spinner = it.findViewById<Spinner>(R.id.level)

             spinner.setSelection(1)

            launch {
                onView(isRoot())
                 .perform(waitFor(10L))
                    .check { view, _ -> run {

                        val lv = view.findViewById<ListView>(R.id.game_board)

                        val adapter = lv.adapter as GameBoardArrayAdapter

                        assertEquals(lv.size, Level.MEDIUM.getRowCount())
                        assertEquals(adapter.getItem(0)?.size, Level.MEDIUM.getColumnCount())
                    }  }
            }


        }
    }

    private fun waitFor(delay: Long) = object :ViewAction{

        override fun getDescription() = "wait for $delay milliseconds"

        override fun getConstraints(): Matcher<View>  = isRoot()

        override fun perform(uiController: UiController?, view: View?) {
            uiController?.loopMainThreadForAtLeast(delay)
        }

    }

    private fun clickOnView(v: View) = object : ViewAction {
        override fun getDescription() = "Click on view"

        override fun getConstraints(): Matcher<View> = isRoot()

        override fun perform(uiController: UiController?, view: View?) {
           v.callOnClick()
        }

    }

    private fun assertAllCards(lv:ListView, inBack: Boolean, level: Level) {

        for(i in 0 until level.getRowCount()) {

            val lContainer =   lv.getChildAt(i) as LinearLayout

            for( j in 0 until level.getColumnCount()) {

                val fL =  lContainer.getChildAt(j)

                val cardFront = fL.findViewById<FrameLayout>(R.id.card_front)
                val cardBack = fL.findViewById<FrameLayout>(R.id.card_back)


                assertEquals(cardFront.visibility, if (!inBack)  View.VISIBLE else View.GONE)
                assertEquals(cardBack.visibility, if (inBack)  View.VISIBLE else View.GONE)
            }
        }
    }

}