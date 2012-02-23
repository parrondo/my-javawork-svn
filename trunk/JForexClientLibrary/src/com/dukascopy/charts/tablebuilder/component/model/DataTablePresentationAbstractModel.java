/*    */ package com.dukascopy.charts.tablebuilder.component.model;
/*    */ 
/*    */ import com.dukascopy.charts.data.datacache.Data;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableAnnotatedTableModel;
/*    */ 
/*    */ public abstract class DataTablePresentationAbstractModel<ColumnBean extends Enum<ColumnBean>, T extends Data> extends JLocalizableAnnotatedTableModel<ColumnBean, T>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public DataTablePresentationAbstractModel(Class<ColumnBean> columnBeanClass, Class<T> infoClass)
/*    */   {
/* 16 */     super(columnBeanClass, infoClass);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.tablebuilder.component.model.DataTablePresentationAbstractModel
 * JD-Core Version:    0.6.0
 */