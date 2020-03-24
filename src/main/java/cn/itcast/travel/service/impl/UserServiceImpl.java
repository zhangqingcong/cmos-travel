package cn.itcast.travel.service.impl;

import cn.itcast.travel.dao.UserDao;
import cn.itcast.travel.dao.impl.UserDaoImpl;
import cn.itcast.travel.domain.User;
import cn.itcast.travel.service.UserService;
import cn.itcast.travel.util.MailUtils;
import cn.itcast.travel.util.UuidUtil;

/**
 * 用户服务实现
 * @author wzm
 */
public class UserServiceImpl implements UserService {
    private UserDao userDao = new UserDaoImpl();

    //注册用户
    @Override
    public boolean regist(User user) {
        //根据用户名查询用户对象
        User u = userDao.findByUsername(user.getUsername());
        //判断u是否为null
        if (u!=null) {

            //注册失败
            return false;
        }
        //否则，保存用户信息
        //设置激活码，唯一字符串
        user.setCode(UuidUtil.getUuid());
        //设置激活码状态
        user.setStatus("N");
        userDao.save(user);

        //激活邮件
        String content = "<a href='http://localhost/travel/user/active?code=\"+user.getCode()+\"'>点击激活【黑马旅游网】</a>";
        MailUtils.sendMail(user.getEmail(),content,"激活邮件");
        return true;
    }

    //激活用户

    @Override
    public boolean active(String code) {
        //根据激活码查询用户对象
        User user = userDao.findByCode(code);
        if (user!=null) {
            //这里调用Dao的修改激活状态方法
            userDao.updateStatus(user);
            return true;
        }else {
            return false;
        }
    }

    //登录方法


    @Override
    public User login(User user) {
        return userDao.findByUsernameAndPassword(user.getUsername(),user.getPassword());
    }
}
