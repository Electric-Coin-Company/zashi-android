package co.electriccoin.zcash.ui.screen.pin.view

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
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
import co.electriccoin.zcash.ui.common.WALLET_PASSWORD_LENGTH
import co.electriccoin.zcash.ui.common.WRONG_VIBRATION_PATTERN
import co.electriccoin.zcash.ui.design.component.TitleLarge
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.send.nighthawk.model.NumberPadValueTypes
import kotlinx.coroutines.delay

@Preview
@Composable
fun AuthenticatePinPreview() {
    ZcashTheme(darkTheme = false) {
        Surface {
            AuthenticatePin(lastPin = "111111", onBack = {}, onAuthentication = {})
        }
    }
}

@Composable
fun AuthenticatePin(lastPin: String, onBack: () -> Unit, onAuthentication: (Boolean) -> Unit) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(dimensionResource(id = R.dimen.screen_standard_margin))
    ) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = LocalContext.current.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            LocalContext.current.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        val enteredPassword = remember {
            mutableStateOf("")
        }
        val isFullPasswordEntered = remember {
            derivedStateOf {
                enteredPassword.value.length == WALLET_PASSWORD_LENGTH
            }
        }
        val incorrectPassword = remember {
            derivedStateOf {
                isFullPasswordEntered.value && lastPin != enteredPassword.value
            }
        }
        if (isFullPasswordEntered.value) {
            if (lastPin == enteredPassword.value) {
                onAuthentication(true)
            } else {
                LaunchedEffect(key1 = isFullPasswordEntered.value) {
                    if (vibrator.hasVibrator()) {
                        vibrator.vibrate(VibrationEffect.createWaveform(WRONG_VIBRATION_PATTERN, -1))
                    }
                    delay(1000)
                    enteredPassword.value = ""
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

        EnterPinCommonUI(
            incorrectPassword = incorrectPassword.value,
            enteredPassword = enteredPassword.value,
            message = stringResource(id = if (incorrectPassword.value) R.string.ns_security_try_again else R.string.enter_six_digit_pin_code),
            onKeyPressed = {
                if (it is NumberPadValueTypes.BackSpace) {
                    if (enteredPassword.value.isNotBlank()) {
                        enteredPassword.value = enteredPassword.value.dropLast(1)
                    }
                } else {
                    enteredPassword.value += it.keyValue
                }
            }
        )
    }
}
