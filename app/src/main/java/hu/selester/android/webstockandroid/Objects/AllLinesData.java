package hu.selester.android.webstockandroid.Objects;

import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;

import org.apache.commons.collections.map.HashedMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AllLinesData {

    private static Map<String, String[]> params = new LinkedHashMap<>();

    public static String[] getParam(String id){
        return params.get(id);
    }

    public static Map<String, String[]> getAllParam(){
        return params;
    }

    public static void insertParam(int num, String id, String[] value){
        Map<String, String[]> copy = new LinkedHashMap<>(params);
        params.clear();
        boolean puted = false;
        int count = 0;
        for (Map.Entry<String, String[]> entry : copy.entrySet())
        {
            if( num == count){
                params.put(id,value);
                puted = true;
            }
            params.put(entry.getKey(),entry.getValue());
            count++;
        }
        if( !puted ) params.put(id,value);
    }

    public static int getRowCount(String id){
        int count = 0;
        for (Map.Entry<String, String[]> entry : params.entrySet())
        {
            if( id.equals(entry.getKey()) ){
                return count;
            }
            params.put(entry.getKey(),entry.getValue());
            count++;
        }
        return 0;
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
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            Log.i("TAG",entry.getKey() + " : " + Arrays.toString(entry.getValue()));
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

    public static List<String[]> findSameItemsFromMap(String find, int itemNum, int qRefId) {
        List<String[]> result = new ArrayList<>();
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            String value = entry.getValue()[itemNum];
            if (value.equals(find)) {
                if( entry.getValue()[qRefId].equals("0") ) {
                    result.add(entry.getValue());
                }
            }
        }
        return result;
    }

    public static List<String[]> findSameUniqueItemsFromMap(String find, int itemNum, int qRefId) {
        List<String> removeIDS = new ArrayList<>();
        List<String[]> result = new ArrayList<>();
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            String value = entry.getValue()[itemNum];
            if (value.equals(find)) {
                if( entry.getValue()[qRefId].equals("0") ) {
                    result.add(entry.getValue());
                }else{
                    removeIDS.add(""+entry.getValue()[qRefId]);
                }
            }
        }

        Iterator<String[]> i = result.iterator();
        while (i.hasNext()) {
            String[] s = i.next();
            Log.i("TAG CONTAIN",Arrays.toString(s));
            if( removeIDS.contains( s[0] ) ){
                i.remove();
            }
        }
        return result;
    }


    public static List<String[]> findItemsFromMap(String find, int itemNum) {
        List<String[]> result = new ArrayList<>();
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            String value = entry.getValue()[itemNum];
            if(find != null && value != null) {
                if (value.equals(find)) {
                    result.add(entry.getValue());
                }
            }
        }
        return result;
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
                if (cur < max) {
                    return id;
                }
            }
        }
        return null;
    }

    public static int getPlaceCount(int placeNum, int curValuePosition, String findplace) {
        int count = 0;
        int valueCount = 0;
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            String value = entry.getValue()[placeNum];
            try {
                if(curValuePosition == 99999){
                    valueCount = 1;
                }else{
                    valueCount = Integer.parseInt(entry.getValue()[curValuePosition]);
                }

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

    public  static void setParamsPosition(int checkValuePosition, int insertPosition, String checkData, String insertData){
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            try {

                    if (entry.getValue()[checkValuePosition].equals(checkData)) {
                        setItemParams(entry.getKey(), insertPosition, insertData);
                    }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static List<String> getParamsPosition(int checkValuePosition, int getPosition, String checkData){
        Set<String> result = new HashSet<>();
        List<String> resultList = new ArrayList<>();
        try {
            for (Map.Entry<String, String[]> entry : params.entrySet()) {

                if (entry.getValue()[checkValuePosition].equals(checkData)) {
                    if (!entry.getValue()[getPosition].equals("")) {
                        result.add(entry.getValue()[getPosition]);
                    }
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        resultList.addAll(result);
        return resultList;
    }

    public static String getParamPosition(int checkValuePosition, int getPosition, String checkData){
        try {
            for (Map.Entry<String, String[]> entry : params.entrySet()) {

                if (entry.getValue()[checkValuePosition].equals(checkData)) {
                    if (!entry.getValue()[getPosition].equals("")) {
                        return entry.getValue()[getPosition];
                    }
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public  static Map<String,Integer> getParamsPositionMap(int checkValuePosition, int getPosition, String checkData){
        Map<String,Integer> result = new HashedMap();
        try {
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                if(entry.getValue()[checkValuePosition] != null) {
                    if (entry.getValue()[checkValuePosition].equals(checkData)) {
                        if (!entry.getValue()[getPosition].equals("")) {
                            if (result.get(entry.getValue()[getPosition]) != null) {
                                result.put(entry.getValue()[getPosition], result.get(entry.getValue()[getPosition]) + Integer.parseInt(entry.getValue()[11]));
                            } else {
                                result.put(entry.getValue()[getPosition], Integer.parseInt(entry.getValue()[11]));
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public  static Map<String,Integer> getParamsPositionMapSecond(int checkValuePosition, int getPosition, int getSecondPosition, String checkData){
        Map<String,Integer> result = new HashedMap();
        try {
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                if(entry.getValue()[checkValuePosition] != null) {
                    if (entry.getValue()[checkValuePosition].equals(checkData)) {
                        if (!entry.getValue()[getPosition].equals("")) {

                            if (result.get(entry.getValue()[getPosition]+"#"+entry.getValue()[getSecondPosition]) != null) {
                                result.put(entry.getValue()[getPosition]+"#"+entry.getValue()[getSecondPosition], result.get(entry.getValue()[getPosition]+"#"+entry.getValue()[getSecondPosition]) + Integer.parseInt(entry.getValue()[11]));
                            } else {
                                result.put(entry.getValue()[getPosition]+"#"+entry.getValue()[getSecondPosition], Integer.parseInt(entry.getValue()[11]));
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public  static List<String> getParamsMainPosition(int getPosition){
        Set<String> result = new HashSet<>();
        List<String> resultList = new ArrayList<>();
        try {
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                if(entry.getValue()[getPosition] != null) {
                    if (!entry.getValue()[getPosition].equals("")) {
                        result.add(entry.getValue()[getPosition]);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        resultList.addAll(result);
        return resultList;
    }

    public static boolean isValidateValue(int searchPosition, String searchData){
        try {
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                if( entry.getValue()[searchPosition].equals(searchData) ){
                    return true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public static List<String[]> getGroupByParam(int qGroupBy, int sumItemId, int currentId, int missingId){
        Map<String, String[]> newParams = new LinkedHashMap<>();
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            String[] data = new String[entry.getValue().length];
            for(int i=0 ; i< entry.getValue().length ;i++){
                data[i] = entry.getValue()[i];
            }
            if( newParams.get(entry.getValue()[qGroupBy]) == null ){
                data[missingId] = String.valueOf( Integer.parseInt(data[sumItemId]) - Integer.parseInt(data[currentId]) );
                if( data[currentId].equals("0") || data[currentId].equals("") ) data[missingId] = "0";
                newParams.put(entry.getValue()[qGroupBy], data);
            }else{
                try {
                    data[sumItemId] = String.valueOf( Integer.parseInt(newParams.get(entry.getValue()[qGroupBy])[sumItemId]) + Integer.parseInt(entry.getValue()[sumItemId]) );
                    data[currentId] = String.valueOf( Integer.parseInt(newParams.get(entry.getValue()[qGroupBy])[currentId]) + Integer.parseInt(entry.getValue()[currentId]) );
                    data[missingId] = String.valueOf( Integer.parseInt(data[sumItemId]) - Integer.parseInt(data[currentId]) );
                    if( data[currentId].equals("0") || data[currentId].equals("") ) data[missingId] = "0";
                    newParams.put(entry.getValue()[qGroupBy], data);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        return new ArrayList<>(newParams.values());
    }

    public static void clearDatas(){
        params.clear();
    }

    public static void delRow(String key){
        params.remove(key);
    }

    public static Map<String,String> getItemsCol(int key, int qEvidNum, String evidNum){
        Map<String,String> result = new LinkedHashMap<>();
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            if( !entry.getValue()[key].equals("") ){
                if( entry.getValue()[qEvidNum].equals(evidNum) ) {
                    result.put(entry.getValue()[key], "1");
                }
            }
        }
        return result;
    }

}