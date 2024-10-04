package co.electriccoin.zcash.ui.screen.scan.view

import android.Manifest
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.content.ContextCompat
import cash.z.ecc.android.sdk.type.AddressType
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.design.component.SmallTopAppBar
import co.electriccoin.zcash.ui.design.component.TopAppBarBackNavigation
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.screen.scan.ScanTag
import co.electriccoin.zcash.ui.screen.scan.model.ScanState
import co.electriccoin.zcash.ui.screen.scan.util.ImageUriToQrCodeConverter
import co.electriccoin.zcash.ui.screen.scan.util.QrCodeAnalyzer
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
@Suppress("LongParameterList", "UnusedMaterial3ScaffoldPaddingParameter", "LongMethod")
fun Scan(
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onScanned: (String) -> Unit,
    onScanError: () -> Unit,
    onOpenSettings: () -> Unit,
    onScanStateChanged: (ScanState) -> Unit,
    topAppBarSubTitleState: TopAppBarSubTitleState,
    addressValidationResult: AddressType?
) = ZcashTheme(forceDarkMode = true) { // forces dark theme for this screen
    val permissionState =
        if (LocalInspectionMode.current) {
            remember {
                object : PermissionState {
                    override val permission = Manifest.permission.CAMERA
                    override val status = PermissionStatus.Granted

                    override fun launchPermissionRequest() = Unit
                }
            }
        } else {
            rememberPermissionState(
                Manifest.permission.CAMERA
            )
        }

    val (scanState, setScanState) =
        if (LocalInspectionMode.current) {
            remember {
                mutableStateOf(ScanState.Scanning)
            }
        } else {
            rememberSaveable {
                mutableStateOf(
                    if (permissionState.status.isGranted) {
                        ScanState.Scanning
                    } else {
                        ScanState.Permission
                    }
                )
            }
        }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { _ ->
        Box {
            ScanMainContent(
                addressValidationResult = addressValidationResult,
                onScanned = onScanned,
                onScanError = onScanError,
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
                subTitleState = topAppBarSubTitleState,
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(painter = painterResource(R.drawable.ic_scan_info), contentDescription = failureText)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = failureText,
                    style = ZashiTypography.textXs,
                    color = ZashiColors.Text.textPrimary,
                    fontWeight = FontWeight.Medium,
                    modifier =
                        Modifier
                            .weight(1f)
                            .testTag(ScanTag.FAILED_TEXT_STATE)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        when (scanState) {
            ScanState.Scanning, ScanState.Failed -> {
                ZashiButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onBack,
                    text = stringResource(id = R.string.scan_cancel_button)
                )
            }

            ScanState.Permission -> {
                ZashiButton(
                    onClick = onOpenSettings,
                    text = stringResource(id = R.string.scan_settings_button)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
    }
}

@Composable
private fun ScanTopAppBar(
    onBack: () -> Unit,
    showBack: Boolean,
    subTitleState: TopAppBarSubTitleState
) {
    SmallTopAppBar(
        subTitle =
            when (subTitleState) {
                TopAppBarSubTitleState.Disconnected -> stringResource(id = R.string.disconnected_label)
                TopAppBarSubTitleState.Restoring -> stringResource(id = R.string.restoring_wallet_label)
                TopAppBarSubTitleState.None -> null
            },
        navigationAction = {
            if (showBack) {
                TopAppBarBackNavigation(
                    backText = stringResource(id = R.string.back_navigation).uppercase(),
                    backContentDescriptionText = stringResource(R.string.back_navigation_content_description),
                    onBack = onBack
                )
            }
        },
        colors = ZcashTheme.colors.transparentTopAppBarColors,
    )
}

const val CAMERA_TRANSLUCENT_BORDER = 0.5f

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
@Suppress("LongMethod", "LongParameterList", "CyclomaticComplexMethod", "MagicNumber")
@Composable
private fun ScanMainContent(
    addressValidationResult: AddressType?,
    onScanned: (String) -> Unit,
    onScanError: () -> Unit,
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

    val density = LocalDensity.current

    val decreaseCutoutSizeByPx = with(density) { 3.dp.toPx() }

    // Calculate the best frame size for the current device screen
    var layoutCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    val layoutX by remember {
        derivedStateOf {
            (layoutCoordinates?.positionInParent()?.x ?: 0f) + decreaseCutoutSizeByPx / 2
        }
    }
    val layoutY by remember {
        derivedStateOf {
            (layoutCoordinates?.positionInParent()?.y ?: 0f) + decreaseCutoutSizeByPx / 2
        }
    }

    val cutoutWidth by remember {
        derivedStateOf {
            (layoutCoordinates?.size?.width ?: 0) - decreaseCutoutSizeByPx
        }
    }
    val cutoutHeight by remember {
        derivedStateOf {
            (layoutCoordinates?.size?.height ?: 0) - decreaseCutoutSizeByPx
        }
    }

    val configuration = LocalConfiguration.current

    val framePosition =
        FramePosition(
            left = layoutX,
            top = layoutY,
            right = layoutX + cutoutWidth,
            bottom = layoutY + cutoutHeight,
            screenHeight = with(density) { configuration.screenHeightDp.dp.roundToPx() },
            screenWidth = with(density) { configuration.screenWidthDp.dp.roundToPx() }
        )

    val (isTorchOn, setIsTorchOn) = rememberSaveable { mutableStateOf(false) }

    val convertImageUriToQrCode by remember { mutableStateOf(ImageUriToQrCodeConverter()) }

    val scope = rememberCoroutineScope()

    val context = LocalContext.current

    val galleryLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
            onResult = { uri ->
                uri?.let {
                    scope.launch {
                        val qrCode = convertImageUriToQrCode(context = context, uri = uri)
                        if (qrCode == null) {
                            onScanError()
                        } else {
                            onScanned(qrCode)
                        }
                    }
                }
            }
        )

    ConstraintLayout(modifier = modifier) {
        val (frame, bottomItems, topAnchor) = createRefs()

        when (scanState) {
            ScanState.Permission -> {
                // Keep initial ui state
                onScanStateChanged(ScanState.Permission)
            }

            ScanState.Scanning -> {
                onScanStateChanged(ScanState.Scanning)

                if (!LocalInspectionMode.current) {
                    ScanCameraView(
                        framePosition = framePosition,
                        isTorchOn = isTorchOn,
                        onScanned = onScanned,
                        permissionState = permissionState,
                        setScanState = setScanState,
                    )
                }

                Canvas(modifier = Modifier.fillMaxSize()) {
                    clipPath(
                        clipOp = ClipOp.Difference,
                        path =
                            Path().apply {
                                addRoundRect(
                                    roundRect =
                                        RoundRect(
                                            left = framePosition.left,
                                            top = framePosition.top,
                                            right = framePosition.right,
                                            bottom = framePosition.bottom,
                                            topLeftCornerRadius = CornerRadius(24.dp.toPx()),
                                            topRightCornerRadius = CornerRadius(24.dp.toPx()),
                                            bottomRightCornerRadius = CornerRadius(24.dp.toPx()),
                                            bottomLeftCornerRadius = CornerRadius(24.dp.toPx()),
                                        )
                                )
                            }
                    ) {
                        drawRect(Color.Black.copy(alpha = CAMERA_TRANSLUCENT_BORDER))
                    }
                }

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .offset(
                                x = 0.dp,
                                y = with(density) { framePosition.bottom.toDp() }
                            )
                            .padding(top = 36.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    ImageButton(
                        painter = painterResource(R.drawable.ic_scan_gallery),
                        contentDescription = stringResource(id = R.string.gallery_content_description),
                    ) {
                        galleryLauncher.launch("image/*")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    ImageButton(
                        painter =
                            if (isTorchOn) {
                                painterResource(R.drawable.ic_scan_torch_off)
                            } else {
                                painterResource(R.drawable.ic_scan_torch)
                            },
                        contentDescription = stringResource(id = R.string.scan_torch_content_description),
                    ) {
                        setIsTorchOn(!isTorchOn)
                    }
                }
            }

            ScanState.Failed -> {
                onScanStateChanged(ScanState.Failed)
            }
        }

        Box(
            modifier =
                Modifier
                    .constrainAs(frame) {
                        top.linkTo(topAnchor.bottom)
                        start.linkTo(parent.start, 78.dp)
                        end.linkTo(parent.end, 78.dp)
                        this
                            .height = Dimension.ratio("1:1.08") // height is 8% larger than width
                        width = Dimension.matchParent
                    }
                    .onGloballyPositioned { coordinates ->
                        layoutCoordinates = coordinates
                    },
            contentAlignment = Alignment.Center
        ) {
            ScanFrame(
                modifier = Modifier.fillMaxSize(),
                isScanning = scanState == ScanState.Scanning
            )
        }

        Spacer(
            modifier =
                Modifier
                    .fillMaxHeight(.285f)
                    .constrainAs(topAnchor) {
                        top.linkTo(parent.top)
                    },
        )

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
                            horizontal = 24.dp
                        )
            )
        }
    }
}

@Composable
private fun ImageButton(
    painter: Painter,
    contentDescription: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Image(
        painter = painter,
        contentDescription = contentDescription,
        modifier =
            modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable { onClick() }
    )
}

@Composable
fun ScanFrame(
    isScanning: Boolean,
    modifier: Modifier = Modifier,
) {
    @Suppress("MagicNumber")
    Box(
        modifier =
            modifier
                .background(
                    if (isScanning) {
                        Color.Transparent
                    } else {
                        ZcashTheme.colors.cameraDisabledFrameColor
                    }
                )
                .testTag(ScanTag.QR_FRAME)
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_scan_corner),
            tint = Color.White,
            contentDescription = null,
            modifier =
                Modifier
                    .rotate(270f)
                    .align(Alignment.TopStart),
        )
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_scan_corner),
            tint = Color.White,
            contentDescription = null,
            modifier =
                Modifier
                    .rotate(0f)
                    .align(Alignment.TopEnd),
        )
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_scan_corner),
            tint = Color.White,
            contentDescription = null,
            modifier =
                Modifier
                    .rotate(180f)
                    .align(Alignment.BottomStart),
        )
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_scan_corner),
            tint = Color.White,
            contentDescription = null,
            modifier =
                Modifier
                    .rotate(90f)
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
            framePosition = framePosition,
        ).collectAsState(initial = null).value?.let {
            onScanned(it)
        }
    }
}

// Using callbackFlow because QrCodeAnalyzer has a non-suspending callback which makes
// a basic flow builder not work here.
@Composable
fun ImageAnalysis.qrCodeFlow(framePosition: FramePosition): Flow<String> {
    val context = LocalContext.current

    return remember {
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
}

@PreviewScreens
@Composable
private fun ScanPreview() =
    ZcashTheme {
        Surface(
            color = Color.Blue,
            shape = RectangleShape,
        ) {
            Scan(
                snackbarHostState = SnackbarHostState(),
                onBack = {},
                onScanned = {},
                onScanError = {},
                onOpenSettings = {},
                onScanStateChanged = {},
                topAppBarSubTitleState = TopAppBarSubTitleState.None,
                addressValidationResult = AddressType.Invalid(),
            )
        }
    }
