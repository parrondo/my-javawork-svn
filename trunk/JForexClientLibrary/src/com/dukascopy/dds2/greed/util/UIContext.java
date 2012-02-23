/*    */ package com.dukascopy.dds2.greed.util;
/*    */ 
/*    */ import java.awt.Toolkit;
/*    */ import javax.swing.LookAndFeel;
/*    */ import javax.swing.UIManager;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class UIContext
/*    */ {
/* 12 */   private static final Logger LOGGER = LoggerFactory.getLogger(UIContext.class);
/*    */ 
/*    */   public static OsType getOperatingSystemType()
/*    */   {
/* 18 */     OsType result = null;
/* 19 */     String osName = System.getProperty("os.name");
/* 20 */     if (osName.toLowerCase().startsWith("windows"))
/* 21 */       result = OsType.WINDOWS;
/* 22 */     else if (osName.toLowerCase().startsWith("linux"))
/*    */     {
/* 24 */       result = OsType.LINUX;
/* 25 */     } else if (osName.toLowerCase().startsWith("mac os x"))
/* 26 */       result = OsType.MACOSX;
/*    */     else {
/* 28 */       result = OsType.OTHER;
/*    */     }
/* 30 */     return result;
/*    */   }
/*    */ 
/*    */   public static LookAndFeelType getLookAndFeelType() {
/* 34 */     LookAndFeel laf = UIManager.getLookAndFeel();
/* 35 */     Toolkit toolkit = Toolkit.getDefaultToolkit();
/*    */ 
/* 37 */     String osName = System.getProperty("os.name");
/* 38 */     boolean isVista = osName.trim().endsWith("Vista");
/* 39 */     boolean isWin7 = osName.trim().endsWith("Windows 7");
/* 40 */     boolean isXP = osName.trim().endsWith("XP");
/*    */ 
/* 42 */     boolean xpThemeActive = Boolean.TRUE.equals(toolkit.getDesktopProperty("win.xpstyle.themeActive"));
/*    */ 
/* 44 */     boolean noxp = System.getProperty("swing.noxp") != null;
/*    */ 
/* 46 */     boolean isClassic = false;
/* 47 */     boolean isVistaStyle = false;
/* 48 */     boolean isMetal = false;
/* 49 */     boolean isXPStyle = false;
/*    */ 
/* 52 */     isClassic = (laf.getClass().getName().endsWith("WindowsClassicLookAndFeel")) || ((laf.getClass().getName().endsWith("WindowsLookAndFeel")) && ((!xpThemeActive) || (noxp)));
/*    */ 
/* 56 */     if (isXP)
/* 57 */       isXPStyle = (laf.getClass().getName().endsWith("WindowsLookAndFeel")) && (!isClassic) && (xpThemeActive);
/* 58 */     else if ((isVista) || (isWin7)) {
/* 59 */       isVistaStyle = (laf.getClass().getName().endsWith("WindowsLookAndFeel")) && (!isClassic);
/*    */     }
/*    */ 
/* 62 */     isMetal = laf.getClass().getName().endsWith("metal.MetalLookAndFeel");
/*    */ 
/* 64 */     if (isXPStyle) {
/* 65 */       return LookAndFeelType.WINDOWS_XP;
/*    */     }
/* 67 */     if (isMetal) {
/* 68 */       return LookAndFeelType.METAL;
/*    */     }
/* 70 */     if (isClassic) {
/* 71 */       return LookAndFeelType.CLASSIC;
/*    */     }
/* 73 */     if (isVista) {
/* 74 */       return LookAndFeelType.VISTA;
/*    */     }
/* 76 */     if (isWin7) {
/* 77 */       return LookAndFeelType.WIN7;
/*    */     }
/*    */ 
/* 80 */     if (osName.toLowerCase().startsWith("mac os x")) {
/* 81 */       return LookAndFeelType.MACOS;
/*    */     }
/* 83 */     return LookAndFeelType.OTHER;
/*    */   }
/*    */ 
/*    */   public static enum LookAndFeelType
/*    */   {
/* 15 */     WINDOWS_XP, CLASSIC, VISTA, WIN7, MACOS, METAL, OTHER;
/*    */   }
/*    */ 
/*    */   public static enum OsType
/*    */   {
/* 14 */     WINDOWS, MACOSX, LINUX, OTHER;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.UIContext
 * JD-Core Version:    0.6.0
 */