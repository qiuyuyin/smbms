package com.yili.servlet.user;

import com.alibaba.fastjson.JSONArray;
import com.mysql.cj.util.StringUtils;
import com.yili.pojo.Role;
import com.yili.pojo.User;
import com.yili.service.Role.RoleServiceImpl;
import com.yili.service.User.UserServiceImpl;
import com.yili.util.Constants;
import com.yili.util.PageSupport;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getParameter("method");
        if (method != null && method.equals("savepwd")) {
            this.updatePwd(req, resp);
        } else if (method != null && method.equals("pwdmodify")) {
            this.getPwdByUserId(req, resp);
        } else if (method != null && method.equals("query")) {
            this.query(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    private void updatePwd(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Object attribute = req.getSession().getAttribute(Constants.USER_SESSION);
        String newpassword = req.getParameter("newpassword");
        boolean flag = false;
        if (attribute != null && !StringUtils.isNullOrEmpty(newpassword)) {
            UserServiceImpl userService = new UserServiceImpl();

            flag = userService.updatePwd(((User) attribute).getId(), newpassword);
            if (flag) {
                req.setAttribute(Constants.USER_MESSAGE, "修改密码成功，请退出并重新登录！");
                req.getSession().removeAttribute(Constants.USER_SESSION);
            } else {
                req.setAttribute(Constants.USER_MESSAGE, "修改密码失败！");
            }
        } else {
            req.setAttribute(Constants.USER_MESSAGE, "修改密码失败！");
        }
        req.getRequestDispatcher("pwdmodify.jsp").forward(req, resp);
    }

    private void getPwdByUserId(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Object attribute = req.getSession().getAttribute(Constants.USER_SESSION);
        String oldpassword = req.getParameter("oldpassword");
        Map<String, String> resultMap = new HashMap<>();
        if (attribute == null) {
            resultMap.put("result", "sessionerror");
        } else if (StringUtils.isNullOrEmpty(oldpassword)) {
            resultMap.put("result", "error");
        } else {
            String userPassword = ((User) attribute).getUserPassword();
            if (userPassword.equals(oldpassword)) {
                resultMap.put("result", "true");
            } else resultMap.put("result", "false");
        }

        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        writer.write(JSONArray.toJSONString(resultMap));
        writer.flush();
        writer.close();

    }

    private void query(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String queryname = req.getParameter("queryname");
        String queryUserRole = req.getParameter("queryUserRole");
        String pageIndex = req.getParameter("pageIndex");
        int queryRole = 0;
        int pageSize = 5;
        int currentPage = 1;
        List<User> users = null;
        if (queryname == null) {
            queryname = "";
        }
        if (queryUserRole != null && !queryUserRole.equals("")) {
            queryRole = Integer.parseInt(queryUserRole);
        }
        if (pageIndex != null) {
            currentPage = Integer.parseInt(pageIndex);
        }
        UserServiceImpl userService = new UserServiceImpl();

        int userCount = userService.getUserCount(queryname, queryRole);

        PageSupport pageSupport = new PageSupport();
        pageSupport.setPageSize(pageSize);
        pageSupport.setCurrentPageNo(currentPage);
        pageSupport.setTotalCount(userCount);
        int totalPageCount = pageSupport.getTotalPageCount();

        if (currentPage < 1) {
            currentPage = 1;
        } else if (currentPage > totalPageCount) {
            currentPage = totalPageCount;
        }

        users = userService.getUserList(queryname, queryRole, currentPage, pageSize);

        RoleServiceImpl roleService = new RoleServiceImpl();
        List<Role> roles= roleService.getRoleList();

        req.setAttribute("userList",users);
        req.setAttribute("roleList",roles);
        req.setAttribute("queryUserName",queryname);
        req.setAttribute("queryUserRole",queryRole);
        req.setAttribute("totalCount",userCount);
        req.setAttribute("currentPageNo",currentPage);
        req.setAttribute("totalPageCount",totalPageCount);
        req.getRequestDispatcher("userlist.jsp").forward(req, resp);


    }
}
