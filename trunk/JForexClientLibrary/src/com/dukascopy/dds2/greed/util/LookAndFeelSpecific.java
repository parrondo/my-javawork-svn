/*    */ package com.dukascopy.dds2.greed.util;
/*    */ 
/*    */ public abstract interface LookAndFeelSpecific
/*    */ {
/*  4 */   public static final boolean WINDOWS_XP = UIContext.getLookAndFeelType().equals(UIContext.LookAndFeelType.WINDOWS_XP);
/*  5 */   public static final boolean METAL = UIContext.getLookAndFeelType().equals(UIContext.LookAndFeelType.METAL);
/*  6 */   public static final boolean CLASSIC = UIContext.getLookAndFeelType().equals(UIContext.LookAndFeelType.CLASSIC);
/*  7 */   public static final boolean VISTA = UIContext.getLookAndFeelType().equals(UIContext.LookAndFeelType.VISTA);
/*  8 */   public static final boolean WIN7 = UIContext.getLookAndFeelType().equals(UIContext.LookAndFeelType.WIN7);
/*  9 */   public static final boolean MACOS = UIContext.getLookAndFeelType().equals(UIContext.LookAndFeelType.MACOS);
/* 10 */   public static final boolean OTHER = UIContext.getLookAndFeelType().equals(UIContext.LookAndFeelType.OTHER);
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.LookAndFeelSpecific
 * JD-Core Version:    0.6.0
 */