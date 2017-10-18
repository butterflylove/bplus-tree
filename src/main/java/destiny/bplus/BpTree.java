package destiny.bplus;

/**
 * B+树
 * @author zhangtianlong
 */
public class BpTree implements Tree {

    /**
     * B+树根节点
     */
    BpNode root;

    /**
     * B+树叶子节点的头结点
     */
    BpNode head;

    public BpTree() {
        root = new BpNode(true, true);
        head = root;
    }

    public BpNode getHead() {
        return head;
    }

    public Tuple find(Tuple key) {
        return root.get(key);
    }

    public void insert(Tuple key) {
        root.insert(key, this);
    }

    public boolean remove(Tuple key) {
        return root.remove(key, this);
    }
}
