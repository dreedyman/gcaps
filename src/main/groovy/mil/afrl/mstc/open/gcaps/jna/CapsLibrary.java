package mil.afrl.mstc.open.gcaps.jna;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

@SuppressWarnings("unused")
public class CapsLibrary implements Library {
	private static final String CAPS_LIBRARY_NAME = "caps";
	private static final NativeLibrary CAPS_NATIVE_LIB = NativeLibrary.getInstance(CAPS_LIBRARY_NAME);
	static {
		Native.register(CapsLibrary.class, CAPS_NATIVE_LIB);
	}

	public void close() {
		CAPS_NATIVE_LIB.dispose();
	}

	public interface CapsFidelity {
		int ALL = 0;
		int WAKE = 32;
		int STRUCTURE = 64;
		int LINEARAERO = 128;
		int FULLPOTENTIAL = 256;
		int CFD = 512;
	}

	public interface CapsOType {
		int BODIES = -2;
		int ATTRIBUTES = -1;
		int UNUSED = 0;
		int PROBLEM = 1;
		int VALUE = 2;
		int ANALYSIS = 3;
		int BOUND = 4;
		int VERTEXSET = 5;
		int DATASET = 6;
	}

	public interface CapsSType {
		int NONE = 0;
		int STATIC = 1;
		int PARAMETRIC = 2;
		int GEOMETRYIN = 3;
		int GEOMETRYOUT = 4;
		int BRANCH = 5;
		int PARAMETER = 6;
		int USER = 7;
		int ANALYSISIN = 8;
		int ANALYSISOUT = 9;
		int CONNECTED = 10;
		int UNCONNECTED = 11;
	}

	int CAPSMAJOR = (int)0;
	int CAPSMINOR = (int)73;
	public static int CAPSMAGIC = 1234321;

	/* =========================================================================================================== *
	 *  Native methods
	 * =========================================================================================================== */

	public native int caps_open(String filename, String pname, PointerByReference pobject);
    public native int caps_open(String filename, String pname, CapsObject.ByReference pobject);
	public native int caps_close(CapsObject pobject);
	public native int caps_outLevel(CapsObject pobject, int outLevel);
	public native int caps_load(CapsObject pobj, Pointer anam, Pointer apath, int fidelity, int nparent, PointerByReference parents, PointerByReference aobj);
	public native int caps_load(CapsObject pobj, Pointer anam, Pointer apath, int fidelity, int nparent, CapsObject.ByReference parents, CapsObject.ByReference aobj);
	public native int caps_childByName(CapsObject object, int typ, int styp, String name, PointerByReference child);
	public native int caps_getValue(CapsObject object, IntByReference type, IntByReference vlen, PointerByReference data, PointerByReference units, IntByReference nErr, PointerByReference errors);
	public native int caps_setValue(CapsObject object, int nrow, int ncol, Pointer data);
	public native int caps_setValue(CapsObject object, int nrow, int ncol, CapsTuple.ByReference data);
	/*public native int caps_info(CapsObject object,
								PointerByReference name,
								IntByReference type,
								IntByReference subtype,
								PointerByReference link,
								PointerByReference parent,
								PointerByReference last);*/


	public static native int caps_info(CapsObject object,
									   PointerByReference name,
									   IntByReference type,
									   IntByReference subtype,
									   CapsObject.ByReference link,
									   CapsObject.ByReference parent,
									   CapsOwn last);

	public native int caps_postAnalysis(CapsObject aobject, CapsOwn current, IntByReference nErr, PointerByReference errors);

	public native int caps_postAnalysis(CapsObject aobject, CapsOwn.ByValue current, IntByReference nErr, CapsErrs.ByReference errors);

	public native int caps_preAnalysis(CapsObject aobject, IntByReference nErr, PointerByReference errors);

	public native void printObjects(CapsObject object, int indent);
	public native int caps_getValueShape(CapsObject object, IntByReference dim, IntByReference lfix, IntByReference sfix, IntByReference ntype, IntByReference nrow, IntByReference ncol);

}
