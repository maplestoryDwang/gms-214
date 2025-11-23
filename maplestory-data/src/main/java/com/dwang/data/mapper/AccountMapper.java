package com.dwang.data.mapper;

import com.dwang.dao.Account;
import org.apache.ibatis.annotations.Select;

/**
 * @author dwang
 * @version 1.0.0
 * @Title
 * @ClassName AccountMapper.java
 * @Description TODO
 * @createTime 2025-11-23 20:13
 */

public interface AccountMapper {

    @Select("SELECT * FROM accounts WHERE id = #{id}")
    Account getAccount(int id);
}
