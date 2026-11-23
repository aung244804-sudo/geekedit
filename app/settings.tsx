import { StyleSheet, Text, View } from "react-native";
export default function SettingsScreen() {
  return <View style={s.page}>
    <Text style={s.title}>EDIT GEEK SETTINGS</Text>
    <Text style={s.text}>Language: English / မြန်မာ</Text>
    <Text style={s.text}>Account: Not required</Text>
    <Text style={s.text}>Subscription: None</Text>
    <Text style={s.note}>AI inference settings will appear when an editing engine is connected.</Text>
  </View>;
}
const s=StyleSheet.create({
 page:{flex:1,backgroundColor:"#101214",padding:22},
 title:{color:"#b8bec2",fontSize:20,fontWeight:"900",letterSpacing:2,marginBottom:24},
 text:{color:"#d7dcdf",paddingVertical:12,borderBottomWidth:1,borderBottomColor:"#343b40"},
 note:{color:"#858e94",marginTop:24,lineHeight:20}
});
