/*     */ package com.dukascopy.charts.view.swing;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.drawings.IOhlcChartObject.OhlcAlignment;
/*     */ import com.dukascopy.charts.drawings.IDrawingsManager;
/*     */ import com.dukascopy.charts.drawings.OhlcChartObject;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.util.SpringUtilities;
/*     */ import java.awt.Color;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import javax.swing.ButtonGroup;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JRadioButton;
/*     */ import javax.swing.SpringLayout;
/*     */ 
/*     */ public class OhlcChartWidgetPanel extends AbstractChartWidgetPanel
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private final OhlcChartObject ohlcChartObject;
/*     */ 
/*     */   public OhlcChartWidgetPanel(OhlcChartObject ohlcChartObject, IDrawingsManager drawingManager)
/*     */   {
/*  39 */     super(ohlcChartObject, drawingManager);
/*     */ 
/*  41 */     this.ohlcChartObject = ohlcChartObject;
/*     */   }
/*     */ 
/*     */   protected String getTitle()
/*     */   {
/*  46 */     return LocalizationManager.getText("item.ohlc.informer");
/*     */   }
/*     */ 
/*     */   protected JPanel createInfoContentPanel()
/*     */   {
/*  52 */     return new JPanel() {
/*     */       private static final long serialVersionUID = 1L;
/*     */ 
/*     */       protected void paintComponent(Graphics g) {
/*  57 */         OhlcChartWidgetPanel.this.ohlcChartObject.paintContent(g);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   protected void beforePaintingBackground()
/*     */   {
/*  65 */     if (this.mode == "VIEW_MODE")
/*  66 */       this.ohlcChartObject.evaluateContent(getGraphics(), this.ohlcChartObject.getSize(), getParent().getWidth(), getParent().getHeight());
/*     */   }
/*     */ 
/*     */   public void setWidgetBounds(Rectangle r)
/*     */   {
/*  73 */     setWidgetPosition(r.x, r.y);
/*     */ 
/*  75 */     if (((r.width != getWidth()) || (r.height != getHeight())) && 
/*  76 */       ("VIEW_MODE".equals(this.mode))) {
/*  77 */       this.ohlcChartObject.resetFontSize();
/*  78 */       this.ohlcChartObject.evaluateContent(getGraphics(), new Dimension(r.width, r.height), getParent().getWidth(), getParent().getHeight());
/*  79 */       drawingModelModified();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void onCustomEditPanelShown(JPanel editPanel)
/*     */   {
/*  90 */     editPanel.removeAll();
/*     */ 
/*  92 */     SpringLayout layout = new SpringLayout();
/*  93 */     editPanel.setLayout(layout);
/*     */ 
/*  95 */     Color color = this.ohlcChartObject.getColor();
/*  96 */     String fontName = this.ohlcChartObject.getFont().getFamily();
/*     */ 
/*  99 */     JLabel label = createLabel("menu.item.ohlc.alignment", fontName, color);
/* 100 */     label.setPreferredSize(new Dimension(160, label.getPreferredSize().height));
/*     */ 
/* 102 */     JRadioButton rdbAlignmentAuto = createRadioButton("menu.item.ohlc.auto", fontName, color);
/* 103 */     rdbAlignmentAuto.setSelected(IOhlcChartObject.OhlcAlignment.AUTO.equals(this.ohlcChartObject.getAlignment()));
/*     */ 
/* 105 */     JRadioButton rdbAlignmentHz = createRadioButton("menu.item.ohlc.horizontal", fontName, color);
/* 106 */     rdbAlignmentHz.setSelected(IOhlcChartObject.OhlcAlignment.HORIZONTAL.equals(this.ohlcChartObject.getAlignment()));
/*     */ 
/* 108 */     JRadioButton rdbAlignmentVt = createRadioButton("menu.item.ohlc.vertical", fontName, color);
/* 109 */     rdbAlignmentVt.setSelected(IOhlcChartObject.OhlcAlignment.VERTICAL.equals(this.ohlcChartObject.getAlignment()));
/*     */ 
/* 112 */     ButtonGroup group = new ButtonGroup();
/* 113 */     group.add(rdbAlignmentAuto);
/* 114 */     group.add(rdbAlignmentHz);
/* 115 */     group.add(rdbAlignmentVt);
/*     */ 
/* 117 */     rdbAlignmentAuto.addActionListener(createRadioButtonActionListener(IOhlcChartObject.OhlcAlignment.AUTO));
/* 118 */     rdbAlignmentHz.addActionListener(createRadioButtonActionListener(IOhlcChartObject.OhlcAlignment.HORIZONTAL));
/* 119 */     rdbAlignmentVt.addActionListener(createRadioButtonActionListener(IOhlcChartObject.OhlcAlignment.VERTICAL));
/*     */ 
/* 121 */     editPanel.add(label);
/* 122 */     editPanel.add(rdbAlignmentAuto);
/* 123 */     editPanel.add(new JLabel());
/* 124 */     editPanel.add(rdbAlignmentHz);
/* 125 */     editPanel.add(new JLabel());
/* 126 */     editPanel.add(rdbAlignmentVt);
/*     */ 
/* 135 */     DataType dataType = this.ohlcChartObject.getDataType();
/* 136 */     Enum[] params = this.ohlcChartObject.getAllInfoParamsByDataType(dataType);
/* 137 */     if (params == null) {
/* 138 */       throw new IllegalStateException("unknown DataType");
/*     */     }
/*     */ 
/* 142 */     label = createLabel("menu.item.ohlc.visibility", fontName, color);
/*     */ 
/* 144 */     editPanel.add(label);
/*     */ 
/* 146 */     int paramCount = 0;
/* 147 */     for (Enum param : params) {
/* 148 */       if (paramCount != 0) {
/* 149 */         editPanel.add(new JLabel());
/*     */       }
/*     */ 
/* 152 */       JCheckBox chkBox = createCheckBox(LocalizationManager.getText(this.ohlcChartObject.getParamMenuLocalizationKey(param)), fontName, color);
/* 153 */       chkBox.setSelected(this.ohlcChartObject.getParamVisibility(param));
/* 154 */       chkBox.addActionListener(new ActionListener(param)
/*     */       {
/*     */         public void actionPerformed(ActionEvent e)
/*     */         {
/* 158 */           JCheckBox source = (JCheckBox)e.getSource();
/* 159 */           OhlcChartWidgetPanel.this.ohlcChartObject.setParamVisibility(this.val$param, Boolean.valueOf(source.isSelected()).booleanValue());
/* 160 */           OhlcChartWidgetPanel.this.drawingModelModified();
/*     */         }
/*     */       });
/* 164 */       editPanel.add(chkBox);
/*     */ 
/* 166 */       paramCount++;
/*     */     }
/*     */ 
/* 173 */     Map indicatorCheckBoxMap = new HashMap();
/* 174 */     Map indVisibilityMap = this.ohlcChartObject.getIndVisibilityMap();
/*     */ 
/* 176 */     if (!indVisibilityMap.isEmpty()) {
/* 177 */       label = createLabel("menu.item.ohlc.indicator.values", fontName, color);
/* 178 */       editPanel.add(label);
/*     */     }
/*     */ 
/* 182 */     boolean first = true;
/* 183 */     for (Map.Entry entry : indVisibilityMap.entrySet()) {
/* 184 */       JCheckBox chkBox = createCheckBox((String)entry.getKey(), fontName, color);
/* 185 */       chkBox.setSelected(((Boolean)entry.getValue()).booleanValue());
/*     */ 
/* 187 */       chkBox.addActionListener(new ActionListener(indVisibilityMap, entry)
/*     */       {
/*     */         public void actionPerformed(ActionEvent e)
/*     */         {
/* 191 */           JCheckBox source = (JCheckBox)e.getSource();
/* 192 */           this.val$indVisibilityMap.put(this.val$entry.getKey(), Boolean.valueOf(source.isSelected()));
/*     */         }
/*     */       });
/* 196 */       if (first)
/* 197 */         first = false;
/*     */       else {
/* 199 */         editPanel.add(new JLabel());
/*     */       }
/* 201 */       editPanel.add(chkBox);
/* 202 */       indicatorCheckBoxMap.put(entry.getKey(), chkBox);
/*     */ 
/* 204 */       paramCount++;
/*     */     }
/*     */ 
/* 208 */     SpringUtilities.makeCompactGrid(editPanel, paramCount + 3, 2, 3, 3, 5, 3);
/*     */ 
/* 210 */     editPanel.setMinimumSize(layout.minimumLayoutSize(editPanel));
/*     */   }
/*     */ 
/*     */   private ActionListener createRadioButtonActionListener(IOhlcChartObject.OhlcAlignment alignment) {
/* 214 */     return new ActionListener(alignment)
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 218 */         if (((JRadioButton)e.getSource()).isSelected()) {
/* 219 */           OhlcChartWidgetPanel.this.ohlcChartObject.setAlignment(this.val$alignment);
/* 220 */           OhlcChartWidgetPanel.this.drawingModelModified();
/*     */         }
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   protected JRadioButton createRadioButton(String localizationKey, String fontName, Color color) {
/* 228 */     JRadioButton radioButton = super.createRadioButton(localizationKey, fontName, color);
/* 229 */     radioButton.setPreferredSize(new Dimension(110, 12));
/* 230 */     radioButton.setMinimumSize(new Dimension(110, 12));
/* 231 */     radioButton.setMaximumSize(new Dimension(110, 12));
/*     */ 
/* 233 */     return radioButton;
/*     */   }
/*     */ 
/*     */   protected JCheckBox createCheckBox(String title, String fontName, Color color)
/*     */   {
/* 238 */     JCheckBox chkBox = new JCheckBox();
/*     */ 
/* 240 */     Font currFont = chkBox.getFont();
/* 241 */     chkBox.setFont(new Font(fontName, currFont.getStyle(), currFont.getSize()));
/* 242 */     chkBox.setOpaque(false);
/* 243 */     chkBox.setText(title);
/* 244 */     chkBox.setForeground(color);
/* 245 */     chkBox.setSelected(true);
/* 246 */     chkBox.setPreferredSize(new Dimension(50, 13));
/* 247 */     chkBox.setMaximumSize(new Dimension(100, 13));
/* 248 */     chkBox.setMinimumSize(new Dimension(30, 13));
/*     */ 
/* 250 */     return chkBox;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.swing.OhlcChartWidgetPanel
 * JD-Core Version:    0.6.0
 */