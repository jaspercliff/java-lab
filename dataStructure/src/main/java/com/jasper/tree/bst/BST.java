package com.jasper.tree.bst;

/**
 * @author jasper
 * @since 2026-04-23 17:41:01
 */
public class BST {

    public TreeNode insert(TreeNode root, int val) {
        if (root == null) return new TreeNode(val);
        TreeNode current = root;
        while (true) {
            if (val < current.val) {
                if (current.left == null) {
                    current.left = new TreeNode(val);
                    break;
                }
                //  一直往下走
                current = current.left;
            } else {
                if (current.right == null) {
                    current.right = new TreeNode(val);
                    break;
                }
                current = current.right;
            }
        }
        return root;
    }

    public TreeNode delete(TreeNode root, int key) {
        if (root == null) {
            return null;
        }
        if (key < root.val) {
            root.left = delete(root.left, key);
        } else if (key > root.val) {
            root.right = delete(root.right, key);
        } else {
            // 找到要删除的节点

            // 返回的是新子树根  叶子节点 || 单子节点
            if (root.left == null) return root.right;
            if (root.right == null) return root.left;

            // 俩个子节点 找右子树最小
            TreeNode success = root.right;
            while (success.left != null) success = success.left;
            // root和右子树最小节点替换
            root.val = success.val;
            // 删除替换节点
            root.right = delete(root.right, success.val);
        }
        return root;
    }
}
