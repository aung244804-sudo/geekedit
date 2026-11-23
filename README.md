# Edit Geek

Expo React Native Android starter for a free photo-editing app.

## Build an APK with GitHub Actions

1. Create an Expo account and an Expo access token.
2. In GitHub, open **Settings → Secrets and variables → Actions**.
3. Add a repository secret named `EXPO_TOKEN`.
4. Open **Actions → Edit Geek Android APK → Run workflow**.
5. Open the EAS build link in the workflow logs or your Expo dashboard to download the APK.

A push to `main` also triggers the workflow.

## Local development

```bash
npm install
npx expo start
```

## Current implementation status

The starter currently includes gallery selection, camera capture, navigation, a stainless-steel visual theme, and image sharing. The AI inpainting engine, prompt-based replacement, brush mask editor, offline ONNX model, persistent edit history, and language switching still need implementation and integration. The UI does not pretend those AI operations are already working.

No login, subscription, or paywall is included.
