/*    */ package com.dukascopy.calculator;
/*    */ 
/*    */ import java.awt.BorderLayout;
/*    */ import java.awt.Container;
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.ActionListener;
/*    */ import javax.swing.JApplet;
/*    */ import javax.swing.JButton;
/*    */ import javax.swing.JFrame;
/*    */ import javax.swing.SwingUtilities;
/*    */ 
/*    */ public class CalcButtonApplet extends JApplet
/*    */   implements ActionListener
/*    */ {
/*    */   private JFrame frame;
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public void init()
/*    */   {
/*    */     try
/*    */     {
/* 19 */       SwingUtilities.invokeAndWait(new Runnable() {
/*    */         public void run() {
/* 21 */           CalcButtonApplet.this.setup();
/*    */         }
/*    */       });
/*    */     }
/*    */     catch (Exception e)
/*    */     {
/*    */     }
/*    */   }
/*    */ 
/*    */   public void setup() {
/* 32 */     this.frame = MainCalculatorPanel.createFrame();
/* 33 */     this.frame.setVisible(false);
/* 34 */     this.frame.setDefaultCloseOperation(1);
/*    */ 
/* 36 */     getContentPane().removeAll();
/*    */ 
/* 38 */     getContentPane().setLayout(new BorderLayout());
/* 39 */     JButton button = new JButton("calculator");
/* 40 */     button.addActionListener(this);
/* 41 */     getContentPane().add(button);
/*    */   }
/*    */ 
/*    */   public void actionPerformed(ActionEvent actionEvent)
/*    */   {
/* 50 */     this.frame.setVisible(!this.frame.isVisible());
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.CalcButtonApplet
 * JD-Core Version:    0.6.0
 */