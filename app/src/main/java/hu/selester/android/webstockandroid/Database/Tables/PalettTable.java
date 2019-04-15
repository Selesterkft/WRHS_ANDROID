package hu.selester.android.webstockandroid.Database.Tables;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class PalettTable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String palett;
    private String collect;

    public PalettTable(String palett, String collect) {
        this.palett = palett;
        this.collect = collect;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPalett() {
        return palett;
    }

    public void setPalett(String palett) {
        this.palett = palett;
    }

    public String getCollect() {
        return collect;
    }

    public void setCollect(String collect) {
        this.collect = collect;
    }

    @Override
    public String toString() {
        return "PalettTable{" +
                "  id=" + id +
                ", palett='" + palett + '\'' +
                ", collect='" + collect + '\'' +
                '}';
    }
}
