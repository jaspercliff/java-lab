package jasper.jvm.classload.loading.pluginDemo;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class PluginManager {

    // 当前规则（原子替换）
    //它利用了底层的 CAS（Compare And Swap）指令。替换过程是瞬间完成的。
    // 对于外部线程来说，要么看到的是“旧规则”，要么看到的是“新规则”，绝不会看到“正在替换中”的规则
    //AtomicReference 内部使用了 volatile 关键字。一旦 currentRule.set(rule) 执行完毕，
    // JMM（Java 内存模型）会强制刷新主内存，并让其他线程的缓存失效。所有线程立即可见新规则
    private static final AtomicReference<RiskRule> currentRule = new AtomicReference<>();

    public static void loadRule(String jarPath, String className) {
        try {
            File file = new File(jarPath);
            URL url = file.toURI().toURL();

            //自定义类加载器 打破双亲委派机制
            Class<?> clazz;
            try (PluginClassLoader loader = new PluginClassLoader(
                    new URL[]{url},
                    RiskRule.class.getClassLoader()
            )) {
                clazz = loader.loadClass(className);
                RiskRule rule = (RiskRule) clazz.getDeclaredConstructor().newInstance();
                // 原子替换
                currentRule.set(rule);
                log.info("规则加载成功: {}" ,jarPath);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public static boolean check(int amount) {
        RiskRule rule = currentRule.get();
        if (rule == null) {
            throw new IllegalStateException("规则未加载");
        }
        return rule.check(amount);
    }
}