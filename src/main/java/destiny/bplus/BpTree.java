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


    @Override
    public Tuple find(Tuple key) {
        return null;
    }

    @Override
    public void insert(Tuple key) {

    }

    @Override
    public boolean remove(Tuple key) {
        return false;
    }
}
