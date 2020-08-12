package hu.selester.android.webstockandroid.Objects;

import java.util.HashMap;
import java.util.Map;

public class SessionClass {
    private static Map<String,String> params = new HashMap<>();

    public static String getParam(String key){
        return params.get(key);
    }

    public static void setParam(String key, String value){
        params.put(key,value);
    }
}
