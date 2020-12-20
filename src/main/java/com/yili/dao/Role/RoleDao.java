package com.yili.dao.Role;

import com.yili.pojo.Role;

import java.sql.Connection;
import java.util.List;

public interface RoleDao {
    public List<Role> getRoleList(Connection connection) throws Exception;
}
