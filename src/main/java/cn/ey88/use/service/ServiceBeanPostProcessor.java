package cn.ey88.use.service;

import cn.ey88.myspring.annotation.Component;
import cn.ey88.myspring.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Proxy;

@Component
public class ServiceBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        if (bean instanceof UserService) {
//            System.out.println("userService 初始化前");
        }

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
//        System.out.println("bean初始化后");

        // 动态代理 - spring 源码也是基于该接口开发的aop
        if (bean instanceof UserService) {
            ClassLoader classLoader = bean.getClass().getClassLoader();
            Class<?>[] interfaces = bean.getClass().getInterfaces();
            return Proxy.newProxyInstance(classLoader, interfaces, (proxy, method, args) -> {
                System.out.println("执行" + method + "方法前");
                Object result = method.invoke(bean, args);
                System.out.println("执行" + method + "方法后");
                return result;
            });
        }

        return bean;
    }
}
