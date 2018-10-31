package hu.selester.android.webstockandroid.Objects;

public class MainMenuItem {

    private int menuid;
    private String label;
    private int icon;
    private String helpText;

    public MainMenuItem(int menuid, String label, int icon, String helpText) {
        this.menuid = menuid;
        this.label = label;
        this.icon = icon;
        this.helpText = helpText;
    }

    public int getMenuid() {
        return menuid;
    }

    public void setMenuid(int menuid) {
        this.menuid = menuid;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getHelpText() {
        return helpText;
    }

    public void setHelpText(String helpText) {
        this.helpText = helpText;
    }
}
