import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class Test {
    /**
     * Definition for binary tree
     */
    public class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }

    public class Solution {
        public List<Integer> rightSideView(TreeNode root) {
            List<Integer> result = new ArrayList<Integer>();
            if (root == null) return result;
            LinkedList<TreeNode> queue = new LinkedList<TreeNode>();
            queue.add(root);
            queue.add(null);
            int level = 0;
            while (queue.size() != 1) {
                TreeNode tmp = queue.poll();
                if (tmp == null) {
                    queue.add(null);
                    level++;
                } else {
                    if (result.size() == level) {
                        result.add(tmp.val);
                    } else {
                        result.set(level, tmp.val);
                    }
                    if (tmp.left != null) queue.add(tmp.left);
                    if (tmp.right != null) queue.add(tmp.right);
                }
            }
            return result;
        }

        public Vector<Integer> rightSideView1(TreeNode root) {
            Vector<Integer> result = new Vector<>();
            if (root == null) return result;
            LinkedList<TreeNode> queue = new LinkedList<TreeNode>();
            queue.add(root);
            queue.add(null);
            int level = 0;
            while (queue.size() != 1) {
                TreeNode tmp = queue.poll();
                if (tmp == null) {
                    queue.add(null);
                    level++;
                } else {
                    if (result.size() == level) {
                        result.add(tmp.val);
                    } else {
                        result.set(level, tmp.val);
                    }
                    if (tmp.left != null) queue.add(tmp.left);
                    if (tmp.right != null) queue.add(tmp.right);
                }
            }
            return result;
        }
    }
}
