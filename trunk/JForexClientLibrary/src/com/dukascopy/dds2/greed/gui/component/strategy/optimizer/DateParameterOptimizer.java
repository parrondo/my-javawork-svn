/*    */ package com.dukascopy.dds2.greed.gui.component.strategy.optimizer;
/*    */ 
/*    */ import com.toedter.calendar.JDateChooser;
/*    */ import com.toedter.calendar.JTextFieldDateEditor;
/*    */ import java.awt.Component;
/*    */ import java.text.ParseException;
/*    */ import java.text.SimpleDateFormat;
/*    */ import java.util.Date;
/*    */ 
/*    */ public class DateParameterOptimizer extends AbstractLookupParameterOptimizer
/*    */ {
/*    */   private static final String PATTERN = "dd-MM-yyyy HH:mm:ss";
/*    */   private SimpleDateFormat dateFormatter;
/*    */ 
/*    */   public DateParameterOptimizer(Date value, boolean mandatory, boolean readOnly)
/*    */   {
/* 27 */     super(new JDateChooser(new JTextFieldDateEditor()), value, mandatory, readOnly);
/* 28 */     getDateEditor().setDateFormatString("dd-MM-yyyy HH:mm:ss");
/* 29 */     getDateEditor().setColumns(15);
/*    */ 
/* 31 */     this.dateFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
/* 32 */     this.dateFormatter.setLenient(false);
/*    */   }
/*    */ 
/*    */   protected JTextFieldDateEditor getDateEditor() {
/* 36 */     return (JTextFieldDateEditor)((JDateChooser)this.mainComponent).getDateEditor();
/*    */   }
/*    */ 
/*    */   protected Object getValue(Component mainComponent)
/*    */   {
/* 41 */     Date date = ((JDateChooser)mainComponent).getDate();
/* 42 */     return date;
/*    */   }
/*    */ 
/*    */   protected void setValue(Component mainComponent, Object value)
/*    */   {
/* 47 */     if ((value instanceof Date))
/* 48 */       ((JDateChooser)mainComponent).setDate((Date)value);
/*    */     else
/* 50 */       getDateEditor().setText(null);
/*    */   }
/*    */ 
/*    */   protected void validateValue(Component mainComponent)
/*    */     throws CommitErrorException
/*    */   {
/* 56 */     JTextFieldDateEditor editor = (JTextFieldDateEditor)((JDateChooser)mainComponent).getDateEditor();
/* 57 */     String text = editor.getText();
/* 58 */     if (text.trim().length() < 1) {
/* 59 */       if (isMandatory())
/* 60 */         throw new CommitErrorException("optimizer.dialog.error.date.must.be.selected");
/*    */     }
/*    */     else
/*    */       try
/*    */       {
/* 65 */         this.dateFormatter.parse(text);
/*    */       } catch (ParseException e) {
/* 67 */         throw new CommitErrorException("optimizer.dialog.error.template.not.valid.date.value", new Object[] { text });
/*    */       }
/*    */   }
/*    */ 
/*    */   protected String valueToString(Object value)
/*    */   {
/* 74 */     if ((value instanceof Date)) {
/* 75 */       SimpleDateFormat format = new SimpleDateFormat(getDateEditor().getDateFormatString());
/* 76 */       return format.format((Date)value);
/*    */     }
/* 78 */     return null;
/*    */   }
/*    */ 
/*    */   protected Object[] showDialog(Component parent, Object[] values)
/*    */   {
/* 84 */     CalendarParameterOptimizer.DateListOptimizerDialog dialog = new CalendarParameterOptimizer.DateListOptimizerDialog(parent, "optimizer.dialog.select.elements.title", getDateEditor().getDateFormatString());
/*    */ 
/* 90 */     if (values == null) {
/* 91 */       return dialog.showModal(null);
/*    */     }
/* 93 */     Date[] elements = new Date[values.length];
/* 94 */     for (int i = 0; i < elements.length; i++) {
/* 95 */       elements[i] = ((Date)values[i]);
/*    */     }
/* 97 */     return dialog.showModal(elements);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.optimizer.DateParameterOptimizer
 * JD-Core Version:    0.6.0
 */