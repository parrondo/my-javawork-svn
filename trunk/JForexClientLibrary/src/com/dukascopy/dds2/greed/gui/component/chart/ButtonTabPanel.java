/*     */ package com.dukascopy.dds2.greed.gui.component.chart;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.gui.component.message.TabComponent;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.StrategiesContentPane;
/*     */ import com.dukascopy.dds2.greed.gui.resizing.components.ResizableIcon;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.event.MouseListener;
/*     */ import java.util.Calendar;
/*     */ import javax.swing.AbstractButton;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.Icon;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.plaf.basic.BasicButtonUI;
/*     */ 
/*     */ public abstract class ButtonTabPanel extends JPanel
/*     */ {
/*  34 */   protected static final Icon undockImageEnabled = new ResizableIcon("titlebar_chart_undock_active.png");
/*  35 */   protected static final Icon undockImageDisabled = new ResizableIcon("titlebar_chart_undock_inactive.png");
/*     */ 
/*  37 */   protected static final Icon closeImageEnabled = new ResizableIcon("titlebar_close_tab_active.gif");
/*  38 */   protected static final Icon closeImageDisabled = new ResizableIcon("titlebar_close_tab_inactive.gif");
/*     */   protected final int panelId;
/*     */   protected final JButton closeTabButton;
/*     */   protected final JButton undockTabButton;
/*     */   protected final ActionListener actionListener;
/*     */   protected final TabedPanelType panelType;
/*  47 */   protected String title = null;
/*  48 */   protected JLabel textLabel = null;
/*  49 */   protected boolean isActive = true;
/*  50 */   protected boolean isCloseButtonEnabled = true;
/*     */ 
/* 187 */   private static final MouseListener buttonMouseListener = new MouseAdapter() {
/*     */     public void mouseEntered(MouseEvent e) {
/* 189 */       Component component = e.getComponent();
/* 190 */       if ((component instanceof AbstractButton)) {
/* 191 */         AbstractButton button = (AbstractButton)component;
/* 192 */         if (button.isEnabled())
/* 193 */           button.setBorderPainted(true);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void mouseExited(MouseEvent e)
/*     */     {
/* 199 */       Component component = e.getComponent();
/* 200 */       if ((component instanceof AbstractButton)) {
/* 201 */         AbstractButton button = (AbstractButton)component;
/* 202 */         if (button.isEnabled())
/* 203 */           button.setBorderPainted(false);
/*     */       }
/*     */     }
/* 187 */   };
/*     */ 
/*     */   protected ButtonTabPanel(int panelId, String title, ActionListener actionListener, TabedPanelType panelType)
/*     */   {
/*  53 */     this.actionListener = actionListener;
/*  54 */     this.panelId = panelId;
/*  55 */     this.title = title;
/*  56 */     this.panelType = panelType;
/*  57 */     this.closeTabButton = new TabButton("Close this tab", closeImageEnabled, closeImageDisabled, actionListener, "closeTabAndInternalFrame");
/*  58 */     this.undockTabButton = new TabButton("Undock this tab", undockImageEnabled, undockImageDisabled, actionListener, "undockInternalFrame");
/*     */ 
/*  60 */     setOpaque(false);
/*  61 */     setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
/*     */   }
/*     */ 
/*     */   public int getPanelId() {
/*  65 */     return this.panelId;
/*     */   }
/*     */ 
/*     */   public void setTitle(String title) {
/*  69 */     this.title = title;
/*  70 */     localizeTitle();
/*  71 */     relaxTabTitle();
/*     */   }
/*     */ 
/*     */   public String getTitle() {
/*  75 */     return this.title;
/*     */   }
/*     */ 
/*     */   public void setIconIsActive(boolean isIconActive) {
/*  79 */     this.isActive = isIconActive;
/*     */   }
/*     */ 
/*     */   public void setUndockBtnEnabled(boolean isEnabled)
/*     */   {
/*  85 */     this.undockTabButton.setEnabled(isEnabled);
/*     */   }
/*     */ 
/*     */   public void setCloseBtnEnabled(boolean isEnabled) {
/*  89 */     this.isCloseButtonEnabled = isEnabled;
/*  90 */     this.closeTabButton.setEnabled(isEnabled);
/*     */   }
/*     */ 
/*     */   protected abstract void localizeTitle();
/*     */ 
/*     */   protected void relaxTabTitle() {
/*  97 */     this.closeTabButton.dispatchEvent(new MouseEvent(this.closeTabButton, 504, Calendar.getInstance().getTimeInMillis(), 504, this.closeTabButton.getX() + 3, this.closeTabButton.getY() + 3, 0, false));
/*     */ 
/* 109 */     this.closeTabButton.dispatchEvent(new MouseEvent(this.closeTabButton, 505, Calendar.getInstance().getTimeInMillis(), 505, this.closeTabButton.getX(), this.closeTabButton.getY(), 0, false));
/*     */   }
/*     */ 
/*     */   public static Component createButtonTabPanel(int panelId, String title, JComponent content, ActionListener actionListener)
/*     */   {
/* 124 */     if ((content instanceof ChartPanel)) {
/* 125 */       ButtonTabPanel buttonTabPanel = new ChartButtonTabPanel(actionListener, panelId, title);
/* 126 */       content.putClientProperty("progress", buttonTabPanel);
/* 127 */       return buttonTabPanel;
/* 128 */     }if ((content instanceof BottomPanelForMessages))
/* 129 */       return new TabComponent(panelId, title, actionListener);
/* 130 */     if ((content instanceof BottomPanelWithoutProfitLossLabelCustom))
/* 131 */       return new ButtonTabPanelForButtomCustomPanel(panelId, title, actionListener);
/* 132 */     if ((content instanceof BottomPanelWithProfitLossLabel))
/* 133 */       return new ButtonTabPanelForBottomPanel(panelId, title, actionListener);
/* 134 */     if ((content instanceof BottomPanelWithoutProfitLossLabel)) {
/* 135 */       ButtonTabPanelForBottomPanelWithCloseButton tabPanel = new ButtonTabPanelForBottomPanelWithCloseButton(panelId, title, actionListener);
/*     */ 
/* 138 */       JComponent innerContent = ((BottomPanelWithoutProfitLossLabel)content).getContent();
/* 139 */       if ((innerContent instanceof StrategiesContentPane)) {
/* 140 */         tabPanel.setCloseBtnEnabled(((StrategiesContentPane)innerContent).isClosable());
/*     */       }
/*     */ 
/* 143 */       ((BottomPanelWithoutProfitLossLabel)content).getContent().putClientProperty("tabPanel", tabPanel);
/* 144 */       return tabPanel;
/* 145 */     }if ((content instanceof TabsAndFramePanelWithToolBar)) {
/* 146 */       return new IconButtonTabPanel(panelId, title, actionListener, ((TabsAndFramePanelWithToolBar)content).getPanelType());
/*     */     }
/* 148 */     return new IconButtonTabPanel(panelId, title, actionListener, TabedPanelType.OTHER);
/*     */   }
/*     */ 
/*     */   protected class TabButton extends JButton
/*     */   {
/*     */     protected TabButton(String toolTip, Icon enabledIcon, Icon disabledIcon, ActionListener actionListener, String actionCommand)
/*     */     {
/* 155 */       super();
/* 156 */       setPreferredSize(new Dimension(17, 17));
/* 157 */       setToolTipText(toolTip);
/* 158 */       setUI(new BasicButtonUI());
/* 159 */       setContentAreaFilled(false);
/* 160 */       setFocusable(false);
/* 161 */       setBorder(BorderFactory.createLineBorder(Color.GRAY));
/* 162 */       setBorderPainted(false);
/* 163 */       addMouseListener(new MouseAdapter(ButtonTabPanel.this, enabledIcon, disabledIcon) {
/*     */         public void mouseEntered(MouseEvent e) {
/* 165 */           ButtonTabPanel.TabButton.this.setIcon(this.val$enabledIcon);
/*     */         }
/*     */         public void mouseExited(MouseEvent e) {
/* 168 */           ButtonTabPanel.TabButton.this.setIcon(this.val$disabledIcon);
/*     */         }
/*     */       });
/* 171 */       addMouseListener(ButtonTabPanel.buttonMouseListener);
/* 172 */       setRolloverEnabled(true);
/* 173 */       addActionListener(new ActionListener(ButtonTabPanel.this, actionListener, actionCommand) {
/*     */         public void actionPerformed(ActionEvent e) {
/* 175 */           this.val$actionListener.actionPerformed(new ActionEvent(e.getSource(), ButtonTabPanel.this.panelId, this.val$actionCommand));
/*     */         }
/*     */       });
/*     */     }
/*     */ 
/*     */     public void updateUI()
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.chart.ButtonTabPanel
 * JD-Core Version:    0.6.0
 */