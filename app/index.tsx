import { useState } from "react";
import { Alert, Pressable, ScrollView, StyleSheet, Text, View } from "react-native";
import * as ImagePicker from "expo-image-picker";
import { router } from "expo-router";
export default function HomeScreen(){
 const [busy,setBusy]=useState(false);
 async function openGallery(){setBusy(true);try{const r=await ImagePicker.launchImageLibraryAsync({mediaTypes:["images"],allowsEditing:false,quality:1});if(!r.canceled&&r.assets[0])router.push({pathname:"/editor",params:{uri:r.assets[0].uri}});}catch(e){Alert.alert("Gallery error",e instanceof Error?e.message:"Unable to open gallery.");}finally{setBusy(false);}}
 async function openCamera(){const p=await ImagePicker.requestCameraPermissionsAsync();if(!p.granted){Alert.alert("Camera permission","Allow camera access to take a photo.");return;}const r=await ImagePicker.launchCameraAsync({quality:1});if(!r.canceled&&r.assets[0])router.push({pathname:"/editor",params:{uri:r.assets[0].uri}});}
 return <ScrollView contentContainerStyle={s.page}><View style={s.panel}>
 <Text style={s.eyebrow}>STAINLESS STEEL EDITION</Text><Text style={s.logo}>EDIT GEEK</Text><Text style={s.sub}>AI PHOTO WORKSHOP</Text><View style={s.rule}/>
 <Text style={s.heading}>CHOOSE YOUR IMAGE</Text><Text style={s.muted}>Select a photo or capture a new one to begin.</Text>
 <Pressable disabled={busy} style={s.primary} onPress={openGallery}><Text style={s.primaryText}>{busy?"OPENING…":"▣  OPEN GALLERY"}</Text></Pressable>
 <Pressable style={s.secondary} onPress={openCamera}><Text style={s.secondaryText}>◎  USE CAMERA</Text></Pressable>
 <Pressable onPress={()=>router.push("/history")}><Text style={s.settings}>▤  RECENT EXPORTS</Text></Pressable>
 <Pressable onPress={()=>router.push("/settings")}><Text style={s.settings}>⚙  SETTINGS & LANGUAGE</Text></Pressable>
 </View><Text style={s.footer}>FREE TO USE  •  NO ACCOUNT  •  NO SUBSCRIPTION</Text></ScrollView>;
}
const s=StyleSheet.create({page:{flexGrow:1,justifyContent:"center",padding:20,backgroundColor:"#101214"},panel:{borderWidth:1,borderColor:"#555d62",borderRadius:18,padding:22,backgroundColor:"#202428",shadowColor:"#000",shadowOpacity:.4,shadowRadius:14,elevation:8},eyebrow:{color:"#b17c4d",fontSize:10,letterSpacing:3,textAlign:"center"},logo:{color:"#b8bec2",fontSize:36,fontWeight:"900",letterSpacing:3,textAlign:"center",marginTop:12},sub:{color:"#858e94",fontSize:11,letterSpacing:4,textAlign:"center",marginTop:5},rule:{height:1,backgroundColor:"#596168",marginVertical:26},heading:{color:"#b8bec2",fontSize:16,fontWeight:"800",letterSpacing:2},muted:{color:"#929ba1",fontSize:13,marginTop:8,marginBottom:22},primary:{backgroundColor:"#aeb5b9",borderRadius:12,padding:18,alignItems:"center",marginBottom:12},primaryText:{color:"#171a1c",fontWeight:"900",letterSpacing:2},secondary:{borderWidth:1,borderColor:"#858e94",borderRadius:12,padding:17,alignItems:"center"},secondaryText:{color:"#b8bec2",fontWeight:"800",letterSpacing:2},settings:{color:"#b17c4d",textAlign:"center",marginTop:22,fontSize:12,letterSpacing:1},footer:{color:"#697278",fontSize:9,letterSpacing:1.5,textAlign:"center",marginTop:20}});
