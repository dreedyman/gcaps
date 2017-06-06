package mil.afrl.mstc.open.gcaps.jna;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;

public class CapsTuple extends Structure {
	/**
	 * the name<br>
	 * C type : char*
	 */
	public Pointer name;
	/**
	 * the value for the pair<br>
	 * C type : char*
	 */
	public Pointer value;
	public CapsTuple() {
		super();
	}
	protected List<String> getFieldOrder() {
		return Arrays.asList("name", "value");
	}

	public CapsTuple(Pointer peer) {
		super(peer);
	}

	public static class ByReference extends CapsTuple implements Structure.ByReference {
		public ByReference() { }
		public ByReference(Pointer p) { super(p); read(); }
	}
	public static class ByValue extends CapsTuple implements Structure.ByValue {
		public ByValue() { }
		public ByValue(Pointer p) { super(p); read(); }
	}
}
