/*     */ package com.dukascopy.dds2.greed.gui.component.strategy.optimizer;
/*     */ 
/*     */ import com.toedter.calendar.JDateChooser;
/*     */ import com.toedter.calendar.JTextFieldDateEditor;
/*     */ import java.awt.Component;
/*     */ import java.text.ParseException;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ public class CalendarParameterOptimizer extends AbstractLookupParameterOptimizer
/*     */ {
/*     */   private static final String PATTERN = "dd-MM-yyyy HH:mm:ss";
/*     */   private SimpleDateFormat dateFormatter;
/*     */ 
/*     */   public CalendarParameterOptimizer(Calendar value, boolean mandatory, boolean readOnly)
/*     */   {
/*  28 */     super(new JDateChooser(new JTextFieldDateEditor()), value, mandatory, readOnly);
/*  29 */     getDateEditor().setDateFormatString("dd-MM-yyyy HH:mm:ss");
/*  30 */     getDateEditor().setColumns(15);
/*     */ 
/*  32 */     this.dateFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
/*  33 */     this.dateFormatter.setLenient(false);
/*     */   }
/*     */ 
/*     */   protected JTextFieldDateEditor getDateEditor() {
/*  37 */     return (JTextFieldDateEditor)((JDateChooser)this.mainComponent).getDateEditor();
/*     */   }
/*     */ 
/*     */   protected Object getValue(Component mainComponent)
/*     */   {
/*  42 */     Date date = ((JDateChooser)mainComponent).getDate();
/*  43 */     if (date != null) {
/*  44 */       Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/*  45 */       c.setTime(date);
/*  46 */       return c;
/*     */     }
/*     */ 
/*  49 */     return null;
/*     */   }
/*     */ 
/*     */   protected void setValue(Component mainComponent, Object value)
/*     */   {
/*  55 */     if ((value instanceof Calendar)) {
/*  56 */       Date date = ((Calendar)value).getTime();
/*  57 */       ((JDateChooser)mainComponent).setDate(date);
/*     */     } else {
/*  59 */       getDateEditor().setText(null);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected Object[] showDialog(Component parent, Object[] values)
/*     */   {
/*  65 */     DateListOptimizerDialog dialog = new DateListOptimizerDialog(parent, "optimizer.dialog.select.elements.title", getDateEditor().getDateFormatString());
/*     */     Object[] dates;
/*     */     Object[] dates;
/*  72 */     if (values == null) {
/*  73 */       dates = dialog.showModal(null);
/*     */     } else {
/*  75 */       Date[] elements = new Date[values.length];
/*  76 */       for (int i = 0; i < elements.length; i++) {
/*  77 */         elements[i] = ((Calendar)values[i]).getTime();
/*     */       }
/*  79 */       dates = dialog.showModal(elements);
/*     */     }
/*  81 */     if (dates == null) {
/*  82 */       return null;
/*     */     }
/*  84 */     Calendar[] result = new Calendar[dates.length];
/*  85 */     for (int i = 0; i < dates.length; i++) {
/*  86 */       result[i] = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/*  87 */       result[i].setTime((Date)dates[i]);
/*     */     }
/*  89 */     return result;
/*     */   }
/*     */ 
/*     */   protected void validateValue(Component mainComponent)
/*     */     throws CommitErrorException
/*     */   {
/*  95 */     JTextFieldDateEditor editor = (JTextFieldDateEditor)((JDateChooser)mainComponent).getDateEditor();
/*  96 */     String text = editor.getText();
/*  97 */     if (text.trim().length() < 1) {
/*  98 */       if (isMandatory())
/*  99 */         throw new CommitErrorException("optimizer.dialog.error.date.must.be.selected");
/*     */     }
/*     */     else
/*     */       try
/*     */       {
/* 104 */         this.dateFormatter.parse(text);
/*     */       } catch (ParseException e) {
/* 106 */         throw new CommitErrorException("optimizer.dialog.error.template.not.valid.date.value", new Object[] { text });
/*     */       }
/*     */   }
/*     */ 
/*     */   protected String valueToString(Object value)
/*     */   {
/* 113 */     if ((value instanceof Calendar)) {
/* 114 */       Date date = ((Calendar)value).getTime();
/* 115 */       SimpleDateFormat format = new SimpleDateFormat(getDateEditor().getDateFormatString());
/* 116 */       return format.format(date);
/*     */     }
/* 118 */     return null;
/*     */   }
/*     */ 
/*     */   static class DateListOptimizerDialog extends AbstractParameterOptimizerDialog<Date, JDateChooser>
/*     */   {
/*     */     public DateListOptimizerDialog(Component parent, String titleKey, String pattern)
/*     */     {
/* 127 */       super(titleKey, new JDateChooser(new JTextFieldDateEditor()));
/* 128 */       ((JDateChooser)this.editor).setDateFormatString(pattern);
/* 129 */       ((JTextFieldDateEditor)((JDateChooser)this.editor).getDateEditor()).setColumns(15);
/*     */     }
/*     */ 
/*     */     protected Date getValue(JDateChooser editor)
/*     */     {
/* 134 */       String pattern = editor.getDateFormatString();
/* 135 */       SimpleDateFormat formatter = new SimpleDateFormat(pattern);
/*     */ 
/* 137 */       String text = ((JTextFieldDateEditor)editor.getDateEditor()).getText();
/*     */       try {
/* 139 */         formatter.parse(text);
/* 140 */         return editor.getDate();
/*     */       } catch (ParseException e) {
/*     */       }
/* 143 */       return null;
/*     */     }
/*     */ 
/*     */     String getValueAsString(Date value)
/*     */     {
/* 149 */       if (value == null) {
/* 150 */         return "";
/*     */       }
/* 152 */       String pattern = ((JDateChooser)this.editor).getDateFormatString();
/* 153 */       SimpleDateFormat formatter = new SimpleDateFormat(pattern);
/* 154 */       return formatter.format(value);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.optimizer.CalendarParameterOptimizer
 * JD-Core Version:    0.6.0
 */