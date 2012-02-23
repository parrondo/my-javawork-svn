/*    */ package com.dukascopy.calculator.graph;
/*    */ 
/*    */ import com.dukascopy.calculator.ReadOnlyCalculatorApplet;
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.io.PrintStream;
/*    */ import javax.swing.AbstractAction;
/*    */ import javax.swing.ActionMap;
/*    */ import javax.swing.InputMap;
/*    */ import javax.swing.JMenu;
/*    */ import javax.swing.JMenuBar;
/*    */ import javax.swing.JMenuItem;
/*    */ import javax.swing.KeyStroke;
/*    */ 
/*    */ public class Menu extends JMenuBar
/*    */ {
/*    */   private JMenu editMenu;
/*    */   private AxisDialog xAxisDialog;
/*    */   private AxisDialog yAxisDialog;
/*    */   private ReadOnlyCalculatorApplet applet;
/*    */   private Model model;
/*    */   private View view;
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public Menu(ReadOnlyCalculatorApplet applet, View view, Model model)
/*    */   {
/* 16 */     this.applet = applet;
/* 17 */     this.view = view;
/* 18 */     this.model = model;
/* 19 */     this.editMenu = new JMenu("Edit");
/* 20 */     this.editMenu.setMnemonic('E');
/* 21 */     JMenuItem xAxisItem = new JMenuItem("x-axis");
/* 22 */     this.xAxisDialog = new AxisDialog(applet, view, model.getXAxis(), true, "x axis");
/* 23 */     xAxisItem.addActionListener(this.xAxisDialog);
/* 24 */     xAxisItem.setMnemonic('x');
/* 25 */     this.editMenu.add(xAxisItem);
/* 26 */     JMenuItem yAxisItem = new JMenuItem("y-axis");
/* 27 */     this.yAxisDialog = new AxisDialog(applet, view, model.getYAxis(), false, "y axis");
/* 28 */     yAxisItem.addActionListener(this.yAxisDialog);
/* 29 */     yAxisItem.setMnemonic('y');
/* 30 */     this.editMenu.add(yAxisItem);
/* 31 */     add(this.editMenu);
/*    */ 
/* 34 */     EditMenuAction editMenuAction = new EditMenuAction();
/* 35 */     getInputMap().put(KeyStroke.getKeyStroke(69, 0), editMenuAction.toString());
/*    */ 
/* 38 */     getActionMap().put(editMenuAction.toString(), editMenuAction);
/*    */   }
/*    */ 
/*    */   void updateSizes()
/*    */   {
/* 45 */     if (this.xAxisDialog != null)
/* 46 */       this.xAxisDialog.setBounds();
/* 47 */     if (this.yAxisDialog != null)
/* 48 */       this.yAxisDialog.setBounds();
/*    */   }
/*    */ 
/*    */   public class EditMenuAction extends AbstractAction
/*    */   {
/*    */     private static final long serialVersionUID = 1L;
/*    */ 
/*    */     public EditMenuAction() {
/*    */     }
/*    */ 
/*    */     public void actionPerformed(ActionEvent actionEvent) {
/* 60 */       System.out.println("Ouch! That hurt.");
/* 61 */       Menu.this.editMenu.doClick(20);
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.graph.Menu
 * JD-Core Version:    0.6.0
 */