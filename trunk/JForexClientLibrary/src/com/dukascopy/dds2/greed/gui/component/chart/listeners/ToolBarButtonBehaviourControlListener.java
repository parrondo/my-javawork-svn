/*     */ package com.dukascopy.dds2.greed.gui.component.chart.listeners;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.event.FocusEvent;
/*     */ import java.awt.event.FocusListener;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.event.MouseListener;
/*     */ import java.beans.PropertyChangeEvent;
/*     */ import java.beans.PropertyChangeListener;
/*     */ import java.util.Calendar;
/*     */ 
/*     */ public class ToolBarButtonBehaviourControlListener extends MouseAdapter
/*     */   implements FocusListener, PropertyChangeListener
/*     */ {
/*  23 */   private boolean wasMousePressed = false;
/*  24 */   private boolean wasMouseReleased = false;
/*     */   public static final String ENABLE_PROPERTY = "enabled";
/*     */ 
/*     */   public void mouseReleased(MouseEvent e)
/*     */   {
/*  30 */     setWasMousePressed(false);
/*  31 */     setWasMouseReleased(true);
/*     */   }
/*     */ 
/*     */   public void mousePressed(MouseEvent e)
/*     */   {
/*  36 */     setWasMousePressed(true);
/*  37 */     setWasMouseReleased(false);
/*     */   }
/*     */ 
/*     */   public void focusGained(FocusEvent e)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void focusLost(FocusEvent e)
/*     */   {
/*  47 */     if (e == null) {
/*  48 */       return;
/*     */     }
/*     */ 
/*  54 */     if ((Boolean.FALSE.equals(Boolean.valueOf(getWasMouseReleased()))) && (e.getOppositeComponent() == null))
/*  55 */       sendMouseReleasedEvent(e.getComponent());
/*     */   }
/*     */ 
/*     */   public void propertyChange(PropertyChangeEvent evt)
/*     */   {
/*  61 */     if (evt == null) {
/*  62 */       return;
/*     */     }
/*     */ 
/*  67 */     Object source = evt.getSource();
/*  68 */     Object oldValue = evt.getOldValue();
/*  69 */     Object newValue = evt.getNewValue();
/*     */ 
/*  71 */     if (("enabled".equals(evt.getPropertyName())) && ((source instanceof Component)) && ((oldValue instanceof Boolean)) && ((newValue instanceof Boolean)))
/*     */     {
/*  76 */       Boolean oldEnabledValue = (Boolean)oldValue;
/*  77 */       Boolean newEnabledValue = (Boolean)newValue;
/*     */ 
/*  79 */       if ((Boolean.TRUE.equals(oldEnabledValue)) && (Boolean.FALSE.equals(newEnabledValue)) && (Boolean.FALSE.equals(Boolean.valueOf(getWasMouseReleased())))) {
/*  80 */         Component component = (Component)source;
/*  81 */         component.setEnabled(oldEnabledValue.booleanValue());
/*     */ 
/*  83 */         sendMouseReleasedEvent(component);
/*  84 */         sendMouseExitedEvent(component);
/*     */ 
/*  86 */         component.setEnabled(newEnabledValue.booleanValue());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void sendMouseExitedEvent(Component component)
/*     */   {
/*  96 */     MouseListener[] mouseListeners = component.getMouseListeners();
/*  97 */     if (mouseListeners != null)
/*  98 */       for (MouseListener ml : mouseListeners) {
/*  99 */         MouseEvent me = createMouseExitedEvent(component);
/* 100 */         if (me != null)
/* 101 */           ml.mouseExited(me);
/*     */       }
/*     */   }
/*     */ 
/*     */   private void sendMouseReleasedEvent(Component component)
/*     */   {
/* 112 */     MouseListener[] mouseListeners = component.getMouseListeners();
/* 113 */     if (mouseListeners != null)
/* 114 */       for (MouseListener ml : mouseListeners) {
/* 115 */         MouseEvent me = createMouseRealeasedEvent(component);
/* 116 */         if (me != null)
/* 117 */           ml.mouseReleased(me);
/*     */       }
/*     */   }
/*     */ 
/*     */   private MouseEvent createMouseExitedEvent(Component component)
/*     */   {
/* 124 */     MouseEvent me = createMouseExitedEvent(component, 505);
/* 125 */     return me;
/*     */   }
/*     */ 
/*     */   private MouseEvent createMouseRealeasedEvent(Component component) {
/* 129 */     MouseEvent me = createMouseExitedEvent(component, 502);
/* 130 */     return me;
/*     */   }
/*     */ 
/*     */   private MouseEvent createMouseExitedEvent(Component component, int eventType) {
/* 134 */     if (component == null) {
/* 135 */       return null;
/*     */     }
/*     */ 
/* 138 */     MouseEvent me = new MouseEvent(component, eventType, Calendar.getInstance().getTimeInMillis(), 1, component.getX(), component.getY(), 1, false);
/*     */ 
/* 146 */     return me;
/*     */   }
/*     */ 
/*     */   public boolean getWasMousePressed() {
/* 150 */     return this.wasMousePressed;
/*     */   }
/*     */ 
/*     */   public void setWasMousePressed(boolean wasMousePressed) {
/* 154 */     this.wasMousePressed = wasMousePressed;
/*     */   }
/*     */ 
/*     */   public boolean getWasMouseReleased() {
/* 158 */     return this.wasMouseReleased;
/*     */   }
/*     */ 
/*     */   public void setWasMouseReleased(boolean wasMouseReleased) {
/* 162 */     this.wasMouseReleased = wasMouseReleased;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.chart.listeners.ToolBarButtonBehaviourControlListener
 * JD-Core Version:    0.6.0
 */