/*    */ package com.dukascopy.dds2.greed.export.historicaldata;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import java.util.LinkedList;
/*    */ import java.util.List;
/*    */ 
/*    */ public class ExportProcessControl
/*    */ {
/* 16 */   private volatile State state = State.INITIAL;
/* 17 */   private List<ExportProcessControlListener> listeners = new LinkedList();
/*    */ 
/*    */   public void setInitialState()
/*    */   {
/* 23 */     this.state = State.INITIAL;
/* 24 */     fireStateChanged();
/*    */   }
/*    */ 
/*    */   public void onStart() {
/* 28 */     this.state = State.STARTED;
/* 29 */     fireStateChanged();
/*    */   }
/*    */ 
/*    */   public void onFinished() {
/* 33 */     this.state = State.FINISHED;
/* 34 */     fireStateChanged();
/*    */   }
/*    */ 
/*    */   public synchronized void cancel() {
/* 38 */     this.state = State.CANCELED;
/*    */   }
/*    */ 
/*    */   public synchronized void onCanceled() {
/* 42 */     if (this.state == State.CANCELED)
/* 43 */       fireStateChanged();
/*    */   }
/*    */ 
/*    */   public synchronized boolean isCanceled()
/*    */   {
/* 48 */     return this.state == State.CANCELED;
/*    */   }
/*    */ 
/*    */   public boolean isFinished() {
/* 52 */     return this.state == State.FINISHED;
/*    */   }
/*    */ 
/*    */   public boolean isStarted() {
/* 56 */     return this.state == State.STARTED;
/*    */   }
/*    */ 
/*    */   public void addExportControlListener(ExportProcessControlListener exportControlListener) {
/* 60 */     this.listeners.add(exportControlListener);
/*    */   }
/*    */ 
/*    */   public void removeExportControlListener(ExportProcessControlListener exportControlListener) {
/* 64 */     this.listeners.remove(exportControlListener);
/*    */   }
/*    */ 
/*    */   public State getState() {
/* 68 */     return this.state;
/*    */   }
/*    */ 
/*    */   public void onValidated(DataField dataField, boolean error, Instrument instrument, int column, String errorText) {
/* 72 */     fireValidated(dataField, error, instrument, column, errorText);
/*    */   }
/*    */ 
/*    */   private void fireValidated(DataField dataField, boolean error, Instrument instrument, int column, String errorText) {
/* 76 */     for (ExportProcessControlListener listener : this.listeners)
/* 77 */       listener.validated(dataField, error, instrument, column, errorText);
/*    */   }
/*    */ 
/*    */   public synchronized void onProgressChanged(int progressValue, String progressBarText)
/*    */   {
/* 82 */     fireProgressChanged(progressValue, progressBarText);
/*    */   }
/*    */ 
/*    */   private void fireProgressChanged(int progressValue, String progressBarText) {
/* 86 */     for (ExportProcessControlListener listener : this.listeners)
/* 87 */       listener.progressChanged(progressValue, progressBarText);
/*    */   }
/*    */ 
/*    */   private void fireStateChanged()
/*    */   {
/* 92 */     for (ExportProcessControlListener listener : this.listeners)
/* 93 */       listener.stateChanged(this.state);
/*    */   }
/*    */ 
/*    */   public static enum State
/*    */   {
/* 10 */     INITIAL, 
/* 11 */     STARTED, 
/* 12 */     FINISHED, 
/* 13 */     CANCELED;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.export.historicaldata.ExportProcessControl
 * JD-Core Version:    0.6.0
 */