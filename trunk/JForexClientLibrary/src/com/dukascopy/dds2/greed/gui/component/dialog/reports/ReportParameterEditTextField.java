/*    */ package com.dukascopy.dds2.greed.gui.component.dialog.reports;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.request.ReportParameter;
/*    */ import java.awt.Font;
/*    */ import javax.swing.JTextField;
/*    */ 
/*    */ public class ReportParameterEditTextField extends JTextField
/*    */   implements ReportParamDetailsInf
/*    */ {
/*    */   private ReportParameter param;
/*    */ 
/*    */   public ReportParameterEditTextField(ReportParameter param, Font font)
/*    */   {
/* 19 */     setFont(font);
/* 20 */     this.param = param;
/*    */   }
/*    */   public String getKey() {
/* 23 */     return this.param.getKey();
/*    */   }
/*    */   public String getValue() {
/* 26 */     return getText();
/*    */   }
/*    */   public String getUrlString() {
/* 29 */     return getKey() + "=" + getValue();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.dialog.reports.ReportParameterEditTextField
 * JD-Core Version:    0.6.0
 */