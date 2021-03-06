package cn.itcast.travel.dao;

import cn.itcast.travel.domain.Route;

import java.util.List;

/**
 * @author wzm
 */
public interface RouteDao {

    /**
     * 根据cid查询总记录数
     * @param cid
     * @param rname
     * @return
     */
    public int findTotalCount(int cid,String rname);

    /**
     * 根据cid，start,pageSize查询当前页的数据集合
     * @param cid
     * @param star
     * @param pageSize
     * @param rname
     * @return
     */
    public List<Route> findByPage(int cid,int star,int pageSize,String rname);

    /**
     * 根据id查询
     * @param rid
     * @return
     */
    public Route findOne(int rid);
}
