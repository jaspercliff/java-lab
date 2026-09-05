package com.jasper.tree.bst;


import com.jasper.tree.TreeUtils;

/**
 * @author jasper
 * @since 2026-04-24 11:38:24
 */
public class Demo {
    /*
    *    8
        / \
       3   1
      / \    \
     1   6    14
        / \   /
       4   7 13
    */
    public static void main(String[] args) {
        BST bst = new BST();
        TreeNode root = null;

        int[] nums = {8, 3, 10, 1, 6, 14, 4, 7, 13};

        // 构建树
        for (int n : nums) {
            root = bst.insert(root, n);
        }

        System.out.println("levelOrder：");
        TreeUtils.levelOrder(root); // 8 3 10 1 6 14 4 7 13
        System.out.println();

        System.out.println("preOrder:");
        TreeUtils.preOrder(root); // 8, 3, 1, 6, 4, 7, 10, 14, 13
        System.out.println();

        System.out.println("inOrder:");
        TreeUtils.inOrder(root); // 1, 3, 4, 6, 7, 8, 10, 13, 14
        System.out.println();

        System.out.println("postOrder:");
        TreeUtils.postOrder(root); // 1, 4, 7, 6, 3, 13, 14, 10, 8
        System.out.println();

        // 删除 1（叶子）
        root = bst.delete(root, 1);
        TreeUtils.inOrder(root);
        System.out.println();

        // 删除 14（单子节点）
        root = bst.delete(root, 14);
        TreeUtils.inOrder(root);
        System.out.println();

        // 删除 3（两个孩子）
        root = bst.delete(root, 3);
        TreeUtils.inOrder(root);
        System.out.println();

        // 删除 8（根节点）
        root = bst.delete(root, 8);
        TreeUtils.inOrder(root);
        System.out.println();
    }
}
