package destiny.bplus;

import java.util.ArrayList;
import java.util.List;

/**
 * B+树节点
 * @author zhangtianlong
 */
public class BpNode {

    /**
     * 是否为叶子节点
     */
    boolean isLeaf;

    /**
     * 是否为跟节点
     */
    boolean isRoot;

    /**
     * 父节点
     */
    BpNode parent;

    /**
     * 叶节点的前节点
     */
    BpNode previous;

    /**
     * 叶节点的后节点
     */
    BpNode next;


    /**
     * 节点的关键字列表
     */
    List<Tuple> entries;

    /**
     * 节点的指针列表
     */
    List<BpNode> children;

    /**
     * 节点关键字的最大值
     */
    int maxLength = 16;

    public BpNode(boolean isLeaf) {
        this.isLeaf = isLeaf;
        if (!isLeaf) {
            children = new ArrayList<BpNode>();
        }
        entries = new ArrayList<Tuple>();
    }

    public BpNode(boolean isLeaf, boolean isRoot) {
        this(isLeaf);
        this.isRoot = isRoot;
    }

    public Tuple get(Tuple key) {
        if (isLeaf) {
            for (Tuple tuple : entries) {
                if (key.compare(tuple) == 0) {
                    return tuple;
                }
            }
            return null;
        } else {
            // 小于首节点
            if (key.compare(entries.get(0)) < 0) {
                return children.get(0).get(key);
            } else if (key.compare(entries.get(entries.size() - 1)) >= 0) {
                return children.get(children.size() - 1).get(key);
            } else {
                // TODO 后续改为二分查找
                for (int i = 0; i < (entries.size() - 1); i++) {
                    if (key.compare(entries.get(i)) >= 0 && key.compare(entries.get(i + 1)) < 0) {
                        return children.get(i + 1).get(key);
                    }
                }
            }
        }
        return null;
    }

    public void insert(Tuple key, BpTree tree) {
        if (isLeaf) {
            if (!isLeafToSplit()) {
                // 叶节点无需分裂
                insertInLeaf(key);
            } else {
                //需要分裂为左右两个节点
                BpNode left = new BpNode(true);
                BpNode right = new BpNode(true);
                if (previous != null) {
                    left.previous = previous;
                    previous.next = left;
                }
                if (next != null) {
                    right.next = next;
                    next.previous = right;
                }
                if (previous == null) {
                    tree.head = left;
                }
                left.next = right;
                right.previous = left;
                // for GC
                previous = null;
                next = null;
                //插入后再分裂
                insertInLeaf(key);
                int leftSize = entries.size() / 2;
                int rightSize = entries.size() - leftSize;

            }
        }
    }

    /**
     * 叶子节点是否需要分裂
     */
    private boolean isLeafToSplit() {
        if (isLeaf) {
            if (entries.size() >= maxLength) {
                return true;
            }
            return false;
        } else {
            throw new UnsupportedOperationException("the node is not leaf.");
        }
    }

    /**
     * 插入到当前叶子节点中
     */
    private void insertInLeaf(Tuple key) {
        if (entries.size() == 0) {
            entries.add(key);
            return;
        }
        //遍历插入
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).compare(key) == 0) {
                // 如果该键值已存在,则不插入
                return;
            } else if (entries.get(i).compare(key) > 0) {
                entries.add(i, key);
                return;
            }
        }
        // 插入到末尾
        entries.add(entries.size(), key);
        return;
    }


}
