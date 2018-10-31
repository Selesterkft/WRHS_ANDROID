package hu.selester.android.webstockandroid.Database.Tables;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class ProductData {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int selid;
    private String prodid;
    private String name;
    private String bar1;
    private Long transactId;

    public ProductData(int selid,String prodid, String name, String bar1, Long transactId) {
        this.selid = selid;
        this.name = name;
        this.bar1 = bar1;
        this.prodid = prodid;
        this.transactId = transactId;
    }

    public String getProdid() {
        return prodid;
    }

    public void setProdid(String prodid) {
        this.prodid = prodid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSelid() {
        return selid;
    }

    public void setSelid(int selid) {
        this.selid = selid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBar1() {
        return bar1;
    }

    public void setBar1(String bar1) {
        this.bar1 = bar1;
    }

    public Long getTransactId() {
        return transactId;
    }

    public void setTransactId(Long transactId) {
        this.transactId = transactId;
    }

    @Override
    public String toString() {
        return "ProductData{" +
                "id=" + id +
                ", selid=" + selid +
                ", prodid=" + prodid +
                ", name='" + name + '\'' +
                ", bar1='" + bar1 + '\'' +
                ", transactId=" + transactId +
                '}';
    }
}
