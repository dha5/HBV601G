package com.example.folfapp

import androidx.annotation.StringRes
import androidx.compose.material3.ExperimentalMaterial3Api

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

import com.example.folfapp.ui.FolfViewModel
import com.example.folfapp.ui.LoginScreen
import com.example.folfapp.ui.MainScreen


enum class FolfScreen(@StringRes val title: Int) {
    Login(title = R.string.login_title),
    Main(title = R.string.main_title)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolfAppBar(
    currentScreen: FolfScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}

@Composable
fun FolfApp(
    viewModel: FolfViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = FolfScreen.valueOf(
        backStackEntry?.destination?.route ?: FolfScreen.Login.name
    )

    Scaffold(
        topBar = {
            FolfAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        val uiState by viewModel.uiState.collectAsState()

        NavHost(
            navController = navController,
            startDestination = FolfScreen.Login.name,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            composable(route = FolfScreen.Login.name) {
                LoginScreen(
                    viewModel = viewModel,
                    loginButtonClicked = {
                        navController.navigate(FolfScreen.Main.name)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        //.padding(dimensionResource(R.dimen.padding_medium))
                )
            }
            composable(route = FolfScreen.Main.name) {
                val context = LocalContext.current
                MainScreen(
                    onNextButtonClicked = { navController.navigate(FolfScreen.Login.name) },
                    modifier = Modifier.fillMaxHeight()
                )
            }
        }
    }
}
