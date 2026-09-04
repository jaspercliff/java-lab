package jasper.jvm.classload.loading.pluginDemo;

import lombok.extern.slf4j.Slf4j;

import java.nio.file.*;

@Slf4j
public class RuleWatcher implements Runnable {

    private final String dir;

    public RuleWatcher(String dir) {
        this.dir = dir;
    }

    @Override
    public void run() {
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();

            Paths.get(dir).register(
                    watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY
            );

            System.out.println("监听目录: " + dir);

            while (true) {
                WatchKey key = watchService.take();

                for (WatchEvent<?> event : key.pollEvents()) {
                    Path fileName = (Path) event.context();

                    if (fileName.toString().equals("rule.jar")) {
                        String fullPath = dir + "/" + fileName;

                        System.out.println("检测到规则更新🔥");

                        PluginManager.loadRule(
                                fullPath,
                                "com.jasper.classload.loading.pluginDemo.RuleImpl"
                        );
                    }
                }

                key.reset();
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}