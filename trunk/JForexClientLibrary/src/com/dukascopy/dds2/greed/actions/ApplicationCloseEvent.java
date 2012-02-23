/*    */ package com.dukascopy.dds2.greed.actions;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ import com.dukascopy.dds2.greed.gui.settings.PreferencesStorage;
/*    */ import com.dukascopy.dds2.greed.util.PlatformInitUtils;
/*    */ import java.awt.Frame;
/*    */ import javax.swing.JFrame;
/*    */ import javax.swing.JOptionPane;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class ApplicationCloseEvent extends AppActionEvent
/*    */ {
/* 27 */   private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationCloseEvent.class);
/*    */ 
/*    */   public ApplicationCloseEvent() {
/* 30 */     super("Application close", false, false);
/*    */   }
/*    */ 
/*    */   public void doAction()
/*    */   {
/* 35 */     PlatformInitUtils.closeApplication();
/*    */   }
/*    */ 
/*    */   public static boolean confirmExit()
/*    */   {
/* 41 */     setStateForOpenedFrames(1);
/*    */ 
/* 43 */     PreferencesStorage.setJForexMode(!GreedContext.IS_JCLIENT_INVOKED);
/* 44 */     String title = GuiUtilsAndConstants.LABEL_SHORT_NAME;
/* 45 */     if (0 == JOptionPane.showConfirmDialog((JFrame)GreedContext.get("clientGui"), LocalizationManager.getText("joption.pane.want.quit"), title, 0, 3, GuiUtilsAndConstants.PLATFPORM_ICON))
/*    */     {
/* 54 */       return true;
/*    */     }
/*    */ 
/* 59 */     setStateForOpenedFrames(0);
/*    */ 
/* 62 */     return false;
/*    */   }
/*    */ 
/*    */   private static void setStateForOpenedFrames(int state) {
/* 66 */     Frame[] frame = JFrame.getFrames();
/*    */ 
/* 68 */     for (int i = 0; i < frame.length; i++) {
/* 69 */       if ((!(frame[i] instanceof JFrame)) || (!frame[i].isVisible()) || (frame[i].getName().equals("ID_JF_CLIENTFORM")) || (frame[i].getName().equals("ID_JF_LOGINFORM")))
/*    */       {
/*    */         continue;
/*    */       }
/*    */ 
/* 74 */       frame[i].setExtendedState(state);
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.ApplicationCloseEvent
 * JD-Core Version:    0.6.0
 */