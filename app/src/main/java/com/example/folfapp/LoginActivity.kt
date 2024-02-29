package com.example.folfapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.folfapp.ui.theme.FolfAppTheme

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FolfAppTheme {
                FolfApp()
            }
        }
    }
}



@Preview(showBackground = true, widthDp = 320)
@Composable
fun LoginActivityPreview() {
    FolfAppTheme {
        FolfApp()
    }
}