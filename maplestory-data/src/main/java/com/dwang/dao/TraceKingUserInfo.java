package com.dwang.dao;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dwang
 * @version 1.0.0
 * @Title
 * @ClassName TraceKillUserInfo.java
 * @Description 存放用户信息
 * @createTime 2025-11-18 15:38
 */

public class TraceKingUserInfo {

    private Integer id;
    // 角色id
    private Integer chrid;

    // 当前正在和哪个shop交易
    private Integer shopNpc;

    // 当前承受重量
    private Integer cWeight;

    // 当前金币数量
    private Integer count;

    // 最大承重
    private Integer mWeight;

    // 总容量
    private Integer scount;

    // 配置的工人 例如： "1=4;4=1"
    private String worker;

    // 选择的坐骑
    private Integer ride;

    // 创建时间
    private LocalDateTime createTime;

    // 更新时间
    private LocalDateTime updateTime;

    // 记录购买商品的数量key QR_QRex.  value, 数量
    private Map<String, Integer> itemNum = new HashMap<>();

    private int lastQuestExCode;


    public TraceKingUserInfo() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getChrid() {
        return chrid;
    }

    public void setChrid(Integer chrid) {
        this.chrid = chrid;
    }

    public Integer getShopNpc() {
        return shopNpc;
    }

    public void setShopNpc(Integer shopNpc) {
        this.shopNpc = shopNpc;
    }

    public Integer getcWeight() {
        return cWeight;
    }

    public void setcWeight(Integer cWeight) {
        this.cWeight = cWeight;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getmWeight() {
        return mWeight;
    }

    public void setmWeight(Integer mWeight) {
        this.mWeight = mWeight;
    }

    public Integer getScount() {
        return scount;
    }

    public void setScount(Integer scount) {
        this.scount = scount;
    }

    public String getWorker() {
        return worker;
    }

    public void setWorker(String worker) {
        this.worker = worker;
    }

    public Integer getRide() {
        return ride;
    }

    public void setRide(Integer ride) {
        this.ride = ride;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public Map<String, Integer> getItemNum() {
        return itemNum;
    }

    public void setItemNum(Map<String, Integer> itemNum) {
        this.itemNum = itemNum;
    }

    public int getLastQuestExCode() {
        return lastQuestExCode;
    }

    public void setLastQuestExCode(int lastQuestExCode) {
        this.lastQuestExCode = lastQuestExCode;
    }
}
