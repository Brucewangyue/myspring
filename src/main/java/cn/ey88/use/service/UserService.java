package cn.ey88.use.service;

import cn.ey88.myspring.annotation.Autowired;
import cn.ey88.myspring.annotation.Component;
import cn.ey88.myspring.annotation.Scope;

@Component("userService")
@Scope("prototype")
public class UserService {

    @Autowired
    private OrderService orderService;

    public void test(){
        System.out.println(orderService);
    }
}
