/*     */ package com.dukascopy.dds2.greed.gui.component.chart;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*     */ import com.dukascopy.dds2.greed.gui.JForexClientFormLayoutManager;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.Localizable;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Insets;
/*     */ import java.awt.LayoutManager;
/*     */ import java.beans.PropertyVetoException;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JInternalFrame;
/*     */ import javax.swing.JRootPane;
/*     */ import javax.swing.border.Border;
/*     */ import javax.swing.event.InternalFrameAdapter;
/*     */ import javax.swing.event.InternalFrameEvent;
/*     */ import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
/*     */ import javax.swing.plaf.basic.BasicInternalFrameUI;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import sun.swing.DefaultLookup;
/*     */ 
/*     */ class HeadlessJInternalFrame extends JInternalFrame
/*     */   implements DockedUndockedFrame, Localizable
/*     */ {
/*  40 */   private static final Logger LOGGER = LoggerFactory.getLogger(HeadlessJInternalFrame.class);
/*     */ 
/*  42 */   private boolean titleHidden = true;
/*  43 */   private HeadlessLayout layout = new HeadlessLayout(null);
/*     */   private Border border;
/*     */   private String title;
/*     */   private final ExpandListener expandListener;
/*     */ 
/*     */   public HeadlessJInternalFrame(String title, boolean expanded, ExpandListener expandListener)
/*     */   {
/*  50 */     super(title, true, true, true, false);
/*     */ 
/*  52 */     this.title = title;
/*  53 */     this.expandListener = expandListener;
/*     */ 
/*  55 */     setRootPaneCheckingEnabled(false);
/*  56 */     setLayout(this.layout);
/*  57 */     if (expanded)
/*  58 */       hideTitleAndBorder();
/*     */     else {
/*  60 */       showTitleAndBorder();
/*     */     }
/*     */ 
/*  63 */     setFrameIcon(GuiUtilsAndConstants.ICON_TITLEBAR_CHART_ACTIVE);
/*     */ 
/*  65 */     addInternalFrameListener(new InternalFrameAdapter()
/*     */     {
/*     */       public void internalFrameActivated(InternalFrameEvent e) {
/*  68 */         HeadlessJInternalFrame.this.setFrameIcon(GuiUtilsAndConstants.getTitlbarIcon(TabedPanelType.CHART, true));
/*  69 */         if (((HeadlessJInternalFrame.this.getContent() instanceof ChartPanel)) && ((GreedContext.get("layoutManager") instanceof JForexClientFormLayoutManager)))
/*  70 */           ((JForexClientFormLayoutManager)GreedContext.get("layoutManager")).getTabbedPane().setLastActiveChartPanelId(HeadlessJInternalFrame.this.getPanelId());
/*     */       }
/*     */ 
/*     */       public void internalFrameDeactivated(InternalFrameEvent e) {
/*  74 */         HeadlessJInternalFrame.this.setFrameIcon(GuiUtilsAndConstants.getTitlbarIcon(TabedPanelType.CHART, false));
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   void setTitleVisible(boolean titleShouldBeVisible)
/*     */   {
/*  82 */     if ((titleShouldBeVisible) && (this.titleHidden))
/*  83 */       showTitleAndBorder();
/*  84 */     else if ((!titleShouldBeVisible) && (!this.titleHidden))
/*  85 */       hideTitleAndBorder();
/*     */   }
/*     */ 
/*     */   public void setLayout(LayoutManager manager)
/*     */   {
/*  90 */     if (manager == null)
/*  91 */       super.setLayout(manager);
/*     */     else
/*  93 */       super.setLayout(this.layout);
/*     */   }
/*     */ 
/*     */   public void setBorder(Border border)
/*     */   {
/*  98 */     this.border = border;
/*  99 */     if (!this.titleHidden)
/* 100 */       super.setBorder(border);
/*     */   }
/*     */ 
/*     */   public TabsAndFramePanel getContent()
/*     */   {
/* 105 */     return (TabsAndFramePanel)getContentPane();
/*     */   }
/*     */ 
/*     */   public int getPanelId() {
/* 109 */     return getContent().getPanelId();
/*     */   }
/*     */ 
/*     */   public void updateMenuItems(TabsOrderingMenuContainer tabsOrderingMenuContainer) {
/* 113 */     tabsOrderingMenuContainer.makeMenuListForButton();
/*     */   }
/*     */ 
/*     */   public void setSelected(boolean selected)
/*     */   {
/*     */     try {
/* 119 */       super.setSelected(selected);
/*     */     } catch (PropertyVetoException ex) {
/* 121 */       LOGGER.warn("Unable to select headless frame : " + this, ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setMaximum(boolean isMaximum) {
/*     */     try {
/* 127 */       if (isMaximum) {
/* 128 */         this.expandListener.onExpand(this);
/*     */       }
/* 130 */       setTitleVisible(!isMaximum);
/* 131 */       super.setMaximum(isMaximum);
/*     */     } catch (PropertyVetoException e) {
/* 133 */       LOGGER.warn("Unable to maximize headless frame : " + this, e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isUndocked() {
/* 138 */     return false;
/*     */   }
/*     */ 
/*     */   public void setAlwaysOnTop(boolean isAlwaysOnTop)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setContentPane(Container c) {
/* 146 */     super.setContentPane(c);
/*     */ 
/* 148 */     if (shouldBeLocalized()) {
/* 149 */       LocalizationManager.addLocalizable(this);
/*     */     }
/*     */ 
/* 152 */     setTitle(this.title);
/*     */ 
/* 156 */     for (Component comp : c.getComponents()) {
/* 157 */       if ((!(comp.getParent() instanceof ServiceSourceEditorPanel)) && (!(comp.getParent() instanceof ChartPanel)))
/*     */         continue;
/* 159 */       setFrameIcon(GuiUtilsAndConstants.getTitlbarIcon(((TabsAndFramePanelWithToolBar)comp.getParent()).getPanelType(), true));
/* 160 */       return;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean shouldBeLocalized()
/*     */   {
/* 166 */     return ((getContent() instanceof ChartPanel)) || ((getContent() instanceof BottomPanelWithProfitLossLabel)) || ((getContent() instanceof BottomPanelWithProfitLossLabel));
/*     */   }
/*     */ 
/*     */   public void localize()
/*     */   {
/* 171 */     setTitle(this.title);
/*     */   }
/*     */ 
/*     */   public void setTitle(String title) {
/* 175 */     this.title = title;
/* 176 */     if ((getContent() instanceof ChartPanel)) {
/* 177 */       String output = LocalizationManager.getTextWithArgumentKeys("tab.title.tamplate", (Object[])this.title.split(","));
/* 178 */       super.setTitle(output);
/*     */     } else {
/* 180 */       super.setTitle(this.title);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getTitle_() {
/* 185 */     return this.title;
/*     */   }
/*     */ 
/*     */   private void hideTitleAndBorder() {
/* 189 */     this.titleHidden = true;
/* 190 */     super.setBorder(null);
/*     */   }
/*     */ 
/*     */   private void showTitleAndBorder() {
/* 194 */     this.titleHidden = false;
/* 195 */     super.setBorder(this.border);
/*     */   }
/*     */   public static abstract interface ExpandListener {
/*     */     public abstract void onExpand(HeadlessJInternalFrame paramHeadlessJInternalFrame);
/*     */   }
/*     */   private class HeadlessLayout implements LayoutManager { private JComponent savedNorthPane;
/*     */ 
/*     */     private HeadlessLayout() {  }
/*     */ 
/*     */     public void addLayoutComponent(String name, Component comp) {  }
/*     */ 
/*     */     public void removeLayoutComponent(Component comp) {  }
/*     */ 
/* 207 */     public void layoutContainer(Container parent) { Insets i = HeadlessJInternalFrame.this.getInsets();
/*     */ 
/* 209 */       int cx = i.left;
/* 210 */       int cy = i.top;
/* 211 */       int cw = HeadlessJInternalFrame.this.getWidth() - i.left - i.right;
/* 212 */       int ch = HeadlessJInternalFrame.this.getHeight() - i.top - i.bottom;
/*     */ 
/* 214 */       BasicInternalFrameUI basicInternalFrameUI = (BasicInternalFrameUI)HeadlessJInternalFrame.this.getUI();
/*     */ 
/* 216 */       JComponent northPane = basicInternalFrameUI.getNorthPane();
/* 217 */       if (HeadlessJInternalFrame.this.titleHidden) {
/* 218 */         if (northPane != null) {
/* 219 */           this.savedNorthPane = northPane;
/*     */         }
/* 221 */         basicInternalFrameUI.setNorthPane(null);
/*     */       } else {
/* 223 */         if (northPane == null) {
/* 224 */           northPane = this.savedNorthPane;
/*     */         }
/* 226 */         if (northPane != null) {
/* 227 */           basicInternalFrameUI.setNorthPane(northPane);
/* 228 */           Dimension size = northPane.getPreferredSize();
/* 229 */           if (DefaultLookup.getBoolean(HeadlessJInternalFrame.this, HeadlessJInternalFrame.this.getUI(), "InternalFrame.layoutTitlePaneAtOrigin", false)) {
/* 230 */             cy = 0;
/* 231 */             ch += i.top;
/* 232 */             northPane.setBounds(0, 0, HeadlessJInternalFrame.this.getWidth(), size.height);
/*     */           } else {
/* 234 */             northPane.setBounds(cx, cy, cw, size.height);
/*     */           }
/*     */ 
/* 237 */           cy += size.height;
/* 238 */           ch -= size.height;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 243 */       JComponent southPane = basicInternalFrameUI.getSouthPane();
/* 244 */       if (southPane != null) {
/* 245 */         Dimension size = southPane.getPreferredSize();
/* 246 */         southPane.setBounds(cx, HeadlessJInternalFrame.this.getHeight() - i.bottom - size.height, cw, size.height);
/* 247 */         ch -= size.height;
/*     */       }
/*     */ 
/* 250 */       JComponent westPane = basicInternalFrameUI.getWestPane();
/* 251 */       if (westPane != null) {
/* 252 */         Dimension size = westPane.getPreferredSize();
/* 253 */         westPane.setBounds(cx, cy, size.width, ch);
/* 254 */         cw -= size.width;
/* 255 */         cx += size.width;
/*     */       }
/*     */ 
/* 258 */       JComponent eastPane = basicInternalFrameUI.getEastPane();
/* 259 */       if (eastPane != null) {
/* 260 */         Dimension size = eastPane.getPreferredSize();
/* 261 */         eastPane.setBounds(cw - size.width, cy, size.width, ch);
/* 262 */         cw -= size.width;
/*     */       }
/*     */ 
/* 265 */       if (HeadlessJInternalFrame.this.getRootPane() != null)
/* 266 */         HeadlessJInternalFrame.this.getRootPane().setBounds(cx, cy, cw, ch);
/*     */     }
/*     */ 
/*     */     public Dimension minimumLayoutSize(Container parent)
/*     */     {
/* 274 */       Dimension result = new Dimension();
/* 275 */       JComponent northPane = ((BasicInternalFrameUI)HeadlessJInternalFrame.this.getUI()).getNorthPane();
/* 276 */       if ((northPane != null) && ((northPane instanceof BasicInternalFrameTitlePane))) {
/* 277 */         result = new Dimension(northPane.getMinimumSize());
/*     */       }
/* 279 */       Insets i = HeadlessJInternalFrame.this.getInsets();
/* 280 */       result.width += i.left + i.right;
/* 281 */       result.height += i.top + i.bottom;
/*     */ 
/* 283 */       return result;
/*     */     }
/*     */ 
/*     */     public Dimension preferredLayoutSize(Container parent)
/*     */     {
/* 288 */       Insets i = HeadlessJInternalFrame.this.getInsets();
/*     */ 
/* 290 */       Dimension result = new Dimension(HeadlessJInternalFrame.this.getRootPane().getPreferredSize());
/* 291 */       result.width += i.left + i.right;
/* 292 */       result.height += i.top + i.bottom;
/*     */ 
/* 294 */       JComponent northPane = ((BasicInternalFrameUI)HeadlessJInternalFrame.this.getUI()).getNorthPane();
/* 295 */       if (northPane != null) {
/* 296 */         Dimension d = northPane.getPreferredSize();
/* 297 */         result.width = Math.max(d.width, result.width);
/* 298 */         result.height += d.height;
/*     */       }
/*     */ 
/* 301 */       JComponent southPane = ((BasicInternalFrameUI)HeadlessJInternalFrame.this.getUI()).getSouthPane();
/* 302 */       if (southPane != null) {
/* 303 */         Dimension d = southPane.getPreferredSize();
/* 304 */         result.width = Math.max(d.width, result.width);
/* 305 */         result.height += d.height;
/*     */       }
/*     */ 
/* 308 */       JComponent eastPane = ((BasicInternalFrameUI)HeadlessJInternalFrame.this.getUI()).getEastPane();
/* 309 */       if (eastPane != null) {
/* 310 */         Dimension d = eastPane.getPreferredSize();
/* 311 */         result.width += d.width;
/* 312 */         result.height = Math.max(d.height, result.height);
/*     */       }
/*     */ 
/* 315 */       JComponent westPane = ((BasicInternalFrameUI)HeadlessJInternalFrame.this.getUI()).getWestPane();
/* 316 */       if (westPane != null) {
/* 317 */         Dimension d = westPane.getPreferredSize();
/* 318 */         result.width += d.width;
/* 319 */         result.height = Math.max(d.height, result.height);
/*     */       }
/* 321 */       return result;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.chart.HeadlessJInternalFrame
 * JD-Core Version:    0.6.0
 */