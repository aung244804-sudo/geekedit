export type InpaintRequest = {
  imageUri: string;
  maskSvg: string;
  prompt?: string;
};
export type EngineResult = { uri: string };

const API_URL = process.env.EXPO_PUBLIC_AI_API_URL?.trim();

/**
 * Remote engine adapter. The app never silently sends photos anywhere:
 * configure EXPO_PUBLIC_AI_API_URL only after deploying a secure inference API
 * and showing an explicit consent screen to users.
 */
export async function runInpainting(_request: InpaintRequest): Promise<EngineResult> {
  if (!API_URL) {
    throw new Error("AI engine is not configured. The selected photo stays on your device. Connect a self-hosted inpainting API to enable AI edits.");
  }
  throw new Error("The configured AI endpoint adapter is not implemented yet. Add your server's documented upload contract before enabling remote photo uploads.");
}
