/*     */ package com.dukascopy.dds2.greed.gui.component.tree;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.gui.resizing.components.ResizableIcon;
/*     */ import com.dukascopy.dds2.greed.util.GuiResourceLoader;
/*     */ import java.awt.Component;
/*     */ import java.awt.Graphics;
/*     */ import javax.swing.Icon;
/*     */ 
/*     */ public class WorkspaceTreeIcon
/*     */   implements Icon
/*     */ {
/*  22 */   private static final Icon iconInitializing = new ResizableIcon("tree_strategy_state_wait.png");
/*  23 */   private static final Icon iconRunningLocally = new ResizableIcon("tree_strategy_state_local.png");
/*     */ 
/*  25 */   private static final Icon iconRunningRemotely = new ResizableIcon("tree_strategy_state_remote.png");
/*     */   private Icon baseIcon;
/*     */   private TreeIconMode mode;
/*     */ 
/*     */   public WorkspaceTreeIcon(Icon baseIcon)
/*     */   {
/*  35 */     this.baseIcon = baseIcon;
/*     */   }
/*     */ 
/*     */   public WorkspaceTreeIcon(String resourceName)
/*     */   {
/*  43 */     this.baseIcon = GuiResourceLoader.getInstance().loadImageIcon(resourceName);
/*     */   }
/*     */ 
/*     */   public int getIconHeight()
/*     */   {
/*  48 */     return this.baseIcon.getIconHeight();
/*     */   }
/*     */ 
/*     */   public int getIconWidth()
/*     */   {
/*  53 */     return this.baseIcon.getIconWidth();
/*     */   }
/*     */ 
/*     */   public TreeIconMode getMode()
/*     */   {
/*  62 */     return this.mode;
/*     */   }
/*     */ 
/*     */   public void setMode(TreeIconMode mode)
/*     */   {
/*  71 */     this.mode = mode;
/*     */   }
/*     */ 
/*     */   public void paintIcon(Component c, Graphics g, int x, int y)
/*     */   {
/*  76 */     this.baseIcon.paintIcon(c, g, x, y);
/*     */ 
/*  79 */     if (this.mode != null)
/*     */     {
/*     */       Icon overIcon;
/*  81 */       switch (1.$SwitchMap$com$dukascopy$dds2$greed$gui$component$tree$WorkspaceTreeIcon$TreeIconMode[this.mode.ordinal()]) {
/*     */       case 1:
/*  83 */         overIcon = iconInitializing;
/*  84 */         break;
/*     */       case 2:
/*  87 */         overIcon = iconRunningLocally;
/*  88 */         break;
/*     */       case 3:
/*  95 */         overIcon = iconRunningRemotely;
/*  96 */         break;
/*     */       default:
/* 100 */         return;
/*     */       }
/* 102 */       overIcon.paintIcon(c, g, x, y);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static enum TreeIconMode
/*     */   {
/* 109 */     RUNNING_LOCALLY, 
/*     */ 
/* 111 */     INITIALIZING, 
/*     */ 
/* 113 */     RUNNING_REMOTELY, 
/*     */ 
/* 115 */     RUNNING_PROTECTED;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.WorkspaceTreeIcon
 * JD-Core Version:    0.6.0
 */