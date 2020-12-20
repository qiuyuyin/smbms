package com.yili.dao.user;

import com.yili.pojo.User;
import com.yili.util.Constants;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface UserDao {
    //得到要进行登录的用户
    public User getLoginUser(Connection connection,String userCode) throws Exception;
   //修改当前用户的密码
    public int updatePwd(Connection connection,int id,String password) throws Exception;
    //查询用户总数，可以根据姓名和职业等级进行查询。
    public int getUserCount(Connection connection,String username ,int userRole) throws Exception;
    //得到用户列表
    public List<User> getUserList(Connection connection, String userName , int userRole, int currentPage, int pageSize) throws Exception;

}
