package hu.selester.android.webstockandroid.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hu.selester.android.webstockandroid.Helper.HelperClass;
import hu.selester.android.webstockandroid.Helper.KeyboardUtils;
import hu.selester.android.webstockandroid.Objects.AllLinesData;
import hu.selester.android.webstockandroid.Objects.InsertedList;
import hu.selester.android.webstockandroid.Objects.SessionClass;
import hu.selester.android.webstockandroid.Objects.XD_Dialog_ItemsParameters;
import hu.selester.android.webstockandroid.R;
import hu.selester.android.webstockandroid.Threads.SaveAllSessionTemp;

public class XD_Dialog_ItemsParametersListAdapter extends RecyclerView.Adapter<XD_Dialog_ItemsParametersListAdapter.ViewHolder> {

    private List<XD_Dialog_ItemsParameters> dataList;
    private Context context;
    private int adapterType;
    private int qEvidNum, qNeed, qCurrent, qWeight, qWidth, qHeight, qLength, qBarcode, qSelID, qToPlace, qMissing, qRefLineId;
    private String tranCode, evidNum;
    private OnEventUpdate onEventUpdate;
    private OnSelectMoreItems onSelectMoreItems;
    private Long checkNum;
    private Activity activity;
    private boolean[] checkedArray;
    private int selectedPosition = -1;

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public OnEventUpdate getOnEventUpdate() {
        return onEventUpdate;
    }

    public void setOnEventUpdate(OnEventUpdate onEventUpdate) {
        this.onEventUpdate = onEventUpdate;
    }

    public void setOnSelectMoreItems(XD_ItemsParametersListAdapter.OnSelectMoreItems onSelectMoreItems) {
    }

    public interface OnEventUpdate{
        public void onUpdatePanel();
    }

    public void setOnSelectMoreItems(OnSelectMoreItems onSelectMoreItems) {
        this.onSelectMoreItems = onSelectMoreItems;
    }

    public interface OnSelectMoreItems{
        public void onContainerClick(Long id);
    }



    public String getEvidNum() {
        return evidNum;
    }

    public void setEvidNum(String evidNum) {
        this.evidNum = evidNum;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView item_sumpcs, item_pcs, item_weight, item_length, item_width, item_height, item_toPlace;
        public ImageView delBtn;
        public CheckBox checkBox;
        public LinearLayout container;

        public ViewHolder(View itemView) {
            super(itemView);
            container   = itemView.findViewById(R.id.row_line_container);
            item_sumpcs = itemView.findViewById(R.id.row_line_sumpcs);
            item_pcs    = itemView.findViewById(R.id.row_line_pcs);
            item_weight = itemView.findViewById(R.id.row_line_weight);
            item_length = itemView.findViewById(R.id.row_line_length);
            item_width  = itemView.findViewById(R.id.row_line_width);
            item_height = itemView.findViewById(R.id.row_line_height);
            item_toPlace = itemView.findViewById(R.id.row_line_toplace);
            delBtn      = itemView.findViewById(R.id.row_line_del);
            checkBox    = itemView.findViewById(R.id.row_line_check);
        }
    }

    public XD_Dialog_ItemsParametersListAdapter(@NonNull Context context, List<XD_Dialog_ItemsParameters> dataList, int adapterType) {
        this.dataList = dataList;
        this.context = context;
        this.adapterType = adapterType;
        checkedArray = new boolean[1000];
        tranCode = SessionClass.getParam("tranCode");
        qSelID = 0;
        qBarcode = HelperClass.getArrayPosition("Barcode", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qEvidNum = HelperClass.getArrayPosition("EvidNum", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qNeed = HelperClass.getArrayPosition("Needed_Qty", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qMissing = HelperClass.getArrayPosition("Missing_Qty", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qCurrent = HelperClass.getArrayPosition("Current_Qty", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qWeight  = HelperClass.getArrayPosition("Weight", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qWidth   = HelperClass.getArrayPosition("Size_Width", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qHeight  = HelperClass.getArrayPosition("Size_Height", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qLength  = HelperClass.getArrayPosition("Size_Length", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qToPlace  = HelperClass.getArrayPosition("To_Place", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qRefLineId  = HelperClass.getArrayPosition("Ref_Line_ID",   SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        for(int i=0;i<dataList.size();i++){
            checkedArray[i]=false;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.container.setTag("container_" + position);
        holder.item_sumpcs.setVisibility(View.VISIBLE);
        holder.delBtn.setVisibility(View.GONE);
        holder.checkBox.setVisibility(View.VISIBLE);
        holder.checkBox.setVisibility(View.VISIBLE);
        if ( adapterType == 1 ) {
            holder.item_sumpcs.setVisibility(View.GONE);
            holder.delBtn.setVisibility(View.VISIBLE);
            holder.delBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    delItem(String.valueOf(dataList.get(position).getItem_id()));
                }
            });
            if( SessionClass.getParam("XD_CHECKED") == null ) SessionClass.setParam("XD_CHECKED","0");
            if( SessionClass.getParam("XD_CHECKED").equals(""+dataList.get(position).getItem_id() ) ){
                holder.checkBox.setChecked(true);
                checkedArray[position] = true;
            }else{
                holder.checkBox.setChecked(false);
            }

            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if( ((CheckBox) v).isChecked() ) {
                        SessionClass.setParam("XD_CHECKED", "" + dataList.get(position).getItem_id());
                        for (int i = 0;i<checkedArray.length;i++){
                            if(i == position){
                                checkedArray[i]=true;
                                selectedPosition = i;
                            }else{
                                checkedArray[i]=false;
                            }
                        }
                        notifyDataSetChanged();
                    }else{
                        SessionClass.setParam("XD_CHECKED", "0");
                    }
                }
            });
        }else if (adapterType == 0) {
            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    modifyDataDialog(position);
                }
            });
            if(SessionClass.getParam("XD_CHECKED2") == null) SessionClass.setParam("XD_CHECKED2","0");
            if(SessionClass.getParam("XD_CHECKED2").equals(""+dataList.get(position).getItem_id())){
                holder.checkBox.setChecked(true);
                checkedArray[position] = true;
            }else{
                holder.checkBox.setChecked(false);
            }

            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if( ((CheckBox) v).isChecked() ) {
                        SessionClass.setParam("XD_CHECKED2", "" + dataList.get(position).getItem_id());
                        for (int i = 0;i<checkedArray.length;i++){
                            if(i == position){
                                checkedArray[i]=true;
                                selectedPosition = i;
                            }else{
                                checkedArray[i]=false;
                            }
                        }
                        notifyDataSetChanged();
                    }else{
                        SessionClass.setParam("XD_CHECKED2", "0");
                    }
                }
            });
        }else if (adapterType == 3) {
            if(SessionClass.getParam("XD_CHECKED3") == null) SessionClass.setParam("XD_CHECKED3","0");
            if(SessionClass.getParam("XD_CHECKED3").equals(""+dataList.get(position).getItem_id())){
                holder.checkBox.setChecked(true);
                checkedArray[position] = true;
            }else{
                holder.checkBox.setChecked(false);
            }
            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if( ((CheckBox) v).isChecked() ) {
                        SessionClass.setParam("XD_CHECKED3", "" + dataList.get(position).getItem_id());
                        for (int i = 0;i<checkedArray.length;i++){
                            if(i == position){
                                checkedArray[i]=true;
                                selectedPosition = i;
                            }else{
                                checkedArray[i]=false;
                            }
                        }
                        notifyDataSetChanged();
                    }else{
                        SessionClass.setParam("XD_CHECKED3", "0");
                    }
                }
            });
        }
        holder.item_sumpcs.setText("" + dataList.get(position).getItem_sumpcs());
        holder.item_pcs.setText("" + dataList.get(position).getItem_pcs());
        if( dataList.get(position).getItem_weight() == 0) holder.item_weight.setText(""); else holder.item_weight.setText("" + dataList.get(position).getItem_weight());
        if( dataList.get(position).getItem_width()  == null) holder.item_width.setText("");  else holder.item_width.setText( "" + dataList.get(position).getItem_width());
        if( dataList.get(position).getItem_height() == null) holder.item_height.setText(""); else holder.item_height.setText("" + dataList.get(position).getItem_height());
        if( dataList.get(position).getItem_length() == null) holder.item_length.setText(""); else holder.item_length.setText("" + dataList.get(position).getItem_length());
        if( dataList.get(position).getItem_toPlace() == null) holder.item_toPlace.setText(""); else holder.item_toPlace.setText("" + dataList.get(position).getItem_toPlace());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.row_xd_dialog_items_paramteres, parent, false);
        return new XD_Dialog_ItemsParametersListAdapter.ViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void update(String queryPlace) {
        List<XD_Dialog_ItemsParameters> itemsList = new ArrayList<>();
        List<String[]> resDataList = AllLinesData.findItemsFromMap(evidNum, qEvidNum);
        if( resDataList != null && resDataList.size() > 0 ){
            for(int i=0; i < resDataList.size(); i++){
                if( resDataList.get(i)[qToPlace].equals(queryPlace) ) {
                    try {
                        itemsList.add(new XD_Dialog_ItemsParameters(Long.parseLong(resDataList.get(i)[0]), Integer.parseInt(resDataList.get(i)[qNeed]), Integer.parseInt(resDataList.get(i)[qCurrent]), Integer.parseInt(resDataList.get(i)[qWeight]), HelperClass.convertStringToFloat(resDataList.get(i)[qLength]), HelperClass.convertStringToFloat(resDataList.get(i)[qWidth]), HelperClass.convertStringToFloat(resDataList.get(i)[qHeight]), resDataList.get(i)[qToPlace]));
                    }catch (Exception e){
                        e.printStackTrace();
                        HelperClass.messageBox(activity,"Adatmegadás","Hibás érték formátum " , HelperClass.ERROR);
                    }
                }
            }
        }
        dataList.clear();
        dataList = itemsList;
        notifyDataSetChanged();
    }

    public void update(List<XD_Dialog_ItemsParameters> data) {
        dataList.clear();
        dataList = data;
        notifyDataSetChanged();
    }

    public Long getCheckNum() {
        return checkNum;
    }

    void delItem(final String delID) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Rakhelyről törlés");
        builder.setMessage("Biztos, hogy törli a rakhelyről ezt az árut?");
        builder.setPositiveButton("Igen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(context, "" + delID, Toast.LENGTH_LONG).show();
                int curr = Integer.parseInt( AllLinesData.getParam(delID)[qCurrent] );
                String parentID = AllLinesData.getParam(delID)[qRefLineId];
                AllLinesData.delRow(delID);
                InsertedList.removeInsertElement(delID);
                String[] data = AllLinesData.getParam(parentID);
                data[qCurrent] = String.valueOf( Integer.parseInt( data[qCurrent] ) + curr );
                update("");
                SaveAllSessionTemp sst = new SaveAllSessionTemp(context);
                sst.start();
                dialog.dismiss();
                onEventUpdate.onUpdatePanel();
            }
        });
        builder.setNegativeButton("Nem", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private void modifyDataDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Adatok módosítása!");
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_xd_itemmodify,null, false);
        final EditText ED1 = dialogView.findViewById(R.id.dialog_xd_item_editText1);
        final EditText ED2 = dialogView.findViewById(R.id.dialog_xd_item_editText2);
        final EditText ED3 = dialogView.findViewById(R.id.dialog_xd_item_editText3);
        final EditText ED4 = dialogView.findViewById(R.id.dialog_xd_item_editText4);
        if (dataList.get(position).getItem_weight() == 0 ) ED1.setText( "" ); else ED1.setText( String.valueOf(dataList.get(position).getItem_weight()) );
        if (dataList.get(position).getItem_length() == null ) ED2.setText( "" ); else ED2.setText( String.valueOf(dataList.get(position).getItem_length()) );
        if (dataList.get(position).getItem_width()  == null ) ED3.setText( "" ); else ED3.setText( String.valueOf(dataList.get(position).getItem_width()) );
        if (dataList.get(position).getItem_height() == null ) ED4.setText( "" ); else ED4.setText( String.valueOf(dataList.get(position).getItem_height()) );
        builder.setView(dialogView);
        builder.setNegativeButton("mégsem", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                KeyboardUtils.hideKeyboard(activity);
                dialog.cancel();
            }
        });
        builder.setPositiveButton("módosítom", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if( !ED1.getText().toString().isEmpty() ) AllLinesData.setParamsPosition(qSelID,qWeight, AllLinesData.getParam( String.valueOf(dataList.get(position).getItem_id()))[qSelID], ED1.getText().toString());
                if( !ED2.getText().toString().isEmpty() ) AllLinesData.setParamsPosition(qSelID,qLength, AllLinesData.getParam( String.valueOf(dataList.get(position).getItem_id()))[qSelID], ED2.getText().toString());
                if( !ED3.getText().toString().isEmpty() ) AllLinesData.setParamsPosition(qSelID,qWidth,  AllLinesData.getParam( String.valueOf(dataList.get(position).getItem_id()))[qSelID], ED3.getText().toString());
                if( !ED4.getText().toString().isEmpty() ) AllLinesData.setParamsPosition(qSelID,qHeight, AllLinesData.getParam( String.valueOf(dataList.get(position).getItem_id()))[qSelID], ED4.getText().toString());

                if( !ED1.getText().toString().isEmpty() ) AllLinesData.setParamsPosition(qRefLineId,qWeight, AllLinesData.getParam( String.valueOf(dataList.get(position).getItem_id()))[qSelID], ED1.getText().toString());
                if( !ED2.getText().toString().isEmpty() ) AllLinesData.setParamsPosition(qRefLineId,qLength, AllLinesData.getParam( String.valueOf(dataList.get(position).getItem_id()))[qSelID], ED2.getText().toString());
                if( !ED3.getText().toString().isEmpty() ) AllLinesData.setParamsPosition(qRefLineId,qWidth,  AllLinesData.getParam( String.valueOf(dataList.get(position).getItem_id()))[qSelID], ED3.getText().toString());
                if( !ED4.getText().toString().isEmpty() ) AllLinesData.setParamsPosition(qRefLineId,qHeight, AllLinesData.getParam( String.valueOf(dataList.get(position).getItem_id()))[qSelID], ED4.getText().toString());
                update("");
                KeyboardUtils.hideKeyboard(activity);
                onEventUpdate.onUpdatePanel();
                dialog.cancel();
            }
        });
        builder.show();
    }
}