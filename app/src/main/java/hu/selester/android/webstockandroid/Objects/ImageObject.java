package hu.selester.android.webstockandroid.Objects;

public class ImageObject {
    private long id;
    private String date;
    private int moveNum;
    private int tranCode;
    private int userid;
    private String terminal;

    public ImageObject(long id, String date, int moveNum, int tranCode, int userid, String terminal) {
        this.id         = id;
        this.date       = date;
        this.moveNum    = moveNum;
        this.tranCode   = tranCode;
        this.userid     = userid;
        this.terminal   = terminal;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getMoveNum() {
        return moveNum;
    }

    public void setMoveNum(int moveNum) {
        this.moveNum = moveNum;
    }

    public int getTranCode() {
        return tranCode;
    }

    public void setTranCode(int tranCode) {
        this.tranCode = tranCode;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "ImageObject{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", moveNum=" + moveNum +
                ", tranCode=" + tranCode +
                ", userid=" + userid +
                ", terminal='" + terminal + '\'' +
                '}';
    }
}
