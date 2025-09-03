package com.example.artgallary

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.io.File
import android.util.Log
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject



@Composable
fun AddScreen() {
    val context = LocalContext.current

    // ✅ 1. Permission launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // ✅ 2. Ask permission when screen opens
    LaunchedEffect(Unit) {
        launcher.launch(Manifest.permission.CAMERA)
    }

    // ✅ 3. Show preview + capture
    CameraPreviewWithCapture()
}

@Composable
fun CameraPreviewWithCapture() {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

    Box(modifier = Modifier.fillMaxSize()) {

        // 📷 Camera Preview
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()

                    val preview = Preview.Builder().build()
                    val selector = CameraSelector.DEFAULT_BACK_CAMERA
                    val capture = ImageCapture.Builder().build()

                    imageCapture = capture

                    preview.setSurfaceProvider(previewView.surfaceProvider)

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            selector,
                            preview,
                            capture
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, ContextCompat.getMainExecutor(ctx))

                previewView
            },
            modifier = Modifier.fillMaxSize()
        )
        // Capture Button

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp) // outer circle
                    .padding(4.dp)
                    .border(4.dp, Color.White, CircleShape)
                    .clickable {
                        val photoFile = File(
                            context.externalMediaDirs.first(),
                            "${System.currentTimeMillis()}.jpg"
                        )

                        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                        imageCapture?.takePicture(
                            outputOptions,
                            ContextCompat.getMainExecutor(context),
                            object : ImageCapture.OnImageSavedCallback {
                                override fun onError(exc: ImageCaptureException) {
                                    Toast.makeText(context, "Capture failed: ${exc.message}", Toast.LENGTH_SHORT).show()
                                }

                                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                    val savedUri = Uri.fromFile(photoFile)
                                    Toast.makeText(
                                        context,
                                        "Photo saved: $savedUri",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    // Upload to Cloudinary
                                    UploadToCloudinary(context, savedUri)


                                }
                            }
                        )
                    }
                    .background(Color.White, CircleShape)
            )
        }

    }
}



fun UploadToCloudinary(context: android.content.Context, imageUri: Uri) {
    val firestore = FirebaseFirestore.getInstance()
    val cloudName = "drleesn9c"       // e.g. ""
    val uploadPreset = "art gallary" // e.g. ""

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val file = File(imageUri.path!!)
            val client = OkHttpClient()

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "file", file.name,
                    file.asRequestBody("image/*".toMediaTypeOrNull())
                )
                .addFormDataPart("upload_preset", uploadPreset)
                .build()

            val request = Request.Builder()
                .url("https://api.cloudinary.com/v1_1/$cloudName/image/upload")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            val json = JSONObject(responseBody ?: "{}")
            val imageUrl = json.optString("secure_url")

            if (imageUrl.isNotEmpty()) {
                firestore.collection("posts").add(mapOf("imageUrl" to imageUrl))
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(context, "Uploaded & saved!", Toast.LENGTH_SHORT).show()
                }
            }

        } catch (e: Exception) {
            Log.e("Cloudinary", "Upload failed: ${e.message}")
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(context, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}




