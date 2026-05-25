package com.example.designsystem.components

import androidx.compose.material3.Text
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CatalogComponentsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testCatalogFloatingAppBar_rendersContent() {
        composeTestRule.setContent {
            CatalogFloatingAppBar {
                Text("Test FAB")
            }
        }

        composeTestRule.onNodeWithText("Test FAB").assertIsDisplayed()
    }
    
    @Test
    fun testCatalogPullToRefresh_rendersContent() {
        composeTestRule.setContent {
            CatalogPullToRefresh(isRefreshing = false, onRefresh = {}) {
                Text("Refresh Content")
            }
        }

        composeTestRule.onNodeWithText("Refresh Content").assertIsDisplayed()
    }
    
    @Test
    fun testCatalogCarousel_rendersContent() {
        composeTestRule.setContent {
            CatalogCarousel {
                Text("Carousel Item")
            }
        }

        composeTestRule.onNodeWithText("Carousel Item").assertIsDisplayed()
    }
}
