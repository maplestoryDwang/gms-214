package net.swordie.ms.tracekill;

/**
 * @author dwang
 * @version 1.0.0
 * @Title
 * @ClassName TraceKillUserInfo.java
 * @Description 存放用户信息
 * @createTime 2025-11-18 15:38
 */

public class TraceKillUserInfo {


    // 当前正在和哪个shop交易
    private int shopNpc;



    private int cWeight;
    private int count;
    private int mWeight;
    private int scount;


    public TraceKillUserInfo() {
    }

    public int getShopNpc() {
        return shopNpc;
    }

    public void setShopNpc(int shopNpc) {
        this.shopNpc = shopNpc;
    }

    public int getcWeight() {
        return cWeight;
    }

    public void setcWeight(int cWeight) {
        this.cWeight = cWeight;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getmWeight() {
        return mWeight;
    }

    public void setmWeight(int mWeight) {
        this.mWeight = mWeight;
    }

    public int getScount() {
        return scount;
    }

    public void setScount(int scount) {
        this.scount = scount;
    }
}
