/*    */ package com.dukascopy.dds2.greed.gui.component.connect;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*    */ import javax.swing.Icon;
/*    */ 
/*    */ public class ConnectLabel extends JLocalizableLabel
/*    */ {
/*    */   private ConnectStatus status;
/* 18 */   private Icon online = new ConnectedIcon();
/* 19 */   private Icon offline = new DisconnectedIcon();
/* 20 */   private Icon onlineFlash = new ConnectedFlashIcon();
/*    */ 
/*    */   public ConnectLabel(ConnectStatus status)
/*    */   {
/* 24 */     setStatus(status);
/*    */   }
/*    */ 
/*    */   public void setStatus(ConnectStatus status) {
/* 28 */     this.status = status;
/* 29 */     if (status == ConnectStatus.ONLINE) {
/* 30 */       setText("label.connected");
/* 31 */       setIcon(this.online);
/*    */     } else {
/* 33 */       setText("label.disconnected");
/* 34 */       setIcon(this.offline);
/*    */     }
/*    */ 
/* 37 */     repaint();
/*    */   }
/*    */ 
/*    */   public ConnectStatus getStatus() {
/* 41 */     return this.status;
/*    */   }
/*    */ 
/*    */   public void flash() {
/* 45 */     if (this.status == ConnectStatus.ONLINE) {
/* 46 */       if ((getIcon() instanceof ConnectedIcon))
/* 47 */         setIcon(this.onlineFlash);
/*    */       else {
/* 49 */         setIcon(this.online);
/*    */       }
/*    */     }
/*    */ 
/* 53 */     repaint();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.connect.ConnectLabel
 * JD-Core Version:    0.6.0
 */