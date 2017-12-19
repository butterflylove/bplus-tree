package destiny.bplus.value;

/**
 * @author zhangtianlong
 */
public class BooleanValue extends Value {

    private boolean b;

    public BooleanValue() {

    }

    public BooleanValue(boolean b) {
        this.b = b;
    }

    public boolean getBoolean() {
        return b;
    }

    public void setBoolean(boolean b) {
        this.b = b;
    }

    @Override
    public int getLength() {
        return 1 + 1;
    }

    @Override
    public byte getType() {
        return BOOLEAN;
    }

    @Override
    public int compare(Value value) {
        boolean target = ((BooleanValue) value).getBoolean();
        return (b == target) ? 0 : (b ? 1 : -1);
    }

    @Override
    public String toString() {
        if (b) {
            return "true";
        } else {
            return "false";
        }
    }
}
