package pl.gs.konwerterliczb

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.gs.konwerterliczb.ui.theme.KonwerterLiczbTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KonwerterLiczbTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var orientation by remember { mutableIntStateOf(Configuration.ORIENTATION_PORTRAIT) }
                    val configuration = LocalConfiguration.current

                    LaunchedEffect(configuration) {
                        snapshotFlow { configuration.orientation }
                            .collect { orientation = it }
                    }

                    when (orientation) {
                        Configuration.ORIENTATION_LANDSCAPE -> {
                            LandscapeNumberConverterLayout()
                        }
                        else -> {
                            PortraitNumberConverterLayout()
                        }
                    }
                }
            }
        }
    }
}

enum class Format {
    DECIMAL,
    HEXADECIMAL,
    OCTAL,
    BINARY
}

@Composable
fun LandscapeNumberConverterLayout(mainViewModel: MainViewModel = viewModel()) {
    val numberInput = mainViewModel.numberInput
    val selectedFormat = mainViewModel.selectedFormat

    LaunchedEffect(key1 = numberInput + selectedFormat) {
        mainViewModel.formatNumber()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(R.string.app_name),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(bottom = 16.dp, top = 16.dp)
                .align(alignment = Alignment.CenterHorizontally)
        )
        EditNumberField(
            label = R.string.input_number,
            leadingIcon = R.drawable.numbers,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done,
            ),
            value = numberInput,
            onValueChanged = { mainViewModel.numberInput = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 64.dp, end = 64.dp),
        )
        Text(
            text = stringResource(R.string.format_label),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .padding(top = 16.dp, bottom = 8.dp, start = 32.dp)
                .align(alignment = Alignment.CenterHorizontally)
        )
        val formatLabels = listOf(stringResource(R.string.decimal), stringResource(R.string.hexadecimal), stringResource(R.string.octal), stringResource(R.string.binary))
        val formatValues = Format.entries
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 32.dp, end = 32.dp),
            horizontalArrangement = Arrangement.Center
        ){
            formatLabels.forEachIndexed { index, label ->
            Row(
                Modifier
                    .padding(8.dp)
                    .clickable {
                        mainViewModel.selectedFormat = formatValues[index]
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (mainViewModel.selectedFormat == formatValues[index]),
                    onClick = { mainViewModel.selectedFormat = formatValues[index] },
                )
                Text(
                    text = label,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            }
        }
        Divider(
            color = MaterialTheme.colorScheme.secondary,
            thickness = 1.dp
        )

        Text(
            text = stringResource(R.string.format_result),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(top = 12.dp, bottom = 16.dp)
                .align(alignment = Alignment.CenterHorizontally)
        )
        Text(
            text = mainViewModel.formattedNumberState,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

    }
}

@Composable
fun PortraitNumberConverterLayout(mainViewModel: MainViewModel = viewModel()) {
    val numberInput = mainViewModel.numberInput
    val selectedFormat = mainViewModel.selectedFormat

    LaunchedEffect(key1 = numberInput + selectedFormat) {
        mainViewModel.formatNumber()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(R.string.app_name),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(bottom = 16.dp, top = 40.dp)
                .align(alignment = Alignment.CenterHorizontally)
        )
        EditNumberField(
            label = R.string.input_number,
            leadingIcon = R.drawable.numbers,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done,
            ),
            value = mainViewModel.numberInput,
            onValueChanged = { mainViewModel.numberInput = it },
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth(),
        )
        Text(
            text = stringResource(R.string.format_label),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .padding(top = 8.dp, bottom = 8.dp, start = 32.dp)
                .align(alignment = Alignment.Start)
        )
        val formatLabels = listOf(stringResource(R.string.decimal), stringResource(R.string.hexadecimal), stringResource(R.string.octal), stringResource(R.string.binary))
        val formatValues = Format.entries

        Column {
            formatLabels.forEachIndexed { index, label ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            mainViewModel.selectedFormat = formatValues[index]
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    RadioButton(
                        selected = (mainViewModel.selectedFormat == formatValues[index]),
                        onClick = {mainViewModel.selectedFormat = formatValues[index]},
                        modifier = Modifier.padding( start = 16.dp)
                    )
                    Text(
                        text = label,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }
            }
        }
        Divider(
            color = MaterialTheme.colorScheme.secondary,
            thickness = 1.dp
        )
        Text(
            text = stringResource(R.string.format_result),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(top = 16.dp, bottom = 16.dp)
                .align(alignment = Alignment.CenterHorizontally)
        )
        Text(
            text = mainViewModel.formattedNumberState,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colorScheme.primary,
        )

    }
}

@Composable
fun EditNumberField(
    @StringRes label: Int,
    @DrawableRes leadingIcon: Int,
    keyboardOptions: KeyboardOptions,
    value: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    TextField(
        value = value,
        singleLine = true,
        leadingIcon = { Icon(painter = painterResource(id = leadingIcon), null) },
        modifier = modifier.focusRequester(focusRequester),
        onValueChange = { newValue ->
            if (newValue.all { it.isDigit() } && newValue.length <= 9) {
                onValueChanged(newValue)
            }
        },
        label = { Text(stringResource(label)) },
        keyboardOptions = keyboardOptions,
        keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }
                )
    )
}

suspend fun formatNumberInBackground(number: String, format: Format): String = withContext(Dispatchers.IO) {
    if (number.isNotEmpty()) {
        when (format) {
            Format.DECIMAL -> try {
                number.toInt().toString()
            } catch (e: NumberFormatException) {
                "Invalid decimal input"
            }

            Format.HEXADECIMAL -> try {
                Integer.toHexString(number.toInt()).uppercase() // Parse as hex
            } catch (e: NumberFormatException) {
                "Invalid hexadecimal input"
            }

            Format.OCTAL -> try {
                Integer.toOctalString(number.toInt()) // Parse as octal
            } catch (e: NumberFormatException) {
                "Invalid octal input"
            }

            Format.BINARY -> try {
                Integer.toBinaryString(number.toInt()) // Parse as binary
            } catch (e: NumberFormatException) {
                "Invalid binary input"
            }
        }
    }else {
        ""
    }
}