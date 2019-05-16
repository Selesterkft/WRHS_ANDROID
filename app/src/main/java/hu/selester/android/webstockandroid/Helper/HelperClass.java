package hu.selester.android.webstockandroid.Helper;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.se.omapi.SEService;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import hu.selester.android.webstockandroid.Database.SelesterDatabase;
import hu.selester.android.webstockandroid.Database.Tables.SessionTemp;
import hu.selester.android.webstockandroid.Dialogs.MessageDialog;
import hu.selester.android.webstockandroid.Fragments.MovesSubTableFragment;
import hu.selester.android.webstockandroid.MainActivity;
import hu.selester.android.webstockandroid.Objects.AllLinesData;
import hu.selester.android.webstockandroid.Objects.CheckedList;
import hu.selester.android.webstockandroid.Objects.InsertedList;
import hu.selester.android.webstockandroid.Objects.MessageBoxSettingsObject;
import hu.selester.android.webstockandroid.Objects.SessionClass;
import hu.selester.android.webstockandroid.R;
import hu.selester.android.webstockandroid.Threads.ChangeStatusThread;
import it.sephiroth.android.library.tooltip.Tooltip;

public class HelperClass {

    public static final String SelexpedVersion="V01";

    public static String getAndroidID(Context context){
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static void loadFragment(FragmentActivity activity, Fragment f){
        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragments,f).addToBackStack("app").commit();

    }

    public static String intToTime(int i){
        int hour=(i/4)+8;
        int min=(i%4)*15;
        String hourStr="";
        String minStr="";
        if(hour<10){hourStr="0"+hour;}else{hourStr=String.valueOf(hour);}
        if(min<10){minStr="0"+min;}else{minStr=String.valueOf(min);}
        return hourStr+":"+minStr;
    }

    public static int timeToInt(String time){
        String hourStr = time.substring(0,2);
        String minStr = time.substring(3,5);
        int hour=Integer.parseInt(hourStr);
        int min=Integer.parseInt(minStr);
        int i=((hour-8) * 4) + (min/15);
        return i;
    }

    public static int timeToInt60(String time){
        String hourStr = time.substring(0,2);
        String minStr = time.substring(3,5);
        int hour=Integer.parseInt(hourStr);
        int min=Integer.parseInt(minStr);
        int i=((hour-8) * 60) + (min);
        return i;
    }

    public static String addCalendar(String calText, int addDay){
        String dateToIncr = calText;
        String dt = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(dateToIncr));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        c.add(Calendar.DAY_OF_MONTH, addDay);  // number of days to add
        dt = sdf.format(c.getTime());
        return dt;
    }

    public static int getWeekOfDay(String dateText){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        Calendar c = Calendar.getInstance(Locale.US);
        c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        try {
            c.setTime(sdf.parse(dateText));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int day=c.get(Calendar.DAY_OF_WEEK);
       // if(day==1){day=7;}else{day--;}
        return day;
    }

    public static String getTodayDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");
        Date nowDate = new Date();
        return formatter.format(nowDate);
    }

    public static void messageDialogBox(FragmentManager fm, int iconPic, String headerText, String messageText, MessageBoxSettingsObject settings, DialogInterface.OnClickListener listener1){
        MessageDialog messageDialog = new MessageDialog(iconPic,headerText,messageText,settings,listener1);
        messageDialog.show(fm,"Sample");
    }

    public static void messageDialogBox(FragmentManager fm, int iconPic, String headerText, String messageText, MessageBoxSettingsObject settings){
        MessageDialog messageDialog = new MessageDialog(iconPic,headerText,messageText,settings);
        messageDialog.show(fm,"Sample");
    }

    public static void messageDialogBox(FragmentManager fm, int iconPic, String headerText, String messageText){
        MessageDialog messageDialog = new MessageDialog(iconPic,headerText,messageText);
        messageDialog.show(fm,"Sample");
    }

    public static int dpToPx(Context context, int dp) {
        float density = context.getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }

    public static int[] stringArrayToIntArray(String[] strings) {
        int[] returnValue = new int[strings.length];
        Integer[] intarray=new Integer[strings.length];
        int i=0;
        for(String str:strings){
            intarray[i]=Integer.parseInt(str.trim());
            returnValue[i]=intarray[i].intValue();
            i++;
        }
        return returnValue;
    }

    public static String isBarcode(String barcode){
        if(barcode.length()>1){
            if(!barcode.isEmpty()) {
                barcode = barcode.replace(" ","");
                int suffixLen = SessionClass.getParam("barcodeSuffix").length();
                if ((barcode.substring(barcode.length() - suffixLen, barcode.length()).equals(SessionClass.getParam("barcodeSuffix")))) {
                    String bar = barcode.substring(0, barcode.length() - suffixLen);
                    if(bar.charAt(0)=='0') {
                        try {
                            bar = bar.substring(1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return bar;
                }
            }
        }
        return null;
    }

    public static String getTrimmedText(String str){
        String get = "";
        try{
            get = str.substring( Integer.parseInt( SessionClass.getParam("trimFrom") ) , str.length() - Integer.parseInt( SessionClass.getParam("trimTo") ) );
        }catch (Exception e ){

        }
        return get;
    }

    public static String isBarcodeALLTEXT(String barcode){
        if(barcode.length()>3){
            if(!barcode.isEmpty()) {
                barcode = barcode.replace(" ","");

                int suffixLen = SessionClass.getParam("barcodeSuffix").length();
                if ((barcode.substring(barcode.length() - suffixLen, barcode.length()).equals(SessionClass.getParam("barcodeSuffix")))) {
                    String bar = barcode.substring(0, barcode.length() - suffixLen);
                    return bar;
                }
            }
        }
        return null;
    }

    public static void tooltipBuild(Context context, View aView, String text, int style){
        Tooltip.make(context,
                new Tooltip.Builder(101)
                        .anchor(aView, Tooltip.Gravity.LEFT)
                        .closePolicy(new Tooltip.ClosePolicy()
                                .insidePolicy(true, false)
                                .outsidePolicy(true, false), 5000)
                        .activateDelay(0)
                        .showDelay(300)
                        .text(text)
                        .maxWidth(500)
                        .withStyleId(style)
                        .withArrow(true)
                        .withOverlay(true)
                        .floatingAnimation(Tooltip.AnimationBuilder.DEFAULT)
                        .build()
        ).show();
    }

    public static void reloadTempSession(Context context, int datacount) {
        SelesterDatabase db = SelesterDatabase.getDatabase(context);
        List<SessionTemp> tempList = db.sessionTempDao().getAllData();
        if(tempList != null && tempList.size() > 0) {
            for(int i=0; i < tempList.size() ; i++ ){
                String[] strArray = new String[datacount];
                if( datacount > 0 ) strArray[0] = tempList.get(i).getParam0();
                if( datacount > 1 ) strArray[1] = tempList.get(i).getParam1();
                if( datacount > 2 ) strArray[2] = tempList.get(i).getParam2();
                if( datacount > 3 ) strArray[3] = tempList.get(i).getParam3();
                if( datacount > 4 ) strArray[4] = tempList.get(i).getParam4();
                if( datacount > 5 ) strArray[5] = tempList.get(i).getParam5();
                if( datacount > 6 ) strArray[6] = tempList.get(i).getParam6();
                if( datacount > 7 ) strArray[7] = tempList.get(i).getParam7();
                if( datacount > 8 ) strArray[8] = tempList.get(i).getParam8();
                if( datacount > 9 ) strArray[9] = tempList.get(i).getParam9();
                if( datacount > 10 ) strArray[10] = tempList.get(i).getParam10();
                if( datacount > 11 ) strArray[11] = tempList.get(i).getParam11();
                if( datacount > 12 ) strArray[12] = tempList.get(i).getParam12();
                if( datacount > 13 ) strArray[13] = tempList.get(i).getParam13();
                if( datacount > 14 ) strArray[14] = tempList.get(i).getParam14();
                if( datacount > 15 ) strArray[15] = tempList.get(i).getParam15();
                if( datacount > 16 ) strArray[16] = tempList.get(i).getParam16();
                if( datacount > 17 ) strArray[17] = tempList.get(i).getParam17();
                if( datacount > 18 ) strArray[18] = tempList.get(i).getParam18();
                if( datacount > 19 ) strArray[19] = tempList.get(i).getParam19();
                if( datacount > 20 ) strArray[20] = tempList.get(i).getParam20();
                if( datacount > 21 ) strArray[21] = tempList.get(i).getParam21();
                if( datacount > 22 ) strArray[22] = tempList.get(i).getParam22();
                if( datacount > 23 ) strArray[23] = tempList.get(i).getParam23();
                if( datacount > 24 ) strArray[24] = tempList.get(i).getParam24();
                if( datacount > 25 ) strArray[25] = tempList.get(i).getParam25();
                if( datacount > 26 ) strArray[26] = tempList.get(i).getParam26();
                if( datacount > 27 ) strArray[27] = tempList.get(i).getParam27();
                if( datacount > 28 ) strArray[28] = tempList.get(i).getParam28();
                if( datacount > 29 ) strArray[29] = tempList.get(i).getParam29();
                if( datacount > 30 ) strArray[30] = tempList.get(i).getParam30();
                if( datacount > 31 ) strArray[31] = tempList.get(i).getParam31();
                if( datacount > 32 ) strArray[32] = tempList.get(i).getParam32();
                if( datacount > 33 ) strArray[33] = tempList.get(i).getParam33();
                if( datacount > 34 ) strArray[34] = tempList.get(i).getParam34();
                if( datacount > 35 ) strArray[35] = tempList.get(i).getParam35();
                if( datacount > 36 ) strArray[36] = tempList.get(i).getParam36();
                if( datacount > 37 ) strArray[37] = tempList.get(i).getParam37();
                if( datacount > 38 ) strArray[38] = tempList.get(i).getParam38();
                if( datacount > 39 ) strArray[39] = tempList.get(i).getParam39();

                AllLinesData.setParam(String.valueOf(tempList.get(i).getId()), strArray);
                CheckedList.setParamItem(strArray[0],tempList.get(i).getStatus());
                if( tempList.get(i).isInsertRow() ) {
                    Log.i("INSERT SAVE DATA", "" + String.valueOf(tempList.get(i).getId()) );
                    InsertedList.setInsertElement(String.valueOf(tempList.get(i).getId()), "0");
                }
            }
            AllLinesData.toStringLog();
        }else{
            Log.i("SQL","DATABASE EMPTY");
        }
    }

    public static SessionTemp createSessionTempFormat(long id, int num, String[] data){
        String[] tempdata = new String[40];
        for(int i=0; i<40; i++){
            tempdata[i] = "not";
        }
        for(int i=0; i<data.length; i++){
            tempdata[i] = data[i];
        }
        Log.i("TAG","SAVE SESSIONTEMP: " + id + " - " + num + " - " + tempdata[0] + " - " + tempdata[1] + " - " + tempdata[2] + " - " + tempdata[3] + " - " +
                tempdata[4] + " - " + tempdata[5] + " - " + tempdata[6] + " - " + tempdata[7] + " - " + tempdata[8] + " - " + tempdata[9] + " - " + tempdata[10] + " - " +
                tempdata[11] + " - " + tempdata[12] + " - " + tempdata[13] + " - " + tempdata[14] + " - " + tempdata[15] + " - " + tempdata[16] + " - " + tempdata[17] + " - " +
                tempdata[18] + " - " + tempdata[19] + " - " + tempdata[20] + " - " + tempdata[21] + " - " + tempdata[22] + " - " + tempdata[23] + " - " + tempdata[24] + " - " +
                tempdata[25] + " - " + tempdata[26] + " - " + tempdata[27] + " - " + tempdata[28] + " - " + tempdata[29] + " - " + tempdata[30] + " - " +
                tempdata[31] + " - " + tempdata[32] + " - " + tempdata[33] + " - " + tempdata[34] + " - " + tempdata[35] + " - " + tempdata[36] + " - " +
                tempdata[37] + " - " + tempdata[38] + " - " + tempdata[39] + " - " +  CheckedList.getParamItem(tempdata[0]) + " - " +  InsertedList.isInsert(String.valueOf(id))
        );
        return new SessionTemp(id,num,tempdata[0],tempdata[1],tempdata[2],tempdata[3],tempdata[4],tempdata[5],tempdata[6],tempdata[7],tempdata[8],tempdata[9],tempdata[10],tempdata[11],tempdata[12],tempdata[13],tempdata[14],tempdata[15],tempdata[16],tempdata[17],tempdata[18],tempdata[19],tempdata[20],tempdata[21],tempdata[22],tempdata[23],tempdata[24],tempdata[25],tempdata[26],tempdata[27],tempdata[28],tempdata[29],tempdata[30],tempdata[31],tempdata[32],tempdata[33],tempdata[34],tempdata[35],tempdata[36],tempdata[37],tempdata[38],tempdata[39], CheckedList.getParamItem(tempdata[0]), InsertedList.isInsert(String.valueOf(id)) );
    }

    public static ProgressDialog loadingDialogOn(Activity activity){
        ProgressDialog pd = new ProgressDialog(activity);
        pd.setMessage("Adatok betöltése...");
        pd.setCancelable(false);
        pd.show();
        return pd;
    }

    public static String getCurrentDate(){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd");
        String formattedDate = df.format(c);
        return formattedDate;
    }

    public static String getCurrentTime(){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        String formattedTime = df.format(c);
        return formattedTime;
    }

    public static void errorSound(Activity activity){
        MediaPlayer mPlayer = MediaPlayer.create(activity, R.raw.alert);
        mPlayer.start();

    }

    public static int getArrayPosition(String findElement, String parameterField){
        if(parameterField != null) {
            String[] field = parameterField.split(",");
            return Arrays.asList(field).indexOf(findElement);
        }else{
            return -1;
        }

    }

    public static boolean inArrayString(String findString, String arrayString){
        if( findString != null && !findString.equals("") && arrayString != null && !arrayString.equals("") ) {
            if (findString.charAt(0) == '0') {
                findString = findString.substring(1);
            }
            String[] tag = arrayString.split("\\|");
            for (int i = 0; i < tag.length; i++) {
                if( !tag[i].equals("") ) {
                    if (tag[i].charAt(0) == '0') {
                        tag[i] = tag[i].substring(1);
                    }
                    if (findString.equals(tag[i])) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static int getResId(String resName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static void setDrag(View view){
        ClipData.Item item = new ClipData.Item((CharSequence) view.getTag());
        String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
        ClipData data = new ClipData(view.getTag().toString(), mimeTypes, item);
        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
        view.startDrag(data, shadowBuilder, view, 0);
        view.setVisibility(View.INVISIBLE);

    }


    public static void logMapParamteres(Map<String,String> mapParams){
        for(Map.Entry<String, String> entry: mapParams.entrySet() ){
            Log.i("logMap",entry.getKey()+  " " + entry.getValue());
        }
    }

    public final static int DONE    = 0;
    public final static int WARNING = 1;
    public final static int ERROR   = 2;
    public final static int DELETE  = 3;

    public static void messageBox(final Activity activity, String title, String message, int status) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View rootView = LayoutInflater.from(activity).inflate(R.layout.dialog_messagebox,null, false);
        TextView titleTV = rootView.findViewById(R.id.dialog_messagebox_title);
        titleTV.setText(title);
        TextView contentTV = rootView.findViewById(R.id.dialog_messagebox_content);
        contentTV.setText(message);
        ImageView statusImg = rootView.findViewById(R.id.dialog_messagebox_image);
        titleTV.setBackgroundColor( ContextCompat.getColor(activity, R.color.secondColor) );
        switch (status){
            case 0:
                statusImg.setBackground( ContextCompat.getDrawable(activity, R.drawable.dialog_done) );
                break;
            case 1:
                statusImg.setBackground( ContextCompat.getDrawable(activity, R.drawable.dialog_warning) );
                titleTV.setBackgroundColor( ContextCompat.getColor(activity, R.color.warningColor) );
                break;
            case 2:
                statusImg.setBackground( ContextCompat.getDrawable(activity, R.drawable.dialog_error) );
                titleTV.setBackgroundColor( ContextCompat.getColor(activity, R.color.errorColor) );
                break;
            case 3:
                statusImg.setBackground( ContextCompat.getDrawable(activity, R.drawable.dialog_delete) );
                titleTV.setBackgroundColor( ContextCompat.getColor(activity, R.color.errorColor) );
                break;
        }
        builder.setView(rootView);
        builder.setNegativeButton("Rendben", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                KeyboardUtils.hideKeyboard(activity);
                dialog.cancel();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    public static View.OnFocusChangeListener getFocusListener(final Context context){
        return new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    v.setBackground(ContextCompat.getDrawable(context, R.drawable.et_shape_select));
                } else {
                    v.setBackground( null );
                }
            }
        };
    }

    public static View.OnFocusChangeListener getFocusBorderListener(final Context context){
        return new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    v.setBackground(ContextCompat.getDrawable(context, R.drawable.et_shape_select));
                } else {
                    v.setBackground( ContextCompat.getDrawable(context, R.drawable.et_shape) );
                }
            }
        };
    }

}
