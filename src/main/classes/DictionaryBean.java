public class DictionaryBean {
    private String zdmc;
    private String zdlx;
    private String beizhu;
    private String zdcd;
    private String xsjd;

    public DictionaryBean(String zdmc, String zdlx, String beizhu, String zdcd, String xsjd) {
        this.zdmc = zdmc;
        this.zdlx = zdlx;
        this.beizhu = beizhu;
        this.zdcd = zdcd;
        this.xsjd = xsjd;
    }

    public String getZdmc() {
        return zdmc;
    }

    public void setZdmc(String zdmc) {
        this.zdmc = zdmc;
    }

    public String getZdlx() {
        return zdlx;
    }

    public void setZdlx(String zdlx) {
        this.zdlx = zdlx;
    }

    public String getBeizhu() {
        return beizhu;
    }

    public void setBeizhu(String beizhu) {
        this.beizhu = beizhu;
    }

    public String getZdcd() {
        return zdcd;
    }

    public void setZdcd(String zdcd) {
        this.zdcd = zdcd;
    }

    public String getXsjd() {
        return xsjd;
    }

    public void setXsjd(String xsjd) {
        this.xsjd = xsjd;
    }
}
