package hu.selester.android.webstockandroid.Objects;

public class XD_ItemsParameters {

    private Long item_id;
    private int item_sumpcs;
    private int item_pcs;
    private float item_weight;
    private float item_length;
    private float item_width;
    private float item_height;

    public XD_ItemsParameters(Long item_id, int item_sumpcs,int item_pcs, float item_weight, float item_length, float item_width, float item_height) {
        this.item_id = item_id;
        this.item_pcs = item_pcs;
        this.item_weight = item_weight;
        this.item_length = item_length;
        this.item_width = item_width;
        this.item_height = item_height;
        this.item_sumpcs = item_sumpcs;
    }

    public Long getItem_id() {
        return item_id;
    }

    public void setItem_id(Long item_id) {
        this.item_id = item_id;
    }

    public int getItem_sumpcs() {
        return item_sumpcs;
    }

    public void setItem_sumpcs(int item_sumpcs) {
        this.item_sumpcs = item_sumpcs;
    }

    public int getItem_pcs() {
        return item_pcs;
    }

    public void setItem_pcs(int item_pcs) {
        this.item_pcs = item_pcs;
    }

    public float getItem_weight() {
        return item_weight;
    }

    public void setItem_weight(float item_weight) {
        this.item_weight = item_weight;
    }

    public float getItem_length() {
        return item_length;
    }

    public void setItem_length(float item_length) {
        this.item_length = item_length;
    }

    public float getItem_width() {
        return item_width;
    }

    public void setItem_width(float item_width) {
        this.item_width = item_width;
    }

    public float getItem_height() {
        return item_height;
    }

    public void setItem_height(float item_height) {
        this.item_height = item_height;
    }

    @Override
    public String toString() {
        return "XD_ItemsParameters{" +
                "item_id=" + item_id +
                ", item_sumpcs=" + item_sumpcs +
                ", item_pcs=" + item_pcs +
                ", item_weight=" + item_weight +
                ", item_length=" + item_length +
                ", item_width=" + item_width +
                ", item_height=" + item_height +
                '}';
    }
}
