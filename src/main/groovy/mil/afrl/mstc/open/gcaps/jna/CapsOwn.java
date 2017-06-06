package mil.afrl.mstc.open.gcaps.jna;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;

public class CapsOwn extends Structure {
	/**
	 * the process name -- NULL from Problem<br>
	 * C type : char*
	 */
	public Pointer pname;
	/**
	 * the process ID   -- NULL from Problem<br>
	 * C type : char*
	 */
	public Pointer pID;
	/**
	 * the user name    -- NULL from Problem<br>
	 * C type : char*
	 */
	public Pointer user;
	/**
	 * the date/time stamp<br>
	 * C type : short[6]
	 */
	public short[] datetime = new short[6];
	/** the CAPS sequence number */
	public NativeLong sNum;
	public CapsOwn() {
		super();
	}
    protected List<String> getFieldOrder() {
		return Arrays.asList("pname", "pID", "user", "datetime", "sNum");
	}

	public CapsOwn(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends CapsOwn implements Structure.ByReference {
		public ByReference() { }
		public ByReference(Pointer p) { super(p); read(); }
	}
	public static class ByValue extends CapsOwn implements Structure.ByValue {
		public ByValue() { }
		public ByValue(Pointer p) { super(p); read(); }
	}

    @Override public String toString() {
        return "CapsOwn{" +
               "pname=" + pname.getString(0) +
               ", pID=" + pID.getString(0) +
               ", user=" + user.getString(0) +
               ", datetime=" + Arrays.toString(datetime) +
               ", sNum=" + sNum.longValue() +
               '}';
    }
}
