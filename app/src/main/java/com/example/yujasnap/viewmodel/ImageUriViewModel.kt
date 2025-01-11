package com.example.yujasnap.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
/**
 * ViewModel that holds and manages the URI of an image.
 *
 * This ViewModel exposes a `LiveData` object to provide the image URI to the UI,
 * and provides a method to update the image URI.
 */
class ImageUriViewModel : ViewModel() {
    // MutableLiveData for holding the image URI.
    private val _imageUri = MutableLiveData<Uri?>()
    /**
     * LiveData that observes the image URI.
     *
     * The UI can observe this property to get updates to the image URI.
     */
    val imageUri: LiveData<Uri?> = _imageUri

    /**
     * Updates the image URI.
     *
     * This method is called to set the new image URI in the ViewModel.
     *
     * @param uri The new image URI to set.
     */
    fun setImageUri(uri: Uri) {
        _imageUri.value = uri
    }
}