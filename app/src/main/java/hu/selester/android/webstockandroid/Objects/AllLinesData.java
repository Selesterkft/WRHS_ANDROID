package hu.selester.android.webstockandroid.Objects;

import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AllLinesData {

    private static Map<String, String[]> params = new LinkedHashMap<>();

    public static String[] getParam(String id){
        return params.get(id);
    }

    public static Map<String, String[]> getAllParam(){
        return params;
    }

    public static void setParam(String id, String[] value){
        params.put(id,value);
    }

    public static void delParams(){
        params.clear();
    }

    public static int getLinesCount(){
        return params.size();
    }

    public static void toStringLog(){
        String str="";
        for (Map.Entry<String, String[]> entry : params.entrySet())
        {
            Log.i("MAP_LIST",entry.getKey() + " : " + Arrays.toString(entry.getValue()));
        }
    }

    public static ArrayList<String> findKeyFromMap(String find, int itemNum){
        ArrayList<String> hit = new ArrayList<>();
        if(!find.isEmpty()) {
            if (find.charAt(0) == '0') {
                try {
                    find = find.substring(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                String value = entry.getValue()[itemNum];

                if (!value.isEmpty()) {
                    if (value.substring(value.length() - 1).equals("|")) {
                        value = value.substring(0, value.length() - 1);
                    }
                    String[] bars = value.split("\\|");
                    for (int j = 0; j < bars.length; j++) {
                        try {
                            bars[j] = String.valueOf(Long.parseLong(bars[j]));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();

                        }
                        if (bars[j].equals(find)) {
                            if (!hit.contains(entry.getKey())) {
                                hit.add(entry.getKey());
                            }
                        }
                    }
                }
            }
        }
        return hit;
    }

    public static List<String[]> getAllDataList(){
        return new ArrayList<String[]>(params.values());
    }

    public static void setItemParams(String id, int position, String value){
        String[] strArray = params.get(id);
        strArray[position] = value;
        params.put(id,strArray);
    }

    public static String searchFirstItem(int position, int maxValuePosition, int curValuePosition, String value){
        ArrayList<String> findArray = findKeyFromMap(value,position);
        if(findArray.size()>0) {
            for (int i = 0; i < findArray.size(); i++) {
                String id = findArray.get(i);
                int max = Integer.parseInt(AllLinesData.getParam(id)[maxValuePosition]);
                int cur = Integer.parseInt(AllLinesData.getParam(id)[curValuePosition]);
                Log.i("ALLDATA",max+" - "+cur);
                if (cur < max) {
                    return id;
                }
            }
            //return findArray.get(findArray.size()-1);
        }
        return null;
    }

    public static int getPlaceCount(int placeNum, int curValuePosition, String findplace) {
        int count = 0;
        int valueCount = 0;
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            String value = entry.getValue()[placeNum];
            try {
                valueCount = Integer.parseInt(entry.getValue()[curValuePosition]);
            }catch (Exception e){
                e.printStackTrace();
            }

            if (!value.isEmpty()) {
                if (value.equals(findplace)) {
                    count += valueCount;
                }
            }
        }
        return count;
    }

    public static int findPosition(String id){
        int i=0;
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            if( entry.getKey().equals(id) ){
                return i;
            }
            i++;
        }
        return -1;
    }

    public static int getAllCurrentCount(int curValuePosition) {
        int count = 0;
        int valueCount = 0;
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            try {
                valueCount = Integer.parseInt(entry.getValue()[curValuePosition]);
            }catch (Exception e){
                e.printStackTrace();
            }
            count += valueCount;
        }
        return count;
    }

}
