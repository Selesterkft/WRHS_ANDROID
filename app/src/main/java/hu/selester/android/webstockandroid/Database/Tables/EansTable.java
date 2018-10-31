package hu.selester.android.webstockandroid.Database.Tables;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class EansTable {


     //@PrimaryKey(autoGenerate = true)
  @PrimaryKey
    private int id;
    private String eancode;
    private String eancode2;
    private String time;

    public EansTable(int id, String eancode, String eancode2, String time) {
        this.id = id;
        this.eancode = eancode;
        this.eancode2 = eancode2;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEancode() {
        return eancode;
    }

    public void setEancode(String name) {
        this.eancode = eancode;
    }

    public String getEancode2() {
        return eancode2;
    }

    public void setEancode2(String account) {
        this.eancode2 = eancode2;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String password) {
        this.time = time;
    }



    @Override
    public String toString() {
        return "|" +
               id +"," + eancode + ", " + eancode2 + "," +time ;

    }


//    @Override
//    public String toString() {
//        return "Eans{" +
//                "id=" + id +
//                ", eancode='" + eancode + '\'' +
//                ", eancode2='" + eancode2 + '\'' +
//                ", time='" + time + '\'' +
//                '}';
//    }
}