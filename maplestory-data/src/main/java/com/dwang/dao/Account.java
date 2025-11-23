package com.dwang.dao;

import org.apache.ibatis.mapping.FetchType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author dwang
 * @version 1.0.0
 * @Title
 * @ClassName Accuntion.java
 * @Description TODO
 * @createTime 2025-11-23 20:18
 */


import java.time.LocalDateTime;

public class Account {
    private Integer id;
    private Integer worldId;
    private Integer userId;
    private Integer trunkId;
    private Integer nxCredit;
    private Integer friendshipPoints;
    private Integer dojoPoints;
    private Integer shipLevel;
    private Integer shipExp;
    private Integer monsterCollectionId;
    private Integer employeeTrunkId;
    private Integer unionId;
    private LocalDateTime secondaryPendantEndDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getWorldId() {
        return worldId;
    }

    public void setWorldId(Integer worldId) {
        this.worldId = worldId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getTrunkId() {
        return trunkId;
    }

    public void setTrunkId(Integer trunkId) {
        this.trunkId = trunkId;
    }

    public Integer getNxCredit() {
        return nxCredit;
    }

    public void setNxCredit(Integer nxCredit) {
        this.nxCredit = nxCredit;
    }

    public Integer getFriendshipPoints() {
        return friendshipPoints;
    }

    public void setFriendshipPoints(Integer friendshipPoints) {
        this.friendshipPoints = friendshipPoints;
    }

    public Integer getDojoPoints() {
        return dojoPoints;
    }

    public void setDojoPoints(Integer dojoPoints) {
        this.dojoPoints = dojoPoints;
    }

    public Integer getShipLevel() {
        return shipLevel;
    }

    public void setShipLevel(Integer shipLevel) {
        this.shipLevel = shipLevel;
    }

    public Integer getShipExp() {
        return shipExp;
    }

    public void setShipExp(Integer shipExp) {
        this.shipExp = shipExp;
    }

    public Integer getMonsterCollectionId() {
        return monsterCollectionId;
    }

    public void setMonsterCollectionId(Integer monsterCollectionId) {
        this.monsterCollectionId = monsterCollectionId;
    }

    public Integer getEmployeeTrunkId() {
        return employeeTrunkId;
    }

    public void setEmployeeTrunkId(Integer employeeTrunkId) {
        this.employeeTrunkId = employeeTrunkId;
    }

    public Integer getUnionId() {
        return unionId;
    }

    public void setUnionId(Integer unionId) {
        this.unionId = unionId;
    }

    public LocalDateTime getSecondaryPendantEndDate() {
        return secondaryPendantEndDate;
    }

    public void setSecondaryPendantEndDate(LocalDateTime secondaryPendantEndDate) {
        this.secondaryPendantEndDate = secondaryPendantEndDate;
    }
}
