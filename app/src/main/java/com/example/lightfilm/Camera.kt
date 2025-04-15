package com.example.lightfilm

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
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
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
fun CameraPreviewScreen(
    modifier: Modifier = Modifier,
    imageCapture: ImageCapture,
    lensFacing: Int = CameraSelector.LENS_FACING_BACK,
    linearZoom: Float
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
    LaunchedEffect(lensFacing, linearZoom) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        val camera =
            cameraProvider.bindToLifecycle(lifecycleOwner, cameraxSelector, preview, imageCapture)
        preview.surfaceProvider = previewView.surfaceProvider
        camera.cameraControl.setLinearZoom(linearZoom)
        //TODO Possibility to remove lag when changing zoom?
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
    onEVCalculated: (Double, Double, Double, String) -> Unit
) {
    val callbackObject = object : ImageCapture.OnImageCapturedCallback() {
        override fun onError(error: ImageCaptureException) {
            println("error: $error")
        }

        override fun onCaptureSuccess(image: ImageProxy) {
            super.onCaptureSuccess(image)
            val imageInfo = image.imageInfo
            println("imageInfo: $imageInfo")

            val bb = image.planes[0].buffer
            val buffer = ByteArray(bb.remaining())
            bb.get(buffer)
            val exposureTime = ExifInterface(ByteArrayInputStream(buffer)).getAttribute(
                ExifInterface.TAG_EXPOSURE_TIME
            )
                ?.toDouble() ?: 0.0
            val iso =
                ExifInterface(ByteArrayInputStream(buffer)).getAttribute(ExifInterface.TAG_PHOTOGRAPHIC_SENSITIVITY)
                    ?.toInt() ?: 0
            val aperture = ExifInterface(ByteArrayInputStream(buffer)).getAttribute(
                ExifInterface.TAG_F_NUMBER
            )?.toDouble() ?: 0.0

            val fileName = LocalDateTime.now().toString() + ".png"
            val file = File(applicationContext.filesDir, fileName)
            FileOutputStream(file).use { outputstream ->
                val rotationMatrix =
                    Matrix().apply { postRotate(imageInfo.rotationDegrees.toFloat()) }
                val rotatedBitmap = Bitmap.createBitmap(
                    image.toBitmap(),
                    0,
                    0,
                    image.width,
                    image.height,
                    rotationMatrix,
                    true
                )
                rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputstream)
            }

            println("Exposure time: $exposureTime\nISO: $iso\nAperture: $aperture")
            val ev = calculateSimpleEV(aperture, exposureTime)
            onEVCalculated(ev, aperture, exposureTime, fileName)

            // TODO Save image metadata and connect to film and picture
            image.close()
        }
    }
    imageCapture.takePicture(ContextCompat.getMainExecutor(applicationContext), callbackObject)
}
