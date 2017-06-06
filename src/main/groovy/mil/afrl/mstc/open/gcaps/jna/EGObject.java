package mil.afrl.mstc.open.gcaps.jna;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class EGObject extends Structure {
	/** must be set to validate the object */
	public int magicnumber;
	/** object Class */
	public short oclass;
	/** member Type */
	public short mtype;
	/**
	 * object Attributes or Reference<br>
	 * C type : void*
	 */
	public Pointer attrs;
	/**
	 * blind pointer to object data<br>
	 * C type : void*
	 */
	public Pointer blind;
	/**
	 * top of the hierarchy or context (if top)<br>
	 * C type : egObject*
	 */
	public EGObject.ByReference topObj;
	/**
	 * threaded list of references<br>
	 * C type : egObject*
	 */
	public EGObject.ByReference tref;
	/**
	 * back pointer<br>
	 * C type : egObject*
	 */
	public EGObject.ByReference prev;
	/**
	 * forward pointer<br>
	 * C type : egObject*
	 */
	public EGObject.ByReference next;
	public EGObject() {
		super();
	}

	@Override protected List<String> getFieldOrder() {
		return Arrays.asList("magicnumber", "oclass", "mtype", "attrs", "blind", "topObj", "tref", "prev", "next");
	}

	public EGObject(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends EGObject implements Structure.ByReference {
        public ByReference() { }
        public ByReference(Pointer p) { super(p); read(); }
	}
	public static class ByValue extends EGObject implements Structure.ByValue {
        public ByValue() { }
        public ByValue(Pointer p) { super(p); read(); }
	}
}
