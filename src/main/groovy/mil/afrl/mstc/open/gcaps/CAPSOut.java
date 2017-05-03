package mil.afrl.mstc.open.gcaps;

/**
 * @author Dennis Reedy
 */
public enum CAPSOut {
    /* Options: 0 (or "minimal"), 1 (or "standard") [default], and 2 (or "debug") */
    MINIMAL(0), STANDARD(1), DEBUG(2);
    final int level;
    CAPSOut(int level) {
        this.level = level;
    }
}
