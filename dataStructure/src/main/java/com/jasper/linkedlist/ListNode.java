package com.jasper.linkedlist;

import lombok.Data;

/**
 * @author jasper
 * @since 2026-05-12 13:55:25
 */
@Data
public class ListNode {
    private int val;
    private ListNode next;

    /** insert d after n0 */
    void insert(ListNode n0, ListNode target) {
        target.next = n0.next;
        n0.next = target;
    }

    /**
     * delete first node after n <br>
     * 没有引用垃圾回收会自己删除掉
     */
    void delete(ListNode n) {
        if (n == null || n.next == null) return;
        n.next = n.next.next;
    }

    /** O(n) */
    ListNode access(ListNode head, int index) {
        for (int i = 0; i < index; i++) {
            if (head == null) return null;
            head = head.next;
        }
        return head;
    }

    ListNode search(ListNode head, int target) {
        while (head != null) {
            if (head.val == target) {
                return head;
            }
            head = head.next;
        }
        return head;
    }

    void iter(ListNode head) {
        while (head != null) {
            System.out.println(head.val);
            head = head.next;
        }
    }

    ListNode reverse(ListNode head) {
        ListNode prev = null;
        ListNode curr = head;
        while (curr != null) {
            // temp storage next  null -> 1 ->2
            ListNode next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
        }
        // head node of the reversed linked list
        return prev;
    }

    ListNode reverserRecursion(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }
        ListNode newNode = reverserRecursion(head.next);
        head.next.next = head; // 5->4
        head.next = null; // 4->null
        return newNode;
    }
}
