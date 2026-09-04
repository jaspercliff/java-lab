package jasper.jvm.classload.loading.pluginDemo;

import java.net.URL;
import java.net.URLClassLoader;

public class PluginClassLoader extends URLClassLoader {

    public PluginClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {

        System.out.println("尝试加载类: " + name);

        if (name.startsWith("com.jasper.classload.loading.pluginDemo")) {
            try {
                System.out.println("尝试 findClass: " + name);
                //调用 defineClass() 方法。这是个底层 final 方法，负责把 byte[] 转换成 JVM 内部的 Class 对象
                Class<?> c = findClass(name);
                System.out.println("findClass 成功: " + name);

                //是链接（Linking）阶段，负责校验类结构、准备静态变量等。保持这个逻辑可以确保返回的 Class 是经过完整初始化的
                if (resolve) {
                    resolveClass(c);
                }
                return c;

            } catch (ClassNotFoundException e) {
                System.out.println("findClass 失败: " + name);
            }
        }

        System.out.println("走父加载器: " + name);
        return super.loadClass(name, resolve);
    }
}