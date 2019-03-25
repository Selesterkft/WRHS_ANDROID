package hu.selester.android.webstockandroid.Objects;

public class XD_ItemsParameters {

    private int item_pcs;
    private int item_weight;
    private int item_length;
    private int item_width;
    private int getItem_height;

    public XD_ItemsParameters(int item_pcs, int item_weight, int item_length, int item_width, int getItem_height) {
        this.item_pcs = item_pcs;
        this.item_weight = item_weight;
        this.item_length = item_length;
        this.item_width = item_width;
        this.getItem_height = getItem_height;
    }

    public int getItem_pcs() {
        return item_pcs;
    }

    public void setItem_pcs(int item_pcs) {
        this.item_pcs = item_pcs;
    }

    public int getItem_weight() {
        return item_weight;
    }

    public void setItem_weight(int item_weight) {
        this.item_weight = item_weight;
    }

    public int getItem_length() {
        return item_length;
    }

    public void setItem_length(int item_length) {
        this.item_length = item_length;
    }

    public int getItem_width() {
        return item_width;
    }

    public void setItem_width(int item_width) {
        this.item_width = item_width;
    }

    public int getGetItem_height() {
        return getItem_height;
    }

    public void setGetItem_height(int getItem_height) {
        this.getItem_height = getItem_height;
    }

    @Override
    public String toString() {
        return "XD_ItemsParameters{" +
                "item_pcs=" + item_pcs +
                ", item_weight=" + item_weight +
                ", item_length=" + item_length +
                ", item_width=" + item_width +
                ", getItem_height=" + getItem_height +
                '}';
    }
}
