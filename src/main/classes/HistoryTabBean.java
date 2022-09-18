public class HistoryTabBean {
    private int id;
    private int userid;
    private String dataname;
    private String time;
    private String state;
    private String beizhu;
    public HistoryTabBean(int id, int userid, String dataname, String time, String state, String beizhu) {
        this.id = id;
        this.userid = userid;
        this.dataname = dataname;
        this.time = time;
        this.state = state;
        this.beizhu = beizhu;
    }

    public HistoryTabBean(int id, String dataname, String beizhu) {
        this.id = id;
        this.dataname = dataname;
        this.beizhu = beizhu;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getDataname() {
        return dataname;
    }

    public void setDataname(String dataname) {
        this.dataname = dataname;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getBeizhu() {
        return beizhu;
    }

    public void setBeizhu(String beizhu) {
        this.beizhu = beizhu;
    }
}
