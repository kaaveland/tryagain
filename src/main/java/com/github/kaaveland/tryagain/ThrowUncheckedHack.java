package com.github.kaaveland.tryagain;

public class ThrowUncheckedHack<E extends Exception> {
    public static void throwUnchecked(Exception t) {
        ThrowUncheckedHack.<RuntimeException>throwUp(t);
    }

    public static <E extends Exception> void throwUp(Exception e) throws E {
        throw (E) e;
    }

}
