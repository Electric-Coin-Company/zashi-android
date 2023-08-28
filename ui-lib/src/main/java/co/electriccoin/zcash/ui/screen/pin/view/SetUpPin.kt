package co.electriccoin.zcash.ui.screen.pin.view

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.SUCCESS_VIBRATION_PATTERN
import co.electriccoin.zcash.ui.common.WALLET_PASSWORD_LENGTH
import co.electriccoin.zcash.ui.common.WRONG_VIBRATION_PATTERN
import co.electriccoin.zcash.ui.design.component.TitleLarge
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.send.nighthawk.model.NumberPadValueTypes
import kotlinx.coroutines.delay

@Composable
@Preview(apiLevel = 33)
fun SetUpPinPreview() {
    ZcashTheme(darkTheme = false) {
        Surface {
            SetUpPin(onBack = {}, onPinSelected = {})
        }
    }
}

@Composable
fun SetUpPin(onBack: () -> Unit, onPinSelected: (String) -> Unit) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(dimensionResource(id = R.dimen.screen_standard_margin))
    ) {
        val context = LocalContext.current

        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        val firstTimeEnteredPassword = remember {
            mutableStateOf("")
        }
        val secondEnteredPassword = remember {
            mutableStateOf("")
        }
        val isFirstFullPasswordEntered = remember {
            derivedStateOf {
                firstTimeEnteredPassword.value.length == WALLET_PASSWORD_LENGTH
            }
        }
        val isSecondFullPasswordEntered = remember {
            derivedStateOf {
                secondEnteredPassword.value.length == WALLET_PASSWORD_LENGTH
            }
        }
        val incorrectPassword = remember {
            derivedStateOf {
                isFirstFullPasswordEntered.value && isSecondFullPasswordEntered.value && secondEnteredPassword.value != firstTimeEnteredPassword.value
            }
        }

        if (isFirstFullPasswordEntered.value && isSecondFullPasswordEntered.value) {
            if (secondEnteredPassword.value == firstTimeEnteredPassword.value) {
                if (vibrator.hasVibrator()) {
                    vibrator.vibrate(VibrationEffect.createWaveform(SUCCESS_VIBRATION_PATTERN, -1))
                }
                Toast.makeText(context, context.getString(R.string.pin_code_setup_done), Toast.LENGTH_SHORT).show()
                onPinSelected(firstTimeEnteredPassword.value)
            } else {
                LaunchedEffect(key1 = firstTimeEnteredPassword.value) {
                    if (vibrator.hasVibrator()) {
                        vibrator.vibrate(VibrationEffect.createWaveform(WRONG_VIBRATION_PATTERN, -1))
                    }
                    delay(1000)
                    firstTimeEnteredPassword.value = ""
                    secondEnteredPassword.value = ""
                }
            }
        }
        
        IconButton(onClick = onBack, modifier = Modifier.size(dimensionResource(id = R.dimen.back_icon_size))) {
            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.receive_back_content_description))
        }
        Image(painter = painterResource(id = R.drawable.ic_nighthawk_logo), contentDescription = "logo", contentScale = ContentScale.Inside, modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(Modifier.height(dimensionResource(id = R.dimen.pageMargin)))
        TitleLarge(text = stringResource(id = R.string.ns_nighthawk), textAlign = TextAlign.Center, modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.height(35.dp))

        val messageId = when {
            incorrectPassword.value -> R.string.ns_security_try_again
            isFirstFullPasswordEntered.value -> R.string.verify_pin_code
            else -> R.string.choose_pin_code
        }

        val currentPasswordState = if (isFirstFullPasswordEntered.value) secondEnteredPassword else firstTimeEnteredPassword

        EnterPinCommonUI(
            incorrectPassword = incorrectPassword.value,
            enteredPassword = currentPasswordState.value,
            message = stringResource(id = messageId),
            onKeyPressed = {
                if (it is NumberPadValueTypes.BackSpace) {
                    if (currentPasswordState.value.isNotBlank()) {
                        currentPasswordState.value = currentPasswordState.value.dropLast(1)
                    }
                } else {
                    currentPasswordState.value += it.keyValue
                }
            }
        )
    }
}
