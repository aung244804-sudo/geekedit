import { useState } from "react";
import { Alert, Image, Pressable, ScrollView, StyleSheet, Text, View } from "react-native";
import * as ImagePicker from "expo-image-picker";
import { router } from "expo-router";

const steel = "#b8bec2";
export default function HomeScreen() {
  const [photo, setPhoto] = useState<string | null>(null);

  async function choosePhoto() {
    const result = await ImagePicker.launchImageLibraryAsync({
      mediaTypes: ["images"], allowsEditing: false, quality: 1
    });
    if (!result.canceled && result.assets[0]) {
      setPhoto(result.assets[0].uri);
      router.push({ pathname: "/editor", params: { uri: result.assets[0].uri } });
    }
  }
  async function takePhoto() {
    const permission = await ImagePicker.requestCameraPermissionsAsync();
    if (!permission.granted) {
      Alert.alert("Camera permission", "Allow camera access in Settings to take a photo.");
      return;
    }
    const result = await ImagePicker.launchCameraAsync({ quality: 1 });
    if (!result.canceled && result.assets[0]) {
      setPhoto(result.assets[0].uri);
      router.push({ pathname: "/editor", params: { uri: result.assets[0].uri } });
    }
  }

  return (
    <ScrollView contentContainerStyle={s.page}>
      <View style={s.panel}>
        <Text style={s.eyebrow}>STAINLESS STEEL EDITION</Text>
        <Text style={s.logo}>EDIT GEEK</Text>
        <Text style={s.sub}>AI PHOTO WORKSHOP</Text>
        <View style={s.rule} />
        <Text style={s.heading}>CHOOSE YOUR IMAGE</Text>
        <Text style={s.muted}>Select a photo or capture a new one to begin.</Text>
        <Pressable style={s.primary} onPress={choosePhoto}><Text style={s.primaryText}>▣  OPEN GALLERY</Text></Pressable>
        <Pressable style={s.secondary} onPress={takePhoto}><Text style={s.secondaryText}>◎  USE CAMERA</Text></Pressable>
        <Pressable onPress={() => router.push("/settings")}><Text style={s.settings}>⚙  SETTINGS & LANGUAGE</Text></Pressable>
      </View>
      <Text style={s.footer}>FREE TO USE  •  NO ACCOUNT  •  NO SUBSCRIPTION</Text>
    </ScrollView>
  );
}
const s = StyleSheet.create({
  page:{flexGrow:1,justifyContent:"center",padding:20,backgroundColor:"#101214"},
  panel:{borderWidth:1,borderColor:"#555d62",borderRadius:18,padding:22,backgroundColor:"#202428",shadowColor:"#000",shadowOpacity:.4,shadowRadius:14,elevation:8},
  eyebrow:{color:"#b17c4d",fontSize:10,letterSpacing:3,textAlign:"center"},
  logo:{color:steel,fontSize:36,fontWeight:"900",letterSpacing:3,textAlign:"center",marginTop:12},
  sub:{color:"#858e94",fontSize:11,letterSpacing:4,textAlign:"center",marginTop:5},
  rule:{height:1,backgroundColor:"#596168",marginVertical:26},
  heading:{color:steel,fontSize:16,fontWeight:"800",letterSpacing:2},
  muted:{color:"#929ba1",fontSize:13,marginTop:8,marginBottom:22},
  primary:{backgroundColor:"#aeb5b9",borderRadius:12,padding:18,alignItems:"center",marginBottom:12},
  primaryText:{color:"#171a1c",fontWeight:"900",letterSpacing:2},
  secondary:{borderWidth:1,borderColor:"#858e94",borderRadius:12,padding:17,alignItems:"center"},
  secondaryText:{color:steel,fontWeight:"800",letterSpacing:2},
  settings:{color:"#b17c4d",textAlign:"center",marginTop:25,fontSize:12,letterSpacing:1},
  footer:{color:"#697278",fontSize:9,letterSpacing:1.5,textAlign:"center",marginTop:20}
});
