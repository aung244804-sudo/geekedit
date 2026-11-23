import AsyncStorage from "@react-native-async-storage/async-storage";

const HISTORY_KEY = "editgeek.history.v1";
const SETTINGS_KEY = "editgeek.settings.v1";
export type HistoryItem = { id: string; uri: string; createdAt: number; label: string };
export type AppSettings = { language: "en" | "my"; watermark: boolean };

export async function getHistory(): Promise<HistoryItem[]> {
  try { return JSON.parse((await AsyncStorage.getItem(HISTORY_KEY)) || "[]"); }
  catch { return []; }
}
export async function addHistory(item: HistoryItem) {
  const current = await getHistory();
  await AsyncStorage.setItem(HISTORY_KEY, JSON.stringify([item, ...current].slice(0, 50)));
}
export async function getSettings(): Promise<AppSettings> {
  try {
    const value = await AsyncStorage.getItem(SETTINGS_KEY);
    return value ? { watermark: false, language: "en", ...JSON.parse(value) } : { language: "en", watermark: false };
  } catch { return { language: "en", watermark: false }; }
}
export async function saveSettings(settings: AppSettings) {
  await AsyncStorage.setItem(SETTINGS_KEY, JSON.stringify(settings));
}
