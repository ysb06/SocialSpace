package lab.u2xd.socialspace.worker.warehouse.objects;

import android.content.ContentValues;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ysb on 2015-10-02.
 */
public class Datastone {

    private static final byte TYPE_BOOLEAN = 1;
    private static final byte TYPE_BYTE = 2;
    private static final byte TYPE_CHARACTER = 3;
    private static final byte TYPE_SHORT = 4;
    private static final byte TYPE_INTEGER = 5;
    private static final byte TYPE_LONG = 6;
    private static final byte TYPE_FLOAT = 7;
    private static final byte TYPE_DOUBLE = 8;
    private static final byte TYPE_STRING = 9;

    private static final int[] LIST_TYPE = {TYPE_BOOLEAN, TYPE_BYTE, TYPE_CHARACTER,
            TYPE_SHORT, TYPE_INTEGER, TYPE_LONG, TYPE_FLOAT, TYPE_DOUBLE, TYPE_STRING};

    protected ArrayList<String> listColumn;
    protected HashMap<String, Object> listValue;
    protected HashMap<Object, Byte> listValueType;

    public Datastone() {
        listColumn = new ArrayList<>();
        listValue = new HashMap<>();
        listValueType = new HashMap<>();
    }

    public void put(String column, boolean value) { setValue(column, value, TYPE_BOOLEAN); }
    public void put(String column, byte value) { setValue(column, value, TYPE_BYTE); }
    public void put(String column, char value) { setValue(column, value, TYPE_CHARACTER); }
    public void put(String column, short value) { setValue(column, value, TYPE_SHORT); }
    public void put(String column, int value) { setValue(column, value, TYPE_INTEGER); }
    public void put(String column, long value) { setValue(column, value, TYPE_LONG); }
    public void put(String column, float value) { setValue(column, value, TYPE_FLOAT); }
    public void put(String column, double value) { setValue(column, value, TYPE_DOUBLE); }
    public void put(String column, String value) { setValue(column, value, TYPE_STRING); }

    private void setValue(String column, Object value, byte type) {
        listColumn.add(column);
        listValue.put(column, value);
        listValueType.put(value, type);
    }

    public ContentValues refineToContentValues() {
        ContentValues values = new ContentValues();

        String key;
        Object value;

        for(int i = 0; i < listColumn.size(); i++) {
            key = listColumn.get(i);
            value = listValue.get(key);

            switch (listValueType.get(value)) {
                case Datastone.TYPE_BOOLEAN:
                    values.put(key, (boolean) value);
                    break;
                case Datastone.TYPE_BYTE:
                    values.put(key, (byte) value);
                    break;
                case Datastone.TYPE_CHARACTER:
                    values.put(key, (short)(char) value);
                    break;
                case Datastone.TYPE_SHORT:
                    values.put(key, (short) value);
                    break;
                case Datastone.TYPE_INTEGER:
                    values.put(key, (int) value);
                    break;
                case Datastone.TYPE_LONG:
                    values.put(key, (long) value);
                    break;
                case Datastone.TYPE_FLOAT:
                    values.put(key, (float) value);
                    break;
                case Datastone.TYPE_DOUBLE:
                    values.put(key, (double) value);
                    break;
                case Datastone.TYPE_STRING:
                    values.put(key, (String) value);
                    break;
            }
        }

        return values;
    }

    public String[] refineToStringList() {
        String[] list = new String[listColumn.size()];
        for(int i = 0; i < listColumn.size(); i++) {
            list[i] = (String) listValue.get(listColumn.get(i));
        }
        return list;
    }

    @Override
    public String toString() {
        String str = "";
        for(int i = 0; i < listColumn.size(); i++) {
            str += "[" + i + "]" + " " + listColumn.get(i) + ":\t" + listValue.get(listColumn.get(i)) + "\t";
        }
        return str;
    }
}
