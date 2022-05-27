package co.electriccoin.zcash.ui.screen.scan.view

import android.Manifest
import android.content.Context
import android.content.res.Configuration
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
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
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.SecondaryButton
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.scan.ScanTag
import co.electriccoin.zcash.ui.screen.scan.model.ScanState
import co.electriccoin.zcash.ui.screen.scan.util.QrCodeAnalyzer
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.guava.await
import java.util.UUID
import kotlin.math.roundToInt

// TODO [#423]: https://github.com/zcash/secant-android-wallet/issues/423
@Preview("Scan")
@Composable
fun PreviewScan() {
    ZcashTheme(darkTheme = true) {
        GradientSurface {
            Scan(
                snackbarHostState = SnackbarHostState(),
                onBack = {},
                onScanned = {},
                onOpenSettings = {},
                onScanStateChanged = {}
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Scan(
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onScanned: (String) -> Unit,
    onOpenSettings: () -> Unit,
    onScanStateChanged: (ScanState) -> Unit,
) {
    Scaffold(
        topBar = { ScanTopAppBar(onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) {
        ScanMainContent(
            onScanned,
            onOpenSettings,
            onBack,
            onScanStateChanged,
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

@OptIn(ExperimentalPermissionsApi::class)
@Suppress("MagicNumber", "LongMethod")
@Composable
private fun ScanMainContent(
    onScanned: (String) -> Unit,
    onOpenSettings: () -> Unit,
    onBack: () -> Unit,
    onScanStateChanged: (ScanState) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current

    val permissionState = rememberPermissionState(
        Manifest.permission.CAMERA
    )

    val (scanState, setScanState) = rememberSaveable {
        mutableStateOf(
            if (permissionState.hasPermission) {
                ScanState.Scanning
            } else {
                ScanState.Permission
            }
        )
    }

    if (!permissionState.hasPermission) {
        setScanState(ScanState.Permission)
        LaunchedEffect(key1 = UUID.randomUUID()) {
            permissionState.launchPermissionRequest()
        }
    } else if (scanState == ScanState.Failed) {
        // keep current state
    } else if (permissionState.hasPermission) {
        setScanState(ScanState.Scanning)
    }

    // we calculate the best frame size for the current device screen
    val framePossibleSize = remember { mutableStateOf(IntSize.Zero) }

    val configuration = LocalConfiguration.current
    val frameActualSize = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        (framePossibleSize.value.height * 0.85).roundToInt()
    } else {
        (framePossibleSize.value.width * 0.7).roundToInt()
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        val (frame, bottomItems) = createRefs()

        when (scanState) {
            ScanState.Permission -> {
                // keep initial ui state
                onScanStateChanged(ScanState.Permission)
            }
            ScanState.Scanning -> {
                // TODO [#437]: https://github.com/zcash/secant-android-wallet/issues/437
                onScanStateChanged(ScanState.Scanning)
                ScanCameraView(
                    onScanned = onScanned,
                    setScanState = setScanState
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
                        }
                        .onSizeChanged { coordinates ->
                            framePossibleSize.value = coordinates
                        },
                    contentAlignment = Alignment.Center
                ) {
                    ScanFrame(frameActualSize)
                }
            }
            ScanState.Failed -> {
                onScanStateChanged(ScanState.Failed)
                LaunchedEffect(key1 = true) {
                    setScanState(ScanState.Failed)
                    onScanStateChanged(ScanState.Failed)
                    val snackbarResult = snackbarHostState.showSnackbar(
                        message = context.getString(R.string.scan_setup_failed),
                        actionLabel = context.getString(R.string.scan_setup_back),
                    )
                    if (snackbarResult == SnackbarResult.ActionPerformed) {
                        onBack()
                    }
                }
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
    onScanned: (result: String) -> Unit,
    setScanState: (ScanState) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraProviderFlow = remember {
        flow<ProcessCameraProvider> { emit(ProcessCameraProvider.getInstance(context).await()) }
    }

    val collectedCameraProvider = cameraProviderFlow.collectAsState(initial = null).value

    if (null == collectedCameraProvider) {
        // Show loading indicator
    } else {
        val contentDescription = stringResource(id = R.string.scan_preview_content_description)

        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        AndroidView(
            factory = { factoryContext ->
                val previewView = PreviewView(factoryContext).apply {
                    this.scaleType = PreviewView.ScaleType.FILL_CENTER
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
                previewView.contentDescription = contentDescription
                val selector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()
                val preview = androidx.camera.core.Preview.Builder().build().apply {
                    setSurfaceProvider(previewView.surfaceProvider)
                }

                runCatching {
                    // we must unbind the use-cases before rebinding them
                    collectedCameraProvider.unbindAll()
                    collectedCameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        selector,
                        preview,
                        imageAnalysis
                    )
                }.onFailure {
                    setScanState(ScanState.Failed)
                }

                previewView
            },
            Modifier
                .fillMaxSize()
                .testTag(ScanTag.CAMERA_VIEW)
        )

        imageAnalysis.qrCodeFlow(context).collectAsState(initial = null).value?.let {
            onScanned(it)
        }
    }
}

// Using callbackFlow because QrCodeAnalyzer has a non-suspending callback which makes
// a basic flow builder not work here.
fun ImageAnalysis.qrCodeFlow(context: Context): Flow<String> = callbackFlow {
    setAnalyzer(
        ContextCompat.getMainExecutor(context),
        QrCodeAnalyzer { result ->
            // Note that these callbacks aren't tied to the Compose lifecycle, so they could occur
            // after the view goes away.  Collection needs to occur within the Compose lifecycle
            // to make this not be a problem.
            trySend(result)
        }
    )

    awaitClose {
        // Nothing to close
    }
}
