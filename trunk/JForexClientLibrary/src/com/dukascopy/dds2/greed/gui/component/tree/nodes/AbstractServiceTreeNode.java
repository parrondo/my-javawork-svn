/*    */ package com.dukascopy.dds2.greed.gui.component.tree.nodes;
/*    */ 
/*    */ import com.dukascopy.api.impl.ServiceWrapper;
/*    */ import com.dukascopy.dds2.greed.agent.strategy.ide.api.ServiceSourceLanguage;
/*    */ import com.dukascopy.dds2.greed.agent.strategy.ide.api.ServiceSourceType;
/*    */ import java.io.File;
/*    */ 
/*    */ public abstract class AbstractServiceTreeNode extends WorkspaceTreeNode
/*    */   implements Comparable
/*    */ {
/*    */   private int id;
/* 10 */   protected boolean isEditable = false;
/*    */ 
/*    */   public AbstractServiceTreeNode(int id, boolean allowsChildren, String name) {
/* 13 */     super(allowsChildren, name);
/* 14 */     this.id = id;
/*    */   }
/*    */ 
/*    */   public int getId() {
/* 18 */     return this.id;
/*    */   }
/*    */ 
/*    */   public String getToolTipText() {
/* 22 */     return getServiceWrapper().getName();
/*    */   }
/*    */   public abstract ServiceWrapper getServiceWrapper();
/*    */ 
/*    */   public abstract ServiceSourceType getServiceSourceType();
/*    */ 
/* 30 */   public boolean isEditable() { return this.isEditable; }
/*    */ 
/*    */   public void setEditable(boolean editable)
/*    */   {
/* 34 */     this.isEditable = editable;
/*    */   }
/*    */ 
/*    */   public String getName() {
/* 38 */     ServiceWrapper serviceWrapper = getServiceWrapper();
/* 39 */     if ((serviceWrapper == null) || (serviceWrapper.getName() == null)) {
/* 40 */       return "";
/*    */     }
/* 42 */     String name = serviceWrapper.getName();
/* 43 */     int dotIndex = name.lastIndexOf('.');
/* 44 */     if (dotIndex != -1) {
/* 45 */       name = name.substring(0, dotIndex);
/*    */     }
/* 47 */     if (name.length() > 20) {
/* 48 */       StringBuilder strBuilder = new StringBuilder();
/* 49 */       strBuilder.append(name.substring(0, 15));
/* 50 */       strBuilder.append("...");
/* 51 */       strBuilder.append(name.substring(name.length() - 5));
/* 52 */       name = strBuilder.toString();
/*    */     }
/* 54 */     return name;
/*    */   }
/*    */ 
/*    */   public ServiceSourceLanguage getServiceSourceLanguage() {
/* 58 */     ServiceWrapper serviceWrapper = getServiceWrapper();
/* 59 */     ServiceSourceLanguage language = ServiceSourceLanguage.JAVA;
/*    */ 
/* 61 */     if ((serviceWrapper != null) && (serviceWrapper.getSourceFile() != null)) {
/* 62 */       String path = serviceWrapper.getSourceFile().getAbsolutePath();
/*    */ 
/* 64 */       if (path.endsWith("mq4"))
/* 65 */         language = ServiceSourceLanguage.MQ4;
/* 66 */       else if (path.endsWith("mq5"))
/* 67 */         language = ServiceSourceLanguage.MQ5;
/* 68 */       else if ((path.endsWith("cpp")) || (path.endsWith("c")) || (path.endsWith("hpp")) || (path.endsWith("h")))
/*    */       {
/* 70 */         language = ServiceSourceLanguage.C;
/*    */       }
/*    */     }
/* 73 */     return language;
/*    */   }
/*    */ 
/*    */   public int compareTo(Object o)
/*    */   {
/* 79 */     int result = 0;
/*    */ 
/* 81 */     if (!(o instanceof AbstractServiceTreeNode)) {
/* 82 */       throw new ClassCastException("Invalid object");
/*    */     }
/*    */ 
/* 85 */     if (getServiceSourceType().equals(((AbstractServiceTreeNode)o).getServiceSourceType())) {
/* 86 */       result = getName().compareToIgnoreCase(((AbstractServiceTreeNode)o).getName());
/*    */     } else {
/* 88 */       if (getServiceSourceType().equals(ServiceSourceType.STRATEGY))
/* 89 */         result = 0;
/* 90 */       if (getServiceSourceType().equals(ServiceSourceType.INDICATOR))
/* 91 */         result = 1;
/*    */     }
/* 93 */     return result;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.nodes.AbstractServiceTreeNode
 * JD-Core Version:    0.6.0
 */