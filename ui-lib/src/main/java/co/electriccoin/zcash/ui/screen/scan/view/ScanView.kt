package co.electriccoin.zcash.ui.screen.scan.view

import android.Manifest
import android.content.Context
import android.view.ViewGroup
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.content.ContextCompat
import cash.z.ecc.android.sdk.type.AddressType
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.SecondaryButton
import co.electriccoin.zcash.ui.design.component.Small
import co.electriccoin.zcash.ui.design.component.SmallTopAppBar
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.scan.ScanTag
import co.electriccoin.zcash.ui.screen.scan.model.ScanState
import co.electriccoin.zcash.ui.screen.scan.util.QrCodeAnalyzer
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.guava.await
import kotlin.math.roundToInt

@Preview("Scan")
@Composable
private fun PreviewScan() {
    ZcashTheme(forceDarkMode = false) {
        GradientSurface {
            Scan(
                snackbarHostState = SnackbarHostState(),
                onBack = {},
                onScanned = {},
                onOpenSettings = {},
                onScanStateChanged = {},
                walletRestoringState = WalletRestoringState.NONE,
                addressValidationResult = AddressType.Transparent,
            )
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
@Suppress("LongParameterList", "UnusedMaterial3ScaffoldPaddingParameter")
fun Scan(
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onScanned: (String) -> Unit,
    onOpenSettings: () -> Unit,
    onScanStateChanged: (ScanState) -> Unit,
    walletRestoringState: WalletRestoringState,
    addressValidationResult: AddressType?
) {
    val permissionState =
        rememberPermissionState(
            Manifest.permission.CAMERA
        )

    val (scanState, setScanState) =
        rememberSaveable {
            mutableStateOf(
                if (permissionState.status.isGranted) {
                    ScanState.Scanning
                } else {
                    ScanState.Permission
                }
            )
        }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { _ ->
        Box {
            ScanMainContent(
                addressValidationResult = addressValidationResult,
                onScanned = onScanned,
                onOpenSettings = onOpenSettings,
                onBack = onBack,
                onScanStateChanged = onScanStateChanged,
                permissionState = permissionState,
                scanState = scanState,
                setScanState = setScanState,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(
                            if (scanState != ScanState.Scanning) {
                                ZcashTheme.colors.cameraDisabledBackgroundColor
                            } else {
                                Color.Black
                            }
                        )
                // Intentionally omitting paddingValues to have edge to edge design
            )

            ScanTopAppBar(
                onBack = onBack,
                showBack = scanState != ScanState.Scanning,
                showRestoring = walletRestoringState == WalletRestoringState.RESTORING,
            )
        }
    }
}

@Composable
fun ScanBottomItems(
    addressValidationResult: AddressType?,
    scanState: ScanState,
    onOpenSettings: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        var failureText: String? = null

        // Check validation result, if any
        if (addressValidationResult is AddressType.Invalid) {
            failureText = stringResource(id = R.string.scan_address_validation_failed)
        }

        // Check permission request result, if any
        failureText =
            when (scanState) {
                ScanState.Permission ->
                    stringResource(
                        id = R.string.scan_state_permission,
                        stringResource(id = R.string.app_name)
                    )
                ScanState.Failed -> stringResource(id = R.string.scan_state_failed)
                ScanState.Scanning -> failureText
            }

        if (failureText != null) {
            Small(
                text = failureText,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .testTag(ScanTag.FAILED_TEXT_STATE)
            )
        }

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))

        when (scanState) {
            ScanState.Scanning, ScanState.Failed -> {
                SecondaryButton(
                    onClick = onBack,
                    text = stringResource(id = R.string.scan_cancel_button)
                )
            }
            ScanState.Permission -> {
                SecondaryButton(
                    onClick = onOpenSettings,
                    text = stringResource(id = R.string.scan_settings_button)
                )
            }
        }

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))
    }
}

@Composable
private fun ScanTopAppBar(
    onBack: () -> Unit,
    showBack: Boolean,
    showRestoring: Boolean,
) {
    SmallTopAppBar(
        restoringLabel =
            if (showRestoring) {
                stringResource(id = R.string.restoring_wallet_label)
            } else {
                null
            },
        backText =
            if (showBack) {
                stringResource(id = R.string.scan_back)
            } else {
                null
            },
        backContentDescriptionText = stringResource(id = R.string.scan_back_content_description),
        colors = ZcashTheme.colors.transparentTopAppBarColors,
        onBack = onBack,
    )
}

const val CAMERA_TRANSLUCENT_BORDER = 0.5f

const val FRAME_SIZE_RATIO = 0.6f

data class FramePosition(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
    val screenHeight: Int,
    val screenWidth: Int
) {
    val width: Float = right - left
    val height: Float = bottom - top
}

@OptIn(ExperimentalPermissionsApi::class)
@Suppress("LongMethod", "LongParameterList")
@Composable
private fun ScanMainContent(
    addressValidationResult: AddressType?,
    onScanned: (String) -> Unit,
    onOpenSettings: () -> Unit,
    onBack: () -> Unit,
    onScanStateChanged: (ScanState) -> Unit,
    permissionState: PermissionState,
    scanState: ScanState,
    setScanState: (ScanState) -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        (!permissionState.status.isGranted) -> {
            setScanState(ScanState.Permission)
            if (permissionState.status.shouldShowRationale) {
                // Keep dark screen with a link to the app settings - user denied the permission previously
            } else {
                LaunchedEffect(key1 = true) {
                    permissionState.launchPermissionRequest()
                }
            }
        }
        (scanState == ScanState.Failed) -> {
            // Keep current state
        }
        (permissionState.status.isGranted) -> {
            if (scanState != ScanState.Scanning) {
                setScanState(ScanState.Scanning)
            }
        }
    }

    // Calculate the best frame size for the current device screen
    val framePossibleSize = remember { mutableStateOf(IntSize.Zero) }

    val frameActualSize = (framePossibleSize.value.width * FRAME_SIZE_RATIO).roundToInt()

    val density = LocalDensity.current

    val configuration = LocalConfiguration.current

    val framePosition =
        FramePosition(
            left = (framePossibleSize.value.width - frameActualSize) / 2f,
            top = (framePossibleSize.value.height - frameActualSize) / 2f,
            right = (framePossibleSize.value.width - frameActualSize) / 2f + frameActualSize,
            bottom = (framePossibleSize.value.height - frameActualSize) / 2f + frameActualSize,
            screenHeight = with(density) { configuration.screenHeightDp.dp.roundToPx() },
            screenWidth = with(density) { configuration.screenWidthDp.dp.roundToPx() }
        )

    val (isTorchOn, setIsTorchOn) = rememberSaveable { mutableStateOf(false) }

    ConstraintLayout(modifier = modifier) {
        val (frame, bottomItems) = createRefs()

        when (scanState) {
            ScanState.Permission -> {
                // Keep initial ui state
                onScanStateChanged(ScanState.Permission)
            }
            ScanState.Scanning -> {
                onScanStateChanged(ScanState.Scanning)

                ScanCameraView(
                    framePosition = framePosition,
                    isTorchOn = isTorchOn,
                    onScanned = onScanned,
                    permissionState = permissionState,
                    setScanState = setScanState,
                )

                Canvas(modifier = Modifier.fillMaxSize()) {
                    clipRect(
                        clipOp = ClipOp.Difference,
                        left = framePosition.left,
                        top = framePosition.top,
                        right = framePosition.right,
                        bottom = framePosition.bottom,
                    ) {
                        drawRect(Color.Black.copy(alpha = CAMERA_TRANSLUCENT_BORDER))
                    }
                }

                Image(
                    imageVector =
                        if (isTorchOn) {
                            ImageVector.vectorResource(R.drawable.ic_torch_off)
                        } else {
                            ImageVector.vectorResource(R.drawable.ic_torch_on)
                        },
                    contentDescription = stringResource(id = R.string.scan_torch_content_description),
                    modifier =
                        Modifier
                            .offset(
                                x =
                                    with(density) {
                                        (
                                            framePosition.right.toDp() -
                                                ZcashTheme.dimens.cameraTorchButton -
                                                ZcashTheme.dimens.spacingDefault
                                        )
                                    },
                                y = with(density) { framePosition.bottom.toDp() }
                            )
                            .clip(RoundedCornerShape(ZcashTheme.dimens.regularRippleEffectCorner))
                            .clickable { setIsTorchOn(!isTorchOn) }
                            .padding(ZcashTheme.dimens.spacingDefault)
                            .size(
                                width = ZcashTheme.dimens.cameraTorchButton,
                                height = ZcashTheme.dimens.cameraTorchButton
                            )
                )
            }
            ScanState.Failed -> {
                onScanStateChanged(ScanState.Failed)
            }
        }

        Box(
            modifier =
                Modifier
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
            ScanFrame(
                frameSize = frameActualSize,
                isScanning = scanState == ScanState.Scanning
            )
        }

        Box(
            modifier =
                Modifier
                    .constrainAs(bottomItems) { bottom.linkTo(parent.bottom) }
        ) {
            ScanBottomItems(
                addressValidationResult = addressValidationResult,
                onBack = onBack,
                onOpenSettings = onOpenSettings,
                scanState = scanState,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            vertical = ZcashTheme.dimens.spacingHuge,
                            horizontal = ZcashTheme.dimens.screenHorizontalSpacingBig
                        )
            )
        }
    }
}

@Composable
fun ScanFrame(
    frameSize: Int,
    isScanning: Boolean,
    modifier: Modifier = Modifier,
) {
    @Suppress("MagicNumber")
    Box(
        modifier =
            modifier
                .then(
                    Modifier
                        .size(with(LocalDensity.current) { frameSize.toDp() })
                        .background(
                            if (isScanning) {
                                Color.Transparent
                            } else {
                                ZcashTheme.colors.cameraDisabledFrameColor
                            }
                        )
                        .testTag(ScanTag.QR_FRAME)
                )
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_scan_corner),
            tint = Color.White,
            contentDescription = null,
            modifier =
                Modifier
                    .rotate(0f)
                    .align(Alignment.TopStart),
        )
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_scan_corner),
            tint = Color.White,
            contentDescription = null,
            modifier =
                Modifier
                    .rotate(90f)
                    .align(Alignment.TopEnd),
        )
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_scan_corner),
            tint = Color.White,
            contentDescription = null,
            modifier =
                Modifier
                    .rotate(-90f)
                    .align(Alignment.BottomStart),
        )
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_scan_corner),
            tint = Color.White,
            contentDescription = null,
            modifier =
                Modifier
                    .rotate(180f)
                    .align(Alignment.BottomEnd),
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Suppress("LongMethod")
@Composable
fun ScanCameraView(
    framePosition: FramePosition,
    isTorchOn: Boolean,
    onScanned: (result: String) -> Unit,
    permissionState: PermissionState,
    setScanState: (ScanState) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // We check the permission first, as the ProcessCameraProvider's emit won't be called again after
    // recomposition with the permission granted
    val cameraProviderFlow =
        if (permissionState.status.isGranted) {
            remember {
                flow<ProcessCameraProvider> { emit(ProcessCameraProvider.getInstance(context).await()) }
            }
        } else {
            null
        }

    val collectedCameraProvider = cameraProviderFlow?.collectAsState(initial = null)?.value

    val cameraController = remember { mutableStateOf<CameraControl?>(null) }
    cameraController.value?.enableTorch(isTorchOn)

    if (null == collectedCameraProvider) {
        // Show loading indicator
    } else {
        val contentDescription = stringResource(id = R.string.scan_preview_content_description)

        val imageAnalysis =
            ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

        AndroidView(
            factory = { factoryContext ->
                val previewView =
                    PreviewView(factoryContext).apply {
                        this.scaleType = PreviewView.ScaleType.FILL_CENTER
                        layoutParams =
                            ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                    }
                previewView.contentDescription = contentDescription
                val selector =
                    CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build()
                val preview =
                    androidx.camera.core.Preview.Builder().build().apply {
                        setSurfaceProvider(previewView.surfaceProvider)
                    }

                runCatching {
                    // We must unbind the use-cases before rebinding them
                    collectedCameraProvider.unbindAll()
                    cameraController.value =
                        collectedCameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            selector,
                            preview,
                            imageAnalysis
                        ).cameraControl
                }.onFailure {
                    Twig.error { "Scan QR failed in bind phase with: ${it.message}" }
                    setScanState(ScanState.Failed)
                }

                previewView
            },
            modifier =
                Modifier
                    .fillMaxSize()
                    .testTag(ScanTag.CAMERA_VIEW)
        )

        imageAnalysis.qrCodeFlow(
            context = context,
            framePosition = framePosition,
        ).collectAsState(initial = null).value?.let {
            onScanned(it)
        }
    }
}

// Using callbackFlow because QrCodeAnalyzer has a non-suspending callback which makes
// a basic flow builder not work here.
@Composable
fun ImageAnalysis.qrCodeFlow(
    context: Context,
    framePosition: FramePosition,
): Flow<String> =
    remember {
        callbackFlow {
            setAnalyzer(
                ContextCompat.getMainExecutor(context),
                QrCodeAnalyzer(
                    framePosition = framePosition,
                    onQrCodeScanned = { result ->
                        Twig.debug { "Scan result onQrCodeScanned: $result" }
                        // Note that these callbacks aren't tied to the Compose lifecycle, so they could occur
                        // after the view goes away.  Collection needs to occur within the Compose lifecycle
                        // to make this not be a problem.
                        trySend(result)
                    }
                )
            )

            awaitClose {
                // Nothing to close
            }
        }
    }
