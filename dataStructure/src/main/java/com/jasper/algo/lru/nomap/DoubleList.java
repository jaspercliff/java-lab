package com.jasper.algo.lru.nomap;

/**
 * head <-> a <-> b <-> c <-> tail <br>
 * head is old tail is new
 */
public class DoubleList {
    private final Node head;
    private final Node tail; // 头尾节点
    private int size;

    public DoubleList() {
        head = new Node(0, 0);
        tail = new Node(0, 0);
        head.next = tail;
        tail.prev = head; // head <> tail
        size = 0;
    }

    /**
     * 在尾部添加节点
     *
     * @param node to do add
     */
    public void addLast(Node node) {
        node.prev = tail.prev;
        node.next = tail; // node 前后 指针
        tail.prev.next = node; // node 前的 tail
        tail.prev = node; // tail 的前
        size++;
    }

    /** remove node */
    public void remove(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
        size--;
    }

    /** remove first 最久未使用 */
    public Node removeFirst() {
        if (head.next == tail) return null;
        Node first = head.next;
        remove(first);
        return first; // for hashmap use
    }

    public int getSize() {
        return size;
    }

    public void print() {
        Node p = head.next; // 从第一个真实节点开始
        System.out.print("DoubleList (Head -> Tail): ");
        while (p != tail) {
            System.out.print("[" + p.key + ":" + p.value + "]");
            if (p.next != tail) System.out.print(" <-> ");
            p = p.next;
        }
        System.out.println();
    }
}
