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


}
