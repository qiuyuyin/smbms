package com.yili.service.User;

import com.yili.dao.BaseDao;
import com.yili.dao.user.UserDao;
import com.yili.dao.user.UserDaoImpl;
import com.yili.pojo.Role;
import com.yili.pojo.User;
import com.yili.service.Role.RoleServiceImpl;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class UserServiceImpl implements UserService{
    private UserDao userDao;
    public UserServiceImpl(){
        userDao=new UserDaoImpl();
    }

    @Override
    public List<User> getUserList(String userName, int userRole, int currentPage, int pageSize) {
        Connection connection = null;
        List<User> users = null;
        try {
            connection  = BaseDao.getConnection();
            users = userDao.getUserList(connection, userName, userRole, currentPage, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection,null, null);
        }
        return users;
    }

    @Override
    public boolean updatePwd(int id, String password) {

        boolean flag=false;
        Connection connection=null;
        try {
            connection = BaseDao.getConnection();
            if(userDao.updatePwd(connection,id,password)>0){
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection,null,null);
        }
        return flag;
    }

    @Override
    public int getUserCount(String userName, int userRole) {
        int count=0;
        Connection connection = null;
        try {
            connection = BaseDao.getConnection();
            count = userDao.getUserCount(connection, userName, userRole);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection,null,null);
        }
        return count;
    }

    @Override
    public User login(String userCode, String userPassword) {
        Connection connection = null;
        User user = null;
        try {
            connection= BaseDao.getConnection();
            user = userDao.getLoginUser(connection, userCode);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection,null,null);
        }
        //匹配密码
        if(null != user){
            if(!user.getUserPassword().equals(userPassword))
                user = null;
        }
        return user;
    }

    @Test
    public void test(){
        UserServiceImpl userService = new UserServiceImpl();
        List<User> userList = userService.getUserList(null, 0, 1, 5);
        for (User user : userList) {
            System.out.println("UserName:"+user.getUserName());
        }
    }


}
