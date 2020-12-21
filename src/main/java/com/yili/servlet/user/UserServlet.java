package com.yili.servlet.user;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.mysql.cj.util.StringUtils;
import com.yili.pojo.Role;
import com.yili.pojo.User;
import com.yili.service.Role.RoleServiceImpl;
import com.yili.service.User.UserService;
import com.yili.service.User.UserServiceImpl;
import com.yili.util.Constants;
import com.yili.util.PageSupport;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
        } else if (method != null && method.equals("modifyexe")) {
            this.modify(req, resp);
        } else if (method != null && method.equals("modify")) {
            this.getUserById(req, resp, "usermodify.jsp");
        } else if (method != null && method.equals("getrolelist")) {
            this.getRoleList(req, resp);
        } else if (method != null && method.equals("deluser")) {
            this.delUserById(req, resp);
        } else if (method != null && method.equals("ucexist")) {
            this.userCodeExist(req, resp);
        } else if (method != null && method.equals("view")) {
            this.getUserById(req, resp, "userview.jsp");
        } else if (method != null && method.equals("add")) {
            this.addUser(req, resp);
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
        List<Role> roles = roleService.getRoleList();

        req.setAttribute("userList", users);
        req.setAttribute("roleList", roles);
        req.setAttribute("queryUserName", queryname);
        req.setAttribute("queryUserRole", queryRole);
        req.setAttribute("totalCount", userCount);
        req.setAttribute("currentPageNo", currentPage);
        req.setAttribute("totalPageCount", totalPageCount);
        req.getRequestDispatcher("userlist.jsp").forward(req, resp);


    }

    private void modify(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("uid");
        String userName = req.getParameter("userName");
        String gender = req.getParameter("gender");
        String birthday = req.getParameter("birthday");
        String phone = req.getParameter("phone");
        String address = req.getParameter("address");
        String userRole = req.getParameter("userRole");

        User user = new User();
        user.setId(Integer.parseInt(id));
        user.setUserName(userName);
        user.setGender(Integer.parseInt(gender));
        try {
            user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        user.setPhone(phone);
        user.setAddress(address);
        user.setUserRole(Integer.valueOf(userRole));
        user.setModifyBy(((User) req.getSession().getAttribute(Constants.USER_SESSION)).getId());
        user.setModifyDate(new Date());

        UserServiceImpl userService = new UserServiceImpl();
        if (userService.modifyUser(user)) {
            resp.sendRedirect(req.getContextPath() + "/jsp/user.do?method=query");
        } else {
            req.getRequestDispatcher("usermodify.jsp").forward(req, resp);
        }
    }

    private void getUserById(HttpServletRequest req, HttpServletResponse resp, String url) throws ServletException, IOException {
        String uid = req.getParameter("uid");
        if (!StringUtils.isNullOrEmpty(uid)) {
            UserServiceImpl userService = new UserServiceImpl();
            int id = Integer.parseInt(uid);
            User user = userService.getUerById(id);
            req.setAttribute("user", user);
            req.getRequestDispatcher(url).forward(req, resp);
        }
    }

    private void getRoleList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Role> roles = null;
        RoleServiceImpl roleService = new RoleServiceImpl();
        roles = roleService.getRoleList();
        PrintWriter writer = resp.getWriter();
        resp.setContentType("application/json");
        writer.write(JSONArray.toJSONString(roles));
        writer.flush();
        writer.close();
    }

    private void delUserById(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userid = req.getParameter("uid");
        int id = 0;
        if (userid != null) {
            id = Integer.parseInt(userid);
        }
        HashMap<String, String> resultMap = new HashMap<>();
        if (id < 0) {
            resultMap.put("delResult", "notexist");
        } else {
            UserServiceImpl userService = new UserServiceImpl();
            if (userService.delUserById(id)) {
                resultMap.put("delResult", "true");
                System.out.println("删除成功");
            } else {
                resultMap.put("delResult", "false");
                System.out.println("删除失败");
            }

        }
        //将resultMap转换为json对象进行输出
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        writer.write(JSONArray.toJSONString(resultMap));
        writer.flush();
        writer.close();

    }

    private void userCodeExist(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userCode = req.getParameter("userCode");
        HashMap<String, String> resultMap = new HashMap<>();
        if (StringUtils.isNullOrEmpty(userCode)) {
            resultMap.put("userCode", "exist");
        } else {
            UserServiceImpl userService = new UserServiceImpl();
            User user = userService.userExist(userCode);
            if (user != null) {
                resultMap.put("userCode", "exist");
            } else {
                resultMap.put("userCode", "notexist");
            }
        }
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        writer.write(JSONArray.toJSONString(resultMap));
        writer.flush();
        writer.close();

    }

    private void addUser(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userCode = req.getParameter("userCode");
        String userName = req.getParameter("userName");
        String userPassword = req.getParameter("userPassword");
        String gender = req.getParameter("gender");
        String birthday = req.getParameter("birthday");
        String phone = req.getParameter("phone");
        String address = req.getParameter("address");
        String userRole = req.getParameter("userRole");

        User user = new User();
        user.setUserCode(userCode);
        user.setUserName(userName);
        user.setUserPassword(userPassword);
        user.setAddress(address);
        try {
            user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        user.setGender(Integer.parseInt(gender));
        user.setPhone(phone);
        user.setUserRole(Integer.parseInt(userRole));
        user.setCreationDate(new Date());
        user.setCreatedBy(((User) req.getSession().getAttribute(Constants.USER_SESSION)).getId());

        UserServiceImpl userService = new UserServiceImpl();
        if (userService.addUser(user)) {
            resp.sendRedirect(req.getContextPath() + "/jsp/user.do?method=query");
        } else {
            req.getRequestDispatcher("useradd.jsp").forward(req, resp);
        }


    }


}
