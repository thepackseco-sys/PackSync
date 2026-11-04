package com.nutomic.syncthingandroid.receiver;

/** Exact application-scoped allowlist. This does not enable broadcast control. */
public final class AppActionPolicy {
    private AppActionPolicy() {}
    public static String suffix(String packageName, String action) {
        if (packageName == null || packageName.isEmpty() || action == null) return null;
        if (action.equals(packageName + ".action.FOLLOW")) return ".action.FOLLOW";
        if (action.equals(packageName + ".action.START")) return ".action.START";
        if (action.equals(packageName + ".action.STOP")) return ".action.STOP";
        return null;
    }
}
