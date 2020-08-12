package hu.selester.android.webstockandroid.Objects;

import android.util.Log;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TempParametersItem {

    private static Map<String, List<XD_ItemsParameters> > params = new LinkedHashMap<>();

    public static void setParamsItem(String key, XD_ItemsParameters item){
        params.get(key).add(item);
    }

    public static List<XD_ItemsParameters> getParamsItem(String key){
        return params.get(key);
    }

    public static void paramToString(){
        for (Map.Entry<String, List<XD_ItemsParameters> > entry : params.entrySet()) {
            for (int i = 0; i < entry.getValue().size(); i++ ) {
                Log.i("TAG",entry.getKey() + " : " + entry.getValue().get(i).toString() );
            }
        }
    }

}
