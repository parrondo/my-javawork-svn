/*   */ package com.dukascopy.dds2.greed.util;
/*   */ 
/*   */ public abstract interface PlatformSpecific
/*   */ {
/* 5 */   public static final boolean MACOSX = UIContext.getOperatingSystemType().equals(UIContext.OsType.MACOSX);
/* 6 */   public static final boolean LINUX = UIContext.getOperatingSystemType().equals(UIContext.OsType.LINUX);
/* 7 */   public static final boolean WINDOWS = UIContext.getOperatingSystemType().equals(UIContext.OsType.WINDOWS);
/*   */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.PlatformSpecific
 * JD-Core Version:    0.6.0
 */