package cn.itcast.travel.service;

import cn.itcast.travel.domain.Category;

import java.util.List;

/**
 * @author wzm
 */
public interface CategoryService {
    public List<Category> findAll();
}
