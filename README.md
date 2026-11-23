# Edit Geek — Android

Expo React Native project for a free photo editing app.

## Build APK
1. Open **Actions** in this repository.
2. Select **Edit Geek Android APK**.
3. Tap **Run workflow** on the main branch.
4. When the workflow succeeds, download **edit-geek-android-apk** from its Artifacts section.

The workflow builds a release APK directly with Gradle on GitHub Actions. No Expo token or paid EAS service is required.

## Local development
```bash
npm install
npx expo start
```

## Current status
Implemented: photo gallery picker, camera capture, brush/eraser mask overlay, undo/clear, prompt field, local settings, history list, and save/share of the original photo.

Not yet implemented: an actual inpainting model/server, applying the mask to a returned AI result, real before/after comparison, and optional watermark compositing. The AI adapter intentionally reports unavailable instead of pretending to edit. No login, paywall, or subscription is present.
