/*    */ package com.dukascopy.calculator.button;
/*    */ 
/*    */ import com.dukascopy.calculator.MainCalculatorPanel;
/*    */ import com.dukascopy.calculator.function.Info;
/*    */ import com.dukascopy.calculator.function.PObject;
/*    */ import java.awt.event.ActionEvent;
/*    */ import javax.swing.JOptionPane;
/*    */ 
/*    */ public class InfoButton extends CalculatorButton
/*    */ {
/*    */   static final String version = "2:0.3";
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public InfoButton(MainCalculatorPanel mainCalculatorPanel)
/*    */   {
/* 13 */     this.mainCalculatorPanel = mainCalculatorPanel;
/* 14 */     setPobject(new Info());
/*    */ 
/* 16 */     int size = mainCalculatorPanel.minSize();
/* 17 */     if (size < 3) size = 3;
/* 18 */     if (size > 9) size = 9;
/*    */ 
/* 22 */     addActionListener(this);
/*    */ 
/* 24 */     setShortcut(getPobject().shortcut());
/* 25 */     setToolTipKey(getPobject().tooltip());
/*    */   }
/*    */ 
/*    */   public void actionPerformed(ActionEvent actionEvent)
/*    */   {
/* 35 */     if (getMainCalculatorPanel().getMode() != 0)
/* 36 */       return;
/* 37 */     JOptionPane.showMessageDialog(this.mainCalculatorPanel, "<html>Java Scientific Calculator 2:0.3, http://jscicalc.sourceforge.net/<br><br>Copyright &#169; 2004&#8211;5, 2007&#8211;8, John D Lamb &#60;J.D.Lamb@btinternet.com&#62;<br>This is free software; see the source for copying conditions.  There is NO warranty;<br>not even for MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.</html>", "About", 1);
/*    */ 
/* 56 */     getMainCalculatorPanel().requestFocusInWindow();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.button.InfoButton
 * JD-Core Version:    0.6.0
 */