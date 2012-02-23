/*    */ package com.dukascopy.dds2.greed.gui.component.menu;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*    */ import java.awt.event.ActionEvent;
/*    */ import javax.swing.AbstractAction;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class ViewRemoteStrategyLogAction extends AbstractAction
/*    */ {
/* 20 */   private static final Logger LOGGER = LoggerFactory.getLogger(ViewRemoteStrategyLogAction.class);
/*    */   private static final String LOG_URL_FORMAT = "%1$s?appello=%2$s&sermo=%3$s&licentio=%4$s";
/*    */ 
/*    */   public ViewRemoteStrategyLogAction()
/*    */   {
/* 25 */     super("menu.item.view.remote.strategy.log");
/*    */   }
/*    */ 
/*    */   public void actionPerformed(ActionEvent e)
/*    */   {
/* 30 */     String logServerUrl = GreedContext.getStringProperty("jss.logserver.url");
/* 31 */     Object accountName = GreedContext.getConfig("account_name");
/* 32 */     Object sessionId = GreedContext.getConfig("SESSION_ID");
/* 33 */     Object ticket = GreedContext.getConfig("TICKET");
/*    */ 
/* 35 */     String url = String.format("%1$s?appello=%2$s&sermo=%3$s&licentio=%4$s", new Object[] { logServerUrl, accountName, sessionId, ticket });
/* 36 */     LOGGER.debug("View remote strategy log : [{}]", url);
/* 37 */     GuiUtilsAndConstants.openURL(url);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.menu.ViewRemoteStrategyLogAction
 * JD-Core Version:    0.6.0
 */