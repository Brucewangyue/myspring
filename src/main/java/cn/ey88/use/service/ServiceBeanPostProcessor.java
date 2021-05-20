package cn.ey88.use.service;

import cn.ey88.myspring.annotation.Component;
import cn.ey88.myspring.beans.factory.config.BeanPostProcessor;

@Component
public class ServiceBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        if(bean instanceof UserService){
            System.out.println("userService 初始化前");
        }

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        System.out.println("bean初始化后");
        return bean;
    }
}
