import { useLocalSearchParams, router } from "expo-router";
import { useState } from "react";
import { Alert, Image, Pressable, ScrollView, StyleSheet, Text, TextInput, View } from "react-native";
import * as Sharing from "expo-sharing";

export default function EditorScreen() {
  const { uri } = useLocalSearchParams<{ uri: string }>();
  const [prompt, setPrompt] = useState("");
  const [busy, setBusy] = useState(false);

  async function shareImage() {
    if (!uri) return;
    if (await Sharing.isAvailableAsync()) await Sharing.shareAsync(uri);
    else Alert.alert("Sharing unavailable", "Sharing is not supported on this device.");
  }
  function explainAI() {
    Alert.alert("AI engine not connected yet", "This starter build can open and share photos. Object removal and prompt-based AI editing need a configured inference server or bundled on-device model.");
  }

  return (
    <ScrollView contentContainerStyle={s.page}>
      {uri ? <Image source={{ uri }} style={s.photo} resizeMode="contain" /> : <Text style={s.text}>No image selected</Text>}
      <Text style={s.label}>PROMPT EDIT</Text>
      <TextInput value={prompt} onChangeText={setPrompt} placeholder="Describe what you want to change…" placeholderTextColor="#899197" style={s.input} multiline />
      <Pressable style={s.button} onPress={explainAI}><Text style={s.dark}>REMOVE OBJECT / APPLY AI</Text></Pressable>
      <Pressable style={s.outline} onPress={shareImage}><Text style={s.text}>EXPORT / SHARE IMAGE</Text></Pressable>
      <Pressable onPress={() => router.back()}><Text style={s.back}>‹  BACK TO GALLERY</Text></Pressable>
      <Text style={s.note}>AI editing is not active until an inference engine is configured. No fake processing is shown.</Text>
    </ScrollView>
  );
}
const s=StyleSheet.create({
 page:{padding:18,backgroundColor:"#101214",flexGrow:1},
 photo:{width:"100%",height:390,backgroundColor:"#202428",borderRadius:14},
 label:{color:"#b17c4d",fontWeight:"800",letterSpacing:2,marginTop:22,marginBottom:8},
 input:{minHeight:88,borderWidth:1,borderColor:"#596168",borderRadius:10,padding:12,color:"#e5e7e8",textAlignVertical:"top"},
 button:{backgroundColor:"#aeb5b9",padding:16,borderRadius:10,alignItems:"center",marginTop:14},
 dark:{color:"#171a1c",fontWeight:"900",letterSpacing:1},
 outline:{borderWidth:1,borderColor:"#929ba1",padding:15,borderRadius:10,alignItems:"center",marginTop:10},
 text:{color:"#d7dcdf",textAlign:"center"},
 back:{color:"#b17c4d",textAlign:"center",marginTop:22},
 note:{color:"#858e94",fontSize:11,textAlign:"center",marginTop:22,lineHeight:17}
});
