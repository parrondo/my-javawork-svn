/*     */ package com.dukascopy.dds2.greed.gui.component;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JFormattedTextField;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JSpinner;
/*     */ import javax.swing.JSpinner.DefaultEditor;
/*     */ import javax.swing.SpinnerListModel;
/*     */ import javax.swing.SpinnerModel;
/*     */ import javax.swing.SwingUtilities;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class ListBasedPipSpinner extends JSpinner
/*     */ {
/*  22 */   private static Logger LOGGER = LoggerFactory.getLogger(ListBasedPipSpinner.class);
/*     */   private static final int MIN_TRAILING_STEP;
/*     */   private static final int MAX_TRAILING_STEP = 10000;
/*     */ 
/*     */   public ListBasedPipSpinner(Integer minValue)
/*     */   {
/*  35 */     List starterModelData = new ArrayList();
/*  36 */     starterModelData.add("");
/*  37 */     for (int i = MIN_TRAILING_STEP; i <= 10000; i++)
/*  38 */       starterModelData.add(String.valueOf(i));
/*  39 */     Object[] modelData = starterModelData.toArray(new Object[starterModelData.size()]);
/*  40 */     SpinnerModel model = new SpinnerListModel(modelData);
/*  41 */     setModel(model);
/*  42 */     getTextField().setHorizontalAlignment(4);
/*     */   }
/*     */ 
/*     */   public JFormattedTextField getTextField()
/*     */   {
/*  47 */     JComponent editor = getEditor();
/*  48 */     if ((editor instanceof JSpinner.DefaultEditor)) {
/*  49 */       return ((JSpinner.DefaultEditor)editor).getTextField();
/*     */     }
/*  51 */     LOGGER.error("Unexpected editor type: " + getEditor().getClass() + " isn't a descendant of DefaultEditor");
/*     */ 
/*  53 */     return null;
/*     */   }
/*     */ 
/*     */   public void setText(String value)
/*     */   {
/*  58 */     if ((value == null) || ("".equals(value))) {
/*  59 */       getModel().setValue("");
/*  60 */       return;
/*     */     }
/*  62 */     LOGGER.debug("seeting model's value to " + value);
/*  63 */     getModel().setValue(value.toString());
/*     */   }
/*     */ 
/*     */   public String getText() {
/*  67 */     if (getModel().getValue() != null) {
/*  68 */       return getModel().getValue().toString();
/*     */     }
/*  70 */     return null;
/*     */   }
/*     */ 
/*     */   public void clear() {
/*  74 */     getModel().setValue("");
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/*  79 */     SwingUtilities.invokeLater(new Runnable()
/*     */     {
/*     */       public void run() {
/*  82 */         JFrame frame = new JFrame("SpinnerDemo");
/*  83 */         frame.setDefaultCloseOperation(3);
/*     */ 
/*  86 */         JPanel newContentPane = new JPanel();
/*  87 */         newContentPane.setLayout(new BoxLayout(newContentPane, 1));
/*  88 */         ListBasedPipSpinner pipSpinner = new ListBasedPipSpinner(Integer.valueOf(10));
/*     */ 
/*  91 */         System.err.println("val = ->" + pipSpinner.getValue() + "<-");
/*  92 */         newContentPane.add(pipSpinner);
/*     */ 
/*  96 */         newContentPane.setOpaque(true);
/*  97 */         frame.setContentPane(newContentPane);
/*     */ 
/* 100 */         frame.pack();
/* 101 */         frame.setVisible(true);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  28 */     if ((!GreedContext.isDemo()) && (!GreedContext.isLive()))
/*  29 */       MIN_TRAILING_STEP = 4;
/*     */     else
/*  31 */       MIN_TRAILING_STEP = 10;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.ListBasedPipSpinner
 * JD-Core Version:    0.6.0
 */