package destiny.bplus;

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



}
