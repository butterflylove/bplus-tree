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
        // TODO: 17/10/15
        return 0;
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
