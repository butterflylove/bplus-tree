package destiny.bplus;

import destiny.bplus.value.Value;

/**
 * 元组
 *
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

    /**
     * 联合索引比较的时候,先比较第一个索引值,若相等则再比较下一个索引值,依次类推
     */
    public int compare(Tuple tuple) {
        int min = values.length < tuple.values.length ? values.length : tuple.values.length;
        for (int i = 0; i < min; i++) {
            int comp = values[i].compare(tuple.values[i]);
            if (comp == 0) {
                continue;
            }
            return comp;
        }
        int res = values.length - tuple.values.length;
        return (res == 0) ? 0 : (res > 1 ? 1 : -1);
    }

}
