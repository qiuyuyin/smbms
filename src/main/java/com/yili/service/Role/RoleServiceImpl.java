package com.yili.service.Role;

import com.yili.dao.BaseDao;
import com.yili.dao.Role.RoleDao;
import com.yili.dao.Role.RoleDaoImpl;
import com.yili.pojo.Role;

import java.sql.Connection;
import java.util.List;

public class RoleServiceImpl implements RoleService{
    private RoleDao roleDao;
    public RoleServiceImpl(){
        roleDao=new RoleDaoImpl();
    }
    @Override
    public List<Role> getRoleList() {
        List<Role> roles = null;
        Connection connection = null;
        try {
            connection = BaseDao.getConnection();
            roles = roleDao.getRoleList(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection,null,null);
        }
        return roles;
    }
}
