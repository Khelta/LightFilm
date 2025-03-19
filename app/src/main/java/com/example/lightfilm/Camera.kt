package com.example.lightfilm

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.io.ByteArrayInputStream
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.log2
import kotlin.math.pow

@Composable
fun CameraPreviewScreen(
    modifier: Modifier = Modifier,
    imageCapture: ImageCapture,
    lensFacing: Int = CameraSelector.LENS_FACING_BACK
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val preview = Preview.Builder().build()
    val previewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FIT_CENTER
        }
    }
    val cameraxSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
    LaunchedEffect(lensFacing) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraxSelector, preview, imageCapture)
        preview.surfaceProvider = previewView.surfaceProvider
    }

    Surface(
        shape = RoundedCornerShape(15),
        modifier = modifier.padding(10.dp),
        color = MaterialTheme.colorScheme.outline
    ) {
        AndroidView(
            factory = { previewView }, modifier = modifier
                .aspectRatio(3f / 4f)
                //TODO choose height independent from camera sensor
                .fillMaxHeight(0.2f)
        )
    }
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener({
                continuation.resume(cameraProvider.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }

fun onImageCaptureClick(
    imageCapture: ImageCapture,
    applicationContext: Context,
    onEVCalculated: (Double) -> Unit = {}
) {
    val callbackObject = object : ImageCapture.OnImageCapturedCallback() {
        override fun onError(error: ImageCaptureException) {
            println("error: $error")
        }

        override fun onCaptureSuccess(image: ImageProxy) {
            super.onCaptureSuccess(image)
            println(image.imageInfo)

            val bb = image.planes[0].buffer
            val buffer = ByteArray(bb.remaining())
            bb.get(buffer)
            val exposureTime = ExifInterface(ByteArrayInputStream(buffer)).getAttribute(
                ExifInterface.TAG_EXPOSURE_TIME
            )
                ?.toDouble() ?: 0.0
            val iso =
                ExifInterface(ByteArrayInputStream(buffer)).getAttribute(ExifInterface.TAG_PHOTOGRAPHIC_SENSITIVITY)
                    ?.toShort() ?: 0
            val aperture = ExifInterface(ByteArrayInputStream(buffer)).getAttribute(
                ExifInterface.TAG_F_NUMBER
            )?.toDouble() ?: 0.0

            println("Exposure time: $exposureTime\nISO: $iso\nAperture: $aperture")
            val ev = log2(100 * aperture.pow(2.0) / (iso * exposureTime))
            onEVCalculated(ev)
        }
    }
    imageCapture.takePicture(ContextCompat.getMainExecutor(applicationContext), callbackObject)
}
