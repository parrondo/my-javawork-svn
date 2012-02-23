/*    */ package com.dukascopy.dds2.greed.gui.component.menu;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*    */ import com.dukascopy.dds2.greed.gui.component.ApplicationClock;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ import com.dukascopy.dds2.greed.util.Cryptos;
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.util.Properties;
/*    */ import javax.swing.AbstractAction;
/*    */ import javax.swing.JFrame;
/*    */ import javax.swing.JOptionPane;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ class ReportMenuAction extends AbstractAction
/*    */ {
/* 23 */   private static final Logger LOGGER = LoggerFactory.getLogger(ReportMenuAction.class);
/*    */   String key;
/*    */ 
/*    */   ReportMenuAction(String key)
/*    */   {
/* 27 */     super(key);
/* 28 */     this.key = key;
/*    */   }
/*    */ 
/*    */   public void actionPerformed(ActionEvent e)
/*    */   {
/* 33 */     String login = (String)GreedContext.getConfig("account_name");
/* 34 */     Properties properties = (Properties)GreedContext.get("properties");
/*    */ 
/* 36 */     String baseUrl = (String)properties.get("wlabel.foUrl");
/*    */ 
/* 38 */     String authorization = GuiUtilsAndConstants.buildAuthorizationRequest(login);
/* 39 */     if (null == authorization) {
/* 40 */       JOptionPane.showMessageDialog((JFrame)GreedContext.get("clientGui"), LocalizationManager.getText("joption.pane.report.not.available"), GuiUtilsAndConstants.LABEL_SHORT_NAME, 0);
/*    */ 
/* 47 */       return;
/*    */     }
/* 49 */     StringBuffer url = new StringBuffer(baseUrl);
/*    */ 
/* 51 */     url.append("/fo/reports/export.php?id=").append(this.key.trim()).append("&").append(authorization).append("&zhash=" + Cryptos.encrypt(((ApplicationClock)GreedContext.get("applicationClock")).getTime()));
/*    */ 
/* 58 */     LOGGER.info("url: " + url);
/* 59 */     GuiUtilsAndConstants.openURL(url.toString());
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.menu.ReportMenuAction
 * JD-Core Version:    0.6.0
 */