package hu.selester.android.webstockandroid.Objects;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class CustomTextWatcher implements TextWatcher {

    private String lineID;
    private int index;
    private EditText myET;

    public CustomTextWatcher(int index, String lineID, EditText myET){
        this.index = index;
        this.lineID = lineID;
        this.myET = myET;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        int suffixLen = SessionClass.getParam("barcodeSuffix").length();
        AllLinesData.setItemParams(lineID, (index), s.toString());
        if (s.length() > 3) {
            if (s.toString().substring(s.length() - suffixLen, s.length()).equals(SessionClass.getParam("barcodeSuffix"))) {
                AllLinesData.setItemParams(lineID, (index) , s.toString().substring(0, s.toString().length() - suffixLen));
                myET.setText(s.toString().substring(0, s.toString().length() - suffixLen));
            }
        }

    }
}
