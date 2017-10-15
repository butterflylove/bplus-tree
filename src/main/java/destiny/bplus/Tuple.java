package destiny.bplus;

import destiny.bplus.value.Value;

/**
 * 元组
 * @author zhangtianlong
 */
public class Tuple {

    Value[] values;

    public Tuple() {
    }

    public Tuple(Value[] values) {
        this.values = values;
    }

    public Value[] getValues() {
        return values;
    }

    public void setValues(Value[] values) {
        this.values = values;
    }

    public int getLength() {
        int sum = 0;
        for (Value value : values) {
            sum += value.getLength();
        }
        return sum;
    }

    public int compare(Tuple tuple) {
        int min = values.length < tuple.values.length ? values.length : tuple.values.length;
        for (int i = 0; i < min; i++) {
            int comp = values[i].compare(tuple.values[i]);
            if (comp == 0) {
                continue;
            }
            return comp;
        }
        // TODO 前缀完全相同,根据长度比较
        return 0;
    }


}
