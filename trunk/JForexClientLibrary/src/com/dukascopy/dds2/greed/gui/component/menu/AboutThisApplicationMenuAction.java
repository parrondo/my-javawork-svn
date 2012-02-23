/*    */ package com.dukascopy.dds2.greed.gui.component.menu;
/*    */ 
/*    */ import com.dukascopy.api.IStrategy;
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*    */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ import java.awt.Frame;
/*    */ import java.awt.event.ActionEvent;
/*    */ import javax.swing.AbstractAction;
/*    */ import javax.swing.JFrame;
/*    */ import javax.swing.JOptionPane;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ final class AboutThisApplicationMenuAction extends AbstractAction
/*    */ {
/* 22 */   private static final Logger LOGGER = LoggerFactory.getLogger(AboutThisApplicationMenuAction.class);
/*    */ 
/*    */   AboutThisApplicationMenuAction(String name) {
/* 25 */     super(name);
/*    */   }
/*    */ 
/*    */   public void actionPerformed(ActionEvent actionEvent)
/*    */   {
/* 30 */     String version = GreedContext.CLIENT_VERSION;
/*    */ 
/* 32 */     String title = GuiUtilsAndConstants.LABEL_TITLE_NAME;
/*    */ 
/* 34 */     if (GreedContext.isStrategyAllowed()) {
/* 35 */       String apiVersion = IStrategy.class.getPackage().getImplementationVersion();
/* 36 */       version = new StringBuilder().append(version).append(LocalizationManager.getText("joption.pane.engine.build")).append(" ").append(apiVersion == null ? "SNAPSHOT" : apiVersion).toString();
/*    */     }
/*    */ 
/* 39 */     for (Frame i : JFrame.getFrames()) {
/* 40 */       if ((i.isVisible()) && (!i.getName().equals("ID_JF_CLIENTFORM")) && (i.isAlwaysOnTop())) {
/* 41 */         i.setState(1);
/*    */       }
/*    */     }
/*    */ 
/* 45 */     ClientForm clientForm = (ClientForm)GreedContext.get("clientGui");
/* 46 */     JOptionPane.showMessageDialog(clientForm, new StringBuilder().append("<html>").append(GuiUtilsAndConstants.LABEL_SHORT_NAME).append(" ").append(LocalizationManager.getText("joption.pane.ver.")).append(" ").append(version).toString(), title, -1, GuiUtilsAndConstants.PLATFPORM_LOGO);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.menu.AboutThisApplicationMenuAction
 * JD-Core Version:    0.6.0
 */