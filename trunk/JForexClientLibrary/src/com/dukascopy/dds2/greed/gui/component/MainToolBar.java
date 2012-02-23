/*    */ package com.dukascopy.dds2.greed.gui.component;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*    */ import com.dukascopy.dds2.greed.util.GuiResourceLoader;
/*    */ import java.awt.event.ActionEvent;
/*    */ import javax.swing.AbstractAction;
/*    */ import javax.swing.Icon;
/*    */ import javax.swing.JButton;
/*    */ import javax.swing.JToolBar;
/*    */ 
/*    */ public class MainToolBar extends JToolBar
/*    */ {
/*    */   private AbstractAction connectAction;
/*    */   private AbstractAction disconnectAction;
/*    */   private ClientForm form;
/*    */ 
/*    */   public MainToolBar(ClientForm form)
/*    */   {
/* 26 */     this.form = form;
/* 27 */     this.connectAction = new AbstractAction("Connect", GuiResourceLoader.getInstance().loadImageIcon("rc/media/connect-icon.gif"))
/*    */     {
/*    */       public void actionPerformed(ActionEvent e)
/*    */       {
/*    */       }
/*    */     };
/* 31 */     this.disconnectAction = new AbstractAction("Disconnect", GuiResourceLoader.getInstance().loadImageIcon("rc/media/disconnect-icon.gif"))
/*    */     {
/*    */       public void actionPerformed(ActionEvent e)
/*    */       {
/*    */       }
/*    */     };
/* 35 */     this.disconnectAction.setEnabled(false);
/* 36 */     build();
/*    */   }
/*    */ 
/*    */   private void build() {
/* 40 */     setName("Main");
/* 41 */     JButton connectButton = new JButton();
/* 42 */     JButton disconnectButton = new JButton();
/* 43 */     initActions(connectButton, disconnectButton);
/*    */   }
/*    */ 
/*    */   private void initActions(JButton connectButton, JButton disconnectButton) {
/* 47 */     connectButton.setAction(this.connectAction);
/* 48 */     disconnectButton.setAction(this.disconnectAction);
/* 49 */     add(connectButton);
/* 50 */     add(disconnectButton);
/*    */   }
/*    */ 
/*    */   public AbstractAction getConnectAction() {
/* 54 */     return this.connectAction;
/*    */   }
/*    */ 
/*    */   public AbstractAction getDisconnectAction() {
/* 58 */     return this.disconnectAction;
/*    */   }
/*    */ 
/*    */   public ClientForm getForm() {
/* 62 */     return this.form;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.MainToolBar
 * JD-Core Version:    0.6.0
 */