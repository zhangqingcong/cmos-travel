package cn.itcast.travel.web.servlet;

import cn.itcast.travel.domain.PageBean;
import cn.itcast.travel.domain.Route;
import cn.itcast.travel.domain.User;
import cn.itcast.travel.service.FavoriteService;
import cn.itcast.travel.service.RouteService;
import cn.itcast.travel.service.impl.FavoriteServiceImpl;
import cn.itcast.travel.service.impl.RouteServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author wzm
 */
@WebServlet("/route/*")
public class RouteServlet extends BaseServlet{

    private RouteService routeService = new RouteServiceImpl();
    private FavoriteService favoriteService = new FavoriteServiceImpl();

    /**
     * 分页查询
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    public void pageQuery(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //1.接受参数
        String currentPageStr = req.getParameter("currentPage");
        String pageSizeStr = req.getParameter("pageSize");
        String cidStr = req.getParameter("cid");

        //接受rname线路名称
        String rname = req.getParameter("rname");
        rname = new String(rname.getBytes("iso-8859-1"),"utf-8");

        //类别id
        int cid = 0;
        //2.处理参数
        if (cidStr!=null&&cidStr.length()>0&&!"null".equals(cidStr)){
            cid = Integer.parseInt(cidStr);
        }
        //当前页码，如果不传递，则默认为第一页
        int currentPage = 0;
        if (currentPageStr!=null&&currentPageStr.length()>0) {
            currentPage = Integer.parseInt(currentPageStr);
        }else {
            currentPage = 1;
        }

        //每页显示条数，如果不传递，默认每页显示5条记录
        int pageSize = 0;
        if (pageSizeStr !=null&&pageSizeStr.length()>0) {
            pageSize = Integer.parseInt(pageSizeStr);
        }else {
            pageSize = 5;
        }

        //调用service查询PageBean对象
        PageBean<Route> pb = routeService.pageQuery(cid,currentPage,pageSize,rname);

        //将pageBean对象序列化为json，返回
        writeValue(pb,resp);
    }

    public void findOne(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException {
        //接受id
        String rid = req.getParameter("rid");
        //调用service查询route对象
        Route route = routeService.findOne(rid);
        //转为json写回客户端
        writeValue(route,resp);
    }

    /**
     * 判断当前登录用户是否收藏过该线路
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    public void isFavorite(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException {
        //获取线路id
        String rid = req.getParameter("rid");

        //获取当前登录的用户user
        User user = (User)req.getSession().getAttribute("user");
        //用户id
        int uid;
        if (user == null) {
            //用户尚未登录
            uid = 0;
        }else {
            //已经登录
            uid = user.getUid();
        }

        //调用FavoriteService查用户是否收藏
        boolean flag = favoriteService.isFavorite(rid,uid);

        //写会客户端
        writeValue(flag,resp);
    }

    /**
     * 添加收藏
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    public void addFavorite(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException {
        //获取线路id
        String rid= req.getParameter("rid");
        //获取当前登录用户
        User user = (User)req.getSession().getAttribute("user");
        int uid;
        if (user==null) {
            //尚未登录
            return;
        }else {
            //已经登录
            uid = user.getUid();
        }
        //调用service
        favoriteService.add(rid,uid);
    }


}
