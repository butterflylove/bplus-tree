package destiny.bplus.value;

/**
 * Created by zhangtianlong on 17/10/15.
 */
public class LongValue extends Value {

    private long i;

    public LongValue() {
    }

    public LongValue(long i) {
        this.i = i;
    }

    public long getLong() {
        return i;
    }

    public void setLong(long i) {
        this.i = i;
    }

    @Override
    public int getLength() {
        return 1 + 8;
    }

    @Override
    public byte getType() {
        return LONG;
    }

    @Override
    public int compare(Value value) {
        long target = ((LongValue) value).getLong();
        if (i > target) {
            return 1;
        } else if (i == target) {
            return 0;
        } else {
            return -1;
        }
    }

    @Override
    public String toString() {
        return String.valueOf(i);
    }
}
