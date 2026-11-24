package com.editgeek.nativeapp

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.exifinterface.media.ExifInterface
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val Ink = Color(0xFF0B0D12)
private val Panel = Color(0xFF171A23)
private val Violet = Color(0xFF9B8CFF)
private val SoftWhite = Color(0xFFF2F0FF)
private val Muted = Color(0xFFAAA8BC)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { EditGeekTheme { EditorScreen() } }
    }
}

@Composable
private fun EditGeekTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Violet,
            onPrimary = Ink,
            background = Ink,
            surface = Panel,
            onSurface = SoftWhite,
            secondary = Color(0xFF63D8C5)
        ),
        content = content
    )
}

@Composable
private fun EditorScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val editorState: EditorViewModel = viewModel()
    var bitmap by editorState.bitmapState
    var originalBitmap by editorState.originalBitmapState
    var brightness by editorState.brightnessState
    var contrast by editorState.contrastState
    var saturation by editorState.saturationState
    var prompt by editorState.promptState
    var showMask by editorState.showMaskState
    var busy by editorState.busyState
    val strokes = editorState.strokes
    val engine = remember { AiEngine() }
    var apiKey by remember { mutableStateOf("") }

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val decoded = withContext(Dispatchers.IO) {
                    runCatching { decodeOrientedBitmap(context, uri) }.getOrNull()
                }
                if (decoded != null) {
                    bitmap = decoded
                    originalBitmap = decoded
                    brightness = 0f
                    contrast = 1f
                    saturation = 1f
                    strokes.clear()
                    showMask = true
                } else {
                    Toast.makeText(context, "Could not open this image. Please choose another photo.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = Ink) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).imePadding().padding(horizontal = 18.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("EDIT GEEK", color = Violet, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Black)
                    Text("AI Image Studio", color = SoftWhite, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                }
                Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(14.dp)) {
                    Text("LOCAL FIRST", modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp), color = Color(0xFF63D8C5), style = MaterialTheme.typography.labelSmall)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }, modifier = Modifier.weight(1f)) {
                    Text(if (bitmap == null) "Import photo" else "Change photo")
                }
                OutlinedButton(
                    onClick = {
                        bitmap?.let { current ->
                            val matrix = Matrix().apply { postRotate(90f) }
                            bitmap = Bitmap.createBitmap(current, 0, 0, current.width, current.height, matrix, true)
                            originalBitmap?.let { source ->
                                originalBitmap = Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
                            }
                            strokes.clear()
                        }
                    },
                    enabled = bitmap != null,
                    modifier = Modifier.weight(1f)
                ) { Text("Rotate 90°") }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Panel),
                contentAlignment = Alignment.Center
            ) {
                if (bitmap == null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("✦", color = Violet, style = MaterialTheme.typography.displayLarge)
                        Text("Your next edit starts here", color = SoftWhite, fontWeight = FontWeight.SemiBold)
                        Text("Import a photo, then paint over an object.", color = Muted, style = MaterialTheme.typography.bodySmall)
                    }
                } else {
                    Image(
                        bitmap = bitmap!!.asImageBitmap(),
                        contentDescription = "Photo being edited",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(bitmap, showMask) {
                                detectDragGestures(
                                    onDragStart = { point -> if (showMask) strokes.add(listOf(point)) },
                                    onDrag = { change, _ ->
                                        change.consume()
                                        if (showMask && strokes.isNotEmpty()) {
                                            val last = strokes.last()
                                            strokes[strokes.lastIndex] = last + change.position
                                        }
                                    }
                                )
                            }
                    ) {
                        if (showMask) {
                            strokes.forEach { points ->
                                if (points.size >= 2) {
                                    val path = Path().apply {
                                        moveTo(points.first().x, points.first().y)
                                        points.drop(1).forEach { lineTo(it.x, it.y) }
                                    }
                                    drawPath(path, color = Violet.copy(alpha = 0.82f), style = Stroke(width = 26f, cap = StrokeCap.Round))
                                }
                                points.forEach { point ->
                                    drawCircle(Violet.copy(alpha = 0.82f), radius = 13f, center = point)
                                }
                            }
                        }
                    }
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Panel),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("OFFLINE ADJUSTMENTS", color = Violet, fontWeight = FontWeight.Bold)
                    Text("Brightness  ${brightness.toInt()}", color = SoftWhite, style = MaterialTheme.typography.bodySmall)
                    Slider(value = brightness, onValueChange = { brightness = it }, valueRange = -60f..60f, enabled = bitmap != null && !busy)
                    Text("Contrast  ${(contrast * 100).toInt()}%", color = SoftWhite, style = MaterialTheme.typography.bodySmall)
                    Slider(value = contrast, onValueChange = { contrast = it }, valueRange = 0.5f..1.8f, enabled = bitmap != null && !busy)
                    Text("Saturation  ${(saturation * 100).toInt()}%", color = SoftWhite, style = MaterialTheme.typography.bodySmall)
                    Slider(value = saturation, onValueChange = { saturation = it }, valueRange = 0f..2f, enabled = bitmap != null && !busy)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = {
                                bitmap = originalBitmap
                                brightness = 0f
                                contrast = 1f
                                saturation = 1f
                            },
                            enabled = originalBitmap != null && !busy,
                            modifier = Modifier.weight(1f)
                        ) { Text("Reset") }
                        Button(
                            onClick = {
                                val source = originalBitmap ?: return@Button
                                if (busy) return@Button
                                busy = true
                                scope.launch {
                                    val result = runCatching {
                                        withContext(Dispatchers.Default) {
                                            applyImageAdjustments(source, brightness, contrast, saturation)
                                        }
                                    }.getOrNull()
                                    if (result != null) {
                                        bitmap = result
                                        Toast.makeText(context, "Adjustments applied on device.", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Could not adjust this image. Try a smaller photo.", Toast.LENGTH_LONG).show()
                                    }
                                    busy = false
                                }
                            },
                            enabled = bitmap != null && !busy,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Violet, contentColor = Ink)
                        ) { Text(if (busy) "Working…" else "Apply adjustments", fontWeight = FontWeight.Bold) }
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = { showMask = !showMask }, modifier = Modifier.weight(1f), enabled = bitmap != null) {
                    Text(if (showMask) "Hide mask" else "Show mask")
                }
                OutlinedButton(onClick = { if (strokes.isNotEmpty()) strokes.removeAt(strokes.lastIndex) }, modifier = Modifier.weight(1f), enabled = strokes.isNotEmpty()) {
                    Text("Undo stroke")
                }
                OutlinedButton(onClick = { strokes.clear() }, modifier = Modifier.weight(1f), enabled = strokes.isNotEmpty()) {
                    Text("Clear")
                }
            }

            OutlinedTextField(
                value = apiKey,
                onValueChange = { apiKey = it.trim() },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Pollinations API key") },
                placeholder = { Text("Paste your personal API key") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                supportingText = { Text("Key stays in this screen's memory and is not saved to disk.") }
            )

            OutlinedTextField(
                value = prompt,
                onValueChange = { prompt = MaskRules.limitPromptInput(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Describe the edit") },
                placeholder = { Text("Remove the sign and refill the wall…") },
                supportingText = { Text("${prompt.codePointCount(0, prompt.length)}/500 characters · Prompt-based AI edit") },
                maxLines = 2
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        val validatedPrompt = MaskRules.normalizedPrompt(prompt)
                        val source = bitmap
                        when {
                            source == null -> Toast.makeText(context, "Import a photo first.", Toast.LENGTH_SHORT).show()
                            validatedPrompt.isBlank() -> Toast.makeText(context, "Describe the edit first.", Toast.LENGTH_SHORT).show()
                            apiKey.isBlank() -> Toast.makeText(context, "Enter your Pollinations API key first.", Toast.LENGTH_LONG).show()
                            busy -> Unit
                            else -> {
                                busy = true
                                scope.launch {
                                    try {
                                        val result = withContext(Dispatchers.IO) {
                                            engine.editImage(source, validatedPrompt, apiKey)
                                        }
                                        bitmap = result
                                        originalBitmap = result
                                        brightness = 0f
                                        contrast = 1f
                                        saturation = 1f
                                        strokes.clear()
                                        Toast.makeText(context, "AI edit complete. Review it before saving.", Toast.LENGTH_LONG).show()
                                    } catch (error: Exception) {
                                        val message = error.message?.take(220) ?: "AI edit failed. Check your connection and API key."
                                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                    } finally {
                                        busy = false
                                    }
                                }
                            }
                        }
                    },
                    enabled = !busy,
                    modifier = Modifier.weight(1.2f),
                    colors = ButtonDefaults.buttonColors(containerColor = Violet, contentColor = Ink)
                ) { Text(if (busy) "Editing…" else "AI Edit", fontWeight = FontWeight.Bold) }
                OutlinedButton(
                    onClick = {
                        val current = bitmap
                        if (current == null || busy) return@OutlinedButton
                        busy = true
                        scope.launch {
                            val saved = withContext(Dispatchers.IO) { saveBitmap(context, current) }
                            busy = false
                            Toast.makeText(
                                context,
                                if (saved) "Image saved to Pictures/EditGeek." else "Save failed. Please try again.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = bitmap != null && !busy
                ) { Text(if (busy) "Saving…" else "Save image") }
            }

            Text(
                "Brightness, contrast and saturation work offline. Tapping AI Edit uploads the current photo to Pollinations for prompt-based editing and may use your account quota. The purple brush overlay is visual-only in this version; it is not sent as an inpainting mask. Do not submit private photos unless you accept the provider's terms.",
                color = Muted,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.height(2.dp))
        }
    }
}

private fun saveBitmap(context: android.content.Context, bitmap: Bitmap): Boolean {
    var pendingUri: android.net.Uri? = null
    return try {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "EditGeek-${System.currentTimeMillis()}.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/EditGeek")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            ?: return false
        pendingUri = uri
        val written = context.contentResolver.openOutputStream(uri)?.use { output ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
        } ?: false
        if (!written) {
            context.contentResolver.delete(uri, null, null)
            pendingUri = null
            false
        } else {
            val completed = ContentValues().apply { put(MediaStore.Images.Media.IS_PENDING, 0) }
            val updated = context.contentResolver.update(uri, completed, null, null)
            if (updated > 0) {
                pendingUri = null
                true
            } else {
                context.contentResolver.delete(uri, null, null)
                pendingUri = null
                false
            }
        }
    } catch (_: Exception) {
        pendingUri?.let { uri -> runCatching { context.contentResolver.delete(uri, null, null) } }
        false
    }
}

/** Decode a user-selected image with EXIF orientation and bounded memory use. */
private fun decodeOrientedBitmap(context: android.content.Context, uri: Uri): Bitmap? {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        ?: return null
    if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

    var sample = 1
    while (bounds.outWidth / sample > 2048 || bounds.outHeight / sample > 2048) {
        sample *= 2
    }
    val options = BitmapFactory.Options().apply { inSampleSize = sample }
    val decoded = context.contentResolver.openInputStream(uri)?.use {
        BitmapFactory.decodeStream(it, null, options)
    } ?: return null

    val orientation = context.contentResolver.openInputStream(uri)?.use {
        ExifInterface(it).getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
    } ?: ExifInterface.ORIENTATION_NORMAL
    val matrix = Matrix()
    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
        ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
        ExifInterface.ORIENTATION_TRANSPOSE -> {
            matrix.postRotate(90f)
            matrix.postScale(-1f, 1f)
        }
        ExifInterface.ORIENTATION_TRANSVERSE -> {
            matrix.postRotate(270f)
            matrix.postScale(-1f, 1f)
        }
    }
    return if (matrix.isIdentity) decoded else {
        Bitmap.createBitmap(decoded, 0, 0, decoded.width, decoded.height, matrix, true)
            .also { if (it !== decoded) decoded.recycle() }
    }
}
