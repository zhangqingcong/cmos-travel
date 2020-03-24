package cn.itcast.travel.service.impl;

import cn.itcast.travel.dao.CategoryDao;
import cn.itcast.travel.dao.impl.CategoryDaoImpl;
import cn.itcast.travel.domain.Category;
import cn.itcast.travel.service.CategoryService;
import cn.itcast.travel.util.JedisUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * 分类服务实现，运用redis缓存
 * @author wzm
 */
public class CategoryServiceImpl implements CategoryService {

    private CategoryDao categoryDao = new CategoryDaoImpl();

    @Override
    public List<Category> findAll() {
        //从redis中查询
        //获取jedis客户端
        Jedis jedis = JedisUtil.getJedis();

        //查询sortedset中的分数(cid)和值(cname)，相当于获取步骤
        Set<Tuple> categorys = jedis.zrangeWithScores("category",0,-1);
        List<Category> cs = null;

        //判断查询的集合是否为空
        if (categorys==null||categorys.size()==0) {
            System.out.println("从数据库中查询...");
            //如果为空,需要从数据库查询,在将数据存入redis
            //从数据库查询
            cs = categoryDao.findAll();
            //将集合数据存储到redis中的 category的key，相当于存储步骤
            for (int i=0;i<cs.size();i++) {
                jedis.zadd("category",cs.get(i).getCid(),cs.get(i).getCname());
            }
        }else {
            System.out.println("从redis中查询...");
            //如果不为空,将set的数据存入list
            cs = new ArrayList<Category>();
            for (Tuple tuple: categorys
                 ) {
                Category category = new Category();
                category.setCname(tuple.getElement());
                category.setCid((int)tuple.getScore());
                cs.add(category);
            }
        }
        return cs;
    }
}
