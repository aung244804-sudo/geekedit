import { useMemo, useRef, useState } from "react";
import { Alert, Image, PanResponder, Pressable, ScrollView, StyleSheet, Text, TextInput, View, useWindowDimensions } from "react-native";
import Svg, { Path } from "react-native-svg";
import { useLocalSearchParams, router } from "expo-router";
import * as Sharing from "expo-sharing";
import * as MediaLibrary from "expo-media-library";
import { addHistory } from "../src/storage";
import { runInpainting } from "../src/ai/engine";

type Stroke = { d: string; color: string; width: number };
export default function EditorScreen() {
 const {uri}=useLocalSearchParams<{uri:string}>(); const {width}=useWindowDimensions();
 const [tool,setTool]=useState<"brush"|"erase">("brush"); const [strokes,setStrokes]=useState<Stroke[]>([]);
 const [prompt,setPrompt]=useState(""); const [busy,setBusy]=useState(false); const [showOriginal,setShowOriginal]=useState(false);
 const current=useRef(""); const maskWidth=Math.max(250,width-36);
 const pan=useMemo(()=>PanResponder.create({
  onStartShouldSetPanResponder:()=>true,onMoveShouldSetPanResponder:()=>true,
  onPanResponderGrant:e=>{current.current=`M ${e.nativeEvent.locationX} ${e.nativeEvent.locationY}`;},
  onPanResponderMove:e=>{current.current+=` L ${e.nativeEvent.locationX} ${e.nativeEvent.locationY}`;},
  onPanResponderRelease:()=>{if(current.current)setStrokes(v=>[...v,{d:current.current,color:tool==="brush"?"#ff5c35":"#202428",width:tool==="brush"?22:28}]);current.current="";}
 }),[tool]);
 const maskSvg=`<svg xmlns="http://www.w3.org/2000/svg" width="${maskWidth}" height="400">${strokes.filter(x=>x.color!=="#202428").map(x=>`<path d="${x.d}" stroke="white" stroke-width="${x.width}" fill="none" stroke-linecap="round"/>`).join("")}</svg>`;
 async function exportPhoto(){if(!uri)return;try{const p=await MediaLibrary.requestPermissionsAsync();if(p.granted){await MediaLibrary.saveToLibraryAsync(uri);await addHistory({id:String(Date.now()),uri,createdAt:Date.now(),label:prompt||"Photo export"});Alert.alert("Saved","Original photo saved. AI changes are not applied.");}else if(await Sharing.isAvailableAsync())await Sharing.shareAsync(uri);}catch(e){Alert.alert("Export failed",e instanceof Error?e.message:"Please try again.");}}
 async function applyAI(){if(!uri)return;if(!strokes.some(x=>x.color!=="#202428")){Alert.alert("Paint an area","Use the brush to mark the object first.");return;}setBusy(true);try{await runInpainting({imageUri:uri,maskSvg,prompt});}catch(e){Alert.alert("AI engine unavailable",e instanceof Error?e.message:"Unable to process.");}finally{setBusy(false);}}
 return <ScrollView contentContainerStyle={s.page}>
  <View style={s.toolbar}><Pressable onPress={()=>setTool("brush")} style={[s.tool,tool==="brush"&&s.active]}><Text style={s.text}>✎ BRUSH</Text></Pressable><Pressable onPress={()=>setTool("erase")} style={[s.tool,tool==="erase"&&s.active]}><Text style={s.text}>ERASER</Text></Pressable><Pressable onPress={()=>setStrokes(v=>v.slice(0,-1))} style={s.tool}><Text style={s.text}>UNDO</Text></Pressable><Pressable onPress={()=>setStrokes([])} style={s.tool}><Text style={s.text}>CLEAR</Text></Pressable></View>
  <Pressable onPress={()=>setShowOriginal(v=>!v)}><Text style={s.compare}>{showOriginal?"SHOW MASK":"VIEW ORIGINAL"}</Text></Pressable>
  <View style={[s.canvas,{width:maskWidth,height:400}]} {...pan.panHandlers}>{uri?<Image source={{uri}} style={StyleSheet.absoluteFill} resizeMode="contain"/>:null}{!showOriginal&&<Svg width={maskWidth} height={400} style={StyleSheet.absoluteFill}>{strokes.map((p,i)=><Path key={i} d={p.d} stroke={p.color} strokeWidth={p.width} fill="none" strokeLinecap="round" strokeLinejoin="round" opacity={.75}/>)}</Svg>}</View>
  <Text style={s.hint}>Paint over the object to mark a removal mask.</Text><Text style={s.label}>PROMPT REPLACEMENT</Text>
  <TextInput value={prompt} onChangeText={setPrompt} placeholder="Describe the change…" placeholderTextColor="#899197" style={s.input} multiline/>
  <Pressable disabled={busy} onPress={applyAI} style={s.primary}><Text style={s.dark}>{busy?"PROCESSING…":"APPLY AI EDIT"}</Text></Pressable>
  <Pressable onPress={exportPhoto} style={s.secondary}><Text style={s.text}>SAVE ORIGINAL / SHARE</Text></Pressable>
  <Text style={s.hint}>AI inpainting is not connected. Export currently saves the original image, not a retouched result.</Text><Pressable onPress={()=>router.back()}><Text style={s.back}>‹ BACK</Text></Pressable>
 </ScrollView>;
}
const s=StyleSheet.create({page:{padding:18,backgroundColor:"#101214",flexGrow:1},toolbar:{flexDirection:"row",flexWrap:"wrap",gap:7},tool:{borderWidth:1,borderColor:"#687177",borderRadius:8,paddingVertical:11,paddingHorizontal:12},active:{backgroundColor:"#41494e",borderColor:"#c1c7ca"},text:{color:"#e2e6e8",fontSize:11,fontWeight:"800"},compare:{color:"#b17c4d",textAlign:"right",marginVertical:12,fontSize:11},canvas:{alignSelf:"center",backgroundColor:"#202428",borderRadius:12,overflow:"hidden"},hint:{color:"#899197",fontSize:11,lineHeight:17,marginTop:10},label:{color:"#b17c4d",fontWeight:"800",letterSpacing:2,marginTop:22,marginBottom:8},input:{minHeight:80,borderWidth:1,borderColor:"#596168",borderRadius:10,padding:12,color:"#e5e7e8",textAlignVertical:"top"},primary:{backgroundColor:"#aeb5b9",padding:16,borderRadius:10,alignItems:"center",marginTop:14},dark:{color:"#171a1c",fontWeight:"900"},secondary:{borderWidth:1,borderColor:"#929ba1",padding:15,borderRadius:10,alignItems:"center",marginTop:10},back:{color:"#b17c4d",textAlign:"center",marginTop:22}});
