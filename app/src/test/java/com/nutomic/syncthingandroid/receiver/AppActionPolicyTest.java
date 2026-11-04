package com.nutomic.syncthingandroid.receiver;
import org.junit.Test;
import static org.junit.Assert.*;
public class AppActionPolicyTest {
    private static final String PACKAGE = "com.packseco.packsync";
    @Test public void exactActionsRemainSupported() {
        for (String suffix : new String[]{".action.FOLLOW", ".action.START", ".action.STOP"})
            assertEquals(suffix, AppActionPolicy.suffix(PACKAGE, PACKAGE + suffix));
    }
    @Test public void malformedAndForeignActionsHaveNoAcceptedCommand() {
        String[] invalid = {null, "", ".action.START", "foreign.action.START",
            "comXpacksecoXpacksync.action.START", "prefix." + PACKAGE + ".action.START",
            PACKAGE + ".action.START.suffix", PACKAGE + ".action.UNKNOWN", PACKAGE + ".action.start"};
        for (String action : invalid) assertNull(AppActionPolicy.suffix(PACKAGE, action));
    }
    @Test public void missingPackageIsRejected() {
        assertNull(AppActionPolicy.suffix(null, ".action.START"));
        assertNull(AppActionPolicy.suffix("", ".action.START"));
    }
}
