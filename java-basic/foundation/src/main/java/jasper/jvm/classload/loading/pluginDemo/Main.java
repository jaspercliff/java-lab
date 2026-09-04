package jasper.jvm.classload.loading.pluginDemo;

public class Main {

    public static void main(String[] args) throws Exception {

        String jarPath = System.getProperty("user.dir") + "/basic/jvm/src/main/java/com/jasper/classload/loading/pluginDemo/plugins/rule.jar";
        String pluginDir = System.getProperty("user.dir") + "/basic/jvm/src/main/java/com/jasper/classload/loading/pluginDemo/plugins/";

        // 启动监听线程
        new Thread(new RuleWatcher(pluginDir)).start();

        // 初始加载
        PluginManager.loadRule(
                jarPath,
                "com.jasper.classload.loading.pluginDemo.RuleImpl"
        );

        // 模拟订单请求
        int[] orders = {500, 1500, 3000, 7000};

        while (true) {
            Thread.sleep(3000);

            System.out.println("==== 风控检查 ====");

            for (int amount : orders) {
                boolean ok = PluginManager.check(amount);
                System.out.println("金额 " + amount + " -> " + ok);
            }
        }
    }
}