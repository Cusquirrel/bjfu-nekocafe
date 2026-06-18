package com.bjfu.nekocafe.common;

public final class TraceId {
    private static final ThreadLocal<String> HOLDER = new ThreadLocal<String>();
    private TraceId() {}
    public static String get() { return HOLDER.get(); }
    public static void set(String id) { HOLDER.set(id); }
    public static void clear() { HOLDER.remove(); }
}
