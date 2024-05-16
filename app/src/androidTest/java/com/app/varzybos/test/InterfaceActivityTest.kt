package com.app.varzybos.test

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.app.varzybos.main.InterfaceActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.*


@OptIn(ExperimentalMaterial3Api::class)
class InterfaceActivityTest {
    @get:Rule
    val rule = createComposeRule()

    @Before
    fun setUp() {
        rule.setContent { InterfaceActivity() }
    }


    @Test
    fun onCreate() {
        rule.onNodeWithTag("Pranešimai").assertExists()
    }
}