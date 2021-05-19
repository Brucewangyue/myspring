package cn.ey88.myspring;

import cn.ey88.myspring.annotation.Component;
import cn.ey88.myspring.annotation.ComponentScan;

import java.io.File;
import java.net.URL;

public class AutoAnnotationApplicationContext {
    public AutoAnnotationApplicationContext(Class classConfig) {
        // 解析 classConfig
        if (!classConfig.isAnnotationPresent(ComponentScan.class))
            return;

        ComponentScan componentScanAnnotation = (ComponentScan) classConfig.getDeclaredAnnotation(ComponentScan.class);
        String scanPath = componentScanAnnotation.value().replace(".", "/");
        // todo : 如何处理异常
        if("" == scanPath)
            throw new MySpringException("未配置扫描包路径");

        // 扫描
        // bootstrap -> jre/lib
        // ext -> jre/lib/ext
        // app -> classpath
        ClassLoader classLoader = AutoAnnotationApplicationContext.class.getClassLoader();
        URL resource = classLoader.getResource(scanPath);
//        if(null != resource)
        File dirFile = new File(resource.getFile());
        if (dirFile.isDirectory()) {
            File[] files = dirFile.listFiles();
            for (File file : files) {
                String filePath = file.getAbsolutePath();

                try {
                    filePath = filePath.substring(filePath.indexOf("classes") + 8, filePath.indexOf(".class"));
                    filePath = filePath.replace("\\", ".");

                    Class<?> clazz = classLoader.loadClass(filePath);
                    if (clazz.isAnnotationPresent(Component.class)) continue;
                    // 处理 component 注解类
                    // scope 单例、原型

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Object getBean(String serviceName) {
        return null;
    }
}
