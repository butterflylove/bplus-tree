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
     * 节点指针的最大值
     */
    int maxLength = 17;

    public BpNode getParent() {
        return parent;
    }

    public void setParent(BpNode parent) {
        this.parent = parent;
    }

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

                int remainder = entries.size() % 2;
                int leftSize;
                if (remainder == 0) {
                    leftSize = entries.size() / 2;
                } else {
                    leftSize = entries.size() / 2 + 1;
                }
                int rightSize = entries.size() - leftSize;
                // 左右节点拷贝
                for (int i = 0; i < leftSize; i++) {
                    left.entries.add(entries.get(i));
                }
                for (int i = 0; i < rightSize; i++) {
                    right.entries.add(entries.get(leftSize + i));
                }
                // 不是根节点
                if (parent != null) {
                    // 调整父子节点关系
                    // 寻找当前节点在父节点的位置
                    int index = parent.children.indexOf(this);
                    // 删除当前指针
                    parent.children.remove(index);
                    left.setParent(parent);
                    right.setParent(parent);
                    // 将分裂后节点的指针添加到父节点
                    parent.children.add(index, left);
                    parent.children.add(index + 1, right);
                    // for GC
                    entries = null;
                    children = null;

                    // 父节点[非叶子节点]中插入关键字
                    parent.insertInParent(right.entries.get(0));
                    parent.updateNode(tree);
                    // for GC
                    parent = null;
                } else {
                    // 是根节点
                    isRoot = false;
                    BpNode rootNode = new BpNode(false, true);
                    tree.root = rootNode;
                    left.parent = rootNode;
                    right.parent = rootNode;
                    rootNode.children.add(left);
                    rootNode.children.add(right);
                    // for GC
                    entries = null;
                    children = null;
                    // 根节点插入关键字
                    rootNode.insertInParent(right.entries.get(0));
                }
            }
        } else {
            // 如果不是叶子节点,沿着指针乡下搜索
            if (key.compare(entries.get(0)) < 0) {
                children.get(0).insert(key, tree);
            } else if (key.compare(entries.get(entries.size() - 1)) >= 0) {
                children.get(children.size() - 1).insert(key, tree);
            } else {
                // TODO 二分查找
                // 遍历比较
                for (int i = 0; i < (entries.size() - 1); i++) {
                    if (key.compare(entries.get(i)) >= 0 && key.compare(entries.get(i + 1)) < 0) {
                        children.get(i + 1).insert(key, tree);
                        break;
                    }
                }
            }
        }
    }

    /**
     * 父节点插入关键字后,检查是否需要分裂
     */
    private void updateNode(BpTree tree) {
        // 需要分裂
        if (isNodeToSplit()) {
            BpNode left = new BpNode(false);
            BpNode right = new BpNode(false);
            int remainder = entries.size() % 2;
            int pLeftSize;
            if (remainder == 0) {
                pLeftSize = entries.size() / 2;
            } else {
                pLeftSize = entries.size() / 2 + 1;
            }
            int pRightSize = entries.size() - pLeftSize;
            // 复制左边的关键字
            for (int i = 0; i < (pLeftSize - 1); i++) {
                left.entries.add(entries.get(i));
            }
            // 复制左边的指针
            for (int i = 0; i < pLeftSize; i++) {
                left.children.add(children.get(i));
                children.get(i).setParent(left);
            }

            // 复制右边关键字,右边的第一个关键字提升到父节点
            for (int i = 1;  i < pRightSize; i++) {
                right.entries.add(entries.get(pLeftSize + i));
            }
            // 复制右边的指针
            for (int i = 0; i < pRightSize; i++) {
                right.children.add(children.get(pLeftSize + i));
                children.get(i).setParent(right);
            }
            Tuple keyToParent = entries.get(pLeftSize);
            if (parent != null) {
                int index = parent.children.indexOf(this);
                parent.children.remove(index);
                left.parent = parent;
                right.parent = parent;
                parent.children.add(index, left);
                parent.children.add(index + 1, right);
                // 插入关键字
                parent.insertInParent(keyToParent);
                parent.updateNode(tree);
                entries = null;
                children = null;
                parent = null;
            } else {
                // 是根节点
                isRoot = false;
                BpNode rootNode = new BpNode(false, true);
                tree.root = rootNode;
                left.parent = rootNode;
                right.parent = rootNode;
                rootNode.children.add(left);
                rootNode.children.add(right);
                children = null;
                entries = null;
                // 插入关键字
                rootNode.insertInParent(keyToParent);
            }
        }
    }

    /**
     * 叶子节点是否需要分裂
     */
    private boolean isLeafToSplit() {
        if (isLeaf) {
            if (children.size() >= maxLength) {
                return true;
            }
            return false;
        } else {
            throw new UnsupportedOperationException("the node is not leaf.");
        }
    }

    /**
     * 中间节点是否需要分裂
     */
    private boolean isNodeToSplit() {
        // 由于是先插入关键字,所以不需要[=]
        if (isLeaf) {
            throw new UnsupportedOperationException("error access to leaf");
        }
        if (children.size() > maxLength) {
            return true;
        }
        return false;
    }


    /**
     * 插入到当前叶子节点中,不分裂
     */
    private void insertInLeaf(Tuple key) {
        if (!isLeaf) {
            throw new UnsupportedOperationException("can't insert into middle node.");
        }
        insertImpl(key);
    }

    /**
     * 插入到非叶子节点中,不分裂
     */
    private void insertInParent(Tuple key) {
        if (isLeaf) {
            throw new UnsupportedOperationException("can't insert into leaf node.");
        }
        insertImpl(key);
    }


    private void insertImpl(Tuple key) {
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
