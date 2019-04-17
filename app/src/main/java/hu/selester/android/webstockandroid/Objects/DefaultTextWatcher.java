package hu.selester.android.webstockandroid.Objects;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

public class DefaultTextWatcher implements TextWatcher {

    public interface TextChangedEvent{
        public void Changed();
    }

    private EditText myET;
    private TextChangedEvent event;

    public DefaultTextWatcher(EditText myET, TextChangedEvent event){
        this.myET = myET;
        this.event = event;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        try {
            if (s.length() > 3) {
                int suffixLen = SessionClass.getParam("barcodeSuffix").length();
                if (s.toString().substring(s.length() - suffixLen, s.length()).equals(SessionClass.getParam("barcodeSuffix"))) {
                    myET.setText(s.toString().substring(0, s.toString().length() - suffixLen));
                    event.Changed();
                }
            }
        }catch (Exception e){

        }
    }
}
