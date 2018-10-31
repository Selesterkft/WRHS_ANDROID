package hu.selester.android.webstockandroid.Objects;

public class TaskObject {

    private int id;
    private int imageResInt;
    private String text;

    public TaskObject(int id, int imageResInt, String text) {
        this.id = id;
        this.imageResInt = imageResInt;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImageResInt() {
        return imageResInt;
    }

    public void setImageResInt(int imageResInt) {
        this.imageResInt = imageResInt;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "TaskObject{" +
                "id=" + id +
                ", imageResInt=" + imageResInt +
                ", text='" + text + '\'' +
                '}';
    }
}
