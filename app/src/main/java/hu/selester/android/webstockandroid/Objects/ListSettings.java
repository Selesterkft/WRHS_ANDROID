package hu.selester.android.webstockandroid.Objects;

public class ListSettings {

    private String[] headerText;
    private int[] headerWidth;
    private String[] columnName;
    private boolean checkbox;

    public ListSettings(String[] headerText, int[] headerWidth, String[] columnName, boolean checkbox) {
        this.headerText = headerText;
        this.headerWidth = headerWidth;
        this.columnName = columnName;
        this.checkbox = checkbox;
    }

    public boolean isCheckbox() {
        return checkbox;
    }

    public void setCheckbox(boolean checkbox) {
        this.checkbox = checkbox;
    }

    public String[] getHeaderText() {
        return headerText;
    }

    public void setHeaderText(String[] headerText) {
        this.headerText = headerText;
    }

    public int[] getHeaderWidth() {
        return headerWidth;
    }

    public void setHeaderWidth(int[] headerWidth) {
        this.headerWidth = headerWidth;
    }

    public String[] getColumnName() {
        return columnName;
    }

    public void setColumnName(String[] columnName) {
        this.columnName = columnName;
    }
}

