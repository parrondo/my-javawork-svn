/*    */ package com.dukascopy.dds2.greed.gui.component;
/*    */ 
/*    */ import java.awt.BorderLayout;
/*    */ import java.awt.Cursor;
/*    */ import java.awt.Dimension;
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.ActionListener;
/*    */ import java.awt.event.MouseAdapter;
/*    */ import java.awt.event.MouseEvent;
/*    */ import java.awt.event.MouseListener;
/*    */ import javax.swing.BorderFactory;
/*    */ import javax.swing.JLabel;
/*    */ import javax.swing.JPanel;
/*    */ import javax.swing.JTextArea;
/*    */ import javax.swing.Timer;
/*    */ 
/*    */ public class MessageBox extends JPanel
/*    */   implements MouseListener
/*    */ {
/*    */   private Timer timer;
/* 25 */   private final int DELAY = 5000;
/*    */   private JTextArea lab;
/*    */ 
/*    */   public MessageBox()
/*    */   {
/* 31 */     build();
/* 32 */     addMouseListener(this);
/*    */   }
/*    */ 
/*    */   private void build() {
/* 36 */     setLayout(new BorderLayout());
/* 37 */     setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
/*    */ 
/* 39 */     JLabel label = new JLabel();
/* 40 */     this.lab = new JTextArea(3, 20);
/* 41 */     this.lab.setBackground(getBackground());
/* 42 */     this.lab.setFont(label.getFont());
/* 43 */     this.lab.setLineWrap(true);
/* 44 */     this.lab.setWrapStyleWord(true);
/* 45 */     this.lab.addMouseListener(this);
/* 46 */     add(this.lab, "Center");
/* 47 */     this.lab.addMouseListener(new MouseAdapter() {
/*    */       public void mouseEntered(MouseEvent e) {
/* 49 */         MessageBox.this.setCursor(Cursor.getPredefinedCursor(12));
/*    */       }
/*    */ 
/*    */       public void mouseExited(MouseEvent e) {
/* 53 */         MessageBox.this.setCursor(Cursor.getDefaultCursor());
/*    */       }
/*    */     });
/* 57 */     ActionListener closer = new ActionListener() {
/*    */       public void actionPerformed(ActionEvent actionEvent) {
/* 59 */         MessageBox.this.closeMe();
/*    */       }
/*    */     };
/* 62 */     this.timer = new Timer(5000, closer);
/*    */   }
/*    */ 
/*    */   public void setTextAndStartTimer(String message) {
/* 66 */     this.lab.setText(message);
/* 67 */     setVisible(true);
/* 68 */     this.timer.start();
/*    */   }
/*    */ 
/*    */   private void closeMe() {
/* 72 */     setVisible(false);
/* 73 */     this.timer.stop();
/*    */   }
/*    */ 
/*    */   public void mouseClicked(MouseEvent mouseEvent)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void mousePressed(MouseEvent mouseEvent)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void mouseReleased(MouseEvent mouseEvent) {
/* 85 */     closeMe();
/*    */   }
/*    */ 
/*    */   public void mouseEntered(MouseEvent mouseEvent) {
/* 89 */     setCursor(Cursor.getPredefinedCursor(12));
/*    */   }
/*    */ 
/*    */   public void mouseExited(MouseEvent mouseEvent) {
/* 93 */     setCursor(Cursor.getDefaultCursor());
/*    */   }
/*    */ 
/*    */   public Dimension getMaximumSize()
/*    */   {
/* 99 */     return new Dimension(800, 600);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.MessageBox
 * JD-Core Version:    0.6.0
 */