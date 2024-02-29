package com.example.folfapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    onNextButtonClicked: () -> Unit = {}
) { 
    Column {
        Text(text = "MAINSCREEN")
    }
}