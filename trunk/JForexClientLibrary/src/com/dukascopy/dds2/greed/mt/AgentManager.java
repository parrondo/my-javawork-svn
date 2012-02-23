/*     */ package com.dukascopy.dds2.greed.mt;
/*     */ 
/*     */ import com.dukascopy.api.ITick;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.agent.AgentException;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*     */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*     */ import com.dukascopy.dds2.greed.gui.component.AccountStatementPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsTable;
/*     */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsTableModel;
/*     */ import com.dukascopy.dds2.greed.gui.component.status.GreedStatusBar;
/*     */ import com.dukascopy.dds2.greed.mt.common.IAgent;
/*     */ import com.dukascopy.transport.common.model.type.OrderState;
/*     */ import com.dukascopy.transport.common.model.type.Position;
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*     */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*     */ import com.dukascopy.transport.common.msg.response.NotificationMessage;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.net.BindException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.ThreadFactory;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.SwingUtilities;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class AgentManager
/*     */ {
/*  43 */   private static Logger log = LoggerFactory.getLogger(AgentManager.class.getName());
/*  44 */   private long agentExecutorThreadId = 0L;
/*  45 */   private ExecutorService executorService = null;
/*     */ 
/*  48 */   private PositionsTable positionsTable = null;
/*     */ 
/*  50 */   private Runnable serverRunnable = null;
/*  51 */   private Thread serverThread = null;
/*     */ 
/*     */   public IAgent getAgent() {
/*  54 */     return Agent.getInstance();
/*     */   }
/*     */ 
/*     */   public AgentManager() {
/*     */     try {
/*  59 */       updateDll();
/*     */ 
/*  61 */       ClientForm clientForm = (ClientForm)GreedContext.get("clientGui");
/*  62 */       PositionsPanel positionsPanel = clientForm.getPositionsPanel();
/*  63 */       this.positionsTable = positionsPanel.getTable();
/*     */ 
/*  65 */       JCheckBox oneClickCheckbox = clientForm.getStatusBar().getAccountStatement().getOneClickCheckbox();
/*  66 */       while (!oneClickCheckbox.isVisible()) {
/*  67 */         Thread.sleep(1000L);
/*     */       }
/*  69 */       if (oneClickCheckbox.isSelected()) {
/*  70 */         oneClickCheckbox.setSelected(false);
/*     */       }
/*  72 */       restartExecutorService();
/*  73 */       this.serverRunnable = new ServerRunnable();
/*  74 */       restart();
/*     */     } catch (BindException bindException) {
/*  76 */       if (log.isDebugEnabled())
/*  77 */         log.error("Unable to load DDS Agent Manager: " + bindException.getMessage(), bindException);
/*     */       else
/*  79 */         log.debug("Unable to load DDS Agent Manager: " + bindException.getMessage());
/*     */     }
/*     */     catch (Throwable e) {
/*  82 */       if (log.isDebugEnabled())
/*  83 */         log.error("DDS AgentManager exception: " + e.getMessage(), e);
/*     */       else
/*  85 */         log.error("DDS AgentManager exception: " + e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   private void restart()
/*     */   {
/*  91 */     this.serverThread = new Thread(this.serverRunnable);
/*  92 */     this.serverThread.setDaemon(true);
/*  93 */     this.serverThread.setName("DDS Agent");
/*  94 */     this.serverThread.start();
/*     */   }
/*     */ 
/*     */   private void updateDll()
/*     */   {
/*  99 */     String jlp = System.getProperty("java.library.path");
/* 100 */     String[] paths = jlp.split(File.pathSeparator);
/* 101 */     File targetFile = null;
/* 102 */     for (int i = 0; i < paths.length; i++) {
/* 103 */       if (paths[i].endsWith("system32")) {
/* 104 */         targetFile = new File(paths[i] + File.separator + "mt4jfx.dll");
/* 105 */         break;
/*     */       }
/*     */     }
/* 108 */     if (targetFile != null)
/*     */       try
/*     */       {
/* 111 */         InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("mt4jfx.dll");
/* 112 */         if ((stream != null) && (stream.available() > 0)) {
/* 113 */           if (targetFile.exists()) {
/* 114 */             targetFile.delete();
/* 115 */             targetFile.createNewFile();
/*     */           }
/* 117 */           FileOutputStream outputStream = new FileOutputStream(targetFile);
/* 118 */           StratUtils.turboPipe(stream, outputStream);
/* 119 */           outputStream.close();
/* 120 */           stream.close();
/*     */         }
/*     */       }
/*     */       catch (FileNotFoundException fe) {
/* 124 */         System.err.print(fe.getMessage());
/*     */       } catch (Exception e) {
/* 126 */         e.printStackTrace();
/*     */       }
/*     */   }
/*     */ 
/*     */   private void restartExecutorService()
/*     */   {
/* 133 */     if (this.executorService != null) {
/* 134 */       this.executorService.shutdownNow();
/*     */     }
/* 136 */     this.executorService = Executors.newSingleThreadExecutor(new ThreadFactory() {
/*     */       public Thread newThread(Runnable r) {
/* 138 */         Thread thread = new Thread(r);
/* 139 */         thread.setName("Agent Executor");
/* 140 */         AgentManager.access$002(AgentManager.this, thread.getId());
/* 141 */         return thread;
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public void setError(Integer mtId, int errorCode, String errorMsg)
/*     */   {
/* 167 */     Agent.getInstance().setError(mtId, errorCode, errorMsg);
/*     */   }
/*     */ 
/*     */   public void onOrderGroupReceived(OrderGroupMessage orderGroup)
/*     */   {
/* 180 */     if (orderGroup == null) {
/* 181 */       return;
/*     */     }
/* 183 */     String extSysId = (String)orderGroup.get("extSysId");
/* 184 */     if (extSysId != null) {
/* 185 */       Agent.getInstance().putNotifMsg(extSysId, orderGroup);
/* 186 */       synchronized (Agent.getInstance()) {
/* 187 */         Agent.getInstance().notify();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onNotifyMessage(ProtocolMessage protocolMessage)
/*     */   {
/* 197 */     NotificationMessage notificationMessage = (NotificationMessage)protocolMessage;
/* 198 */     if (notificationMessage.getOrderState() == OrderState.REJECTED) {
/* 199 */       String extSysId = (String)notificationMessage.get("extSysId");
/* 200 */       if (extSysId != null) {
/* 201 */         Agent.getInstance().putNotifMsg(extSysId, notificationMessage);
/* 202 */         synchronized (Agent.getInstance()) {
/* 203 */           Agent.getInstance().notify();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean onMarketStateImpl_dllsupport_depricated(String instrument, ITick tick)
/*     */   {
/* 213 */     return false;
/*     */   }
/*     */ 
/*     */   public void onErrorMessage(ProtocolMessage protocolMessage) {
/* 217 */     if ((protocolMessage instanceof NotificationMessage)) {
/* 218 */       NotificationMessage notificationMessage = (NotificationMessage)protocolMessage;
/* 219 */       if (notificationMessage.getLevel().equals("WARNING"))
/* 220 */         return;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void waitForPosition(String label)
/*     */   {
/*     */     try {
/* 227 */       int counter = 0;
/*     */ 
/* 229 */       while (counter++ < 300) {
/* 230 */         Position position = getPositionByLabelImpl(label);
/* 231 */         if (position != null) {
/* 232 */           if (position.getOrderGroup().getOpeningOrder().getOrderState() == OrderState.FILLED) {
/* 233 */             break;
/*     */           }
/*     */         }
/*     */         else
/* 237 */           wait(100L);
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   protected Position getPositionByLabelImpl(String label) {
/* 246 */     if (Thread.currentThread().getId() != this.agentExecutorThreadId) {
/* 247 */       throw new AgentException(-19);
/*     */     }
/* 249 */     Position rc = null;
/* 250 */     for (Position position : getPositionsListImpl(false, null)) {
/* 251 */       OrderGroupMessage ogm = position.getOrderGroup();
/* 252 */       OrderMessage orderMessage = ogm.getOpeningOrder();
/* 253 */       if ((orderMessage != null) && (
/* 254 */         (ogm.getOrderGroupId().equals(label)) || (label.equals(orderMessage.getExternalSysId()))))
/*     */       {
/* 256 */         rc = position;
/* 257 */         break;
/*     */       }
/*     */     }
/*     */ 
/* 261 */     return rc;
/*     */   }
/*     */ 
/*     */   protected List<Position> getPositionsListImpl(boolean filterWithNoTag, String filterSymbol)
/*     */   {
/* 266 */     if (Thread.currentThread().getId() != this.agentExecutorThreadId) {
/* 267 */       throw new AgentException(-19);
/*     */     }
/* 269 */     List rc = new ArrayList();
/*     */     try {
/* 271 */       SwingUtilities.invokeAndWait(new Runnable(rc) {
/*     */         public void run() {
/* 273 */           PositionsTableModel positionsTableModel = (PositionsTableModel)AgentManager.this.positionsTable.getModel();
/* 274 */           this.val$rc.addAll(positionsTableModel.getPositions());
/*     */         } } );
/*     */     } catch (Exception e) {
/* 278 */       e.printStackTrace();
/*     */     }
/*     */ 
/* 281 */     List toRemove = new ArrayList();
/* 282 */     if (filterWithNoTag) {
/* 283 */       for (Position position : rc) {
/* 284 */         if (position.getOrderGroup().getOpeningOrder().getExternalSysId() == null) {
/* 285 */           toRemove.add(position);
/*     */         }
/*     */       }
/*     */     }
/* 289 */     if (filterSymbol != null) {
/* 290 */       for (Position position : rc) {
/* 291 */         if (!filterSymbol.equals(position.getInstrument())) {
/* 292 */           toRemove.add(position);
/*     */         }
/*     */       }
/*     */     }
/* 296 */     rc.removeAll(toRemove);
/* 297 */     return rc;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.mt.AgentManager
 * JD-Core Version:    0.6.0
 */