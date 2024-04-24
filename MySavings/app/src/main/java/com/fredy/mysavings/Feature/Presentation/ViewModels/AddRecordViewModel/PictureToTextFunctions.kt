package com.fredy.mysavings.Feature.Presentation.ViewModels.AddRecordViewModel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.core.content.FileProvider
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import java.io.File
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Other necessary imports
fun createImageUri(context: Context): Uri {
    val timeStamp = SimpleDateFormat(
        "yyyy_MM_dd_HH_mm_ss", Locale.getDefault()
    ).format(
        Date()
    )
    val imageFileName = "JPEG_${timeStamp}_"
    val imageFile = createImageFile(
        context, imageFileName
    )

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        imageFile
    )
}

fun createImageFile(
    context: Context,
    imageFileName: String,
    environment: String? = null
): File {
    val storageDir = context.getExternalFilesDir(
        environment
    )
    return File.createTempFile(
        imageFileName, ".jpg", storageDir
    )
}

fun detectTextFromImage(
    context: Context,
    uri: Uri,
    onSuccess: (String) -> Unit,
    onFailure: (String) -> Unit
) {
    val firebaseImage = FirebaseVisionImage.fromFilePath(
        context,
        uri
    )
    val recognizer = FirebaseVision.getInstance().onDeviceTextRecognizer
    val resultTask = recognizer.processImage(
        firebaseImage
    )

    resultTask.addOnSuccessListener { result ->
        val text = result.text
        onSuccess(text)
    }.addOnFailureListener { e ->
        e.printStackTrace()
        val error = "Error: ${e.message}"
        onFailure(error)
    }
}

fun detectTextFromImage(
    imageBitmap: ImageBitmap,
    onSuccess: (String) -> Unit,
    onFailure: (String) -> Unit
) {
    val firebaseImage = FirebaseVisionImage.fromBitmap(
        imageBitmap.asAndroidBitmap()
    )
    val recognizer = FirebaseVision.getInstance().onDeviceTextRecognizer
    val resultTask = recognizer.processImage(
        firebaseImage
    )

    resultTask.addOnSuccessListener { result ->
        // Text recognition succeeded
        val text = result.text
        onSuccess(text)
    }.addOnFailureListener { e ->
        // Text recognition failed, handle the error
        e.printStackTrace()
        val error = "Error: ${e.message}"
        onFailure(error)
    }
}

fun saveImageBitmapToSpecificUri(context: Context, imageBitmap: ImageBitmap, desiredUri: Uri) {
    val bitmap = imageBitmap.asAndroidBitmap()
    try {
        val outputStream: OutputStream? = context.contentResolver.openOutputStream(desiredUri)
        outputStream.use { stream ->
            stream?.let {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                    it
                )
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
