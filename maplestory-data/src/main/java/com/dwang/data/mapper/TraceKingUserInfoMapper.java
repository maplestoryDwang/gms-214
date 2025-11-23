package com.dwang.data.mapper;

import com.dwang.dao.TraceKingUserInfo;

/**
 * @author dwang
 * @version 1.0.0
 * @Title
 * @ClassName TraceKillUserInfoMapper.java
 * @Description TODO
 * @createTime 2025-11-23 20:39
 */

public interface TraceKingUserInfoMapper {

    TraceKingUserInfo selectById(int id);

    TraceKingUserInfo selectByCharId(int id);

    int insert(TraceKingUserInfo info);

    int update(TraceKingUserInfo info);

    int updateByCharId(TraceKingUserInfo info);
}
