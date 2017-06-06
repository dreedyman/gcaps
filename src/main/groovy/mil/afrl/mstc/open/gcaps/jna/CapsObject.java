package mil.afrl.mstc.open.gcaps.jna;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class CapsObject extends Structure {
	/** must be set to validate the object */
	public int magicnumber;
	/** object type */
	public int type;
	/** object subtype */
	public int subtype;
	/** object serial number for I/O */
	public int sn;
	/**
	 * object name<br>
	 * C type : char*
	 */
	public Pointer name;
	/**
	 * object attributes<br>
	 * C type : egAttrs*
	 */
	public EGAttrs.ByReference attrs;
	/**
	 * blind pointer to object data<br>
	 * C type : void*
	 */
	public Pointer blind;
	/**
	 * last to modify the object<br>
	 * C type : capsOwn
	 */
	public CapsOwn last;

	/** C type : capsObject* */
	public CapsObject.ByReference parent;

	public CapsObject() {
		super();
	}

	protected List<String> getFieldOrder() {
		return Arrays.asList("magicnumber", "type", "subtype", "sn", "name", "attrs", "blind", "last", "parent");
	}

	public CapsObject(Pointer peer) {
		super(peer);
    }

	public static class ByReference extends CapsObject implements Structure.ByReference {
		public ByReference() { }
		public ByReference(Pointer p) { super(p); read(); }
	}

	public static class ByValue extends CapsObject implements Structure.ByValue {
        public ByValue() { }
        public ByValue(Pointer p) { super(p); read(); }
	}

    @Override public String toString() {
        return "CapsObject{" +
               "\n\tmagicnumber=" + magicnumber +
               "\n\ttype=" + type +
               "\n\tsubtype=" + subtype +
               "\n\tsn=" + sn +
               "\n\tname=" + name.getString(0) +
               "\n\tattrs=" + attrs +
               "\n\tblind=" + blind.getString(0) +
               //", last=" + last==null?"null":last +
               "\n\t, parent=" + parent +
               '}';
    }
}
