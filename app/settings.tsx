import { useEffect, useState } from "react";
import { Alert, Pressable, StyleSheet, Switch, Text, View } from "react-native";
import { AppSettings, getSettings, saveSettings } from "../src/storage";
export default function SettingsScreen(){
 const [settings,setSettings]=useState<AppSettings>({language:"en",watermark:false});
 useEffect(()=>{getSettings().then(setSettings);},[]);
 async function update(next:AppSettings){setSettings(next);await saveSettings(next);}
 return <View style={s.page}><Text style={s.title}>EDIT GEEK SETTINGS</Text>
 <Text style={s.label}>LANGUAGE / ဘာသာစကား</Text><View style={s.row}>
 <Pressable onPress={()=>update({...settings,language:"en"})} style={[s.choice,settings.language==="en"&&s.selected]}><Text style={s.text}>English</Text></Pressable>
 <Pressable onPress={()=>update({...settings,language:"my"})} style={[s.choice,settings.language==="my"&&s.selected]}><Text style={s.text}>မြန်မာ</Text></Pressable></View>
 <View style={s.line}><View style={{flex:1}}><Text style={s.text}>Made with Edit Geek tag</Text><Text style={s.note}>Optional export branding (off by default)</Text></View><Switch value={settings.watermark} onValueChange={v=>update({...settings,watermark:v})}/></View>
 <Text style={s.label}>PRIVACY</Text><Text style={s.note}>No account or subscription. Photos stay on your device unless you choose a future remote AI service. AI processing is not connected in this build.</Text>
 <Pressable onPress={()=>Alert.alert("Edit Geek","Free to use. No login, subscription or paywall.")}><Text style={s.link}>ABOUT EDIT GEEK</Text></Pressable></View>;
}
const s=StyleSheet.create({page:{flex:1,backgroundColor:"#101214",padding:22},title:{color:"#b8bec2",fontSize:20,fontWeight:"900",letterSpacing:2,marginBottom:28},label:{color:"#b17c4d",fontSize:11,fontWeight:"800",letterSpacing:2,marginTop:18,marginBottom:10},row:{flexDirection:"row",gap:10},choice:{flex:1,padding:15,borderRadius:9,borderWidth:1,borderColor:"#687177",alignItems:"center"},selected:{backgroundColor:"#41494e",borderColor:"#c1c7ca"},text:{color:"#e2e6e8"},line:{flexDirection:"row",alignItems:"center",paddingVertical:18,borderBottomWidth:1,borderBottomColor:"#343b40"},note:{color:"#929ba1",fontSize:12,lineHeight:19,marginTop:5},link:{color:"#b17c4d",marginTop:28,fontWeight:"800"}});