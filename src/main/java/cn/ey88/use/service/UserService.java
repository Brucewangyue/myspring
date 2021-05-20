package cn.ey88.use.service;

import cn.ey88.myspring.beans.factory.BeanNameAware;
import cn.ey88.myspring.beans.factory.InitializingBean;

public interface UserService extends BeanNameAware, InitializingBean {
    void test();
}
