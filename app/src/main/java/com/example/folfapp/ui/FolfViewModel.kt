package com.example.folfapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.folfapp.model.AccessToken
import com.example.folfapp.model.UserCreds
import com.example.folfapp.network.FolfApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class FolfUiState(
    /** Login status */
    val userLoggedIn: Boolean = false,
    val accessToken: String = ""
)

/**
 * [FolfViewModel] holds information about folfgames
 */
class FolfViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(FolfUiState())
    val uiState: StateFlow<FolfUiState> = _uiState.asStateFlow()

    private fun setUserLoggedIn(accessToken: String) {
        _uiState.update { currentState ->
            currentState.copy(
                userLoggedIn = true,
                accessToken = accessToken
            )
        }
    }

    fun doLogin(userCreds: UserCreds) {
        viewModelScope.launch {
            val accessToken = FolfApi.retrofitService.doLogin(userCreds)
            println(accessToken.accessToken)
            if (accessToken.accessToken != "") {
                setUserLoggedIn(accessToken=accessToken.accessToken)
            }
        }
    }

    fun reset() {
        _uiState.value = FolfUiState()
    }

    /**
     * Returns the calculated price based on the order details.
     */
    /*
    private fun calculatePrice(
        quantity: Int = _uiState.value.quantity,
        pickupDate: String = _uiState.value.date
    ): String {
        var calculatedPrice = quantity * PRICE_PER_CUPCAKE
        // If the user selected the first option (today) for pickup, add the surcharge
        if (pickupOptions()[0] == pickupDate) {
            calculatedPrice += PRICE_FOR_SAME_DAY_PICKUP
        }
        val formattedPrice = NumberFormat.getCurrencyInstance().format(calculatedPrice)
        return formattedPrice
    }
    */
    /**
     * Returns a list of date options starting with the current date and the following 3 dates.
     */
    /*
    private fun pickupOptions(): List<String> {
        val dateOptions = mutableListOf<String>()
        val formatter = SimpleDateFormat("E MMM d", Locale.getDefault())
        val calendar = Calendar.getInstance()
        // add current date and the following 3 dates.
        repeat(4) {
            dateOptions.add(formatter.format(calendar.time))
            calendar.add(Calendar.DATE, 1)
        }
        return dateOptions
    }
    */
}