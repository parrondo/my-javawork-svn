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
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import com.dukascopy.transport.common.msg.response.ErrorResponseMessage;
/*     */ import com.dukascopy.transport.common.msg.strategy.StrategyProcessDescriptor;
/*     */ import com.dukascopy.transport.common.msg.strategy.StrategyRunRequestMessage;
/*     */ import com.dukascopy.transport.common.msg.strategy.StrategyRunResponseMessage;
/*     */ import java.io.IOException;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.util.Collection;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.TimeoutException;
/*     */ import javax.swing.JOptionPane;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class RunRemoteStrategyAppActionOld extends AppActionEvent
/*     */ {
/*  48 */   private static final Logger LOGGER = LoggerFactory.getLogger(RunRemoteStrategyAppActionOld.class);
/*     */   private static final long MAX_REQUEST_SIZE = 10485760L;
/*  51 */   private static final long REQUEST_TIMEOUT = TimeUnit.SECONDS.toMillis(30L);
/*     */   private WorkspaceTreeController workspaceTreeController;
/*     */   private StrategiesTableModel strategiesModel;
/*     */   private StrategyNewBean strategy;
/*     */   private int strategyRow;
/*     */   private final Collection<Instrument> instruments;
/*     */   private StrategyProcessDescriptor remoteProcessDescriptor;
/*     */   private String errorMessageKey;
/*     */ 
/*     */   public RunRemoteStrategyAppActionOld(WorkspaceTreeController workspaceTreeController, StrategiesTableModel strategiesModel, int strategyRow, StrategyNewBean strategy, Collection<Instrument> instruments)
/*     */   {
/*  63 */     super(strategiesModel, true, true);
/*  64 */     this.workspaceTreeController = workspaceTreeController;
/*  65 */     this.strategiesModel = strategiesModel;
/*  66 */     this.strategyRow = strategyRow;
/*  67 */     this.strategy = strategy;
/*  68 */     this.instruments = instruments;
/*     */   }
/*     */ 
/*     */   public void updateGuiBefore()
/*     */   {
/*  74 */     this.strategy.setStatus(StrategyStatus.STARTING);
/*  75 */     this.strategiesModel.fireTableCellUpdated(this.strategyRow, 7);
/*     */ 
/*  77 */     StrategyWrapper wrapper = new StrategyWrapper();
/*  78 */     wrapper.setBinaryFile(this.strategy.getStrategyBinaryFile());
/*  79 */     StrategyMessages.startingStrategy(true, wrapper);
/*     */   }
/*     */ 
/*     */   public void doAction()
/*     */   {
/*  84 */     this.remoteProcessDescriptor = null;
/*  85 */     this.errorMessageKey = null;
/*     */ 
/*  87 */     GreedTransportClient transport = (GreedTransportClient)GreedContext.get("transportClient");
/*     */     try
/*     */     {
/*  90 */       StrategyRunRequestMessage request = RemoteStrategiesUtil.createRunRequest(this.strategy, this.instruments);
/*     */ 
/*  92 */       int requestSize = request.toProtocolString().length();
/*  93 */       if (requestSize > 10485760L) {
/*  94 */         LOGGER.error("Max request size exceeded : {}/{}", Integer.valueOf(requestSize), Long.valueOf(10485760L));
/*  95 */         this.errorMessageKey = "joption.pane.max.request.size.exceeded";
/*     */       } else {
/*     */         try {
/*  98 */           ProtocolMessage response = transport.controlSynchRequest(request, REQUEST_TIMEOUT);
/*  99 */           if ((response instanceof StrategyRunResponseMessage)) {
/* 100 */             this.remoteProcessDescriptor = ((StrategyRunResponseMessage)response).getStrategyProcessDescriptor();
/* 101 */           } else if ((response instanceof ErrorResponseMessage)) {
/* 102 */             LOGGER.error("Cannot run strategy remotely. " + ((ErrorResponseMessage)response).getReason());
/* 103 */             this.errorMessageKey = "joption.pane.error.starting.remote.strategy";
/*     */           } else {
/* 105 */             LOGGER.error("Cannot run strategy remotely. Unsupported response: " + response);
/* 106 */             this.errorMessageKey = "joption.pane.error.starting.remote.strategy";
/*     */           }
/*     */         } catch (TimeoutException e) {
/* 109 */           LOGGER.error("Timeout running strategy remotely.", e);
/* 110 */           this.errorMessageKey = "joption.pane.timeout.starting.remote.strategy";
/*     */         }
/*     */       }
/*     */     } catch (NoSuchAlgorithmException ex) {
/* 114 */       LOGGER.error("Required algorythm is not supported.");
/* 115 */       this.errorMessageKey = "joption.pane.error.starting.remote.strategy";
/*     */     } catch (UnsupportedEncodingException ex) {
/* 117 */       LOGGER.error("Required encoding is not supported.");
/* 118 */       this.errorMessageKey = "joption.pane.error.starting.remote.strategy";
/*     */     } catch (IOException ex) {
/* 120 */       LOGGER.error("Error reading data.", ex);
/* 121 */       this.errorMessageKey = "joption.pane.error.starting.remote.strategy";
/*     */     }
/*     */   }
/*     */ 
/*     */   public void updateGuiAfter()
/*     */   {
/* 128 */     if (this.errorMessageKey == null)
/*     */     {
/* 130 */       this.strategy.setRemoteProcessId(this.remoteProcessDescriptor.getPid());
/*     */ 
/* 132 */       ClientSettingsStorage clientSettingsStorage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/* 133 */       clientSettingsStorage.saveStrategyNewBean(this.strategy);
/*     */     }
/*     */     else {
/* 136 */       JForexClientFormLayoutManager clientFormLayoutManager = (JForexClientFormLayoutManager)GreedContext.get("layoutManager");
/*     */ 
/* 139 */       JOptionPane.showMessageDialog(clientFormLayoutManager.getStrategiesPanel(), LocalizationManager.getText(this.errorMessageKey), LocalizationManager.getText("joption.pane.error"), 0);
/*     */     }
/*     */ 
/* 147 */     this.strategiesModel.fireTableCellUpdated(this.strategyRow, 7);
/* 148 */     this.workspaceTreeController.strategyUpdated(this.strategy.getId().intValue());
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.actions.RunRemoteStrategyAppActionOld
 * JD-Core Version:    0.6.0
 */