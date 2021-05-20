package cn.ey88.myspring;

import cn.ey88.myspring.annotation.Autowired;
import cn.ey88.myspring.annotation.Component;
import cn.ey88.myspring.annotation.ComponentScan;
import cn.ey88.myspring.annotation.Scope;
import cn.ey88.myspring.beans.factory.BeanNameAware;
import cn.ey88.myspring.beans.factory.InitializingBean;
import cn.ey88.myspring.beans.factory.config.BeanPostProcessor;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AutoAnnotationApplicationContext {

    private static final ConcurrentHashMap<String, Object> singletonMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    private static final List<BeanPostProcessor> beanPostProcessorList = new ArrayList<>();

    public AutoAnnotationApplicationContext(Class<?> classConfig) {
        // 解析 classConfig
        if (!classConfig.isAnnotationPresent(ComponentScan.class))
            return;

        scan(classConfig);

        // 处理单例bean
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            String beanName = entry.getKey();
            BeanDefinition beanDefinition = entry.getValue();
            if ("singleton".equals(beanDefinition.getScope())) {
                singletonMap.put(beanName, createBean(beanName, beanDefinition));
            }
        }
    }

    private void scan(Class<?> classConfig) {
        ComponentScan componentScanAnnotation = classConfig.getDeclaredAnnotation(ComponentScan.class);
        String scanPath = componentScanAnnotation.value().replace(".", "/");
        // todo : 如何处理异常
        if ("".equals(scanPath))
            throw new MySpringException("未配置扫描包路径");

        // 扫描
        // bootstrap -> jre/lib
        // ext -> jre/lib/ext
        // app -> classpath
        ClassLoader classLoader = AutoAnnotationApplicationContext.class.getClassLoader();
        URL resource = classLoader.getResource(scanPath);
        if (null == resource)
            throw new MySpringException("无效的包路径：" + scanPath);

        File dirFile = new File(resource.getFile());
        if (dirFile.isDirectory()) {
            File[] files = dirFile.listFiles();
            for (File file : files) {
                String filePath = file.getAbsolutePath();

                try {
                    filePath = filePath.substring(filePath.indexOf("classes") + 8, filePath.indexOf(".class"));
                    filePath = filePath.replace("\\", ".");
                    Class<?> clazz = classLoader.loadClass(filePath);

                    // 处理 component
                    Component componentAnnotation = clazz.getDeclaredAnnotation(Component.class);
                    if (null == componentAnnotation) continue;

                    if (BeanPostProcessor.class.isAssignableFrom(clazz)) {
                        BeanPostProcessor beanPostProcessorInstance = (BeanPostProcessor) clazz.newInstance();
                        beanPostProcessorList.add(beanPostProcessorInstance);
                    }

                    String beanName = componentAnnotation.value();
                    if ("".equals(beanName)) {
                        // 默认首字母小写
                        char[] chars = clazz.getSimpleName().toCharArray();
                        chars[0] += 32;
                        beanName = String.valueOf(chars);
                    }

                    BeanDefinition beanDefinition = new BeanDefinition();
                    beanDefinition.setClazz(clazz);

                    // 处理 scope 单例、原型
                    Scope scopeAnnotation = clazz.getDeclaredAnnotation(Scope.class);
                    beanDefinition.setScope(scopeAnnotation == null ? "singleton" : scopeAnnotation.value());

                    beanDefinitionMap.put(beanName, beanDefinition);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Object getBean(String beanName) {
        if (!beanDefinitionMap.containsKey(beanName))
            throw new MySpringException("找不到:" + beanName);

        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if ("singleton".equals(beanDefinition.getScope())) {
            return singletonMap.get(beanName);
        } else {
            return createBean(beanName, beanDefinition);
        }
    }

    private Object createBean(String beanName, BeanDefinition beanDefinition) {
        Class<?> clazz = beanDefinition.getClazz();
        try {
            Object o = clazz.getDeclaredConstructor().newInstance();

            // 循环依赖注入
            for (Field field : clazz.getDeclaredFields()) {
                Autowired autowiredAnnotation = field.getAnnotation(Autowired.class);
                if (autowiredAnnotation == null) continue;

                Object fieldBean = getBean(field.getName());
                field.setAccessible(true);
                field.set(o, fieldBean);
            }

            // 回调
            // 1 aware
            if (o instanceof BeanNameAware)
                ((BeanNameAware) o).setBeanName(beanName);
            // 2 initial
            for(BeanPostProcessor beanPostProcessor : beanPostProcessorList){
                beanPostProcessor.postProcessBeforeInitialization(o,beanName);
            }

            if (o instanceof InitializingBean)
                ((InitializingBean) o).afterPropertiesSet();

            for(BeanPostProcessor beanPostProcessor : beanPostProcessorList){
                beanPostProcessor.postProcessAfterInitialization(o,beanName);
            }

            return o;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
