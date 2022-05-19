package co.electriccoin.zcash.ui.screen.scan.view

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.SecondaryButton
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.scan.ScanTag
import co.electriccoin.zcash.ui.screen.scan.model.ScanState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.roundToInt

// TODO [#423]: https://github.com/zcash/secant-android-wallet/issues/423
// TODO [#313]: https://github.com/zcash/secant-android-wallet/issues/313
@Preview("Scan")
@Composable
fun PreviewScan() {
    ZcashTheme(darkTheme = true) {
        GradientSurface {
            Scan(
                snackbarHostState = SnackbarHostState(),
                onBack = {},
                onScan = {},
                onOpenSettings = {}
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("UNUSED_VARIABLE")
@Composable
fun Scan(
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onScan: (String) -> Unit,
    onOpenSettings: () -> Unit,
) {
    Scaffold(
        topBar = { ScanTopAppBar(onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) {
        ScanMainContent(
            onScan,
            onOpenSettings,
            onBack,
            snackbarHostState
        )
    }
}

@Composable
fun ScanBottomItems(
    scanState: ScanState,
    onOpenSettings: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(id = R.string.scan_hint),
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .align(CenterHorizontally)
                .padding(horizontal = 24.dp, vertical = 8.dp)
        )

        Text(
            text = when (scanState) {
                ScanState.Permission -> stringResource(id = R.string.scan_state_permission)
                ScanState.Scanning -> stringResource(id = R.string.scan_state_scanning)
                ScanState.Success -> stringResource(id = R.string.scan_state_success)
                ScanState.Failed -> stringResource(id = R.string.scan_state_failed)
            },
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier
                .fillMaxWidth()
                .align(CenterHorizontally)
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .testTag(ScanTag.TEXT_STATE)
        )

        if (scanState == ScanState.Permission) {
            SecondaryButton(
                onClick = onOpenSettings,
                text = stringResource(id = R.string.scan_settings_button),
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            )
        }
    }
}

@Composable
private fun ScanTopAppBar(onBack: () -> Unit) {
    SmallTopAppBar(
        title = { Text(text = stringResource(id = R.string.scan_header)) },
        navigationIcon = {
            IconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.scan_back_content_description)
                )
            }
        }
    )
}

@Suppress("UNUSED_VARIABLE", "UNUSED_PARAMETER", "MagicNumber", "LongMethod")
@Composable
private fun ScanMainContent(
    onScan: (String) -> Unit,
    onOpenSettings: () -> Unit,
    onBack: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val (scanState, setScanState) = rememberSaveable {
        mutableStateOf(
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                ScanState.Scanning
            } else {
                ScanState.Permission
            }
        )
    }

    val (scanResult, setScanResult) = rememberSaveable { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                setScanState(ScanState.Scanning)
            } else {
                setScanState(ScanState.Permission)
            }
        }
    )

    // We use a random value to show the permission popup after a user grants the CAMERA permission
    // outside of the app (in Settings).
    LaunchedEffect(key1 = UUID.randomUUID()) {
        launcher.launch(Manifest.permission.CAMERA)
    }

    val cameraProviderFlow = remember {
        flow<ProcessCameraProvider> { emit(ProcessCameraProvider.getInstance(context).await()) }
    }

    // we calculate the best frame size for the current device screen
    val framePossibleSize = remember { mutableStateOf(IntSize.Zero) }

    val configuration = LocalConfiguration.current
    val frameActualSize = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        (framePossibleSize.value.height * 0.85).roundToInt()
    } else {
        (framePossibleSize.value.width * 0.7).roundToInt()
    }

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (frame, bottomItems) = createRefs()

        if (scanState == ScanState.Scanning) {
            ScanCameraView(
                onBack,
                cameraProviderFlow,
                lifecycleOwner,
                snackbarHostState
            )

            Box(
                modifier = Modifier
                    .constrainAs(frame) {
                        top.linkTo(parent.top)
                        bottom.linkTo(bottomItems.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }.onSizeChanged { coordinates ->
                        framePossibleSize.value = coordinates
                    },
                contentAlignment = Alignment.Center
            ) {
                ScanFrame(frameActualSize)
            }
        }

        Box(modifier = Modifier.constrainAs(bottomItems) { bottom.linkTo(parent.bottom) }) {
            ScanBottomItems(scanState, onOpenSettings)
        }
    }
}

@Suppress("MagicNumber")
@Composable
fun ScanFrame(frameSize: Int) {
    Box(
        modifier = Modifier
            .size(with(LocalDensity.current) { frameSize.toDp() })
            .background(Color.Transparent)
            .border(BorderStroke(10.dp, Color.White), RoundedCornerShape(10))
            .testTag(ScanTag.QR_FRAME)
    )
}

@Composable
fun ScanCameraView(
    onBack: () -> Unit,
    cameraProviderFlow: Flow<ProcessCameraProvider>,
    lifecycleOwner: LifecycleOwner,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val cameraProvider = cameraProviderFlow.collectAsState(initial = null).value
    if (null == cameraProvider) {
        // Show loading indicator
    } else {
        AndroidView(
            factory = { factoryContext ->
                val previewView = PreviewView(factoryContext).apply {
                    this.scaleType = PreviewView.ScaleType.FILL_CENTER
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
                val preview = androidx.camera.core.Preview.Builder().build()
                val selector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()
                preview.setSurfaceProvider(previewView.surfaceProvider)

                runCatching {
                    // we must unbind the use-cases before rebinding them
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        selector,
                        preview
                    )
                }.onFailure {
                    scope.launch {
                        val snackbarResult = snackbarHostState.showSnackbar(
                            message = context.getString(R.string.scan_setup_failed),
                            actionLabel = context.getString(R.string.scan_setup_back),
                        )
                        if (snackbarResult == SnackbarResult.ActionPerformed) {
                            onBack()
                        }
                    }
                }

                previewView
            },
            Modifier
                .fillMaxSize()
                .testTag(ScanTag.CAMERA_VIEW),
        )
    }
}
