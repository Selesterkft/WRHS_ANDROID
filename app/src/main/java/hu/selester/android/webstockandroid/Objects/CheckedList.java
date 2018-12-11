package hu.selester.android.webstockandroid.Objects;

import android.util.Log;

import org.apache.commons.collections.map.HashedMap;

import java.util.List;
import java.util.Map;

public class CheckedList {

    /* status:
            0 (or not in list or saved) - not changed
            1 - changed
            2 - send WS, waiting respond
            3 - sent
    */

    private  static Map<String,Integer> param = new HashedMap();

    public static int getParamItem(String key){
        if( param.get(key) == null ){
            return 0;
        }else{
            return param.get(key);
        }

    }

    public static void setParamItem(String key, int checked){
        param.put(key,checked);
    }

    public static Map<String,Integer> getParam(){
        return param;
    }
    public static void clearAllData(){
        param.clear();
    }

    public static void setSetChecked(int status){
        for (Map.Entry<String, Integer> entry : CheckedList.getParam().entrySet()) {
            if( param.get(entry.getKey()) == 2 ){
                param.put(entry.getKey(), status);
            }
        }
    }

    public static void toLogString(){
        for (Map.Entry<String, Integer> entry : CheckedList.getParam().entrySet()) {
            Log.i("TAG",entry.getKey()+" - "+entry.getValue());
        }
    }

}
