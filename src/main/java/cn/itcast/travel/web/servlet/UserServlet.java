package cn.itcast.travel.web.servlet;

import cn.itcast.travel.domain.ResultInfo;
import cn.itcast.travel.domain.User;
import cn.itcast.travel.service.UserService;
import cn.itcast.travel.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.BeanUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * 这里整合了User模块的所有方法：注册、登录、激活、退出、查询单个对象。
 */
@WebServlet("/user/*")
public class UserServlet extends BaseServlet {

    /**
     * 声明UserService业务对象
     */
    private UserService service = new UserServiceImpl();

    /**
     * 1.注册功能
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    public void regist(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //验证校验
        String check = req.getParameter("check");
        //从session中获取验证码
        HttpSession session = req.getSession();
        String checkcode_server = (String) session.getAttribute("CHECKCODE_SERVER");
        //为保证验证码只可以用一次
        session.removeAttribute("CHECKCODE_SERVER");
        //比较
        //这里一般都是忽略大小写
        if (checkcode_server == null || checkcode_server.equalsIgnoreCase(check)) {
            //验证码错误
            ResultInfo info = new ResultInfo();
            //注册失败
            info.setFlag(false);
            info.setErrorMsg("验证码错误");
            //将info对象序列化为json
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(info);
            resp.setContentType("application/json;charset=utf-8");
            resp.getWriter().write(json);
            return;
        }
        //获取数据
        Map<String, String[]> map = req.getParameterMap();

        //封装对象
        User user = new User();
        try {
            BeanUtils.populate(user, map);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        //调用service完成注册
        boolean flag = service.regist(user);
        ResultInfo info = new ResultInfo();

        //响应结果
        if (flag) {
            //注册成功
            info.setFlag(true);
        }else {
            //注册失败
            info.setFlag(false);
            info.setErrorMsg("注册失败！");
        }

        //将info对象序列化为json
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(info);

        //将json数据写回客户端,设置content-type
        resp.setContentType("application/json;charset=utf-8");
        resp.getWriter().write(json);

    }

    /**
     * 2.登录功能
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    public void login(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //获取用户名和密码信息
        Map<String,String[]> map = req.getParameterMap();
        //封装user对象
        User user = new User();
        try {
            BeanUtils.populate(user,map);
        }catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        //调用service查询
        User u = service.login(user);

        ResultInfo info = new ResultInfo();

        //判断user是否存在
        if (u==null) {
            //用户名或密码错误
            info.setFlag(false);
            info.setErrorMsg("用户名或密码错误");
        }

        //判断user是否激活
        if (u!=null&&"Y".equals(u.getStatus())) {
            //用户尚未激活
            info.setFlag(false);
            info.setErrorMsg("您尚未激活，请激活");
        }

        //判断登录成功
        if (u!=null&&"Y".equals(u.getStatus())) {
            req.getSession().setAttribute("user",u);
            //登录成功
            info.setFlag(true);
        }

        //响应数据
        ObjectMapper objectMapper = new ObjectMapper();
        resp.setContentType("application/json;charset=utf-8");
        objectMapper.writeValue(resp.getOutputStream(),info);
    }

    /**
     * 3.退出功能
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    public void exit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //销毁session
        req.getSession().invalidate();

        //跳转登录页面
        resp.sendRedirect(req.getContextPath()+"login.html");
    }

    /**
     * 4.激活功能
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    public void active(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //获取激活码
        String code = req.getParameter("code");
        if (code!=null) {
            //调用service完成激活
            boolean flag = service.active(code);
            //判断标记
            String msg = null;
            if (flag) {
                //激活成功
                msg = "激活成功，请<a href='login.html'>登录</a>";
            }else {
                //激活失败
                msg = "激活失败，请联系管理员!";
            }
            resp.setContentType("text/html;charset=utf-8");
            resp.getWriter().write(msg);
        }
    }

    /**
     * 5.查询单个对象功能
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    public void findOne(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException {
        //从session中获取登录用户
        Object user = req.getSession().getAttribute("user");

        //将user写回客户端
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(resp.getOutputStream(),user);
    }
}
