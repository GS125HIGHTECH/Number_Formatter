package pl.gs.konwerterliczb

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel: ViewModel() {
    var numberInput by mutableStateOf("")
    var selectedFormat by mutableStateOf(Format.DECIMAL)
    var formattedNumberState by mutableStateOf("")

    fun formatNumber() {
        viewModelScope.launch {
            val formattedNumber = withContext(Dispatchers.IO) {
                formatNumberInBackground(numberInput, selectedFormat)
            }
            formattedNumberState = formattedNumber
        }
}
}