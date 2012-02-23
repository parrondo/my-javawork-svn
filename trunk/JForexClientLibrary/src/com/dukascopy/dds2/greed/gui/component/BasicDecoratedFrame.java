/*    */ package com.dukascopy.dds2.greed.gui.component;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableFrame;
/*    */ import com.dukascopy.dds2.greed.util.PlatformSpecific;
/*    */ import java.awt.Toolkit;
/*    */ import javax.swing.ImageIcon;
/*    */ import javax.swing.JFrame;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public abstract class BasicDecoratedFrame extends JLocalizableFrame
/*    */   implements PlatformSpecific
/*    */ {
/* 20 */   private static Logger LOGGER = LoggerFactory.getLogger(BasicDecoratedFrame.class);
/*    */ 
/*    */   public BasicDecoratedFrame() {
/* 23 */     Toolkit.getDefaultToolkit().setDynamicLayout(true);
/*    */     try
/*    */     {
/* 26 */       setIconImage(GuiUtilsAndConstants.PLATFPORM_ICON.getImage());
/*    */     } catch (Exception e) {
/* 28 */       LOGGER.error(e.getMessage(), e);
/*    */     }
/*    */ 
/* 31 */     setLocationRelativeTo((JFrame)GreedContext.get("clientGui"));
/* 32 */     setDefaultCloseOperation(2);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.BasicDecoratedFrame
 * JD-Core Version:    0.6.0
 */