package com.yili.dao.user;

import com.mysql.cj.util.StringUtils;
import com.yili.dao.BaseDao;
import com.yili.pojo.User;
import org.w3c.dom.ls.LSException;
import org.w3c.dom.ls.LSInput;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements UserDao {

    @Override
    public int getUserCount(Connection connection, String username, int userRole) throws Exception{
        int flag = 0;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        if(connection!=null){
            StringBuffer sql = new StringBuffer();
            sql.append("select count(1) as count from smbms_user user,smbms_role role where user.userRole = role.id");
            List<Object> list = new ArrayList<Object>();
            if(!StringUtils.isNullOrEmpty(username)){
                sql.append(" and user.userName like ?");
                list.add("%"+username+"%");
            }
            if(userRole>0){
                sql.append(" and role.id like ?");
                list.add(userRole);
            }
            Object[] params = list.toArray();
            rs = BaseDao.execute(connection, pstm, rs, sql.toString(), params);
            if(rs.next()){
                flag=rs.getInt("count");
            }
            BaseDao.closeResource(null,pstm,rs);//这里的pstm为null不用管他
        }
        return flag;
    }

    @Override
    public List<User> getUserList(Connection connection, String userName, int userRole, int currentPage, int pageSize) throws Exception {
        List<User> users = new ArrayList<>();
        PreparedStatement pstm = null;
        ResultSet rs = null;
        if(connection!=null){
            StringBuffer sql = new StringBuffer();
            sql.append("select u.*,r.roleName as userRoleName from smbms_user u,smbms_role r where u.userRole = r.id");
            ArrayList<Object> list = new ArrayList<>();
            if(!StringUtils.isNullOrEmpty(userName)){
                sql.append(" and u.userName like ?");
                list.add("%"+userName+"%");
            }
            if(userRole>0){
                sql.append(" and r.id like ?");
                list.add(userRole);
            }
            sql.append(" order by creationDate DESC limit ?,?");
            int currentPageNo = (currentPage-1)*pageSize;
            list.add(currentPageNo);
            list.add(pageSize);
            Object[] params = list.toArray();
            rs = BaseDao.execute(connection, pstm, rs, sql.toString(), params);
            while (rs.next()){
                User _user = new User();
                _user.setId(rs.getInt("id"));
                _user.setUserCode(rs.getString("userCode"));
                _user.setUserName(rs.getString("userName"));
                _user.setGender(rs.getInt("gender"));
                _user.setBirthday(rs.getDate("birthday"));
                _user.setPhone(rs.getString("phone"));
                _user.setUserRole(rs.getInt("userRole"));
                _user.setUserRoleName(rs.getString("userRoleName"));
                users.add(_user);
            }
            BaseDao.closeResource(null,pstm,rs);
        }
        return users;
    }

    @Override
    public int updatePwd(Connection connection, int id, String password) throws Exception {

        int flag = 0;
        PreparedStatement pstm = null;
        if (connection != null) {
            String sql = "update smbms_user set userPassword= ? where id = ?";
            Object[] params = {password, id};
            flag = BaseDao.execute(connection, pstm, sql, params);
            BaseDao.closeResource(null, pstm, null);
        }
        return flag;
    }

    @Override
    public User getLoginUser(Connection connection, String userCode) throws Exception {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        User user = null;
        if (connection != null) {
            String sql = "select * from smbms_user where userCode=?";
            Object[] params = {userCode};
            rs = BaseDao.execute(connection, pstm, rs, sql, params);
            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUserCode(rs.getString("userCode"));
                user.setUserName(rs.getString("userName"));
                user.setUserPassword(rs.getString("userPassword"));
                user.setGender(rs.getInt("gender"));
                user.setBirthday(rs.getDate("birthday"));
                user.setPhone(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
                user.setUserRole(rs.getInt("userRole"));
                user.setCreatedBy(rs.getInt("createdBy"));
                user.setCreationDate(rs.getTimestamp("creationDate"));
                user.setModifyBy(rs.getInt("modifyBy"));
                user.setModifyDate(rs.getTimestamp("modifyDate"));
            }
            BaseDao.closeResource(null, pstm, rs);
        }

        return user;
    }
}
