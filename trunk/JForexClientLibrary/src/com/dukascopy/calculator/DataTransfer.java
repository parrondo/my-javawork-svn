/*    */ package com.dukascopy.calculator;
/*    */ 
/*    */ import com.dukascopy.calculator.complex.Complex;
/*    */ import java.awt.Toolkit;
/*    */ import java.awt.datatransfer.Clipboard;
/*    */ import java.awt.datatransfer.ClipboardOwner;
/*    */ import java.awt.datatransfer.StringSelection;
/*    */ import java.awt.datatransfer.Transferable;
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.util.regex.Matcher;
/*    */ import java.util.regex.Pattern;
/*    */ import javax.swing.AbstractAction;
/*    */ 
/*    */ public class DataTransfer extends AbstractAction
/*    */   implements ClipboardOwner
/*    */ {
/*    */   private MainCalculatorPanel calculatorApplet;
/*    */   static final int maxLength = 120;
/*    */   static final int sigDigits = 32;
/* 81 */   private static final Pattern html = Pattern.compile("<html>");
/*    */ 
/* 86 */   private static final Pattern htmlend = Pattern.compile("</html>");
/*    */ 
/* 91 */   private static final Pattern minus = Pattern.compile("&#8722;");
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public DataTransfer(MainCalculatorPanel calculatorApplet)
/*    */   {
/* 15 */     this.calculatorApplet = calculatorApplet;
/*    */   }
/*    */ 
/*    */   public void lostOwnership(Clipboard c, Transferable t)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void copy()
/*    */   {
/* 28 */     Object value = this.calculatorApplet.getValue();
/* 29 */     if (!(value instanceof Complex)) {
/* 30 */       return;
/*    */     }
/*    */ 
/* 33 */     Complex d = (Complex)value;
/*    */ 
/* 35 */     Base base = this.calculatorApplet.getBase();
/* 36 */     Notation notation = new Notation();
/* 37 */     double factor = 1.0D;
/* 38 */     if (this.calculatorApplet.getAngleType() == AngleType.DEGREES) {
/* 39 */       factor = 1.0D;
/*    */     }
/*    */ 
/* 42 */     String string = d.toHTMLString(120, 32, base, notation, factor);
/*    */ 
/* 44 */     Matcher matcher = html.matcher(string);
/* 45 */     string = matcher.replaceAll("");
/* 46 */     matcher = htmlend.matcher(string);
/* 47 */     string = matcher.replaceAll("");
/* 48 */     matcher = minus.matcher(string);
/* 49 */     string = matcher.replaceAll("-");
/*    */ 
/* 51 */     StringSelection stringSelection = new StringSelection(string);
/* 52 */     Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
/* 53 */     clipboard.setContents(stringSelection, this);
/*    */   }
/*    */ 
/*    */   public void actionPerformed(ActionEvent actionEvent)
/*    */   {
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.DataTransfer
 * JD-Core Version:    0.6.0
 */