package jasper.commonClass.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

public class FileWatchServiceDemo {
    public static void main(String[] args) throws IOException, InterruptedException {
        // FileSystems.getDefault() 获取当前文件系统
        WatchService watchService = FileSystems.getDefault().newWatchService();
        String pathStr =
                System.getProperty("user.dir")
                        + File.separator
                        + "/basic/commonClass/src/main/java/com/jasper/file/fileForWatch";
        Path path = Paths.get(pathStr);
        path.register(
                watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY);
        WatchKey key;
        while ((key = watchService.take()) != null) { // take block
            for (WatchEvent<?> pollEvents : key.pollEvents()) {
                System.out.println("事件类型：" + pollEvents.kind() + "file is " + pollEvents.context());
                // 事件类型：ENTRY_CREATEfile is test.md
                // 事件类型：ENTRY_MODIFYfile is test.md
                // 事件类型：ENTRY_DELETEfile is test.md
            }
            key.reset(); // 处理完必须充值，否则不在接收新事件
        }
    }
}
