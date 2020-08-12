package hu.selester.android.webstockandroid.Objects;

public class TreeViewGroup {

    private String text;
    private int count;
    private String secondId;

    public TreeViewGroup(String text, int count) {
        this.text = text;
        this.count = count;
    }

    public TreeViewGroup(String text, int count, String secondId) {
        this.text = text;
        this.count = count;
        this.secondId = secondId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getSecondId() {
        return secondId;
    }

    public void setSecondId(String secondId) {
        this.secondId = secondId;
    }

    @Override
    public String toString() {
        return "TreeViewGroup{" +
                "text='" + text + '\'' +
                ", count=" + count +
                ", secondId='" + secondId + '\'' +
                '}';
    }
}
