package com.editgeek.nativeapp

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var prompt by remember { mutableStateOf("") }
    var showMask by remember { mutableStateOf(true) }
    var busy by remember { mutableStateOf(false) }
    val strokes = remember { mutableStateListOf<List<Offset>>() }
    val engine = remember { AiEngine() }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            scope.launch {
                val decoded = withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(uri)?.use(BitmapFactory::decodeStream)
                }
                if (decoded != null) {
                    bitmap = decoded
                    strokes.clear()
                    showMask = true
                } else {
                    Toast.makeText(context, "Could not open this image.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = Ink) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp, vertical = 16.dp),
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
                Button(onClick = { imagePicker.launch("image/*") }, modifier = Modifier.weight(1f)) {
                    Text(if (bitmap == null) "Import photo" else "Change photo")
                }
                OutlinedButton(
                    onClick = {
                        bitmap?.let { original ->
                            val matrix = Matrix().apply { postRotate(90f) }
                            bitmap = Bitmap.createBitmap(original, 0, 0, original.width, original.height, matrix, true)
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
                    .weight(1f)
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
                value = prompt,
                onValueChange = { prompt = MaskRules.normalizedPrompt(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Describe the edit") },
                placeholder = { Text("Remove the sign and refill the wall…") },
                supportingText = { Text("Up to 500 characters · AI result generation is not connected yet") },
                maxLines = 2
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        Toast.makeText(context, engine.unavailableMessage(), Toast.LENGTH_LONG).show()
                    },
                    modifier = Modifier.weight(1.2f),
                    colors = ButtonDefaults.buttonColors(containerColor = Violet, contentColor = Ink)
                ) { Text("AI Edit", fontWeight = FontWeight.Bold) }
                OutlinedButton(
                    onClick = {
                        val current = bitmap
                        if (current == null || busy) return@OutlinedButton
                        busy = true
                        scope.launch {
                            val saved = withContext(Dispatchers.IO) { saveBitmap(current) }
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
                "Offline editing tools are local. AI generation, object removal and generative refill need a verified model or a consent-based server connection; no photo is uploaded by this screen.",
                color = Muted,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.height(2.dp))
        }
    }
}

private fun saveBitmap(bitmap: Bitmap): Boolean = try {
    // The caller supplies app context through the overload below.
    false
} catch (_: Exception) {
    false
}
