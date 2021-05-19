package cn.ey88.myspring;

import cn.ey88.myspring.annotation.Component;
import cn.ey88.myspring.annotation.ComponentScan;
import cn.ey88.myspring.annotation.Scope;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AutoAnnotationApplicationContext {

    private static final ConcurrentHashMap<String, Object> singletonMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    public AutoAnnotationApplicationContext(Class classConfig) {
        // 解析 classConfig
        if (!classConfig.isAnnotationPresent(ComponentScan.class))
            return;

        scan(classConfig);

        // 处理单例bean
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            String beanName = entry.getKey();
            BeanDefinition beanDefinition = entry.getValue();
            if ("singleton".equals(beanDefinition.getScope())) {
                singletonMap.put(beanName, createBean(beanDefinition));
            }
        }

    }

    private void scan(Class classConfig) {
        ComponentScan componentScanAnnotation = (ComponentScan) classConfig.getDeclaredAnnotation(ComponentScan.class);
        String scanPath = componentScanAnnotation.value().replace(".", "/");
        // todo : 如何处理异常
        if ("" == scanPath)
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
                    String beanName = componentAnnotation.value();
                    BeanDefinition beanDefinition = new BeanDefinition();
                    beanDefinition.setClazz(clazz);

                    // 处理 scope 单例、原型
                    Scope scopeAnnotation = clazz.getDeclaredAnnotation(Scope.class);
                    beanDefinition.setScope(scopeAnnotation == null ? "singleton" : scopeAnnotation.value());

                    beanDefinitionMap.put(beanName, beanDefinition);

                } catch (ClassNotFoundException e) {
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
            return createBean(beanDefinition);
        }
    }

    private Object createBean(BeanDefinition beanDefinition) {
        Class clazz = beanDefinition.getClazz();
        try {
            Object o = clazz.getDeclaredConstructor().newInstance();
            return o;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
