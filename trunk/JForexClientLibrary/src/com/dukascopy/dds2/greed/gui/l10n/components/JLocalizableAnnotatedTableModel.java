/*    */ package com.dukascopy.dds2.greed.gui.l10n.components;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ import com.dukascopy.dds2.greed.gui.table.AnnotatedTableModel;
/*    */ 
/*    */ public class JLocalizableAnnotatedTableModel<ColumnBean extends Enum<ColumnBean>, Info> extends AnnotatedTableModel<ColumnBean, Info>
/*    */ {
/*    */   public JLocalizableAnnotatedTableModel(Class<ColumnBean> columnBeanClass, Class<Info> infoClass)
/*    */   {
/* 13 */     super(columnBeanClass, infoClass);
/*    */   }
/*    */ 
/*    */   public String getColumnName(int columnIndex)
/*    */   {
/* 18 */     return LocalizationManager.getText(super.getColumnName(columnIndex));
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableAnnotatedTableModel
 * JD-Core Version:    0.6.0
 */