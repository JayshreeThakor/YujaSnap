package com.example.yujasnap.screens

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import com.example.yujasnap.viewmodel.ImageUriViewModel

/**
 * A composable function that displays the preview of the captured image. If an image URI is available,
 * it decodes the image and displays it on the screen. Otherwise, it shows a message indicating no image
 * has been captured.
 *
 * @param imageUriViewModel The ViewModel that holds the state for the image URI. It is used to
 * observe the current image URI and update the UI accordingly.
 */
@Composable
fun previewScreen(imageUriViewModel: ImageUriViewModel) {
    // Observe the imageUri state from the ViewModel
    val imageUri by imageUriViewModel.imageUri.observeAsState()

    // Box layout to center the content in the screen
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // If an image URI is available, display the image
        if (imageUri != null) {
            val bitmap = BitmapFactory.decodeFile(Uri.parse(imageUri.toString()).path)
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Captured Image",
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Show a message if no image is captured
            Text("No Image Captured")
        }
    }
}

