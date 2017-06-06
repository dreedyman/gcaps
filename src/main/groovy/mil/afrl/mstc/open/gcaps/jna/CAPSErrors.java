package mil.afrl.mstc.open.gcaps.jna;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dennis Reedy
 */
public enum CAPSErrors {
    CAPS_SUCCESS(0),
    CAPS_BADRANK(-301),
    CAPS_BADDSETNAME(-302),
    CAPS_NOTFOUND (-303),
    CAPS_BADINDEX (-304),
    CAPS_NOTCHANGED (-305),
    CAPS_BADTYPE (-306),
    CAPS_NULLVALUE (-307),
    CAPS_NULLNAME (-308),
    CAPS_NULLOBJ (-309),
    CAPS_BADOBJECT (-310),
    CAPS_BADVALUE (-311),
    CAPS_PARAMBNDERR (-312),
    CAPS_NOTCONNECT (-313),
    CAPS_NOTPARMTRIC (-314),
    CAPS_READONLYERR (-315),
    CAPS_FIXEDLEN (-316),
    CAPS_BADNAME (-317),
    CAPS_BADMETHOD (-318),
    CAPS_CIRCULARLINK (-319),
    CAPS_UNITERR (-320),
    CAPS_NULLBLIND (-321),
    CAPS_SHAPEERR (-322),
    CAPS_LINKERR (-323),
    CAPS_MISMATCH (-324),
    CAPS_NOTPROBLEM (-325),
    CAPS_RANGEERR (-326),
    CAPS_DIRTY (-327),
    CAPS_HIERARCHERR (-328),
    CAPS_STATEERR (-329),
    CAPS_SOURCEERR (-330),
    CAPS_EXISTS (-331),
    CAPS_IOERR (-332),
    CAPS_DIRERR (-333),
    CAPS_NOTIMPLEMENT (-334),
    CAPS_EXECERR (-335),
    CAPS_CLEAN (-336),
    CAPS_BADFIDELITY (-337);
    
    private final int code;
    private static Map<Integer, CAPSErrors> map = new HashMap<>();
    static {
        for (CAPSErrors capsErrors : CAPSErrors.values()) {
            map.put(capsErrors.code, capsErrors);
        }
    }

    CAPSErrors(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static CAPSErrors valueOf(int code) {
        return map.get(code);
    }

}
