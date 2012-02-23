/*     */ package com.dukascopy.dds2.greed.gui.component.strategy.tab.actions;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.impl.StrategyMessages;
/*     */ import com.dukascopy.api.impl.StrategyWrapper;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.actions.AppActionEvent;
/*     */ import com.dukascopy.dds2.greed.connection.GreedTransportClient;
/*     */ import com.dukascopy.dds2.greed.gui.JForexClientFormLayoutManager;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.RemoteStrategiesUtil;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyNewBean;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyStatus;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.table.StrategiesTableModel;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceTreeController;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.util.ObjectUtils;
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import com.dukascopy.transport.common.msg.response.ErrorResponseMessage;
/*     */ import com.dukascopy.transport.common.msg.strategy.StrategyRunChunkRequestMessage;
/*     */ import java.io.IOException;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.util.Collection;
/*     */ import java.util.Set;
/*     */ import javax.swing.JOptionPane;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class RunRemoteStrategyAppAction extends AppActionEvent
/*     */ {
/*  40 */   private static final Logger LOGGER = LoggerFactory.getLogger(RunRemoteStrategyAppAction.class);
/*     */   private WorkspaceTreeController workspaceTreeController;
/*     */   private StrategiesTableModel strategiesModel;
/*     */   private StrategyNewBean strategy;
/*     */   private int strategyRow;
/*     */   private final Collection<Instrument> instruments;
/*     */   private String errorMessageKey;
/*     */   private String requestId;
/*     */ 
/*     */   public RunRemoteStrategyAppAction(WorkspaceTreeController workspaceTreeController, StrategiesTableModel strategiesModel, int strategyRow, StrategyNewBean strategy, Collection<Instrument> instruments)
/*     */   {
/*  52 */     super(strategiesModel, true, true);
/*  53 */     this.workspaceTreeController = workspaceTreeController;
/*  54 */     this.strategiesModel = strategiesModel;
/*  55 */     this.strategyRow = strategyRow;
/*  56 */     this.strategy = strategy;
/*  57 */     this.instruments = instruments;
/*     */   }
/*     */ 
/*     */   public void updateGuiBefore()
/*     */   {
/*  63 */     this.strategy.setStatus(StrategyStatus.STARTING);
/*  64 */     this.strategiesModel.fireTableCellUpdated(this.strategyRow, 7);
/*     */ 
/*  66 */     StrategyWrapper wrapper = new StrategyWrapper();
/*  67 */     wrapper.setBinaryFile(this.strategy.getStrategyBinaryFile());
/*  68 */     StrategyMessages.startingStrategy(true, wrapper);
/*     */   }
/*     */ 
/*     */   public void doAction()
/*     */   {
/*  73 */     this.errorMessageKey = null;
/*     */ 
/*  75 */     GreedTransportClient transport = (GreedTransportClient)GreedContext.get("transportClient");
/*     */     try {
/*  77 */       Set requests = RemoteStrategiesUtil.createRunRequests(this.strategy, this.instruments);
/*  78 */       for (StrategyRunChunkRequestMessage request : requests) {
/*  79 */         ProtocolMessage response = transport.controlRequest(request);
/*  80 */         if ((response instanceof ErrorResponseMessage)) {
/*  81 */           LOGGER.error("Cannot run strategy remotely. " + ((ErrorResponseMessage)response).getReason());
/*  82 */           this.errorMessageKey = "joption.pane.error.starting.remote.strategy";
/*  83 */           return;
/*  84 */         }if (ObjectUtils.isNullOrEmpty(this.requestId))
/*  85 */           this.requestId = request.getRequestId();
/*     */       }
/*     */     }
/*     */     catch (NoSuchAlgorithmException ex) {
/*  89 */       LOGGER.error("Required algorythm is not supported.");
/*  90 */       this.errorMessageKey = "joption.pane.error.starting.remote.strategy";
/*     */     } catch (UnsupportedEncodingException ex) {
/*  92 */       LOGGER.error("Required encoding is not supported.");
/*  93 */       this.errorMessageKey = "joption.pane.error.starting.remote.strategy";
/*     */     } catch (IOException ex) {
/*  95 */       LOGGER.error("Error reading data.", ex);
/*  96 */       this.errorMessageKey = "joption.pane.error.starting.remote.strategy";
/*     */     } catch (IllegalArgumentException ex) {
/*  98 */       LOGGER.error("Illegal argument.", ex);
/*  99 */       this.errorMessageKey = "joption.pane.error.starting.remote.strategy";
/*     */     } catch (Throwable e) {
/* 101 */       LOGGER.error(e.getMessage(), e);
/* 102 */       this.errorMessageKey = "joption.pane.error.starting.remote.strategy";
/*     */     }
/*     */   }
/*     */ 
/*     */   public void updateGuiAfter()
/*     */   {
/* 108 */     this.strategy.setRemoteRequestId(null);
/* 109 */     if ((this.errorMessageKey == null) && (!ObjectUtils.isNullOrEmpty(this.requestId))) {
/* 110 */       this.strategy.setRemoteRequestId(this.requestId);
/*     */     } else {
/* 112 */       JForexClientFormLayoutManager clientFormLayoutManager = (JForexClientFormLayoutManager)GreedContext.get("layoutManager");
/*     */ 
/* 115 */       JOptionPane.showMessageDialog(clientFormLayoutManager.getStrategiesPanel(), LocalizationManager.getText(this.errorMessageKey), LocalizationManager.getText("joption.pane.error"), 0);
/*     */ 
/* 121 */       this.strategy.setStatus(StrategyStatus.STOPPED);
/* 122 */       this.strategy.setRemoteProcessId(null);
/*     */     }
/* 124 */     ClientSettingsStorage clientSettingsStorage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/* 125 */     clientSettingsStorage.saveStrategyNewBean(this.strategy);
/* 126 */     this.strategiesModel.fireTableCellUpdated(this.strategyRow, 7);
/* 127 */     this.workspaceTreeController.strategyUpdated(this.strategy.getId().intValue());
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.actions.RunRemoteStrategyAppAction
 * JD-Core Version:    0.6.0
 */