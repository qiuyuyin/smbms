package com.yili.service.User;

import com.yili.dao.BaseDao;
import com.yili.dao.user.UserDao;
import com.yili.dao.user.UserDaoImpl;
import com.yili.pojo.Role;
import com.yili.pojo.User;
import com.yili.service.Role.RoleServiceImpl;
import com.yili.servlet.user.UserServlet;
import org.junit.jupiter.api.Test;

import javax.xml.stream.FactoryConfigurationError;
import java.awt.geom.FlatteningPathIterator;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
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

    @Override
    public boolean modifyUser(User user) {
        Connection connection = null;
        boolean flag = false;
        try {
            connection = BaseDao.getConnection();
            if(userDao.modifyUser(connection,user)>0) flag = true;

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection,null,null);
        }
        return flag;
    }

    @Override
    public User getUerById(int id) {
        Connection connection = null;
        User user = null;
        try {
            connection = BaseDao.getConnection();
            user= userDao.getUerById(connection, id);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection,null,null);
        }
        return user;
    }

    @Override
    public boolean delUserById(int id) {
        Connection connection = null;
        boolean flag = false;
        try {
            connection = BaseDao.getConnection();
            if(userDao.delUserById(connection,id)>0){
                flag = true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection,null,null);
        }
        return flag;
    }

    @Override
    public User userExist(String userCode) {
        Connection connection = null;
        User user = null;
        try {
            connection = BaseDao.getConnection();
            user = userDao.getLoginUser(connection, userCode);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection,null,null);
        }
        return user;
    }

    @Override
    public boolean addUser(User user) {
        Connection connection = null;
        boolean flag = false;
        try {
            connection = BaseDao.getConnection();
            if(userDao.addUser(connection, user)>0){
                flag = true;
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection,null,null);
        }
        return flag;
    }

    @Test
    public void test(){
        UserServiceImpl userService = new UserServiceImpl();
        boolean b = userService.delUserById(16);
        System.out.println(b);
    }


}
