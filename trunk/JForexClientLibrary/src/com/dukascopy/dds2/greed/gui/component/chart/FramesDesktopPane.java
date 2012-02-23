/*     */ package com.dukascopy.dds2.greed.gui.component.chart;
/*     */ 
/*     */ import com.dukascopy.charts.persistence.ITheme;
/*     */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*     */ import com.dukascopy.charts.persistence.ThemeManager;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.toolbar.DockUndockToolBar;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.ComponentAdapter;
/*     */ import java.awt.event.ComponentEvent;
/*     */ import java.beans.PropertyVetoException;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.util.List;
/*     */ import javax.swing.DesktopManager;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JDesktopPane;
/*     */ import javax.swing.JInternalFrame;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JPopupMenu;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ class FramesDesktopPane extends JDesktopPane
/*     */   implements ActionListener
/*     */ {
/*  28 */   private static final Logger LOGGER = LoggerFactory.getLogger(ChartTabsAndFramesPanel.class);
/*     */   private static final int FRAME_DISTANCE = 50;
/*     */   private final TabPanelFrameContainer frameContainer;
/*     */   private TabsOrderingMenuContainer tabsOrderingMenuContainer;
/*  42 */   private boolean ignoreActivationEvents = false;
/*  43 */   private boolean expanded = false;
/*  44 */   private FramesState framesState = FramesState.ORDERED;
/*     */ 
/*     */   FramesDesktopPane(ActionListener actionListener, TabPanelFrameContainer frameContainer)
/*     */   {
/*  48 */     this.frameContainer = frameContainer;
/*  49 */     this.tabsOrderingMenuContainer = new TabsOrderingMenuContainer(this, actionListener);
/*     */ 
/*  51 */     createListeners(frameContainer);
/*     */ 
/*  53 */     DesktopManagerInvocationHandler invocationHandler = new DesktopManagerInvocationHandler(getDesktopManager());
/*  54 */     setDesktopManager((DesktopManager)Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] { DesktopManager.class }, invocationHandler));
/*     */   }
/*     */ 
/*     */   private void createListeners(TabPanelFrameContainer frameContainer)
/*     */   {
/*  59 */     addComponentListener(new ComponentAdapter() {
/*     */       public void componentResized(ComponentEvent e) {
/*  61 */         FramesDesktopPane.this.reorderFrames();
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent e) {
/*  68 */     String actionCommand = e.getActionCommand();
/*  69 */     FramesState framesState = FramesState.valueOf(actionCommand);
/*  70 */     setFramesState(framesState);
/*     */   }
/*     */ 
/*     */   protected void paintComponent(Graphics g)
/*     */   {
/*  75 */     setBackground(ThemeManager.getTheme().getColor(ITheme.ChartElement.BACKGROUND));
/*  76 */     super.paintComponent(g);
/*     */   }
/*     */ 
/*     */   HeadlessJInternalFrame createHeadlessFrame(String name, JPanel content, ActionListener actionListener, int tabCount) {
/*  80 */     HeadlessJInternalFrame internalFrame = new HeadlessJInternalFrame(name, this.expanded, new HeadlessJInternalFrame.ExpandListener() {
/*     */       public void onExpand(HeadlessJInternalFrame source) {
/*  82 */         FramesDesktopPane.this.expand(source);
/*     */       }
/*     */     });
/*  86 */     internalFrame.setContentPane(content);
/*     */ 
/*  88 */     if ((internalFrame.getContent() instanceof TabsAndFramePanelWithToolBar)) {
/*  89 */       ((TabsAndFramePanelWithToolBar)internalFrame.getContent()).getToolBar().setPinButtonVisible(true);
/*     */     }
/*     */ 
/*  92 */     if ((content.getSize().getWidth() == 0.0D) && (content.getSize().getHeight() == 0.0D))
/*     */     {
/*  94 */       internalFrame.setSize(getSize());
/*     */     }
/*  96 */     else internalFrame.setSize(content.getSize());
/*     */ 
/*  98 */     internalFrame.setLocation(50 * tabCount, 50 * tabCount);
/*  99 */     internalFrame.setDefaultCloseOperation(0);
/* 100 */     internalFrame.addInternalFrameListener(new HeadlessJInternalFrameAdapter(internalFrame, content, actionListener));
/*     */ 
/* 102 */     add(internalFrame);
/* 103 */     this.frameContainer.addFrame(internalFrame);
/*     */ 
/* 105 */     return internalFrame;
/*     */   }
/*     */ 
/*     */   void setFramesState(FramesState framesState) {
/* 109 */     this.framesState = framesState;
/* 110 */     this.tabsOrderingMenuContainer.switchFrameStateForPopupMenu(framesState);
/*     */ 
/* 112 */     makeUnexpanded();
/* 113 */     tileFrames();
/*     */   }
/*     */ 
/*     */   FramesState getFramesState() {
/* 117 */     return this.framesState;
/*     */   }
/*     */ 
/*     */   boolean isExpanded() {
/* 121 */     return this.expanded;
/*     */   }
/*     */ 
/*     */   void setIgnoreActivationEvents(boolean ignoreActivationEvents) {
/* 125 */     this.ignoreActivationEvents = ignoreActivationEvents;
/*     */   }
/*     */ 
/*     */   boolean isIgnoreActivationEvents()
/*     */   {
/* 130 */     return this.ignoreActivationEvents;
/*     */   }
/*     */ 
/*     */   public void maximizeFrame(int panelId)
/*     */   {
/* 137 */     this.ignoreActivationEvents = true;
/*     */     try {
/* 139 */       DockedUndockedFrame frame = this.frameContainer.getFrameByPanelId(panelId);
/* 140 */       if ((frame instanceof HeadlessJInternalFrame)) {
/* 141 */         HeadlessJInternalFrame internalFrame = (HeadlessJInternalFrame)frame;
/* 142 */         if (!internalFrame.isMaximum()) {
/* 143 */           internalFrame.setMaximum(true);
/*     */         }
/* 145 */         expand(internalFrame);
/*     */       }
/*     */     } finally {
/* 148 */       this.ignoreActivationEvents = false;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void tileFrames() {
/* 153 */     if (this.expanded) {
/* 154 */       return;
/*     */     }
/*     */ 
/* 157 */     this.ignoreActivationEvents = true;
/*     */     try {
/* 159 */       if (this.framesState == FramesState.CUSTOM)
/*     */         return;
/* 162 */       List frames = this.frameContainer.getJInternalFrames();
/* 163 */       if (frames.size() <= 0)
/*     */         return;
/* 166 */       if (this.framesState == FramesState.ORDERED)
/* 167 */         tileFramesOrdered(frames, frames.size());
/* 168 */       else if ((this.framesState == FramesState.HORIZONTAL) || (this.framesState == FramesState.VERTICAL))
/* 169 */         tileFrames(frames, frames.size(), this.framesState == FramesState.VERTICAL);
/*     */     }
/*     */     finally {
/* 172 */       this.ignoreActivationEvents = false;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void tileFrames(List<HeadlessJInternalFrame> frames, int frameCount, boolean tileVertical) {
/* 177 */     int width = tileVertical ? getWidth() / frameCount : getWidth();
/* 178 */     int height = tileVertical ? getHeight() : getHeight() / frameCount;
/* 179 */     int r = 0;
/* 180 */     int c = 0;
/* 181 */     for (JInternalFrame internalFrame : frames)
/* 182 */       if (!internalFrame.isIcon())
/*     */         try {
/* 184 */           internalFrame.setMaximum(false);
/* 185 */           internalFrame.reshape(c * width, r * height, width, height);
/* 186 */           if (tileVertical)
/* 187 */             c++;
/*     */           else
/* 189 */             r++;
/*     */         }
/*     */         catch (PropertyVetoException e)
/*     */         {
/*     */         }
/*     */   }
/*     */ 
/*     */   private void tileFramesOrdered(List<HeadlessJInternalFrame> frames, int frameCount) {
/* 197 */     int rows = (int)Math.sqrt(frameCount);
/* 198 */     int cols = frameCount / rows;
/* 199 */     int extra = frameCount % rows;
/*     */ 
/* 202 */     int width = getWidth() / cols;
/* 203 */     int height = getHeight() / rows;
/* 204 */     int r = 0;
/* 205 */     int c = 0;
/* 206 */     for (JInternalFrame frame : frames)
/* 207 */       if (!frame.isIcon())
/*     */         try {
/* 209 */           frame.setMaximum(false);
/* 210 */           frame.setBounds(c * width, r * height, width, height);
/* 211 */           r++;
/* 212 */           if (r == rows) {
/* 213 */             r = 0;
/* 214 */             c++;
/* 215 */             if (c == cols - extra) {
/* 216 */               rows++;
/* 217 */               height = getHeight() / rows;
/*     */             }
/*     */           }
/*     */         }
/*     */         catch (PropertyVetoException e) {
/*     */         }
/*     */   }
/*     */ 
/*     */   private void expand(HeadlessJInternalFrame internalFrame) {
/* 226 */     if (this.expanded) {
/* 227 */       return;
/*     */     }
/*     */ 
/* 230 */     this.expanded = true;
/* 231 */     this.frameContainer.hideOtherHeadlessInternalFrames(internalFrame);
/*     */   }
/*     */ 
/*     */   public void makeUnexpanded() {
/* 235 */     if (!this.expanded) {
/* 236 */       return;
/*     */     }
/* 238 */     this.expanded = false;
/* 239 */     List internalFrames = this.frameContainer.getJInternalFrames();
/* 240 */     for (HeadlessJInternalFrame internalFrame : internalFrames) {
/* 241 */       if (internalFrame.isMaximum()) {
/* 242 */         internalFrame.setMaximum(false);
/* 243 */         if ((internalFrame.getWidth() == 0) || (internalFrame.getHeight() == 0)) {
/* 244 */           internalFrame.setBounds(0, 0, getWidth(), getHeight());
/*     */         }
/*     */       }
/*     */     }
/* 248 */     for (JInternalFrame internalFrame : internalFrames)
/* 249 */       if (!internalFrame.isVisible())
/* 250 */         internalFrame.setVisible(true);
/*     */   }
/*     */ 
/*     */   public void reorderFrames()
/*     */   {
/* 257 */     if (!this.expanded)
/* 258 */       setFramesState(this.framesState);
/*     */   }
/*     */ 
/*     */   HeadlessJInternalFrame tabWithHeadlessFrameSelected(HeadlessJInternalFrame internalFrame)
/*     */   {
/* 264 */     internalFrame.moveToFront();
/* 265 */     if (!internalFrame.isSelected()) {
/* 266 */       internalFrame.setSelected(true);
/*     */     }
/* 268 */     if (isExpanded()) {
/* 269 */       this.frameContainer.hideOtherHeadlessInternalFrames(internalFrame);
/*     */ 
/* 271 */       if (!internalFrame.isVisible()) {
/* 272 */         internalFrame.setVisible(true);
/*     */       }
/* 274 */       if (!internalFrame.isMaximum()) {
/* 275 */         internalFrame.setMaximum(true);
/*     */       }
/*     */     }
/* 278 */     return internalFrame;
/*     */   }
/*     */ 
/*     */   public void makeMenuListForWorkspaceTree(JPopupMenu popupMenu)
/*     */   {
/* 283 */     this.tabsOrderingMenuContainer.makeMenuListForWorkspaceTree(popupMenu);
/*     */   }
/*     */ 
/*     */   public JPopupMenu makeMenuListForMainTab(boolean isUndockable, boolean canBeUndocked, ButtonTabPanel buttonTabPanel) {
/* 287 */     return this.tabsOrderingMenuContainer.makeMenuListForMainTab(isUndockable, canBeUndocked, buttonTabPanel);
/*     */   }
/*     */ 
/*     */   public JPopupMenu makeMenuListForBottomTab() {
/* 291 */     return this.tabsOrderingMenuContainer.makeMenuListForBottomTab();
/*     */   }
/*     */ 
/*     */   public JPopupMenu makeMenuListWithUndockAndCloseMenuItems(boolean addCloneTesterMenu) {
/* 295 */     return this.tabsOrderingMenuContainer.makeMenuListWithUndockAndCloseMenuItems(addCloneTesterMenu);
/*     */   }
/*     */   private class DesktopManagerInvocationHandler implements InvocationHandler {
/*     */     private DesktopManager desktopManager;
/*     */     private Method resizeMethod;
/*     */     private Method dragMethod;
/*     */ 
/*     */     public DesktopManagerInvocationHandler(DesktopManager desktopManager) {
/* 306 */       this.desktopManager = desktopManager;
/*     */       try {
/* 308 */         this.dragMethod = DesktopManager.class.getMethod("dragFrame", new Class[] { JComponent.class, Integer.TYPE, Integer.TYPE });
/* 309 */         this.resizeMethod = DesktopManager.class.getMethod("resizeFrame", new Class[] { JComponent.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE });
/*     */       } catch (Exception e) {
/* 311 */         FramesDesktopPane.LOGGER.error(e.getMessage(), e);
/*     */       }
/*     */     }
/*     */ 
/*     */     public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
/* 316 */       if (((method.equals(this.resizeMethod)) || (method.equals(this.dragMethod))) && 
/* 317 */         (FramesState.CUSTOM != FramesDesktopPane.this.getFramesState())) {
/* 318 */         FramesDesktopPane.this.setFramesState(FramesState.CUSTOM);
/*     */       }
/*     */ 
/* 322 */       return method.invoke(this.desktopManager, args);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.chart.FramesDesktopPane
 * JD-Core Version:    0.6.0
 */