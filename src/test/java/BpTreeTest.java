import destiny.bplus.BpNode;
import destiny.bplus.BpTree;
import destiny.bplus.Tuple;
import destiny.bplus.value.IntValue;
import destiny.bplus.value.StringValue;
import destiny.bplus.value.Value;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangtianlong
 */
public class BpTreeTest {

    @Test
    public void test() {
        BpTree bpTree = new BpTree();
        bpTree.insert(genTuple(7699));
        bpTree.insert(genTuple(3825));
        bpTree.insert(genTuple(9358));
        bpTree.insert(genTuple(4519));
        bpTree.insert(genTuple(1362));
        bpTree.insert(genTuple(2288));
        bpTree.insert(genTuple(5599));
        bpTree.insert(genTuple(1562));
        bpTree.insert(genTuple(898));
        bpTree.insert(genTuple(9786));
        bpTree.insert(genTuple(9691));
        bpTree.insert(genTuple(4139));
        bpTree.insert(genTuple(9674));
        bpTree.insert(genTuple(3620));
        bpTree.insert(genTuple(5514));
        bpTree.insert(genTuple(6645));
        bpTree.insert(genTuple(6949));
        bpTree.insert(genTuple(8651));
        bpTree.insert(genTuple(9645));
        bpTree.insert(genTuple(5175));
        bpTree.insert(genTuple(6162));
        bpTree.insert(genTuple(6521));
        bpTree.insert(genTuple(3214));
        bpTree.insert(genTuple(7351));
        bpTree.insert(genTuple(7095));
        bpTree.insert(genTuple(3719));
        bpTree.insert(genTuple(1883));
        bpTree.insert(genTuple(1494));
        bpTree.insert(genTuple(9660));
        bpTree.insert(genTuple(1438));
        bpTree.insert(genTuple(6874));
        bpTree.insert(genTuple(2854));
        bpTree.insert(genTuple(5718));

        BpNode head = bpTree.getHead();
        while (head != null) {
            List<Tuple> list = head.getEntries();
            for (Tuple t : list) {
                for (Value value : t.getValues()) {
                    System.out.print(value.toString());
                }
                System.out.println();
            }
            head = head.getNext();
        }
    }

    private static Tuple genTuple(int i) {
        Value[] values = new Value[2];
        values[0] = new IntValue(i);
        values[1] = new StringValue("fate");
        return new Tuple(values);
    }
}
