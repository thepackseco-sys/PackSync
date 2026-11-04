package com.nutomic.syncthingandroid.receiver;

/** Plain JVM regression test: executes the same policy class used by the receiver. */
public final class ActionPolicyRegression {
    public static void main(String[] args) {
        String p = "com.packseco.packsync"; int checked = 0;
        for (String suffix : new String[]{".action.FOLLOW", ".action.START", ".action.STOP"}) {
            if (!suffix.equals(AppActionPolicy.suffix(p, p + suffix))) throw new AssertionError("Valid action rejected");
            checked++;
        }
        for (String invalid : new String[]{null, "", ".action.START", "foreign.action.START",
                "comXpacksecoXpacksync.action.START", "prefix." + p + ".action.START",
                p + ".action.START.suffix", p + ".action.UNKNOWN", p + ".action.start"}) {
            if (AppActionPolicy.suffix(p, invalid) != null) throw new AssertionError("Malformed action accepted");
            checked++;
        }
        if (AppActionPolicy.suffix(null, ".action.START") != null) throw new AssertionError("Null package accepted");
        if (AppActionPolicy.suffix("", ".action.START") != null) throw new AssertionError("Empty package accepted");
        checked += 2;
        System.out.println("PASS: " + checked + " action-policy regression cases; Android receiver lifecycle not simulated");
    }
}
