package mil.afrl.mstc.open.gcaps;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dennis Reedy
 */
public enum CAPSVtype {
    Boolean (0),
    Integer (1),
    Double  (2),
    String  (3),
    Tuple (4),
    Value (5);
    private final int code;
    private static Map<Integer, CAPSVtype> map = new HashMap<>();
    static {
        for (CAPSVtype capsVtype : CAPSVtype.values()) {
            map.put(capsVtype.code, capsVtype);
        }
    }

    CAPSVtype(int code) {
        this.code = code;
    }

    public static CAPSVtype valueOf(int code) {
        return map.get(code);
    }
}
