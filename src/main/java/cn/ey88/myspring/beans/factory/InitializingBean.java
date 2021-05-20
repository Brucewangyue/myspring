package cn.ey88.myspring.beans.factory;

/**
 * 初始化回调
 */
public interface InitializingBean {
    void afterPropertiesSet() throws Exception;
}
