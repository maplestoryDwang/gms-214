package net.swordie.ms.traceking;

/**
 * @author dwang
 * @version 1.0.0
 * @Title
 * @ClassName TraceKillItem.java
 * @Description item信息
 * @createTime 2025-11-20 07:52
 */

public class TraceKingGold {

    private int itemId;
    private int qr;
    private String qrex;


    public TraceKingGold(int itemId, int qr, String qrex) {
        this.itemId = itemId;
        this.qr = qr;
        this.qrex = qrex;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getQr() {
        return qr;
    }

    public void setQr(int qr) {
        this.qr = qr;
    }

    public String getQrex() {
        return qrex;
    }

    public void setQrex(String qrex) {
        this.qrex = qrex;
    }
}
