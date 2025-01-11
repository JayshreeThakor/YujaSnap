package com.example.yujasnap.screens

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.example.yujasnap.viewmodel.ImageUriViewModel
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Composable function that displays a camera screen for capturing images.
 *
 * This screen sets up the camera, provides a live preview, and allows the user to capture an image.
 * The captured image URI is passed to the `ImageUriViewModel` and navigates to the "preview" screen.
 *
 * @param navController The navigation controller used for navigating between screens.
 * @param imageUriViewModel The ViewModel that holds the image URI for the captured image.
 */
@Composable
fun cameraScreen(navController: NavController, imageUriViewModel: ImageUriViewModel) {
    val context = LocalContext.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val previewView = remember { PreviewView(context) }

    // Store ImageCapture in remember so it persists across recompositions
    val imageCapture = remember { ImageCapture.Builder().build() }

    // Start the camera
    DisposableEffect(Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Create Preview use case
            val preview = Preview.Builder().build().apply {
                surfaceProvider = previewView.surfaceProvider
            }

            // Set up CameraSelector (back camera)
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            // Unbind any existing use cases and bind new ones
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                context as LifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )

        }, ContextCompat.getMainExecutor(context))

        onDispose {
            cameraExecutor.shutdown()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // Camera Preview
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        // Overlay Canvas to draw a semi-transparent background
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            // Draw the semi-transparent background
            drawRect(
                color = Color(0x80000000),
                size = size
            )

            val frameSize = 250.dp.toPx()
            val cornerRadius = 32.dp.toPx()
            val left = (size.width - frameSize) / 2
            val top = (size.height - frameSize) / 2

            drawRoundRect(
                color = Color.Transparent,
                topLeft = Offset(left, top),
                size = Size(frameSize, frameSize),
                cornerRadius = CornerRadius(cornerRadius, cornerRadius),
                blendMode = BlendMode.Clear
            )
        }

        // Scanner Frame in the center
        Box(
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.Center)
        ) {
            scannerFrame()
        }

        // Capture Button at the bottom
        Button(
            onClick = {
                captureImage(context, imageCapture, cameraExecutor) { uri ->
                    // Set captured image URI in ViewModel and navigate to the preview screen
                    imageUriViewModel.setImageUri(uri)
                    navController.navigate("preview")
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Text("Capture")
        }
    }
}


/**
 * Composable function that draws a scanner frame with four colored arcs at the corners.
 *
 * The scanner frame is a square area with a size of 250dp, surrounded by four distinct colored arcs
 * (red, orange, green, and blue). Each arc is drawn at a corner of the frame, giving it a unique look.
 */
@Composable
private fun scannerFrame() {
    Canvas(
        modifier = Modifier
            .size(250.dp)
            .padding(16.dp)
    ) {
        // Define the size and thickness of the arcs
        val arcSize = 60.dp.toPx()
        val arcThickness = 8.dp.toPx()

        // Define colors for each corner arc
        val red = Color(0xFFD3706B)
        val orange = Color(0xFFD88501)
        val green = Color(0xFF21A248)
        val blue = Color(0xFF4E8FF7)

        // Top-left corner arc
        drawArc(
            color = red,
            startAngle = 180f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(0f, 0f),
            size = Size(arcSize, arcSize),
            style = Stroke(width = arcThickness)
        )

        // Top-right corner arc
        drawArc(
            color = orange,
            startAngle = 270f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(size.width - arcSize, 0f),
            size = Size(arcSize, arcSize),
            style = Stroke(width = arcThickness)
        )

        // Bottom-left corner arc
        drawArc(
            color = green,
            startAngle = 90f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(0f, size.height - arcSize),
            size = Size(arcSize, arcSize),
            style = Stroke(width = arcThickness)
        )

        // Bottom-right corner arc
        drawArc(
            color = blue,
            startAngle = 0f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(size.width - arcSize, size.height - arcSize),
            size = Size(arcSize, arcSize),
            style = Stroke(width = arcThickness)
        )
    }
}


/**
 * Captures an image using the provided `ImageCapture` instance and saves it to the app's internal storage.
 *
 * After capturing the image, it is saved to a directory in the cache and then cropped. The URI of the cropped
 * image is passed to the provided `onImageCaptured` callback function. The original image file is deleted after cropping.
 *
 * @param context The context in which the image is captured, used to access the file system and UI thread.
 * @param imageCapture The `ImageCapture` instance responsible for taking the picture.
 * @param cameraExecutor The executor used to run camera operations off the main thread.
 * @param onImageCaptured The callback function that receives the URI of the captured and cropped image.
 */
private fun captureImage(
    context: Context,
    imageCapture: ImageCapture,
    cameraExecutor: Executor,
    onImageCaptured: (Uri) -> Unit
) {

    // Directory to store pictures in the app's internal cache directory
    val picturesFolder = File(context.cacheDir, "Pictures")
    if (!picturesFolder.exists()) {
        picturesFolder.mkdir() // Create the directory if it does not exist
    }

    // Generate a unique file name for the image
    val uniqueFileName = "temp_image_${System.currentTimeMillis()}.jpg"
    val tempInternalFile = File(picturesFolder, uniqueFileName)
    val outputOptions = ImageCapture.OutputFileOptions.Builder(tempInternalFile).build()

    // Take the picture and handle the result in a callback
    imageCapture.takePicture(
        outputOptions,
        cameraExecutor,
        object : ImageCapture.OnImageSavedCallback {
            /**
             * Called when the image is successfully saved.
             * The image is then cropped and the URI of the cropped image is returned via the callback.
             *
             * @param outputFileResults The results containing details of the saved image.
             */
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                // Crop the image after saving
                val croppedUri = cropImage(context, Uri.fromFile(tempInternalFile))

                // Delete the original image file after cropping
                if (tempInternalFile.exists()) {
                    tempInternalFile.delete()
                }

                // Return the cropped image URI on the main UI thread
                (context as Activity).runOnUiThread {
                    onImageCaptured(croppedUri)
                }
            }

            /**
             * Called if an error occurs during image capture.
             *
             * @param exception The exception detailing the error.
             */
            override fun onError(exception: ImageCaptureException) {
                exception.printStackTrace()
            }
        }
    )
}


/**
 * Crops an image from the provided URI and saves the cropped image to internal storage.
 *
 * The function opens the image, crops a 250x250 px section from the center of the image,
 * and saves it as a new file in the app's internal storage. It then returns the URI of the cropped image.
 *
 * @param context The context used to access the content resolver and internal storage.
 * @param uri The URI of the image to be cropped.
 * @return The URI of the cropped image saved to internal storage.
 */
private fun cropImage(context: Context, uri: Uri): Uri {
    // Open the image file as an InputStream
    val inputStream = context.contentResolver.openInputStream(uri)
    val bitmap = BitmapFactory.decodeStream(inputStream)

    // Define the crop rectangle (e.g., crop a 250x250 px section in the center)
    val cropSize = 250
    val left = (bitmap.width - cropSize) / 2
    val top = (bitmap.height - cropSize) / 2

    // Create a cropped bitmap
    val croppedBitmap = Bitmap.createBitmap(bitmap, left, top, cropSize, cropSize)

    // Define the folder where the cropped image will be saved
    val picturesFolder = File(context.filesDir, "Pictures")
    if (!picturesFolder.exists()) {
        picturesFolder.mkdir()  // Create the folder if it doesn't exist
    }

    // Generate a unique file name for the cropped image
    val uniqueFileName = "image_${System.currentTimeMillis()}.jpg"

    // Save the cropped image to a file in internal storage
    val croppedFile = File(picturesFolder, uniqueFileName)
    FileOutputStream(croppedFile).use { outputStream ->
        croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    }

    // Return the Uri pointing to the cropped image
    return Uri.fromFile(croppedFile)
}


