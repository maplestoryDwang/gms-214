package net.swordie.ms.tracekill;

/**
 * @author dwang
 * @version 1.0.0
 * @Title
 * @ClassName TraceKillItemInfo.java
 * @Description 东西信息
 * @createTime 2025-11-18 14:25
 */

public class TraceKillItemInfo {

    private int id;
    private String itemName;
    private int buyPrices;
    private int sellPrices;
    private int qr;
    private int qrEx;

    public TraceKillItemInfo(int id, int buyPrices, int sellPrices, int qr, int qrEx) {
        this.id = id;
        this.buyPrices = buyPrices;
        this.sellPrices = sellPrices;
        this.qr = qr;
        this.qrEx = qrEx;
    }


    public TraceKillItemInfo(int id, String itemName, int buyPrices, int sellPrices, int qr, int qrEx) {
        this.id = id;
        this.itemName = itemName;
        this.buyPrices = buyPrices;
        this.sellPrices = sellPrices;
        this.qr = qr;
        this.qrEx = qrEx;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBuyPrices() {
        return buyPrices;
    }

    public void setBuyPrices(int buyPrices) {
        this.buyPrices = buyPrices;
    }

    public int getSellPrices() {
        return sellPrices;
    }

    public void setSellPrices(int sellPrices) {
        this.sellPrices = sellPrices;
    }

    public int getQr() {
        return qr;
    }

    public void setQr(int qr) {
        this.qr = qr;
    }

    public int getQrEx() {
        return qrEx;
    }

    public void setQrEx(int qrEx) {
        this.qrEx = qrEx;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
