package com.example.beeranking.utilis.imageHandler

import android.content.Context
import android.net.Uri
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import java.io.File

/**
 * A utility class to handle image selection (Camera/Gallery) and Cropping.
 * Must be initialized as a property in a Fragment or Activity to register launchers.
 */
class ImageHandler(
    caller: ActivityResultCaller,
    context: Context,
    private val onImageResult: (Uri?) -> Unit
) {
    private val appContext: Context = context.applicationContext
    private var latestTmpUri: Uri? = null

    data class CropConfig(val x: Int, val y: Int)
    private var pendingConfig: CropConfig? = null
    private val defaultCropConfig: CropConfig = CropConfig(1 ,1)
    enum class ImageSource { CAMERA, GALLERY }

    private val cropImage = caller.registerForActivityResult(CropImageContract()) { result ->
        pendingConfig = null

        if (result.isSuccessful) {
            onImageResult(result.uriContent)
        } else {
            onImageResult(null)
        }
    }

    private val takePictureLauncher = caller.registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            latestTmpUri?.let { uri -> startCrop(uri, this.pendingConfig ?: defaultCropConfig) }
        }
    }

    private val pickImageLauncher = caller.registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { startCrop(it, this.pendingConfig ?: defaultCropConfig) }
    }

    private fun takePhoto() {
        getTmpFileUri().let { uri ->
            latestTmpUri = uri
            takePictureLauncher.launch(uri)
        }
    }

    private fun pickFromGallery() {
        pickImageLauncher.launch("image/*")
    }

    fun getImage(source: ImageSource, cropConfig: CropConfig = defaultCropConfig) {
        this.pendingConfig = cropConfig

        when(source) {
            ImageSource.CAMERA -> takePhoto()
            ImageSource.GALLERY -> pickFromGallery()
        }
    }

    private fun startCrop(uri: Uri, cropConfig: CropConfig) {
        val cropOptions = CropImageOptions().apply {
            guidelines = CropImageView.Guidelines.ON
            aspectRatioX = cropConfig.x
            aspectRatioY = cropConfig.y
            fixAspectRatio = true
            cropShape = CropImageView.CropShape.RECTANGLE
        }

        cropImage.launch(CropImageContractOptions(uri, cropOptions))
    }

    private fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile("tmp_image_file", ".png", appContext.cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }
        return FileProvider.getUriForFile(appContext, "${appContext.packageName}.provider", tmpFile)
    }
}
