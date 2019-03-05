package hu.selester.android.webstockandroid.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hu.selester.android.webstockandroid.Objects.SessionClass;
import hu.selester.android.webstockandroid.R;
import hu.selester.android.webstockandroid.TablePanel.TablePanel;

import static hu.selester.android.webstockandroid.TablePanel.TablePanel.WRAP_MAX_COLUMN;

public class TablePanelAdapter extends RecyclerView.Adapter<TablePanelAdapter.ViewHolder> {

    private List<String[]> list;
    private List<TablePanel.RowSetting> rowSetting;
    private TablePanel.TablePanelSetting tablePanelSetting;
    private Context context;
    private int[] columnWidth;
    private View.OnClickListener rowClickEvent;

    private int columnCount = 0;
    private int width;
    public boolean[] checkedList;
    private int qNeed = 0;
    private int qCurrent = 0;
    private String tranCode;


    public static class ViewHolder extends RecyclerView.ViewHolder{

        public View tableRowLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            tableRowLayout = itemView;
        }
    }

    public List<String[]> getdataList() {
        return list;
    }

    public TablePanelAdapter(Context context, List<String[]> list, int[] columnWidth, List<TablePanel.RowSetting> rowSetting, TablePanel.TablePanelSetting tablePanelSetting) {
        this.list = list;
        this.context = context;
        this.columnWidth = columnWidth;
        this.rowSetting = rowSetting;
        this.tablePanelSetting = tablePanelSetting;
        //this.fontSize = fontSize;
        //this.isCheckable = isCheckable;
        this.rowClickEvent = rowClickEvent;
        this.checkedList = new boolean[list.size()];
        for(int i = 0; i < list.size(); i++){
            this.checkedList[i] = false;
        }
        columnCount = columnWidth.length;
        if(tranCode != null) {
            try {
                if (SessionClass.getParam("tranCode") != null && !SessionClass.getParam("tranCode").equals("")) {
                    Log.i("TAG", "" + tranCode + "_Detail_TextBox_Needed_Qty_Index");
                    if (SessionClass.getParam(tranCode + "_Detail_TextBox_Needed_Qty_Index").equals("")) {
                        qNeed = 0;
                    } else {
                        qNeed = Integer.parseInt(SessionClass.getParam(tranCode + "_Detail_TextBox_Needed_Qty_Index"));
                    }

                    if (SessionClass.getParam(tranCode + "_Detail_TextBox_Current_Qty_Index").equals("")) {
                        qCurrent = 0;
                    } else {
                        qCurrent = Integer.parseInt(SessionClass.getParam(tranCode + "_Detail_TextBox_Current_Qty_Index"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.row_tablepanel,parent,false);
        LinearLayout tableRootLayout = rootView.findViewById(R.id.tablepanel_rootLayout);
        if(tablePanelSetting.getOnRowClickListener() != null) tableRootLayout.setOnClickListener(tablePanelSetting.getOnRowClickListener());
        if(tablePanelSetting.isCheckable()){
            CheckBox checkBox = new CheckBox(context);
            checkBox.setId(R.dimen.tablepanel_row_checkBox);
            Log.i("TAG","CREATE CHECKBOX");
            if(tablePanelSetting.getOnRowClickListener() != null) checkBox.setOnClickListener(tablePanelSetting.getOnRowClickListener());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            checkBox.setLayoutParams(lp);
            tableRootLayout.addView(checkBox);
        }

        for(int column=0;column<columnCount;column++){
            tableRootLayout.addView(addItem((column+1)*10));
        }

        ViewHolder vh = new ViewHolder(rootView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        float fs = 0;
        int bc = 0;
        int fc = 0;
        LinearLayout rootLayout = holder.tableRowLayout.findViewById(R.id.tablepanel_rootLayout);
        if( rowSetting != null && rowSetting.size()>position && rowSetting.get(position)!=null ){

            if( rowSetting.get(position).getFontSize() != 0) fs = rowSetting.get(position).getFontSize(); else fs = tablePanelSetting.getFontSize();
            if (qNeed > 0 && qCurrent > 0) {
                int color = 0 ;
                int curr = Integer.parseInt(list.get(position)[qCurrent]);
                int need = Integer.parseInt(list.get(position)[qNeed]);
                if ((curr < need) && curr != 0) {
                    bc = R.color.productRowPHBack;
                } else if (curr == need) {
                    bc = R.color.productRowOKBack;
                } else {
                    bc = R.color.productRowNOTBack;
                }
            } else {
                if( rowSetting.get(position).getBackColor() != 0) bc = rowSetting.get(position).getBackColor(); else bc = R.color.backcolorTablePanelDefault;
            }
            if( rowSetting.get(position).getFontColor() != 0) fc = rowSetting.get(position).getFontColor(); else fc = R.color.fontcolorTablePanelDefault;

        } else {
            fs = tablePanelSetting.getFontSize();
            bc = R.color.backcolorTablePanelDefault;
            fc = R.color.fontcolorTablePanelDefault;
        }
        rootLayout.setBackgroundResource(bc);

        if(tablePanelSetting.isCheckable()){
            CheckBox checkBox = holder.tableRowLayout.findViewById(R.dimen.tablepanel_row_checkBox);
            checkBox.setTag(position);
            checkBox.setChecked(checkedList[position]);
            checkBox.setBackgroundResource(bc);
        }

        for(int column=0; column<columnCount; column++) {
            TextView tv = holder.tableRowLayout.findViewById((column+1)*10);
            if(columnWidth[column] == WRAP_MAX_COLUMN) width = LinearLayout.LayoutParams.WRAP_CONTENT; else width = columnWidth[column];
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.MATCH_PARENT);
            tv.setLayoutParams(lp);

            tv.setTextSize(fs);
            tv.setBackgroundResource(bc);
            tv.setTextColor(ContextCompat.getColor(context, fc));
            tv.setPadding(tablePanelSetting.getCellLeftRightPadding(),tablePanelSetting.getCellTopBottomPadding(),tablePanelSetting.getCellLeftRightPadding(),tablePanelSetting.getCellTopBottomPadding());
            tv.setText(list.get(position)[column]);
            tv.setMaxLines(1);
            tv.setSingleLine(true);
            tv.setEllipsize(TextUtils.TruncateAt.END);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private View addItem(int id){
        TextView tv = new TextView(context);
        tv.setTextColor(Color.BLACK);
        tv.setGravity(Gravity.CENTER_VERTICAL);
        tv.setId(id);
        return tv;
    }

    public void updateData(List<String[]> list){
        this.list = list;
        this.checkedList = new boolean[list.size()];
        for(int i = 0; i < list.size(); i++){
            this.checkedList[i] = false;
        }
        notifyDataSetChanged();

    }

    public void updateData(List<String[]> list, boolean[] checkedList){
        this.list = list;
        this.checkedList = checkedList;
        notifyDataSetChanged();

    }

    public void update(){
        notifyDataSetChanged();
    }

    public List<String> getCheckedPosition(int position){
        List<String> chkList = new ArrayList<>();
        for(int i=0; i<checkedList.length; i++){
            if(checkedList[i]) chkList.add(list.get(i)[position]);
        }
        return chkList;
    }

    public void updateData(List<String[]> list, boolean[] checkedList, List<TablePanel.RowSetting> rowSetting){
        this.list = list;
        this.checkedList = checkedList;
        this.rowSetting = rowSetting;
        notifyDataSetChanged();
    }

    public void setCheckedArray(int chkPosition){
        for(int i=0;i<list.size();i++){
            if( chkPosition == i ){
                checkedList[i]=true;
            }else{
                checkedList[i]=false;
            }
        }
        notifyDataSetChanged();
    }


}
