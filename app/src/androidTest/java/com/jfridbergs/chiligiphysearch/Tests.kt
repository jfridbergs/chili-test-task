package com.jfridbergs.chiligiphysearch

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.filter
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyPress
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.printToLog
import androidx.compose.ui.test.swipeUp
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.swipeUp
import com.jfridbergs.chiligiphysearch.ui.theme.ChiliGiphyTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class Tests {

    @get:Rule
    val composeTestRule = createComposeRule()
    // use createAndroidComposeRule<YourActivity>() if you need access to
    // an activity

    private val inputCompose = composeTestRule.onNodeWithTag("SearchInput")
    private val outputCompose = composeTestRule.onNodeWithTag("SearchOutput")

    @Before
    fun setup(){
        composeTestRule.setContent {
            ChiliGiphyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PaginatorSample()
                }
            }
        }
    }

    @Test
    fun testInitialCondition(){
        inputCompose.assertIsDisplayed()
        outputCompose
            .onChildren()
            .assertCountEquals(0)
    }

    @Test
    fun testInputAndDisplayResults(){
        simulateAndAssertInitialInput()
    }

    @Test
    fun testResultPaging(){
        simulateAndAssertInitialInput()
        simulateAndAssertSwipeAction()

    }

    @Test
    fun testLiveSearch(){
        simulateAndAssertInitialInput()
        simulateAndAssertSwipeAction()

        //in search textfield type character 's'
        inputCompose.performClick()

        inputCompose.performKeyPress(KeyEvent(
            android.view.KeyEvent(
                android.view.KeyEvent.ACTION_DOWN,
                android.view.KeyEvent.KEYCODE_S

            )
        ))

        Espresso.closeSoftKeyboard()

        composeTestRule.waitForIdle()

        composeTestRule.waitUntil (3000){
            outputCompose.onChildren()
                .fetchSemanticsNodes().isNotEmpty()
        }
        //assert that, after changing search query, a list with max 10 items (initial search amount) is displayed
        assert(
            outputCompose.onChildren()
                .fetchSemanticsNodes().size in 1..10
        )

    }

    @Test
    fun testNoDuplicates(){
        simulateAndAssertInitialInput()

        //i cheated by manually getting a content description of one expected gif item from printToLog call
        val expectedContentDescription = "Bored Cat GIF"
        //assert, that only one item in initial result has this content description
        outputCompose.onChildren().filter(hasContentDescription(expectedContentDescription)).assertCountEquals(1)
        simulateAndAssertSwipeAction()
        //assert that swipe gesture and additional items in list don't have the same content description as one item in initial result list
        outputCompose.onChildren().filter(hasContentDescription(expectedContentDescription)).assertCountEquals(1)
    }

    private fun simulateAndAssertInitialInput(){
        inputCompose.performTextInput("cat")

        //wait till the results are starting to show in LazyVerticalGrid
        composeTestRule.waitUntil (3000){
            outputCompose.onChildren()
                .fetchSemanticsNodes().isNotEmpty()
        }

        Espresso.closeSoftKeyboard()

        //assert initial result count, since the limit for API call is set to 10
        assert(
            outputCompose.onChildren()
                .fetchSemanticsNodes().size in 1..10
        )

        outputCompose.printToLog("MY_TEST")
    }

    private fun simulateAndAssertSwipeAction(){
        //swipe up to start result paging and returning/showing next results
        outputCompose.performTouchInput { swipeUp(800f,400f,1500) }

        //composeTestRule.onNodeWithTag(output).performTouchInput { swipeUp() }

        //assert, that the list now has more than 10 (initial amount) results
        composeTestRule.waitUntil (3000){
            outputCompose.onChildren()
                .fetchSemanticsNodes().size>10
        }

        //assert successful scroll to a item, which was returned after a swiping motion
        outputCompose.performScrollToIndex(15)
    }

}