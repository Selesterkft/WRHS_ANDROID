package hu.selester.android.webstockandroid.TablePanel;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import hu.selester.android.webstockandroid.Adapters.TablePanelAdapter;
import hu.selester.android.webstockandroid.Divider.SimpleDividerItemDecoration;
import hu.selester.android.webstockandroid.R;

public class TablePanel {

    //------------- Sort Result Object ---------------

    public class SortResultObject{
        public List<String[]> data;
        public boolean[] checkData;
        public List<RowSetting> rowSettingData;

        public SortResultObject(List<String[]> data, boolean[] checkData, List<RowSetting> rowSettingData) {
            this.data = data;
            this.checkData = checkData;
            this.rowSettingData = rowSettingData;
        }
    }

    // --------------- Sort DataClass ----------------

    public class ShortClass{
        public String value;
        public int index;

        public ShortClass(int index, String value) {
            this.value = value;
            this.index = index;
        }

        @Override
        public String toString() {
            return "ShortClass{" +
                    "index=" + index +
                    ", value='" + value + '\'' +
                    '}';
        }
    }

    public class CustomComparatorASC implements Comparator<ShortClass> {
        @Override
        public int compare(ShortClass o1, ShortClass o2) {
            Collator collator = Collator.getInstance();
            collator.setDecomposition(Collator.CANONICAL_DECOMPOSITION);
            return collator.compare(o1.value, o2.value);

        }
    }

    public class CustomComparatorDESC implements Comparator<ShortClass> {
        @Override
        public int compare(ShortClass o1, ShortClass o2) {
            Collator collator = Collator.getInstance();
            collator.setDecomposition(Collator.CANONICAL_DECOMPOSITION);
            return collator.compare(o2.value, o1.value);

        }
    }

    // --------- Grid global setting variable ---------

    public class TablePanelSetting{
        private int cellTopBottomPadding;
        private int cellLeftRightPadding;
        private boolean isCheckable;
        private float fontSize;
        private int headerBackground;
        private int headerTextColor;
        private View.OnClickListener onRowClickListener;
        private View.OnClickListener onHeaderClickListener;
        private SwipeRefreshLayout.OnRefreshListener onRefreshListener;

        public TablePanelSetting() {
            this.cellTopBottomPadding = dpToPx(context,5);
            this.cellLeftRightPadding = dpToPx(context,5);
            this.isCheckable = false;
            this.fontSize = 13f;
            this.headerBackground = R.drawable.tablepanel_header_back;
            this.headerTextColor = R.color.fontcolorTablePanelDefault;
            this.onRowClickListener = null;
            this.onHeaderClickListener = null;
            this.onRefreshListener = null;
        }

        public View.OnClickListener getOnRowClickListener() {
            return onRowClickListener;
        }

        public void setOnRowClickListener(View.OnClickListener onRowClickListener) {
            this.onRowClickListener = onRowClickListener;
        }

        public View.OnClickListener getOnHeaderClickListener() {
            return onHeaderClickListener;
        }

        public void setOnHeaderClickListener(View.OnClickListener onHeaderClickListener) {
            this.onHeaderClickListener = onHeaderClickListener;
        }

        public SwipeRefreshLayout.OnRefreshListener getOnRefreshListener() {
            return onRefreshListener;
        }

        public void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener onRefreshListener) {
            this.onRefreshListener = onRefreshListener;
        }

        public int getHeaderTextColor() {
            return headerTextColor;
        }

        public void setHeaderTextColor(int headerTextColor) {
            this.headerTextColor = headerTextColor;
        }

        public int getHeaderBackground() {
            return headerBackground;
        }

        public void setHeaderBackground(int headerBackground) {
            this.headerBackground = headerBackground;
        }

        public int getCellTopBottomPadding() {
            return cellTopBottomPadding;
        }

        public void setCellTopBottomPadding(int cellTopBottomPadding) {
            this.cellTopBottomPadding = cellTopBottomPadding;
        }

        public int getCellLeftRightPadding() {
            return cellLeftRightPadding;
        }

        public void setCellLeftRightPadding(int cellLeftRightPadding) {
            this.cellLeftRightPadding = cellLeftRightPadding;
        }

        public boolean isCheckable() {
            return isCheckable;
        }

        public void setCheckable(boolean checkable) {
            isCheckable = checkable;
        }

        public float getFontSize() {
            return fontSize;
        }

        public void setFontSize(float fontSize) {
            this.fontSize = fontSize;
        }
    }

    // --------- Row setting variable ---------

    public class RowSetting{
        private int backColor;
        private int fontColor;
        private float fontSize;

        public RowSetting() {
            this.backColor = R.drawable.tablepanel_row_back;
            this.fontColor = R.color.fontcolorTablePanelDefault;
            this.fontSize = 13f;
        }

        public int getBackColor() {
            return backColor;
        }

        public void setBackColor(int backColor) {
            this.backColor = backColor;
        }

        public int getFontColor() {
            return fontColor;
        }

        public void setFontColor(int fontColor) {
            this.fontColor = fontColor;
        }

        public float getFontSize() {
            return fontSize;
        }

        public void setFontSize(float fontSize) {
            this.fontSize = fontSize;
        }
    }

    // --------- CLASS variable ---------

    private Context context;
    private View rootView;
    private RecyclerView tablePanel;
    private RecyclerView.LayoutManager mLayountManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView sortSignView;

    // --------- Grid data variable ---------

    private String[] headerText;
    private List<String[]> dataList;

    // --------- Grid settings variable ---------

    private List<RowSetting> rowSetting;
    private TablePanelSetting tablePanelSetting;

    private int[] columnMaxWidth;
    private int[] widths;
    private int columnCount;
    private int sortPosition = -1;
    private int sortRoute = -1;
    private int rootViewRes;

    public static final int WRAP_MAX_COLUMN = -3;

    public TablePanel(Context context, View rootView,int rootViewRes, String[] headerText, List<String[]> dataList, int[] widths) {
        this.context = context;
        this.rootView = rootView;
        this.rootViewRes = rootViewRes;
        this.widths = widths;
        this.headerText = headerText;
        this.dataList = dataList;
        columnCount = widths.length;
        this.tablePanelSetting = new TablePanelSetting();
        rowSetting = new ArrayList<>();
        for (int i=0; i < columnCount; i++){
            rowSetting.add(new RowSetting());
        }
    }

    public RowSetting getRowSettingInstance(){
        return new RowSetting();
    }

    public TablePanelSetting getTablePanelSettingInstance(){
        TablePanelSetting tps = new TablePanelSetting();
        return tps;
    }

    public void setTablePanelSetting(TablePanelSetting tablePanelSetting){
        this.tablePanelSetting = tablePanelSetting;
    }

    public void setRowSetting(List<RowSetting> rowSetting) {
        this.rowSetting = rowSetting;
    }

    public void createTablePanel(){
        this.widths = getMaxWidths();
        initializeLayouts();
        createHeader();
        generatingTablePanel();
    }

    private void createHeader(){
        LinearLayout headerLayout = rootView.findViewById(R.id.tablePanel_header);
        sortSignView = rootView.findViewById(R.id.tablepanel_sort_sign);
        sortSignView.setVisibility(View.GONE);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(dpToPx(context, 10), dpToPx(context, 10));
        Log.i("TAG",""+tablePanelSetting.isCheckable());
        if(tablePanelSetting.isCheckable()){
            TextView tv = new TextView( context );
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getChkWidth(), LinearLayout.LayoutParams.WRAP_CONTENT);
            tv.setLayoutParams(layoutParams);
            tv.setTag(-1);
            tv.setBackgroundResource(tablePanelSetting.getHeaderBackground());
            tv.setPadding(tablePanelSetting.getCellLeftRightPadding(),tablePanelSetting.getCellTopBottomPadding(),tablePanelSetting.getCellLeftRightPadding(),tablePanelSetting.getCellTopBottomPadding());
            headerLayout.addView(tv);

        }
        for(int col = 0 ;col < widths.length; col++){
            TextView tv = new TextView( context );
            tv.setTextSize(tablePanelSetting.getFontSize());
            tv.setText(headerText[col]);
            tv.setTag(col);
            tv.setTextColor(ContextCompat.getColor(context, tablePanelSetting.getHeaderTextColor()));
            if(tablePanelSetting.onHeaderClickListener != null) { tv.setOnClickListener(tablePanelSetting.onHeaderClickListener); }
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(widths[col], LinearLayout.LayoutParams.MATCH_PARENT);
            tv.setLayoutParams(layoutParams);
            tv.setMaxLines(1);
            tv.setSingleLine(true);
            tv.setPadding(tablePanelSetting.getCellLeftRightPadding(),tablePanelSetting.getCellTopBottomPadding(),tablePanelSetting.getCellLeftRightPadding(),tablePanelSetting.getCellTopBottomPadding());
            tv.setGravity(Gravity.CENTER_VERTICAL);
            tv.setBackgroundResource(tablePanelSetting.getHeaderBackground());
            headerLayout.addView(tv);
        }
    }

    private int[] getMaxWidths(){
        columnMaxWidth = new int[columnCount];
        int[] widthsReal = new int[columnCount];
        for (int col = 0; col < columnCount; col++) {
            if(widths[col] == WRAP_MAX_COLUMN) {
                int x = getTvWidth(headerText[col],tablePanelSetting.getFontSize());
                if( columnMaxWidth[col] < x ){
                    columnMaxWidth[col] = x;
                }
            }
        }
        for (int col = 0; col < columnCount; col++) {
            if(widths[col] == WRAP_MAX_COLUMN) {
                int maxStrLen = 0;
                String maxString = "";
                for (int row = 0; row < dataList.size(); row++) {
                    int strLen = dataList.get(row)[col].length();
                    if(strLen > maxStrLen){
                        maxString = dataList.get(row)[col];
                        maxStrLen = strLen;
                    }
                }
                int x = getTvWidth(maxString, getRealRowFontSize(0));
                if( columnMaxWidth[col] < x ){
                    columnMaxWidth[col] = x;
                }
                widthsReal[col] = columnMaxWidth[col];
            }else{
                widthsReal[col] = widths[col];
            }
        }
        Log.i("TAG WIDTHS", Arrays.toString(widthsReal));
        return widthsReal;
    }

    private int getTvWidth(String text, float fontSize){
        TextView tv = new TextView(context);
        tv.setText(text);
        tv.setTextSize(fontSize);
        tv.setPadding(tablePanelSetting.getCellLeftRightPadding(),tablePanelSetting.getCellTopBottomPadding(),tablePanelSetting.getCellLeftRightPadding(),tablePanelSetting.getCellTopBottomPadding());
        tv.measure(0,0);
        return tv.getMeasuredWidth()+10;
    }

    private int getChkWidth(){
        CheckBox tv = new CheckBox(context);
        tv.measure(0,0);
        return tv.getMeasuredWidth();
    }

    private void initializeLayouts(){
        LinearLayout mainLayout = rootView.findViewById(rootViewRes);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View v = inflater.inflate(R.layout.tablepanel,(ViewGroup)rootView, false);
        swipeRefreshLayout = v.findViewById(R.id.tablePanelRefresh);
        swipeRefreshLayout.setOnRefreshListener(tablePanelSetting.onRefreshListener);
        if(tablePanelSetting.getOnRefreshListener() != null) swipeRefreshLayout.setEnabled(true); else swipeRefreshLayout.setEnabled(false);
        mainLayout.removeAllViews();
        mainLayout.addView(v);
        tablePanel = rootView.findViewById(R.id.tablePanel);

    }

    private void generatingTablePanel() {
        mLayountManager = new LinearLayoutManager(context);
        TablePanelAdapter tpa = new TablePanelAdapter(context, dataList, widths, rowSetting, tablePanelSetting);
        tablePanel.setLayoutManager(mLayountManager);
        tablePanel.addItemDecoration(new SimpleDividerItemDecoration(context));
        tablePanel.setAdapter(tpa);
    }

    private float getRealRowFontSize(int row){
        float x;
        if( rowSetting != null && rowSetting.size()>row && rowSetting.get(row)!=null && rowSetting.get(row).getFontSize()!=0 ){
            x = rowSetting.get(row).getFontSize();
        }else{
            x = tablePanelSetting.getFontSize();
        }
        return x;
    }

    public TablePanelAdapter getAdapter(){
       return (TablePanelAdapter) tablePanel.getAdapter();
    }

    public int dpToPx(Context context, int dp) {
        float density = context.getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }

    public SortResultObject sortDataGrid(int position){
        List<ShortClass> changeCol = new ArrayList<>();
        List<String[]> data = getAdapter().getdataList();
        boolean[] sortDataChk = new boolean[data.size()];
        boolean[] dataChk = getAdapter().checkedList;
        Log.i("TAGCHK",Arrays.toString(dataChk));
        for(int i=0; i<data.size(); i++) {
            changeCol.add(new ShortClass(i,data.get(i)[position]));
        }

        Collator c = Collator.getInstance(new Locale("hu","hu"));
        if( position == sortPosition ){
            if(sortRoute == 1) {
                Collections.sort(changeCol, new CustomComparatorDESC());
                sortRoute = 2;
            }else{
                Collections.sort(changeCol, new CustomComparatorASC());
                sortRoute = 1;
            }
        }else{
            sortPosition = position;
            sortRoute = 1;
            Collections.sort(changeCol, new CustomComparatorASC());
        }

        List<String[]> sortList = new ArrayList<>();
        List<RowSetting> sortSettingList = new ArrayList<>();
        for(int i=0; i<changeCol.size(); i++) {
            sortList.add(null);
            sortDataChk[i] = false;
            sortSettingList.add(null);
        }
        for(int i=0; i<changeCol.size(); i++) {
            sortList.set(i,data.get(changeCol.get(i).index));
            sortDataChk[i] = dataChk[changeCol.get(i).index];
            sortSettingList.set(i,rowSetting.get( (int)changeCol.get(i).index ));
        }
        return new SortResultObject(sortList, sortDataChk, sortSettingList);
    }

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }

    public void showSortSign(){
        int x = getChkWidth();
        for(int i=0; i<=sortPosition ; i++){
            x += widths[i];
        }
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams( dpToPx(context,10),dpToPx(context,10) );
        lp.setMargins(x-25,dpToPx(context,tablePanelSetting.cellTopBottomPadding-5),0,0);
        if(sortRoute == 1){
            sortSignView.setImageResource(R.drawable.sort_asc);
        }else{
            sortSignView.setImageResource(R.drawable.sort_desc);
        }
        sortSignView.setLayoutParams(lp);
        sortSignView.setVisibility(View.VISIBLE);
    }

    public void smoothScrollToPosition(int position){
          tablePanel.scrollToPosition(position);
    }

}
