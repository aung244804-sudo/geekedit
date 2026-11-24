# Edit Geek Native Android

This is a native Kotlin + Jetpack Compose Android implementation alongside the existing Expo app. It does not replace or delete the current app.

## Current native scope

- Import an image through Android's system photo picker.
- Rotate the selected bitmap by 90 degrees.
- Paint, hide, undo, and clear a visual mask overlay.
- Save the current bitmap to Pictures/EditGeek.
- Provide an explicit AI-unavailable state; no remote upload is performed by the placeholder AI engine.

## Not yet implemented

The local diffusion/inpainting model and real object removal, generative refill, image generation, object replacement, and remote inference API are not implemented in this first native scaffold. Do not present the mask overlay as an AI-edited result. A model/runtime and a documented, consent-based inference contract must be integrated and benchmarked before those features can be enabled.

## Build

The GitHub Actions workflow at `.github/workflows/build-native-apk.yml` builds and tests the project in `android-native/`. It publishes a debug APK as a workflow artifact after successful checks.

Minimum Android version is API 29. The project targets Android 15 / API 35 and uses JDK 17.
