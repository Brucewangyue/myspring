package cn.ey88.myspring.beans.factory.config;

/**
 * 后置处理器
 * 用于给使用者自定义切面逻辑
 */
public interface BeanPostProcessor {
    Object postProcessBeforeInitialization(Object bean,String beanName);
    Object postProcessAfterInitialization(Object bean,String beanName);
}
