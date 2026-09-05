package com.jasper.tree;


import com.jasper.tree.bst.TreeNode;

import java.util.ArrayDeque;

/**
 * @author jasper
 * @since 2026-04-24 17:01:40
 */
public class TreeUtils {

    /**
     * 层序遍历 O(n)
     *
     * @param root root node
     */
    public static void levelOrder(TreeNode root) {
        if (root == null) return;
        ArrayDeque<TreeNode> queue = new ArrayDeque<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            System.out.print(" " + node.getVal());
            if (node.getLeft() != null) queue.offer(node.getLeft());
            if (node.getRight() != null) queue.offer(node.getRight());
        }
    }

    /**
     * 先序遍历 根-左-右
     *
     * @param root root node
     */
    public static void preOrder(TreeNode root) {
        if (root == null) return;
        System.out.print(" " + root.getVal());
        preOrder(root.getLeft());
        preOrder(root.getRight());
    }

    /**
     * 中序遍历 左 根 右
     *
     * @param root root node
     */
    public static void inOrder(TreeNode root) {
        if (root == null) return;
        inOrder(root.getLeft());
        System.out.print(" " + root.getVal());
        inOrder(root.getRight());
    }

    /**
     * 后序遍历 左 右 根
     *
     * @param root
     */
    public static void postOrder(TreeNode root) {
        if (root == null) return;
        postOrder(root.getLeft());
        postOrder(root.getRight());
        System.out.print(" " + root.getVal());
    }

    /**
     * O(logN) best case <br>
     * O(n) worst case <br>
     * O(hight)
     */
    public static TreeNode search(TreeNode root, int key) {
        while (root != null) {
            if (root.getVal() == key) {
                return root;
            } else if (key < root.getVal()) {
                root = root.getLeft();
            } else {
                root = root.getRight();
            }
        }
        return null;
    }
}
