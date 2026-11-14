package jasper.juc.collections.queue.deque;

import java.util.concurrent.ConcurrentLinkedDeque;

public class ConcurrentLinkedDequeDemo {
    public static void main(String[] args) {
        ConcurrentLinkedDeque<String> deque =
                new ConcurrentLinkedDeque<>();
// 尾部添加
        deque.addLast("A");
        deque.addLast("B");
// 头部添加
        deque.addFirst("C");
// 查看头部，不删除
        String first = deque.peekFirst();
// 查看尾部，不删除
        String last = deque.peekLast();
// 从头部取出并删除
        String head = deque.pollFirst();
// 从尾部取出并删除
        String tail = deque.pollLast();
    }
}
