package hu.selester.android.webstockandroid.Objects;



public class MessageBoxSettingsObject {

    private String btn1Label;
    private String btn2Label;
    private int dialogStyle;

    public String getBtn1Label() {
        return btn1Label;
    }

    public void setBtn1Label(String btn1Label) {
        this.btn1Label = btn1Label;
    }

    public String getBtn2Label() {
        return btn2Label;
    }

    public void setBtn2Label(String btn2Label) {
        this.btn2Label = btn2Label;
    }

    public int getDialogStyle() {
        return dialogStyle;
    }

    public void setDialogStyle(int dialogStyle) {
        this.dialogStyle = dialogStyle;
    }

    @Override
    public String toString() {
        return "MessageBoxSettingsObject{" +
                ", btn1Label='" + btn1Label + '\'' +
                ", btn2Label='" + btn2Label + '\'' +
                ", dialogStyle=" + dialogStyle +
                '}';
    }
}
