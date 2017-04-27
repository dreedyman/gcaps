package mil.afrl.mstc.open.gcaps.jna;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;

public class EGAttrs extends Structure {
	/** number of attributes */
	public int nattrs;
	/**
	 * the attributes<br>
	 * C type : egAttr*
	 */
	public EGAttr.ByReference attrs;
	public EGAttrs() {
		super();
	}
	protected List<String> getFieldOrder() {
		return Arrays.asList("nattrs", "attrs");
	}
	/**
	 * @param nattrs number of attributes<br>
	 * @param attrs the attributes<br>
	 * C type : egAttr*
	 */
	public EGAttrs(int nattrs, EGAttr.ByReference attrs) {
		super();
		this.nattrs = nattrs;
		this.attrs = attrs;
	}
	public EGAttrs(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends EGAttrs implements Structure.ByReference {
        public ByReference() { }
        public ByReference(Pointer p) { super(p); read(); }
	}
	public static class ByValue extends EGAttrs implements Structure.ByValue {
		public ByValue() { }
		public ByValue(Pointer p) { super(p); read(); }
	}
}
