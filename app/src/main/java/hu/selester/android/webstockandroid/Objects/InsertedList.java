package hu.selester.android.webstockandroid.Objects;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InsertedList {

    private  static Map<String,String> params = new LinkedHashMap<>();

    public static void setInsertElement(String Id, String value){
        params.put(Id,value);
    }

    public static String getInsertElement(String Id){
        return params.get(Id);
    }

    public static void removeInsertElement(String Id){
        params.remove(Id);
    }

    public static int getEmptyInsertCount(){
        int c = 0;
        for(Map.Entry<String,String> entry: params.entrySet() ) {
            if ( entry.getValue().equals("0")) {
                c++;
            }
        }
        return c;
    }

    public static boolean isInsert(String Id){
        if (params.get(Id) != null){
            return true;
        }
        return false;
    }

    public static boolean isEmptyInsert(String Id){
        if (params.get(Id) != null && params.get(Id).equals("0") ){
            return true;
        }
        return false;
    }

    public static void clearAll(){
        params.clear();
    }

    public static List<String> getAllElement(){
        return new ArrayList(params.keySet());
    }

    public static List<String> getAllEmptyElement(){
        List<String> elements = new ArrayList<>();
        for(Map.Entry<String,String> entry: params.entrySet() ) {
            if ( entry.getValue().equals("0")) {
                elements.add(entry.getKey());
            }
        }
        return elements;
    }

    public static void toStringLog(){
        String str="";
        for (Map.Entry<String, String> entry : params.entrySet())
        {
            Log.i("INSERT_LIST",entry.getKey() + " : " + entry.getValue());
        }
    }


}
