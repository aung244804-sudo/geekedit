import { Stack } from "expo-router";
import { StatusBar } from "expo-status-bar";
import { SafeAreaProvider } from "react-native-safe-area-context";

export default function RootLayout() {
  return (
    <SafeAreaProvider>
      <StatusBar style="light" />
      <Stack screenOptions={{
        headerStyle: { backgroundColor: "#17191b" },
        headerTintColor: "#e5e7e8",
        contentStyle: { backgroundColor: "#101214" },
      }}>
        <Stack.Screen name="index" options={{ title: "EDIT GEEK" }} />
        <Stack.Screen name="editor" options={{ title: "PHOTO WORKSHOP" }} />
        <Stack.Screen name="settings" options={{ title: "SETTINGS" }} />
      </Stack>
    </SafeAreaProvider>
  );
}
