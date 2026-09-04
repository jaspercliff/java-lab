package jasper.juc.executor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

public class SimpleThreadFactory implements ThreadFactory {

    private AtomicLong threadId;
    private final String namePrefix;
    public SimpleThreadFactory( String namePrefix) {
        this.namePrefix = "custom thread pool".concat("-").concat(namePrefix);
    }

    @Override
    public Thread newThread(final Runnable r) {
        final Thread thread = new Thread(r, namePrefix + threadId.getAndIncrement());
        System.out.println("Creating " + thread.getName());
        return thread;
    }
}
