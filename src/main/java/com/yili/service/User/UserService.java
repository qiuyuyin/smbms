package com.yili.service.User;

import com.yili.pojo.User;

import java.sql.Connection;
import java.util.List;

public interface UserService {
    public User login(String userCode, String userPassword);
    public boolean updatePwd(int id,String password);
    public int getUserCount(String userName,int userRole);
    public List<User> getUserList(String userName , int userRole, int currentPage, int pageSize);

}
