/*    */ package com.dukascopy.dds2.greed.agent.strategy.ide;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.agent.strategy.ide.api.EditorRegistry;
/*    */ import com.dukascopy.dds2.greed.agent.strategy.ide.impl.EditorRegistryImpl;
/*    */ import javax.swing.JFrame;
/*    */ 
/*    */ public class EditorFactory
/*    */ {
/* 17 */   private static EditorRegistry editorRegistry = null;
/*    */ 
/*    */   public static void init(JFrame parentForDialogs)
/*    */   {
/* 21 */     editorRegistry = new EditorRegistryImpl(parentForDialogs);
/*    */   }
/*    */ 
/*    */   public static EditorRegistry getRegistry()
/*    */   {
/* 26 */     return editorRegistry;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.ide.EditorFactory
 * JD-Core Version:    0.6.0
 */