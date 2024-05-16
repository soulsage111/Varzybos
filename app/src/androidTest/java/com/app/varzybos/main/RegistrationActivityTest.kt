package com.app.varzybos.main

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

class RegistrationActivityTest {
    @get:Rule
    val rule = createComposeRule()
    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun test_Register() {
        rule.setContent { RegistrationActivity() }
        rule.onNodeWithTag("vardas", useUnmergedTree = true).performTextInput("Vardenis")
        rule.onNodeWithTag("pavarde").performTextInput("Pavardenis")
        rule.onNodeWithTag("pastas").performTextInput("vardenis@pavardenis.lt")
        rule.onNodeWithTag("slaptazodis").performTextInput("password")
        rule.onNodeWithTag("reg").performClick()
    }
}