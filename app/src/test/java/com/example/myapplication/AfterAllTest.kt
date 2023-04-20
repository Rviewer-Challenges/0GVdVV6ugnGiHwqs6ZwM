package com.example.myapplication

import com.example.myapplication.managers.FlipperCard
import com.example.myapplication.models.GameMemoryItem
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import java.util.function.Consumer

@RunWith(MockitoJUnitRunner::class)
class AfterAllTest {

    @Mock
    private lateinit var mockCallBack:Consumer<GameMemoryItem>

    @Mock
    private var mockGameMemoryItem: GameMemoryItem = mock()

    @Before
    fun setup() {
        mockCallBack = mock()
    }

    @Test
    fun testMethodEnd() {

        val ae = FlipperCard.Companion.AfterAll(mockCallBack, 1)

        ae.end(mockGameMemoryItem)

        verify(mockCallBack).accept(mockGameMemoryItem)
    }

}