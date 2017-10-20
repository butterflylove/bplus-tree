import destiny.bplus.BpNode;
import destiny.bplus.BpTree;
import destiny.bplus.Tuple;
import destiny.bplus.value.IntValue;
import destiny.bplus.value.StringValue;
import destiny.bplus.value.Value;
import org.junit.Test;
import java.util.Random;

/**
 * @author zhangtianlong
 */
public class BpTreeTest {

    @Test
    public void testRandom() {
        BpTree tree = new BpTree();
        int n = 100000;
        Random random = new Random();
        int i;
        for (i = 0; i < n; i++) {
            int x = random.nextInt(n);
            Tuple tuple = createTuple(x);
            tree.insert(tuple);
            boolean isValid = tree.validate();
            if (!isValid) {
                break;
            }
        }
        System.out.println(i);
    }

    @Test
    public void test1() {
        BpTree bpTree = new BpTree();
        for (int i = 1; i <= 20; i++) {
            bpTree.insert(createTuple(i));
        }
        bpTree.insert(createTuple(21));
        printLink(bpTree.getHead());
        BpNode root = bpTree.getRoot();
        printNode(root.getChildren().get(0));
        printNode(root.getChildren().get(1));

        System.out.println("root=" + bpTree.getRoot().getEntries().get(0).getValues()[0]);
        boolean isValid = bpTree.validate();
        System.out.println("isValid:" + isValid);

        Tuple key = bpTree.find(createTuple(16));
        System.out.println("key:" + key.getValues()[0]);
    }

    private static void printNode(BpNode node) {
        for (Tuple key : node.getEntries()) {
            System.out.println(key.getValues()[0]);
        }
    }

    private static void printLink(BpNode head) {
        while (head != null) {
            for (Tuple key : head.getEntries()) {
                System.out.println(key.getValues()[0]);
            }
            head = head.getNext();
        }
    }

    private static Tuple createTuple(int i) {
        Value[] values = new Value[1];
        values[0] = new IntValue(i);
//        values[1] = new StringValue("fate");
        return new Tuple(values);
    }

    @Test
    public void testRemove() {
        int x = 44445 & 1;
        System.out.println(x);
    }
}
