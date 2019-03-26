package com.liugeng.mthttp.router.asm;

public interface ClassMetadata {
    String getClassName();

    boolean isInterface();

    boolean isAnnotation();

    boolean isAbstract();

    boolean isFinal();

    boolean hasSuperClass();

    String getSuperClassName();

    String[] getInterfaceNames();
}
