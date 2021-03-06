package cn.ey88.use.service;

import cn.ey88.myspring.annotation.Autowired;
import cn.ey88.myspring.annotation.Component;
import cn.ey88.myspring.annotation.Scope;

@Component("userService")
@Scope("prototype")
public class UserServiceImpl implements UserService{
    @Autowired
    private OrderService orderService;

    private String beanName;

    public void test(){
        System.out.println(orderService);
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
//        System.out.println(beanName);
        System.out.println("after initializing");
    }
}
