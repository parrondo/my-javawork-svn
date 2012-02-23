/*    */ package com.dukascopy.dds2.greed.util.event;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*    */ import com.dukascopy.dds2.greed.gui.component.status.GreedStatusBar;
/*    */ import java.awt.AWTEvent;
/*    */ import java.awt.event.AWTEventListener;
/*    */ import java.awt.event.ComponentEvent;
/*    */ import javax.swing.JFrame;
/*    */ 
/*    */ public class WindowCountAWTListener
/*    */   implements AWTEventListener
/*    */ {
/*    */   private final ClientForm clientForm;
/*    */ 
/*    */   public WindowCountAWTListener(ClientForm clientForm)
/*    */   {
/* 21 */     this.clientForm = clientForm;
/*    */   }
/*    */ 
/*    */   public void eventDispatched(AWTEvent evt)
/*    */   {
/* 26 */     switch (evt.getID()) {
/*    */     case 200:
/*    */     case 202:
/*    */     case 206:
/* 30 */       ComponentEvent cev = (ComponentEvent)evt;
/* 31 */       if (!(cev.getComponent() instanceof JFrame)) break;
/* 32 */       this.clientForm.getStatusBar().updateFrameCount();
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.event.WindowCountAWTListener
 * JD-Core Version:    0.6.0
 */