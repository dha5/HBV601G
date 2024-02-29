package com.example.folfapp.ui


import androidx.compose.runtime.Composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewModelScope

import com.example.folfapp.R
import com.example.folfapp.model.UserCreds
import com.example.folfapp.network.FolfApi
import com.example.folfapp.ui.theme.FolfAppTheme


@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    loginButtonClicked: () -> Unit = {},
    viewModel: FolfViewModel
) {
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var context = LocalContext.current

    Column(
        modifier = modifier.padding(
            horizontal = dimensionResource(R.dimen.padding_medium),
            vertical = dimensionResource(R.dimen.padding_medium)
        ),
        verticalArrangement = Arrangement.Top
    ) {
        OutlinedTextField(
            modifier = modifier.padding(bottom = dimensionResource(R.dimen.padding_medium)),
            value = username,
            onValueChange = { username = it },
            label = { Text(stringResource(R.string.username_label))},
            singleLine = true
        )

        OutlinedTextField(
            modifier = modifier.padding(bottom = dimensionResource(R.dimen.padding_medium)),
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.password_label))},
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        Button(
            modifier = modifier, //Modifier.weight(1f),
            // the button is enabled when the user makes a selection
            // enabled = selectedValue.isNotEmpty(),
            onClick = {
                viewModel.doLogin(UserCreds(username, password))
                loginButtonClicked()
            }
        ) {
            Text(stringResource(R.string.login_button))
        }
    }

}

@Preview
@Composable
fun LoginPreview() {
    FolfAppTheme {
        LoginScreen(viewModel = viewModel())
    }
}