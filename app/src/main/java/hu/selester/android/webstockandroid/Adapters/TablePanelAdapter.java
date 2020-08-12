package hu.selester.android.webstockandroid.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hu.selester.android.webstockandroid.Database.Tables.SessionTemp;
import hu.selester.android.webstockandroid.Helper.HelperClass;
import hu.selester.android.webstockandroid.Objects.AllLinesData;
import hu.selester.android.webstockandroid.Objects.NotCloseList;
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
    private int qEvidNum;

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
        tranCode = SessionClass.getParam("tranCode");
        if(tranCode != null) {
            try {
                if (SessionClass.getParam("tranCode") != null && !SessionClass.getParam("tranCode").equals("")) {
                    qEvidNum  = HelperClass.getArrayPosition("EvidNum", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
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

    @SuppressLint("ResourceType")
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.row_tablepanel,parent,false);
        LinearLayout tableRootLayout = rootView.findViewById(R.id.tablepanel_rootLayout);
        if(tablePanelSetting.getOnRowClickListener() != null) tableRootLayout.setOnClickListener(tablePanelSetting.getOnRowClickListener());
        if( NotCloseList.getSizeOfChecked() > 0) {
            ImageView image = new ImageView(context);
            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(HelperClass.dpToPx(context, 20), HelperClass.dpToPx(context, 28));
            image.setImageResource(R.drawable.x);
            image.setLayoutParams(lp1);
            image.setId(2);
            image.setPadding(HelperClass.dpToPx(context, 3), HelperClass.dpToPx(context, 3), HelperClass.dpToPx(context, 3), HelperClass.dpToPx(context, 3));
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            tableRootLayout.addView(image);
        }
        if(tablePanelSetting.isCheckable()){
            CheckBox checkBox = new CheckBox(context);
            checkBox.setId(R.dimen.tablepanel_row_checkBox);
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
                int curr; int need;
                if( list.get(position)[qCurrent] != null ){ curr = Integer.parseInt(list.get(position)[qCurrent]);}else{curr = 0;}
                if( list.get(position)[qNeed] != null ){ need = Integer.parseInt(list.get(position)[qNeed]); }else{ need = 0;}
                if ((curr < need) && curr != 0) {
                    bc = R.color.productRowPHBack;
                } else if (curr == need) {
                    bc = R.color.productRowOKBack;
                } else {
                    bc = R.color.productRowNOTBack;
                }
            } else {
                if( rowSetting.get(position).getBackColor() != 0) bc = rowSetting.get(position).getBackColor(); else bc = R.color.productRowINBack;
            }
            if( rowSetting.get(position).getFontColor() != 0) fc = rowSetting.get(position).getFontColor(); else fc = R.color.fontcolorTablePanelDefault;

        } else {
            fs = tablePanelSetting.getFontSize();
            bc = R.drawable.tablepanel_row_back;
            fc = R.color.fontcolorTablePanelDefault;
        }
        if (!SessionClass.getParam("XD").isEmpty() && SessionClass.getParam("XD") != null){
            List<String[]> dataListItem = AllLinesData.findItemsFromMap(list.get(position)[qEvidNum],qEvidNum);
            if( dataListItem != null && dataListItem.size() > 0 ){
                boolean change = false;
                for(int i = 0; i < dataListItem.size(); i++ ){
                    if( dataListItem.get(i)[2].equals("0") && dataListItem.get(i)[qCurrent].equals("0") ) {
                        for(int j = i+1 ; j < dataListItem.size(); j++ ){
                            if( !dataListItem.get(j)[2].equals("0") && dataListItem.get(j)[2] != null ){
                                change = true;
                                break;
                            }
                        }
                    }
                }
                if(change) bc = R.color.productRowINBack;
            }
        }
        rootLayout.setBackgroundResource(bc);

        if(tablePanelSetting.isCheckable()){
            @SuppressLint("ResourceType") CheckBox checkBox = holder.tableRowLayout.findViewById(R.dimen.tablepanel_row_checkBox);
            checkBox.setTag(position);
            checkBox.setChecked(checkedList[position]);
            checkBox.setBackgroundResource(bc);
        }
        if( NotCloseList.getSizeOfChecked() > 0){
            @SuppressLint("ResourceType") ImageView img = holder.tableRowLayout.findViewById(2);
            if( NotCloseList.getParamItem(list.get(position)[0]) == 1){
                img.setImageResource(R.drawable.x);
            }else {
                img.setImageResource(R.drawable.closerow);
            }
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
            if( NotCloseList.getParamItem(list.get(position)[0]) == 1){
            }
        }
    }

    private void multiLineStrikeThrough(TextView description, String textContent){
        description.setText(textContent, TextView.BufferType.SPANNABLE);
        Spannable spannable = (Spannable)description.getText();
        spannable.setSpan(new StrikethroughSpan(), 0, textContent.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
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
