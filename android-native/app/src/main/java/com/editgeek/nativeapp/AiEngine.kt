package com.editgeek.nativeapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

/** Pollinations image edit client. Brush strokes are not sent as a mask. */
class AiEngine {
    fun editImage(source: Bitmap, prompt: String, apiKey: String): Bitmap {
        require(apiKey.isNotBlank()) { "Enter your Pollinations API key first." }
        require(prompt.isNotBlank()) { "Describe the edit first." }

        val boundary = "EditGeek-" + UUID.randomUUID().toString()
        val connection = URL("https://gen.pollinations.ai/v1/images/edits").openConnection() as HttpURLConnection
        try {
            connection.requestMethod = "POST"
            connection.connectTimeout = 20_000
            connection.readTimeout = 180_000
            connection.doOutput = true
            connection.setRequestProperty("Authorization", "Bearer " + apiKey.trim())
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary)
            connection.setRequestProperty("Accept", "application/json")

            val imageBytes = ByteArrayOutputStream().use { output ->
                if (!source.compress(Bitmap.CompressFormat.JPEG, 90, output)) {
                    throw IOException("Could not encode the selected image.")
                }
                output.toByteArray()
            }

            connection.outputStream.use { output ->
                fun field(name: String, value: String) {
                    output.write(("--" + boundary + "\r\n").toByteArray(Charsets.UTF_8))
                    output.write(("Content-Disposition: form-data; name=\"" + name + "\"\r\n\r\n").toByteArray(Charsets.UTF_8))
                    output.write(value.toByteArray(Charsets.UTF_8))
                    output.write("\r\n".toByteArray(Charsets.UTF_8))
                }

                output.write(("--" + boundary + "\r\n").toByteArray(Charsets.UTF_8))
                output.write("Content-Disposition: form-data; name=\"image\"; filename=\"edit-source.jpg\"\r\n".toByteArray(Charsets.UTF_8))
                output.write("Content-Type: image/jpeg\r\n\r\n".toByteArray(Charsets.UTF_8))
                output.write(imageBytes)
                output.write("\r\n".toByteArray(Charsets.UTF_8))
                field("prompt", prompt.trim())
                field("model", "kontext")
                field("response_format", "b64_json")
                output.write(("--" + boundary + "--\r\n").toByteArray(Charsets.UTF_8))
            }

            val status = connection.responseCode
            val response = (if (status in 200..299) connection.inputStream else connection.errorStream)
                ?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
            if (status !in 200..299) {
                val detail = runCatching {
                    val json = JSONObject(response)
                    json.optJSONObject("error")?.optString("message")
                        ?: json.optString("message").takeIf { it.isNotBlank() }
                }.getOrNull()
                throw IOException(detail ?: when (status) {
                    401 -> "API key is missing or invalid. Check your Pollinations key."
                    402 -> "Pollinations account budget or quota is exhausted."
                    413 -> "Image is too large for the API. Try a smaller photo."
                    429 -> "Too many requests. Please wait and try again."
                    else -> "Image edit failed (HTTP " + status + "). Please try again."
                })
            }

            val data = JSONObject(response).optJSONArray("data")
                ?: throw IOException("The AI service returned an unexpected response.")
            if (data.length() == 0) throw IOException("The AI service returned no image.")
            val item = data.getJSONObject(0)
            val base64 = item.optString("b64_json")
            val bytes = if (base64.isNotBlank() && base64 != "null") {
                Base64.decode(base64.substringAfter("base64,", base64), Base64.DEFAULT)
            } else {
                val imageUrl = item.optString("url")
                if (imageUrl.isBlank() || imageUrl == "null") {
                    throw IOException("The AI service response did not contain an image.")
                }
                downloadImage(imageUrl, apiKey)
            }
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                ?: throw IOException("The AI result could not be decoded as an image.")
        } finally {
            connection.disconnect()
        }
    }

    private fun downloadImage(imageUrl: String, apiKey: String): ByteArray {
        val connection = URL(imageUrl).openConnection() as HttpURLConnection
        try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 20_000
            connection.readTimeout = 60_000
            connection.setRequestProperty("Authorization", "Bearer " + apiKey.trim())
            if (connection.responseCode !in 200..299) {
                throw IOException("Could not download the generated image.")
            }
            return connection.inputStream.use { it.readBytes() }
        } finally {
            connection.disconnect()
        }
    }
}
