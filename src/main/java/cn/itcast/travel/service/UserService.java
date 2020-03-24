package cn.itcast.travel.service;

import cn.itcast.travel.domain.User;

/**
 * @author wzm
 */
public interface UserService {
    boolean regist(User user);
    boolean active(String code);
    User login(User user);
}
