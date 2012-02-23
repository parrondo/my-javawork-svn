/*     */ package com.dukascopy.dds2.greed.gui.component.chart;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableMenu;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableMenuItem;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableRadioButtonMenuItem;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.util.EnumMap;
/*     */ import java.util.Map;
/*     */ import javax.swing.ButtonGroup;
/*     */ import javax.swing.JMenu;
/*     */ import javax.swing.JMenuItem;
/*     */ import javax.swing.JPopupMenu;
/*     */ 
/*     */ public class TabsOrderingMenuContainer
/*     */ {
/*  40 */   private final JPopupMenu actionPopupMenu = new JPopupMenu();
/*  41 */   private final Map<Action, JMenuItem> actionMenuItems = new EnumMap(Action.class);
/*  42 */   private final Map<FramesState, JMenuItem> frameStatesMenuItems = new EnumMap(FramesState.class);
/*     */   ActionListener tabMenuActionListener;
/*     */   ActionListener dockCloseActionListener;
/*     */ 
/*     */   public TabsOrderingMenuContainer(ActionListener tabMenuActionListener, ActionListener actionListener)
/*     */   {
/*  48 */     this.tabMenuActionListener = tabMenuActionListener;
/*  49 */     this.dockCloseActionListener = actionListener;
/*  50 */     initMenuItems();
/*  51 */     layoutMenuItems();
/*     */   }
/*     */ 
/*     */   public JPopupMenu makeMenuListWithUndockAndCloseMenuItems(boolean addCloneTesterMenu)
/*     */   {
/*  56 */     this.actionPopupMenu.removeAll();
/*  57 */     if (addCloneTesterMenu) {
/*  58 */       this.actionPopupMenu.add(getMenuItem(Action.CLONE_TESTER));
/*     */     }
/*  60 */     this.actionPopupMenu.add(getMenuItem(Action.UNDOCK));
/*  61 */     addArrangeMenuItemTo(this.actionPopupMenu);
/*  62 */     this.actionPopupMenu.add(getMenuItem(Action.CLOSE));
/*  63 */     return this.actionPopupMenu;
/*     */   }
/*     */ 
/*     */   public JPopupMenu makeMenuListForMainTab(boolean isUndockable, boolean canBeUndocked, ButtonTabPanel buttonTabPanel)
/*     */   {
/*  68 */     makeMenuListForMainTabs(buttonTabPanel);
/*  69 */     if (isUndockable) {
/*  70 */       setMenuItemEnabled(true, new Action[] { Action.UNDOCK });
/*  71 */       if (!canBeUndocked)
/*  72 */         setMenuItemEnabled(false, new Action[] { Action.UNDOCK });
/*     */     }
/*     */     else {
/*  75 */       setMenuItemEnabled(false, new Action[] { Action.UNDOCK });
/*     */     }
/*  77 */     return this.actionPopupMenu;
/*     */   }
/*     */ 
/*     */   public JPopupMenu makeMenuListForBottomTab() {
/*  81 */     this.actionPopupMenu.removeAll();
/*  82 */     addArrangeMenuItemTo(this.actionPopupMenu);
/*  83 */     return this.actionPopupMenu;
/*     */   }
/*     */ 
/*     */   public void switchFrameStateForPopupMenu(FramesState framesState) {
/*  87 */     getMenuItem(FramesState.ORDERED).setSelected(framesState == FramesState.ORDERED);
/*  88 */     getMenuItem(FramesState.CUSTOM).setSelected(framesState == FramesState.CUSTOM);
/*  89 */     getMenuItem(FramesState.HORIZONTAL).setSelected(framesState == FramesState.HORIZONTAL);
/*  90 */     getMenuItem(FramesState.VERTICAL).setSelected(framesState == FramesState.VERTICAL);
/*     */   }
/*     */ 
/*     */   private void layoutMenuItems() {
/*  94 */     this.actionPopupMenu.add(getMenuItem(Action.CLOSE));
/*  95 */     addArrangeMenuItemTo(this.actionPopupMenu);
/*  96 */     this.actionPopupMenu.add(getMenuItem(Action.UNDOCK));
/*     */   }
/*     */ 
/*     */   public void addArrangeMenuItemTo(JPopupMenu popupMenu) {
/* 100 */     JMenu arrangeMenu = new JLocalizableMenu("arrange.popup.menu.item");
/* 101 */     arrangeMenu.add(getMenuItem(FramesState.ORDERED));
/* 102 */     arrangeMenu.add(getMenuItem(FramesState.HORIZONTAL));
/* 103 */     arrangeMenu.add(getMenuItem(FramesState.VERTICAL));
/* 104 */     arrangeMenu.addSeparator();
/* 105 */     arrangeMenu.add(getMenuItem(FramesState.CUSTOM));
/* 106 */     popupMenu.add(arrangeMenu);
/*     */   }
/*     */ 
/*     */   private JMenuItem getMenuItem(Action action) {
/* 110 */     return (JMenuItem)this.actionMenuItems.get(action);
/*     */   }
/*     */ 
/*     */   private JMenuItem getMenuItem(FramesState framesState) {
/* 114 */     return (JMenuItem)this.frameStatesMenuItems.get(framesState);
/*     */   }
/*     */ 
/*     */   private void initMenuItems() {
/* 118 */     for (Action action : Action.values()) {
/* 119 */       JLocalizableMenuItem menuItem = new JLocalizableMenuItem(action.getLabel());
/* 120 */       menuItem.setActionCommand(action.name());
/* 121 */       menuItem.addActionListener(this.dockCloseActionListener);
/* 122 */       this.actionMenuItems.put(action, menuItem);
/*     */     }
/*     */ 
/* 125 */     for (FramesState framesState : FramesState.values()) {
/* 126 */       JMenuItem menuItem = new JLocalizableRadioButtonMenuItem(framesState.getLabel());
/* 127 */       menuItem.setActionCommand(framesState.name());
/* 128 */       menuItem.addActionListener(this.tabMenuActionListener);
/* 129 */       this.frameStatesMenuItems.put(framesState, menuItem);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setMenuItemEnabled(boolean value, Action[] actions)
/*     */   {
/* 136 */     for (Action action : actions)
/* 137 */       getMenuItem(action).setEnabled(value);
/*     */   }
/*     */ 
/*     */   private void makeMenuListForMainTabs(ButtonTabPanel buttonTabPanel)
/*     */   {
/* 142 */     this.actionPopupMenu.removeAll();
/* 143 */     this.actionPopupMenu.add(getMenuItem(Action.UNDOCK));
/* 144 */     addArrangeMenuItemTo(this.actionPopupMenu);
/*     */ 
/* 146 */     if ((buttonTabPanel instanceof ChartButtonTabPanel)) {
/* 147 */       this.actionPopupMenu.addSeparator();
/* 148 */       this.actionPopupMenu.add(getMenuItem(Action.CLONE_CHART));
/* 149 */       this.actionPopupMenu.add(getMenuItem(Action.SAVE_TEMPLATE));
/* 150 */       this.actionPopupMenu.add(getMenuItem(Action.OPEN_TEMPLATE));
/*     */     }
/*     */ 
/* 153 */     this.actionPopupMenu.addSeparator();
/* 154 */     this.actionPopupMenu.add(getMenuItem(Action.CLOSE));
/* 155 */     this.actionPopupMenu.add(getMenuItem(Action.CLOSE_OTHERS));
/* 156 */     this.actionPopupMenu.add(getMenuItem(Action.CLOSE_ALL));
/*     */   }
/*     */ 
/*     */   public void makeMenuListForButton() {
/* 160 */     this.actionPopupMenu.removeAll();
/*     */ 
/* 162 */     ButtonGroup buttonGroup = new ButtonGroup();
/* 163 */     buttonGroup.add(getMenuItem(FramesState.ORDERED));
/* 164 */     buttonGroup.add(getMenuItem(FramesState.HORIZONTAL));
/* 165 */     buttonGroup.add(getMenuItem(FramesState.VERTICAL));
/* 166 */     buttonGroup.add(getMenuItem(FramesState.CUSTOM));
/*     */ 
/* 168 */     addArrangeMenuItemTo(this.actionPopupMenu);
/*     */   }
/*     */ 
/*     */   public void makeMenuListForWorkspaceTree(JPopupMenu popupMenu) {
/* 172 */     ButtonGroup buttonGroup = new ButtonGroup();
/* 173 */     buttonGroup.add(getMenuItem(FramesState.ORDERED));
/* 174 */     buttonGroup.add(getMenuItem(FramesState.HORIZONTAL));
/* 175 */     buttonGroup.add(getMenuItem(FramesState.VERTICAL));
/* 176 */     buttonGroup.add(getMenuItem(FramesState.CUSTOM));
/*     */ 
/* 178 */     addArrangeMenuItemTo(popupMenu);
/*     */   }
/*     */ 
/*     */   public static enum Action
/*     */   {
/*  20 */     CLOSE("close.popup.menu.item"), 
/*  21 */     CLOSE_ALL("close.all.popup.menu.item"), 
/*  22 */     CLOSE_OTHERS("close.others.popup.menu.item"), 
/*  23 */     UNDOCK("undock.popup.menu.item"), 
/*  24 */     CLONE_CHART("clone.chart.popup.menu.item"), 
/*  25 */     SAVE_TEMPLATE("save.template.popup.menu.item"), 
/*  26 */     OPEN_TEMPLATE("open.template.popup.menu.item"), 
/*  27 */     CLONE_TESTER("clone.tester.popup.menu.item");
/*     */ 
/*     */     private String label;
/*     */ 
/*  32 */     private Action(String label) { this.label = label; }
/*     */ 
/*     */     public String getLabel()
/*     */     {
/*  36 */       return this.label;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.chart.TabsOrderingMenuContainer
 * JD-Core Version:    0.6.0
 */