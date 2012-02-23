/*    */ package com.dukascopy.dds2.greed.gui.component.status;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*    */ import java.awt.BorderLayout;
/*    */ import java.awt.Cursor;
/*    */ import java.awt.Dimension;
/*    */ import java.awt.event.MouseAdapter;
/*    */ import java.awt.event.MouseEvent;
/*    */ import java.lang.management.ManagementFactory;
/*    */ import java.lang.management.MemoryMXBean;
/*    */ import java.lang.management.MemoryUsage;
/*    */ import javax.swing.JLabel;
/*    */ import javax.swing.JPanel;
/*    */ import javax.swing.JProgressBar;
/*    */ import javax.swing.SwingUtilities;
/*    */ 
/*    */ public class HeapSizePanel extends JPanel
/*    */   implements Runnable
/*    */ {
/*    */   private JProgressBar heapProgressBar;
/*    */   private JLabel collectButton;
/*    */   private Thread updaterThread;
/* 26 */   private boolean canContinue = true;
/*    */ 
/*    */   public HeapSizePanel() {
/* 29 */     super(new BorderLayout(5, 0));
/*    */ 
/* 31 */     setMaximumSize(new Dimension(2000, 15));
/* 32 */     this.heapProgressBar = new JProgressBar(0, 0, 10);
/* 33 */     this.heapProgressBar.setStringPainted(true);
/* 34 */     this.heapProgressBar.setPreferredSize(new Dimension(120, 0));
/* 35 */     this.heapProgressBar.setMinimumSize(new Dimension(70, 0));
/* 36 */     this.collectButton = new JLabel(StratUtils.loadIcon("rc/media/systray_gc_active.png"));
/* 37 */     this.collectButton.setToolTipText("Collect");
/*    */ 
/* 39 */     add(this.heapProgressBar, "Center");
/* 40 */     add(this.collectButton, "East");
/*    */ 
/* 42 */     this.collectButton.addMouseListener(new MouseAdapter() {
/*    */       public void mouseClicked(MouseEvent e) {
/* 44 */         System.gc();
/* 45 */         HeapSizePanel.this.updateHeapProgressBar();
/*    */       }
/*    */ 
/*    */       public void mouseEntered(MouseEvent e) {
/* 49 */         HeapSizePanel.this.setCursor(Cursor.getPredefinedCursor(12));
/*    */       }
/*    */ 
/*    */       public void mouseExited(MouseEvent e) {
/* 53 */         HeapSizePanel.this.setCursor(Cursor.getDefaultCursor());
/*    */       }
/*    */     });
/* 57 */     this.updaterThread = new Thread(this);
/* 58 */     this.updaterThread.setDaemon(true);
/* 59 */     this.updaterThread.start();
/*    */   }
/*    */ 
/*    */   public Dimension getMaximumSize()
/*    */   {
/* 64 */     return super.getPreferredSize();
/*    */   }
/*    */ 
/*    */   private void updateHeapProgressBar() {
/* 68 */     MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
/* 69 */     MemoryUsage memoryUsage = mbean.getHeapMemoryUsage();
/* 70 */     this.heapProgressBar.setMaximum((int)memoryUsage.getCommitted());
/* 71 */     this.heapProgressBar.setValue((int)memoryUsage.getUsed());
/* 72 */     this.heapProgressBar.setString(Long.toString(memoryUsage.getUsed() / 1048576L) + "M of " + Long.toString(memoryUsage.getCommitted() / 1048576L) + "M");
/* 73 */     this.heapProgressBar.setToolTipText("Heap size: " + Long.toString(memoryUsage.getUsed() / 1048576L) + "Mb of total: " + Long.toString(memoryUsage.getCommitted() / 1048576L) + "Mb used. Max size: " + Long.toString(memoryUsage.getMax() / 1048576L) + "Mb");
/*    */   }
/*    */ 
/*    */   public void run()
/*    */   {
/* 79 */     while (this.canContinue) {
/* 80 */       SwingUtilities.invokeLater(new Runnable()
/*    */       {
/*    */         public void run() {
/* 83 */           HeapSizePanel.this.updateHeapProgressBar();
/*    */         } } );
/*    */       try {
/* 87 */         Thread.sleep(1000L);
/*    */       }
/*    */       catch (InterruptedException e) {
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   public void setCanContinue(boolean canContinue) {
/* 95 */     this.canContinue = canContinue;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.status.HeapSizePanel
 * JD-Core Version:    0.6.0
 */