package destiny.bplus.value;

/**
 * Created by zhangtianlong on 17/10/15.
 */
public class StringValue extends Value {

    private String s;

    public StringValue() {
    }

    public StringValue(String s) {
        this.s = s;
    }

    public String getString() {
        return s;
    }

    public void setString(String s) {
        this.s = s;
    }

    // [type][length][data]
    @Override
    public int getLength() {
        return 1 + 4 + s.length();
    }

    @Override
    public byte getType() {
        return STRING;
    }

    @Override
    public int compare(Value value) {
        String target = ((StringValue) value).getString();
        int result = s.compareTo(target);
        if (result > 0) {
            return 1;
        } else if (result < 0) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return s;
    }

}
