package hu.selester.android.webstockandroid.Objects;

public class XD_ItemsParameters {

    private Long item_id;
    private int item_sumpcs;
    private int item_pcs;
    private Float item_weight;
    private Float item_length;
    private Float item_width;
    private Float item_height;

    public XD_ItemsParameters(Long item_id, int item_sumpcs,int item_pcs, Float item_weight, Float item_length, Float item_width, Float item_height) {
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

    public Float getItem_weight() {
        return item_weight;
    }

    public void setItem_weight(Float item_weight) {
        this.item_weight = item_weight;
    }

    public Float getItem_length() {
        return item_length;
    }

    public void setItem_length(Float item_length) {
        this.item_length = item_length;
    }

    public Float getItem_width() {
        return item_width;
    }

    public void setItem_width(Float item_width) {
        this.item_width = item_width;
    }

    public Float getItem_height() {
        return item_height;
    }

    public void setItem_height(Float item_height) {
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
