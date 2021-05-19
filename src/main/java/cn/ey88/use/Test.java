package cn.ey88.use;

import cn.ey88.myspring.AutoAnnotationApplicationContext;
import cn.ey88.use.service.UserService;

public class Test {
    public static void main(String[] args) {
        AutoAnnotationApplicationContext applicationContext = new AutoAnnotationApplicationContext(AppConfig.class);
        System.out.println(applicationContext.getBean("userService"));
        System.out.println(applicationContext.getBean("userService"));
        System.out.println(applicationContext.getBean("userService"));

        UserService userService = (UserService) applicationContext.getBean("userService");
        userService.test();
    }
}
