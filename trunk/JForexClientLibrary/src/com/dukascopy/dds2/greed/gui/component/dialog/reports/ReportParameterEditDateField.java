/*    */ package com.dukascopy.dds2.greed.gui.component.dialog.reports;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.request.ReportParameter;
/*    */ import com.toedter.calendar.JDateChooser;
/*    */ import com.toedter.calendar.JSpinnerDateEditor;
/*    */ import java.awt.Font;
/*    */ import java.text.SimpleDateFormat;
/*    */ import java.util.Calendar;
/*    */ import java.util.Date;
/*    */ 
/*    */ public class ReportParameterEditDateField extends JDateChooser
/*    */   implements ReportParamDetailsInf
/*    */ {
/*    */   private ReportParameter param;
/*    */   private static final String DATE_FORMAT = "dd/MM/yyyy";
/* 22 */   private final SimpleDateFormat simpleDataFormat = new SimpleDateFormat("dd/MM/yyyy");
/*    */ 
/*    */   public ReportParameterEditDateField(ReportParameter param, Font font) {
/* 25 */     super(null, "dd/MM/yyyy", new JSpinnerDateEditor());
/* 26 */     setDate(new Date());
/*    */ 
/* 28 */     Calendar calendar = Calendar.getInstance();
/* 29 */     calendar.add(5, -1);
/* 30 */     setDate(calendar.getTime());
/*    */ 
/* 32 */     this.param = param;
/*    */   }
/*    */ 
/*    */   public String getKey() {
/* 36 */     return this.param.getKey();
/*    */   }
/*    */ 
/*    */   public String getUrlString() {
/* 40 */     return getKey() + "=" + getValue();
/*    */   }
/*    */ 
/*    */   public String getValue() {
/* 44 */     return this.simpleDataFormat.format(getDate());
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.dialog.reports.ReportParameterEditDateField
 * JD-Core Version:    0.6.0
 */