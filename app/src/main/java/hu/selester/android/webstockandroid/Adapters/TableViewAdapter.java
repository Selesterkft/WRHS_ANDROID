package hu.selester.android.webstockandroid.Adapters;


import android.content.Context;
import android.graphics.Color;
import android.provider.FontsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hu.selester.android.webstockandroid.Helper.HelperClass;
import hu.selester.android.webstockandroid.Objects.ListSettings;
import hu.selester.android.webstockandroid.Objects.SessionClass;
import hu.selester.android.webstockandroid.R;

public class TableViewAdapter extends BaseAdapter {

    static class ViewHolder{
        private CheckBox checkBox;
        private List<TextView> allView;
    }

    public String tranid,movenum;
    public int selectedPosition=-1;
    private boolean[] checkedArray;
    private Context context;
    private List<String[]> textList;
    private List<Integer> textColor;
    private ListSettings listSettings;
    private String tableName;
    private CheckBox chk;
    private int qNeed = 0;
    private int qCurrent = 0;
    private String tranCode;

    public TableViewAdapter(Context context, List<String[]> textList, ListSettings listSettings, String tableName, List<Integer> textColor){
        checkedArray = new boolean[textList.size()];
        this.context = context;
        this.tableName = tableName;
        this.textColor = textColor;
        this.textList = textList;
        this.listSettings = listSettings;
        for(int i=0;i<textList.size();i++){
            checkedArray[i]=false;
        }
    }

    @Override
    public int getCount() {
        return textList.size();
    }

    @Override
    public String[] getItem(int position) {
        return textList.get(position);
    }

    public CheckBox getCheckbox(int position) {
        return chk;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        ViewGroup.LayoutParams lp;
        if(convertView==null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_text, parent, false);
            LinearLayout ll = convertView.findViewById(R.id.contentLayout);

            holder = new ViewHolder();
            holder.allView = new ArrayList<>();
            chk = new CheckBox(context);
            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(70, LinearLayout.LayoutParams.WRAP_CONTENT);
            chk.setLayoutParams(lp2);
            chk.setId(R.id.list_checkbox);
            chk.setPadding(HelperClass.dpToPx(context,5),0,0,0);
            if (!listSettings.isCheckbox()) {
                chk.setVisibility(View.GONE);
            }
            holder.checkBox = chk;
            ll.addView(chk);
            for(int i=0; i<listSettings.getHeaderWidth().length;i++) {
                TextView tv = new TextView(context);
                tv.setTextColor(Color.BLACK);
                tv.setPadding(HelperClass.dpToPx(context,5),0,0,0);

                if( listSettings.getHeaderWidth()[i] == 0 ){
                    lp = new ViewGroup.LayoutParams(0, 0);
                }else{
                    lp = new LinearLayout.LayoutParams(listSettings.getHeaderWidth()[i], LinearLayout.LayoutParams.WRAP_CONTENT );
                }
                tv.setLayoutParams(lp);
                tv.setTextSize(context.getResources().getDimension(R.dimen.line_row_textsize));
                ll.addView(tv);
                holder.allView.add(tv);
            }
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        try {
            if (textColor != null) {
                if (qNeed > 0 && qCurrent > 0) {
                    int color = 0 ;
                    int curr = Integer.parseInt(textList.get(position)[qCurrent]);
                    int need = Integer.parseInt(textList.get(position)[qNeed]);
                    if ((curr < need) && curr != 0) {
                        color = R.color.productRowPHBack;
                    } else if (curr == need) {
                        color = R.color.productRowOKBack;
                    } else {
                        color = R.color.productRowNOTBack;
                    }
                    LinearLayout ll = convertView.findViewById(R.id.contentLayout);
                    ll.setBackgroundResource(color);
                } else {
                    if (textColor.get(position) != null && textColor.get(position) > 0) {
                        LinearLayout ll = convertView.findViewById(R.id.contentLayout);
                        ll.setBackgroundResource(textColor.get(position));
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        for(int i=0; i<listSettings.getHeaderWidth().length;i++) {
            holder.allView.get(i).setText(textList.get(position)[i]);
        }

        holder.checkBox.setChecked(checkedArray[position]);
        final int index = position;
        View.OnClickListener ocl = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            for (int i=0;i<checkedArray.length;i++){
                if(i==index){
                    checkedArray[i]=true;
                    selectedPosition = i;
                }else{
                    checkedArray[i]=false;
                }
            }
            notifyDataSetChanged();
            }
        };
        convertView.setOnClickListener(ocl);
        holder.checkBox.setOnClickListener(ocl);
        return convertView;
    }

    public void updateAdapter(List<String[]> list){
        textList = list;
        notifyDataSetChanged();
    }

    public void setCheckedArray(int chkPosition){
        for(int i=0;i<textList.size();i++){
            if( chkPosition == i ){
                checkedArray[i]=true;
                selectedPosition = i;
            }else{
                checkedArray[i]=false;
            }
        }
        notifyDataSetChanged();
    }
}