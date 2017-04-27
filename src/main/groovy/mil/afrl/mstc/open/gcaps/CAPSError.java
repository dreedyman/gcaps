package mil.afrl.mstc.open.gcaps;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dennis Reedy
 */
public enum CAPSError {
    CAPS_SUCCESS(0),
    CAPS_BADRANK(-301),
    CAPS_BADDSETNAME(-302),
    CAPS_NOTFOUND (-303),
    /** <i>native declaration : /Users/dreedy/dev/src/projects/mstc/caps/EngSketchPad/src/CAPS/include/capsErrors.h</i> */
    CAPS_BADINDEX (-304),
    /** <i>native declaration : /Users/dreedy/dev/src/projects/mstc/caps/EngSketchPad/src/CAPS/include/capsErrors.h</i> */
    CAPS_NOTCHANGED (-305),
    /** <i>native declaration : /Users/dreedy/dev/src/projects/mstc/caps/EngSketchPad/src/CAPS/include/capsErrors.h</i> */
    CAPS_BADTYPE (-306),
    /** <i>native declaration : /Users/dreedy/dev/src/projects/mstc/caps/EngSketchPad/src/CAPS/include/capsErrors.h</i> */
    CAPS_NULLVALUE (-307),
    /** <i>native declaration : /Users/dreedy/dev/src/projects/mstc/caps/EngSketchPad/src/CAPS/include/capsErrors.h</i> */
    CAPS_NULLNAME (-308),
    /** <i>native declaration : /Users/dreedy/dev/src/projects/mstc/caps/EngSketchPad/src/CAPS/include/capsErrors.h</i> */
    CAPS_NULLOBJ (-309),
    /** <i>native declaration : /Users/dreedy/dev/src/projects/mstc/caps/EngSketchPad/src/CAPS/include/capsErrors.h</i> */
    CAPS_BADOBJECT (-310),
    /** <i>native declaration : /Users/dreedy/dev/src/projects/mstc/caps/EngSketchPad/src/CAPS/include/capsErrors.h</i> */
    CAPS_BADVALUE (-311),
    /** <i>native declaration : /Users/dreedy/dev/src/projects/mstc/caps/EngSketchPad/src/CAPS/include/capsErrors.h</i> */
    CAPS_PARAMBNDERR (-312),
    /** <i>native declaration : /Users/dreedy/dev/src/projects/mstc/caps/EngSketchPad/src/CAPS/include/capsErrors.h</i> */
    CAPS_NOTCONNECT (-313),
    /** <i>native declaration : /Users/dreedy/dev/src/projects/mstc/caps/EngSketchPad/src/CAPS/include/capsErrors.h</i> */
    CAPS_NOTPARMTRIC (-314),
    /** <i>native declaration : /Users/dreedy/dev/src/projects/mstc/caps/EngSketchPad/src/CAPS/include/capsErrors.h</i> */
    CAPS_READONLYERR (-315),
    /** <i>native declaration : /Users/dreedy/dev/src/projects/mstc/caps/EngSketchPad/src/CAPS/include/capsErrors.h</i> */
    CAPS_FIXEDLEN (-316),
    /** <i>native declaration : /Users/dreedy/dev/src/projects/mstc/caps/EngSketchPad/src/CAPS/include/capsErrors.h</i> */
    CAPS_BADNAME (-317),
    /** <i>native declaration : /Users/dreedy/dev/src/projects/mstc/caps/EngSketchPad/src/CAPS/include/capsErrors.h</i> */
    CAPS_BADMETHOD (-318),
    /** <i>native declaration : /Users/dreedy/dev/src/projects/mstc/caps/EngSketchPad/src/CAPS/include/capsErrors.h</i> */
    CAPS_CIRCULARLINK (-319),
    /** <i>native declaration : /Users/dreedy/dev/src/projects/mstc/caps/EngSketchPad/src/CAPS/include/capsErrors.h</i> */
    CAPS_UNITERR (-320),
    /** <i>native declaration : /Users/dreedy/dev/src/projects/mstc/caps/EngSketchPad/src/CAPS/include/capsErrors.h</i> */
    CAPS_NULLBLIND (-321),
    /** <i>native declaration : /Users/dreedy/dev/src/projects/mstc/caps/EngSketchPad/src/CAPS/include/capsErrors.h</i> */
    CAPS_SHAPEERR (-322),
    /** <i>native declaration : /Users/dreedy/dev/src/projects/mstc/caps/EngSketchPad/src/CAPS/include/capsErrors.h</i> */
    CAPS_LINKERR (-323),
    /** <i>native declaration : /Users/dreedy/dev/src/projects/mstc/caps/EngSketchPad/src/CAPS/include/capsErrors.h</i> */
    CAPS_MISMATCH (-324),
    /** <i>native declaration : /Users/dreedy/dev/src/projects/mstc/caps/EngSketchPad/src/CAPS/include/capsErrors.h</i> */
    CAPS_NOTPROBLEM (-325),
    /** <i>native declaration : /Users/dreedy/dev/src/projects/mstc/caps/EngSketchPad/src/CAPS/include/capsErrors.h</i> */
    CAPS_RANGEERR (-326),
    /** <i>native declaration : /Users/dreedy/dev/src/projects/mstc/caps/EngSketchPad/src/CAPS/include/capsErrors.h</i> */
    CAPS_DIRTY (-327),
    /** <i>native declaration : /Users/dreedy/dev/src/projects/mstc/caps/EngSketchPad/src/CAPS/include/capsErrors.h</i> */
    CAPS_HIERARCHERR (-328),
    /** <i>native declaration : /Users/dreedy/dev/src/projects/mstc/caps/EngSketchPad/src/CAPS/include/capsErrors.h</i> */
    CAPS_STATEERR (-329),
    /** <i>native declaration : /Users/dreedy/dev/src/projects/mstc/caps/EngSketchPad/src/CAPS/include/capsErrors.h</i> */
    CAPS_SOURCEERR (-330),
    /** <i>native declaration : /Users/dreedy/dev/src/projects/mstc/caps/EngSketchPad/src/CAPS/include/capsErrors.h</i> */
    CAPS_EXISTS (-331),
    /** <i>native declaration : /Users/dreedy/dev/src/projects/mstc/caps/EngSketchPad/src/CAPS/include/capsErrors.h</i> */
    CAPS_IOERR (-332),
    /** <i>native declaration : /Users/dreedy/dev/src/projects/mstc/caps/EngSketchPad/src/CAPS/include/capsErrors.h</i> */
    CAPS_DIRERR (-333),
    /** <i>native declaration : /Users/dreedy/dev/src/projects/mstc/caps/EngSketchPad/src/CAPS/include/capsErrors.h</i> */
    CAPS_NOTIMPLEMENT (-334),
    /** <i>native declaration : /Users/dreedy/dev/src/projects/mstc/caps/EngSketchPad/src/CAPS/include/capsErrors.h</i> */
    CAPS_EXECERR (-335),
    /** <i>native declaration : /Users/dreedy/dev/src/projects/mstc/caps/EngSketchPad/src/CAPS/include/capsErrors.h</i> */
    CAPS_CLEAN (-336),
    /** <i>native declaration : /Users/dreedy/dev/src/projects/mstc/caps/EngSketchPad/src/CAPS/include/capsErrors.h</i> */
    CAPS_BADFIDELITY (-337);
    
    private final int code;
    private static Map<Integer, CAPSError> map = new HashMap<>();
    static {
        for (CAPSError capsError : CAPSError.values()) {
            map.put(capsError.code, capsError);
        }
    }

    CAPSError(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static CAPSError valueOf(int code) {
        return map.get(code);
    }

}
