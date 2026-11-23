import { useCallback,useState } from "react";
import { FlatList,Image,Pressable,StyleSheet,Text,View } from "react-native";
import { useFocusEffect,router } from "expo-router";
import { getHistory,HistoryItem } from "../src/storage";
export default function HistoryScreen(){
 const [items,setItems]=useState<HistoryItem[]>([]);
 useFocusEffect(useCallback(()=>{getHistory().then(setItems);},[]));
 return <View style={s.page}><Text style={s.title}>RECENT EXPORTS</Text>
 <FlatList data={items} keyExtractor={x=>x.id} ListEmptyComponent={<Text style={s.empty}>No saved exports yet.</Text>}
 renderItem={({item})=><Pressable style={s.item} onPress={()=>router.push({pathname:"/editor",params:{uri:item.uri}})}>
 <Image source={{uri:item.uri}} style={s.thumb}/><View style={{flex:1}}><Text style={s.name}>{item.label}</Text><Text style={s.date}>{new Date(item.createdAt).toLocaleString()}</Text></View></Pressable>}/></View>;
}
const s=StyleSheet.create({page:{flex:1,backgroundColor:"#101214",padding:18},title:{color:"#b8bec2",fontSize:19,fontWeight:"900",letterSpacing:2,marginBottom:16},empty:{color:"#929ba1",marginTop:30,textAlign:"center"},item:{flexDirection:"row",gap:12,alignItems:"center",padding:10,borderBottomWidth:1,borderBottomColor:"#343b40"},thumb:{width:64,height:64,borderRadius:8,backgroundColor:"#202428"},name:{color:"#e2e6e8",fontWeight:"700"},date:{color:"#929ba1",fontSize:10,marginTop:5}});