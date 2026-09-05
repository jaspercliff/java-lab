package com.jasper.tree.bst;

import lombok.Data;

/**
 * @author jasper
 * @since 2026-04-24 22:09:11
 */
@Data
public class TreeNode {

    int val;
    TreeNode left, right;

    public TreeNode(int val) {
        this.val = val;
    }
}
