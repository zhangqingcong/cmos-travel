package cn.itcast.travel.web.servlet;

import cn.itcast.travel.domain.Category;
import cn.itcast.travel.service.CategoryService;
import cn.itcast.travel.service.impl.CategoryServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author wzm
 */
@WebServlet("/category/*")
public class CategoryServlet extends BaseServlet{

    private CategoryService service = new CategoryServiceImpl();

    /**
     * 查询所有
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    public void findAll(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //调用service查询所有
        List<Category> cs = service.findAll();
        //序列化json返回
        //ObjectMapper mapper = new ObjectMapper();
        //resp.setContentType("application/json;charset=utf-8");
        //mapper.writeValue(resp.getOutputStream(),cs);
        writeValue(cs,resp);
    }
}
