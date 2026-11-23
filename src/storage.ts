import AsyncStorage from "@react-native-async-storage/async-storage";
const HISTORY_KEY="editgeek.history.v1",SETTINGS_KEY="editgeek.settings.v1";
export type HistoryItem={id:string;uri:string;createdAt:number;label:string};
export type AppSettings={language:"en"|"my";watermark:boolean};
export async function getHistory():Promise<HistoryItem[]>{try{return JSON.parse((await AsyncStorage.getItem(HISTORY_KEY))||"[]");}catch{return[];}}
export async function addHistory(item:HistoryItem){const current=await getHistory();await AsyncStorage.setItem(HISTORY_KEY,JSON.stringify([item,...current].slice(0,50)));}
export async function getSettings():Promise<AppSettings>{try{const v=await AsyncStorage.getItem(SETTINGS_KEY);return v?{language:"en",watermark:false,...JSON.parse(v)}:{language:"en",watermark:false};}catch{return{language:"en",watermark:false};}}
export async function saveSettings(settings:AppSettings){await AsyncStorage.setItem(SETTINGS_KEY,JSON.stringify(settings));}