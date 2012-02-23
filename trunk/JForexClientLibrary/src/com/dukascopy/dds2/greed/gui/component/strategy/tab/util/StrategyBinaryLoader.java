/*    */ package com.dukascopy.dds2.greed.gui.component.strategy.tab.util;
/*    */ 
/*    */ import com.dukascopy.api.IStrategy;
/*    */ import com.dukascopy.dds2.greed.agent.compiler.JFXPack;
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyNewBean;
/*    */ import java.io.File;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class StrategyBinaryLoader
/*    */ {
/*    */   public static final String FILE_EXT_JFX = ".jfx";
/*    */   public static final String FILE_EXT_JAVA = ".java";
/*    */   public static final String FILE_EXT_MQ4 = ".mq4";
/* 28 */   private static final Logger LOGGER = LoggerFactory.getLogger(StrategyBinaryLoader.class);
/*    */ 
/*    */   public static void loadStrategy(File binaryFile, StrategyNewBean strategyBean) throws IncorrectClassTypeException
/*    */   {
/* 32 */     if ((binaryFile != null) && (binaryFile.exists()) && (binaryFile.getName().endsWith(".jfx")))
/*    */     {
/* 34 */       JFXPack jfxPack = null;
/*    */       try
/*    */       {
/* 37 */         jfxPack = JFXPack.loadFromPack(binaryFile);
/*    */       } catch (Exception ex) {
/* 39 */         LOGGER.error(ex.getMessage(), ex);
/* 40 */         return;
/*    */       }
/* 42 */       Class targetClass = jfxPack.getTargetClass();
/* 43 */       if ((targetClass == null) || (!IStrategy.class.isAssignableFrom(targetClass))) {
/* 44 */         throw new IncorrectClassTypeException("File is not implementing IStrategy interface.");
/*    */       }
/*    */ 
/* 47 */       if (jfxPack.isFullAccessRequested()) {
/* 48 */         jfxPack.setFullAccess(true);
/*    */       }
/* 50 */       strategyBean.setStrategy((IStrategy)jfxPack.getTarget());
/* 51 */       strategyBean.setStrategyBinaryFile(binaryFile);
/* 52 */       strategyBean.setPack(jfxPack);
/* 53 */       strategyBean.setLastModifiedDate(binaryFile.lastModified());
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.util.StrategyBinaryLoader
 * JD-Core Version:    0.6.0
 */