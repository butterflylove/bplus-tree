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
    final int maxLength = 5;

    public BpNode getParent() {
        return parent;
    }

    public void setParent(BpNode parent) {
        this.parent = parent;
    }

    public BpNode getNext() {
        return next;
    }

    public List<Tuple> getEntries() {
        return entries;
    }

    public List<BpNode> getChildren() {
        return children;
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
                System.out.println("直接插入叶节点");
                insertInLeaf(key);
            } else {
                System.out.println("插入叶节点,且叶节点分裂");
                //需要分裂为左右两个节点
                BpNode left = new BpNode(true);
                BpNode right = new BpNode(true);
                if (previous != null) {
                    left.previous = previous;
                    previous.next = left;
                } else {
                    tree.head = left;
                }
                if (next != null) {
                    right.next = next;
                    next.previous = right;
                }
                left.next = right;
                right.previous = left;
                // for GC
                previous = null;
                next = null;
                // 插入后再分裂
                insertInLeaf(key);

                int leftSize = getUpper(entries.size(), 2);
                int rightSize = entries.size() - leftSize;
                System.out.printf("leaf key left:%d  right:%d\n", leftSize, rightSize);
                // 左右节点拷贝
                for (int i = 0; i < leftSize; i++) {
                    left.entries.add(entries.get(i));
                }
                for (int i = 0; i < rightSize; i++) {
                    right.entries.add(entries.get(leftSize + i));
                }
                // 不是根节点
                if (!isRoot) {
                    // 调整父子节点关系
                    // 寻找当前节点在父节点的位置
                    System.out.println("parent children is null:" + (parent.children == null));

                    int index = parent.children.indexOf(this);
//                    System.out.println("parent children size:" + parent.children.size());
//                    System.out.println("index:" + index);

                    // 删除当前指针
                    parent.children.remove(this);
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
                    System.out.println("父节点插入key");
                    parent.updateNode(tree);
                    // for GC
                    parent = null;
                } else {
                    // 是根节点
                    System.out.println("生成新的根节点");
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
            // 如果不是叶子节点,沿着指针向下搜索
            if (isRoot) {
                System.out.println("(1)跟节点,向下搜索");
            }
            if (key.compare(entries.get(0)) < 0) {
                System.out.println("中间节点,向下搜索");
                children.get(0).insert(key, tree);
            } else if (key.compare(entries.get(entries.size() - 1)) >= 0) {
                System.out.println("中间节点,向下搜索");
                children.get(children.size() - 1).insert(key, tree);
            } else {
                // TODO 二分查找
                // 遍历比较
                System.out.println("中间节点,向下搜索");
                for (int i = 0; i < (entries.size() - 1); i++) {
                    if (key.compare(entries.get(i)) >= 0 && key.compare(entries.get(i + 1)) < 0) {
                        children.get(i + 1).insert(key, tree);
                        break;
                    }
                }
            }
        }
    }

    public boolean remove(Tuple key, BpTree tree) {
        boolean isFound = false;
        if (isLeaf) {
            // 如果是叶子节点
            if (!contains(key)) {
                // 不包含关键字
                return false;
            }
            // 是 叶子节点 且是 根节点,直接删除
            if (isRoot) {
                if (removeInLeaf(key)) {
                    isFound = true;
                }
            } else {
                if (canRemoveDirectInLeaf()) {
                    // 可以在叶节点中直接删除
                    if (removeInLeaf(key)) {
                        isFound = true;
                    }
                } else {
                    // 如果当前关键字不够,并且前节点有足够的关键字,从前节点借
                    if (leafCanBorrow(previous)) {
                        if (removeInLeaf(key)) {
                            borrowLeafPrevious();
                            isFound = true;
                        }
                    } else if (leafCanBorrow(next)) {
                        if (removeInLeaf(key)) {
                            borrowLeafNext();
                            isFound = true;
                        }
                        // 从后兄弟节点借
                    } else {
                        // 合并叶子节点, 先合并后删除
                        BpNode tmpParent = this.parent;
                        if (leafCanMerge(previous)) {
                            // 和前叶子节点合并
                            mergeToPreLeaf(this.previous, this);
                            if (previous.removeInLeaf(key)) {
                                isFound = true;
                            }
                            // 删除在父节点中的key
                            int parentKeyIdx = getMiddleKeyIdxInParent(this);
                            parent.entries.remove(parentKeyIdx);
                            // 删除在父节点中的指针
                            parent.children.remove(this);
                            // for GC
                            parent = null;
                            entries = null;
                            // 更新 叶节点链表
                            if (this.next != null) {
                                BpNode tmp = this;
                                tmp.previous.next = tmp.next;
                                tmp.next.previous = tmp.previous;
                                tmp.previous = null;
                                tmp.next = null;
                            } else {
                                this.previous.next = null;
                                this.previous = null;
                            }
                        } else if (leafCanMerge(next)) {
                            // 和后叶子节点合并
                            mergeToPreLeaf(this, this.next);
                            if (removeInLeaf(key)) {
                                isFound = true;
                            }
                            // 删除在父节点中的key
                            int parentKeyIdx = getMiddleKeyIdxInParent(this.next);
                            parent.entries.remove(parentKeyIdx);
                            // 删除在父节点中的指针
                            parent.children.remove(this.next);
                            // for GC
                            next.parent = null;
                            next.entries = null;
                            // 更新 叶节点链表
                            if (this.next.next != null) {
                                BpNode tmp = this.next;
                                this.next = tmp.next;
                                tmp.next.previous = this;
                                tmp.previous = null;
                                tmp.next = null;
                            } else {
                                this.next.previous = null;
                                this.next = null;
                            }
                        }
                        tmpParent.updateRemove(tree);
                    }
                }
            }
        } else {
            // 非叶子节点,继续向下搜索
            if (key.compare(entries.get(0)) < 0) {
                if (children.get(0).remove(key, tree)) {
                    isFound = true;
                }
            } else if (key.compare(entries.get(entries.size() - 1)) >= 0) {
                if (children.get(children.size() - 1).remove(key, tree)) {
                    isFound = true;
                }
            } else {
                for (int i = 0; i < (entries.size() - 1); i++) {
                    if (key.compare(entries.get(i)) >= 0 && key.compare(entries.get(i + 1)) < 0) {
                        if (children.get(i + 1).remove(key, tree)) {
                            isFound = true;
                        }
                    }
                    break;
                }
            }
        }
        return isFound;
    }

    /**
     * 中间节点删除后的更新操作
     */
    private void updateRemove(BpTree tree) {
        int half = getUpper(maxLength, 2);
        if (children.size() < half || children.size() < 2) {
            if (isRoot) {
                if (children.size() >= 2) {
                    return;
                } else {
                    // 如果根节点只有一个指针,则删除 根节点,让其孩子节点作为根节点
                    BpNode rootNode = children.get(0);
                    tree.root =rootNode;
                    rootNode.isRoot = true;
                    rootNode.parent = null;
                    // for GC
                    this.entries = null;
                    this.children = null;
                }
            } else {
                // 计算前后兄弟节点
                int curIdx = parent.children.indexOf(this);
                int preIdx = curIdx - 1;
                int nextIdx = curIdx + 1;
                BpNode preNode = null;
                BpNode nextNode = null;
                if (preIdx >= 0) {
                    preNode = parent.children.get(preIdx);
                }
                if (nextIdx < parent.children.size()) {
                    nextNode = parent.children.get(nextIdx);
                }
                if (middleNodeCanBorrow(preNode)) {
                    // 从前节点借
                    borrowMiddleNodePrevious(preNode);
                } else if (middleNodeCanBorrow(nextNode)) {
                    // 从后继节点借
                    borrowMiddleNodeNext(nextNode);
                } else {
                    // 和兄弟节点合并
                    BpNode tmpParent = this.parent;
                    if (middleNodeCanMerge(preNode)) {
                        // 与前节点合并
                        mergeToPreMiddleNode(preNode, this);
                        int parentKeyIdx = getMiddleKeyIdxInParent(this);
                        this.parent.entries.remove(parentKeyIdx);
                        this.parent.children.remove(parentKeyIdx + 1);
                        // for GC
                        this.parent = null;
                        this.entries = null;
                        this.children = null;
                    } else if (middleNodeCanMerge(nextNode)) {
                        mergeToPreMiddleNode(this, nextNode);
                        int parentKeyIdx = getMiddleKeyIdxInParent(nextNode);
                        this.parent.entries.remove(parentKeyIdx);
                        this.parent.children.remove(parentKeyIdx + 1);
                        // for GC
                        nextNode.parent = null;
                        nextNode.entries = null;
                        nextNode.children = null;
                    }
                    tmpParent.updateRemove(tree);
                }
            }
        }
    }

    /**
     * 从前兄弟节点中借
     */
    private void borrowMiddleNodePrevious(BpNode preNode) {
        /**
         *        20
         * 3  7        30
         * ---------------------
         *        7
         *    3        20   30
         */
        int parentKeyIdx = getMiddleKeyIdxInParent(this);
        // 父节点中下沉的 key
        Tuple downKey = parent.entries.get(parentKeyIdx);
        this.entries.add(0, downKey);
        // 从父节点中删除 key
        parent.entries.remove(parentKeyIdx);

        int preSize = preNode.entries.size();
        // 前节点中提升到父节点的key
        Tuple upKey = preNode.entries.get(preSize - 1);
        parent.entries.add(parentKeyIdx, upKey);
        // 删除提升节点
        preNode.entries.remove(preSize - 1);
        // 前节点的最后一个指针后移到当前节点
        int preChildSize = preNode.children.size();
        BpNode borrowPoint = preNode.children.get(preChildSize - 1);
        this.children.add(0 , borrowPoint);
        preNode.children.remove(preChildSize - 1);
        borrowPoint.parent = this;
    }

    /**
     * 从后继兄弟节点中借
     */
    private void borrowMiddleNodeNext(BpNode nextNode) {
        /**
         *        20
         *   7        30   40
         * ---------------------
         *            30
         *   7   20        40
         */
        int parentKeyIdx = getMiddleKeyIdxInParent(nextNode);
        Tuple downKey = parent.entries.get(parentKeyIdx);
        this.entries.add(downKey);
        this.parent.entries.remove(parentKeyIdx);

        Tuple upKey = nextNode.entries.get(0);
        this.parent.entries.add(parentKeyIdx, upKey);
        nextNode.entries.remove(0);
        // 后继节点的第一个指针移到当前节点最后面
        BpNode borrowPoint = nextNode.children.get(0);
        this.children.add(borrowPoint);
        nextNode.children.remove(0);
    }

    /**
     * 将后一个节点中的关键字 合并到 前节点中
     */
    private void mergeToPreLeaf(BpNode first, BpNode sec) {
        for (int i = 0; i < sec.entries.size(); i++) {
            first.entries.add(sec.entries.get(i));
        }
    }

    /**
     * 将后一个中间节点的关键字和指针复制到 前一个中间节点中
     */
    private void mergeToPreMiddleNode(BpNode first, BpNode sec) {
        int parentKeyIdx = getMiddleKeyIdxInParent(sec);
        // 将父节点关键字下沉
        first.entries.add(first.parent.entries.get(parentKeyIdx));

        for (int i = 0; i < sec.entries.size(); i++) {
            first.entries.add(sec.entries.get(i));
        }
        // sec的指针复制
        for (int i = 0; i < sec.children.size(); i++) {
            // 变更父亲节点
            sec.children.get(i).parent = first;
            first.children.add(sec.children.get(i));
        }
    }

    /**
     * 从前兄弟叶子节点 借
     */
    private void borrowLeafPrevious() {
        int size = previous.entries.size();
        Tuple borrowKey = previous.entries.get(size - 1);
        previous.entries.remove(size - 1);
        entries.add(0, borrowKey);
        // 获取 当前节点指针 和 前节点指针 之间的 关键字 在父节点中的位置
        int parentEntryIdx = getMiddleKeyIdxInParent(this);
        parent.entries.remove(parentEntryIdx);
        parent.entries.add(parentEntryIdx, borrowKey);
    }

    /**
     * 从后兄弟叶子节点 借
     */
    private void borrowLeafNext() {
        Tuple borrowKey = next.entries.get(0);
        next.entries.remove(0);
        entries.add(borrowKey);
        // 获取 当前节点指针 和 后节点指针 之间的 关键字 在父节点中的位置
        int parentEntryIdx = getMiddleKeyIdxInParent(this.next);
        parent.entries.remove(parentEntryIdx);
        // 将后叶子节点 第1个关键字(从0开始计数)提升到父节点中
        // 由于前面已经删除第0个关键字,所以这里添加的还是0
        parent.entries.add(parentEntryIdx, next.entries.get(0));
    }

    /**
     * 获取 当前节点指针 和 前节点指针 之间的 关键字 在父节点中的位置
     */
    private int getMiddleKeyIdxInParent(BpNode node) {
        int index = parent.children.indexOf(node);
        return index - 1;
    }

    /**
     * 兄弟叶节点是否能够借出
     */
    private boolean leafCanBorrow(BpNode node) {
        if (node != null) {
            int min = getUpper(maxLength - 1, 2);
            if (node.entries.size() > min && node.parent == parent) {
                return true;
            }
        }
        return false;
    }

    /**
     * 兄弟中间节点是否可以借
     */
    private boolean middleNodeCanBorrow(BpNode node) {
        if (node != null) {
            int min = getUpper(maxLength, 2);
            if (node.children.size() > min && node.parent == parent) {
                return true;
            }
        }
        return false;
    }

    /**
     * 叶子节点是否可以合并
     */
    private boolean leafCanMerge(BpNode node) {
        if (node != null) {
            if ((entries.size() + node.entries.size() - 1) <= (maxLength - 1)
                    && parent == node.parent) {
                return true;
            }
        }
        return false;
    }

    /**
     * 中间节点是否可以合并
     */
    private boolean middleNodeCanMerge(BpNode node) {
        if (node != null) {
            if ((entries.size() + node.entries.size() + 1) <= (maxLength - 1)
                    && parent == node.parent) {
                return true;
            }
        }
        return false;
    }


    /**
     * 上取整
     */
    private int getUpper(int x, int y) {
        if (y == 2) {
            int remainder = x & 1;
            if (remainder == 0) {
                return x >> 1;
            } else {
                return (x >> 1) + 1;
            }
        } else {
            int remainder = x % y;
            if (remainder == 0) {
                return x / y;
            } else {
                return x / y + 1;
            }
        }
    }

    /**
     * 关键字是否可以直接在叶节点中删除
     */
    private boolean canRemoveDirectInLeaf() {
        if (isLeaf) {
            int maxKey = maxLength - 1;
            int remainder = maxKey % 2;
            int half;
            if (remainder == 0) {
                half = maxKey / 2;
            } else {
                half = maxKey / 2 + 1;
            }
            if ((entries.size() - 1) < half) {
                return false;
            } else {
                return true;
            }
        } else {
            throw new UnsupportedOperationException("it isn't leaf node.");
        }
    }

    /**
     * 直接在叶子节点中删除,不改变树结构
     */
    private boolean removeInLeaf(Tuple key) {
        int index = -1;
        boolean isFound = false;
        for (int i = 0; i < entries.size(); i++) {
            if (key.compare(entries.get(i)) == 0) {
                index  = i;
                isFound = true;
                break;
            }
        }
        if (index != -1) {
            entries.remove(index);
        }
        return isFound;
    }

    /**
     * 判断当前节点是否包含关键字
     */
    private boolean contains(Tuple key) {
        for (Tuple tuple : entries) {
            if (key.compare(tuple) == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 非叶节点插入关键字后,检查是否需要分裂
     */
    private void updateNode(BpTree tree) {
        // 需要分裂
        if (isNodeToSplit()) {
            System.out.println("非叶节点插入关键字后,需要分裂");
            BpNode left = new BpNode(false);
            BpNode right = new BpNode(false);

            int pLeftSize = getUpper(children.size(), 2);
            int pRightSize = children.size() - pLeftSize;   //fix bug

            // 提升到父节点的关键字
            Tuple keyToParent = entries.get(pLeftSize - 1);

            System.out.printf("middle node p left:%d  right:%d\n", pLeftSize, pRightSize);
            // 复制左边的关键字
            for (int i = 0; i < (pLeftSize - 1); i++) {
                left.entries.add(entries.get(i));
            }
            // 复制左边的指针
            for (int i = 0; i < pLeftSize; i++) {
                left.children.add(children.get(i));
                left.children.get(i).setParent(left);
            }

            // 复制右边关键字,右边的第一个关键字提升到父节点
            for (int i = 0;  i < (pRightSize - 1); i++) {
                right.entries.add(entries.get(pLeftSize + i));
            }
            // 复制右边的指针
            for (int i = 0; i < pRightSize; i++) {
                right.children.add(children.get(pLeftSize + i));
                right.children.get(i).setParent(right);     // fix index bug
            }

            if (!isRoot) {
                System.out.println("current is root:" + isRoot);
                System.out.println("非叶节点的父节点插入key");
                int index = parent.children.indexOf(this);
                parent.children.remove(index);
                left.parent = parent;
                right.parent = parent;
                parent.children.add(index, left);
                parent.children.add(index + 1, right);
                // 插入关键字
//                parent.insertInParent(keyToParent);
                parent.entries.add(index, keyToParent);
                parent.updateNode(tree);
                entries.clear();
                children.clear();
                entries = null;
                children = null;
                parent = null;
            } else {
                // 是根节点
                System.out.println("current is root:" + isRoot);
                System.out.println("parent null:" + (parent == null));
                isRoot = false;
                BpNode rootNode = new BpNode(false, true);
                tree.root = rootNode;
                left.parent = rootNode;
                right.parent = rootNode;
                rootNode.children.add(left);
                rootNode.children.add(right);
                children.clear();
                entries.clear();
                children = null;
                entries = null;
                // 插入关键字
//                rootNode.insertInParent(keyToParent);
                rootNode.entries.add(keyToParent);
            }
        }
    }

    /**
     * 叶子节点是否需要分裂,
     * 用于插入前进行判断
     */
    private boolean isLeafToSplit() {
        if (isLeaf) {
            if (entries.size() >= (maxLength - 1)) {
                return true;
            }
            return false;
        } else {
            throw new UnsupportedOperationException("the node is not leaf.");
        }
    }

    /**
     * 中间节点是否需要分裂,
     * 已经插入指针和关键字
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
        entries.add(key);
    }

    /**
     * 验证节点是否满足 point数 = key数 + 1
     */
    private boolean checkKeyPointRelation() {
        if (!isLeaf) {
            if ((entries.size() + 1) == children.size()) {
                return true;
            } else {
                System.out.println("不满足 point数 = key数 + 1");
                return false;
            }
        }
        return true;
    }

    /**
     * 检查关键字是否有序
     */
    private boolean keyIsOrder() {
        for (int i = 0; i < (entries.size() - 1); i++) {
            if (entries.get(i).compare(entries.get(i + 1)) > 0) {
                System.out.println("节点关键字 不 有序");
                return false;
            }
        }
        return true;
    }

    /**
     * 验证节点是否符合B+树 定义
     */
    boolean validate() {
        if (checkKeyPointRelation()) {
            // 检查关键字是否有序
            if (keyIsOrder()) {
                if (isLeaf) {
                    if (isRoot) {
                        // 是页节点 且是 根节点
                        return true;
                    } else {
                        // 是叶子节点 不是 根节点
                        if (entries.size() < getUpper(maxLength - 1, 2) || entries.size() > (maxLength - 1)) {
                            System.out.println("叶节点key数 不合法");
                            return false;
                        }
                        if (parent == null) {
                            System.out.println("叶子节点的父节点的指针为空");
                            return false;
                        }
                    }
                    return true;
                } else {
                    // 非叶子节点
                    // 先检查指针数是否符合
                    if (isRoot) {
                        if (children.size() < 2) {
                            System.out.printf("根节点指针数 不合法, children:%d\n", children.size());
                            return false;
                        }
                    } else {
                        if (children.size() < getUpper(maxLength, 2) || children.size() > maxLength) {
                            System.out.printf("非叶节点指针数 不合法, children:%d\n", children.size());
                            System.out.printf("entry:%d\n", entries.size());
                            return false;
                        }
                        for (BpNode node : children) {
                            if (node.parent == null) {
                                System.out.println("中间节点的父指针为空");
                                return false;
                            }
                        }
                    }
                    for (BpNode node : children) {
                        if (node.validate()) {
                            // 子节点符合B+树定义
                            int pIdx = children.indexOf(node);
                            Tuple minChildKey = node.entries.get(0);
                            Tuple maxChildKey = node.entries.get(node.entries.size() - 1);
                            if (pIdx == 0) {
                                // 第一个指针
                                boolean isValid = maxChildKey.compare(entries.get(0)) < 0;
                                if (!isValid) {
                                    System.out.println("子节点与父节点不满足大小关系");
                                    return false;
                                }
                            } else if (pIdx == (children.size() - 1)) {
                                // 最后一个指针
                                boolean isValid = minChildKey.compare(entries.get(entries.size() - 1)) >= 0;
                                if (!isValid) {
                                    System.out.println("子节点与父节点不满足大小关系");
                                    return false;
                                }
                            } else {
                                Tuple preKey = entries.get(pIdx - 1);
                                Tuple nextKey = entries.get(pIdx);
                                boolean isValid = minChildKey.compare(preKey) >= 0
                                        && maxChildKey.compare(nextKey) < 0;
                                if (!isValid) {
                                    System.out.println("子节点与父节点不满足大小关系");
                                    return false;
                                }
                            }
                        } else {
                            // 子节点违反B+树定义
                            System.out.println("子节点违反B+树定义");
                            return false;
                        }
                    }
                    return true;
                }
            } else {
                // 关键字不有序
                return false;
            }
        } else {
            return false;
        }
    }

}
