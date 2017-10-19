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
        int n = 5000;
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            int x = random.nextInt(n);
            Tuple tuple = createTuple(x);
            tree.insert(tuple);
            boolean isValid = tree.validate();
            if (!isValid) {
                break;
            }
        }
//        boolean isValid = tree.validate();
//        System.out.println("isValid:" + isValid);
    }

    @Test
    public void test1() {
        BpTree bpTree = new BpTree();
        bpTree.insert(createTuple(7699));
        bpTree.insert(createTuple(3825));
        bpTree.insert(createTuple(9358));
        bpTree.insert(createTuple(4519));
        bpTree.insert(createTuple(1362));
        bpTree.insert(createTuple(2288));
        bpTree.insert(createTuple(5599));
        bpTree.insert(createTuple(1562));
        bpTree.insert(createTuple(898));
        bpTree.insert(createTuple(9786));
        bpTree.insert(createTuple(9691));
        bpTree.insert(createTuple(4139));
        bpTree.insert(createTuple(9674));
        bpTree.insert(createTuple(3620));
        bpTree.insert(createTuple(5514));
        bpTree.insert(createTuple(6645));
        bpTree.insert(createTuple(6949));
        bpTree.insert(createTuple(8651));
        bpTree.insert(createTuple(9645));
        bpTree.insert(createTuple(5175));
        bpTree.insert(createTuple(6162));
        bpTree.insert(createTuple(6521));
        bpTree.insert(createTuple(3214));
        bpTree.insert(createTuple(7351));
        bpTree.insert(createTuple(7095));
        bpTree.insert(createTuple(3719));
        bpTree.insert(createTuple(1883));
        bpTree.insert(createTuple(1494));
        bpTree.insert(createTuple(9660));
        bpTree.insert(createTuple(1438));
        bpTree.insert(createTuple(6874));
        bpTree.insert(createTuple(2854));
        bpTree.insert(createTuple(5718));

        boolean isValid = bpTree.validate();
        System.out.println("isValid:" + isValid);
    }

    private static Tuple createTuple(int i) {
        Value[] values = new Value[2];
        values[0] = new IntValue(i);
        values[1] = new StringValue("fate");
        return new Tuple(values);
    }

    @Test
    public void testRemove() {
        int x = 44445 & 1;
        System.out.println(x);
    }
}
