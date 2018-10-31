package hu.selester.android.webstockandroid.TableView;

import android.content.Context;
import android.graphics.Color;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import hu.selester.android.webstockandroid.Adapters.TableViewAdapter;
import hu.selester.android.webstockandroid.Helper.HelperClass;
import hu.selester.android.webstockandroid.Objects.ListSettings;
import hu.selester.android.webstockandroid.R;

public class TableView {

    private Context context;
    private View rootView;
    private ListView lv;
    private String tableName = "";

    public TableView(Context context,View view) {
        this.context = context;
        this.rootView = view;
    }

    public void setHeaderData(int layoutResource, ListSettings listSettings){
        LinearLayout ll = rootView.findViewById(layoutResource);
        if(listSettings.isCheckbox()){
            TextView tv = new TextView(context);
            tv.setText("");
            tv.setTextColor(Color.BLACK);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(70,ViewGroup.LayoutParams.WRAP_CONTENT);
            tv.setBackgroundResource(R.drawable.table_view_header_sub);
            tv.setLayoutParams(lp);
            tv.setPadding(HelperClass.dpToPx(context,5),0,0,0);
            ll.addView(tv);
        }
        for(int i=0; i<listSettings.getHeaderWidth().length;i++){
            TextView tv = new TextView(context);
            Log.i("ITEM",listSettings.getHeaderText()[i]);
            tv.setText(listSettings.getHeaderText()[i]);

            tv.setTextColor(Color.BLACK);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(listSettings.getHeaderWidth()[i],ViewGroup.LayoutParams.WRAP_CONTENT);
            tv.setLayoutParams(lp);
            tv.setBackgroundResource(R.drawable.table_view_header_sub);
            tv.setPadding(HelperClass.dpToPx(context,5),0,0,0);
            ll.addView(tv);
        }
    }

    public void setContentData(int listViewResource, List<String[]> listData, ListSettings listSettings, List<Integer> textColor){
        Log.i("TAG","TEXTCOLOR");
        lv = rootView.findViewById(listViewResource);
        TableViewAdapter sa = new TableViewAdapter(context, listData, listSettings, tableName, textColor);
        lv.setAdapter(sa);
    }

    public void setContentData(int listViewResource, List<String[]> listData, ListSettings listSettings){
        Log.i("TAG","TEXTCOLOR NULL");
        lv = rootView.findViewById(listViewResource);
        TableViewAdapter sa = new TableViewAdapter(context, listData, listSettings, tableName, null);
        lv.setAdapter(sa);
    }

    public TableViewAdapter getAdapter(){
        return (TableViewAdapter) lv.getAdapter();
    }

    public void smoothScrollToPosition(int position){
        lv.smoothScrollToPosition(position);
    }

    public void setTableName(String name){
        this.tableName = name;
    }


}
