# Edit Geek Native Android

Native Kotlin + Jetpack Compose Android editor alongside the existing Expo app.

## Implemented

- Import an image through Android's system photo picker, apply EXIF orientation, and downsample to a 2048 px maximum dimension.
- Rotate images, paint/hide/undo/clear a visual brush overlay, and save to `Pictures/EditGeek`.
- Offline brightness, contrast, and saturation with reset/apply controls.
- Prompt-based remote image editing using Pollinations `POST https://gen.pollinations.ai/v1/images/edits` with the `kontext` model.
- Personal API key input is held in screen memory only; it is not persisted or hardcoded into the APK.
- API error handling for invalid key, exhausted budget, rate limits, server errors, and invalid image responses.

## Important limitations

- Pollinations requires a valid API key and may require available account budget/quota; it is not guaranteed to be permanently free.
- AI Edit uploads the selected bitmap to Pollinations only after the user taps AI Edit. Review the provider's terms and privacy policy before sending private photos.
- The brush overlay is currently visual-only and is **not** sent as a model mask. Prompt edits affect the image as a whole; precise mask-based inpainting/object removal is not yet implemented.
- No device-specific performance testing or signed release APK is claimed.

## Build

The workflow at `.github/workflows/build-native-apk.yml` runs unit tests, Android lint, assembles a debug APK, and uploads it as a workflow artifact after successful checks.

Minimum Android version is API 29. The project targets API 35 and uses JDK 17.
