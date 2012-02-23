/*    */ package com.dukascopy.dds2.greed.gui.component.message;
/*    */ 
/*    */ import java.lang.ref.WeakReference;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import javax.swing.SwingUtilities;
/*    */ 
/*    */ public class FontManager
/*    */ {
/* 20 */   private static final List<WeakReference<IFontMonospceable>> CACHE = new ArrayList();
/*    */ 
/*    */   private static synchronized void fireFontChanged()
/*    */   {
/* 26 */     for (WeakReference weakReference : CACHE) {
/* 27 */       IFontMonospceable fontMonospceable = (IFontMonospceable)weakReference.get();
/*    */ 
/* 29 */       if (fontMonospceable != null)
/* 30 */         fontMonospceable.initFont();
/*    */     }
/*    */   }
/*    */ 
/*    */   public static void reinitFonts()
/*    */   {
/* 36 */     SwingUtilities.invokeLater(new Runnable()
/*    */     {
/*    */       public void run() {
/* 39 */         FontManager.access$000();
/*    */       } } );
/*    */   }
/*    */ 
/*    */   public static synchronized void addFontMonospaceable(IFontMonospceable monospceable) {
/* 45 */     CACHE.add(new WeakReference(monospceable));
/* 46 */     monospceable.initFont();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.message.FontManager
 * JD-Core Version:    0.6.0
 */