/*    */ package com.dukascopy.dds2.greed.gui.component.dialog.reports;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.request.ReportParameter;
/*    */ import java.awt.Font;
/*    */ import javax.swing.JCheckBox;
/*    */ 
/*    */ public class ReportParameterEditBooleanField extends JCheckBox
/*    */   implements ReportParamDetailsInf
/*    */ {
/*    */   private ReportParameter param;
/*    */ 
/*    */   public ReportParameterEditBooleanField(ReportParameter param, Font font)
/*    */   {
/* 20 */     this.param = param;
/*    */   }
/*    */ 
/*    */   public String getKey() {
/* 24 */     return this.param.getKey();
/*    */   }
/*    */ 
/*    */   public String getUrlString() {
/* 28 */     return getKey() + "=" + getValue();
/*    */   }
/*    */ 
/*    */   public String getValue() {
/* 32 */     return isSelected() ? "true" : "false";
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.dialog.reports.ReportParameterEditBooleanField
 * JD-Core Version:    0.6.0
 */