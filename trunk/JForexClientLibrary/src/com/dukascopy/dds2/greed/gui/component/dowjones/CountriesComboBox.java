/*    */ package com.dukascopy.dds2.greed.gui.component.dowjones;
/*    */ 
/*    */ import com.dukascopy.api.INewsFilter;
/*    */ import com.dukascopy.api.INewsFilter.Country;
/*    */ import com.dukascopy.api.INewsFilter.Region;
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.util.Set;
/*    */ import javax.swing.AbstractAction;
/*    */ import javax.swing.JComboBox;
/*    */ 
/*    */ public class CountriesComboBox extends JComboBox
/*    */ {
/*    */   private INewsFilter newsFilter;
/* 19 */   private boolean ignoreUpdate = false;
/*    */ 
/*    */   public CountriesComboBox(INewsFilter newsFilter) {
/* 22 */     addItem(null);
/*    */ 
/* 24 */     for (INewsFilter.Country country : INewsFilter.Country.values()) {
/* 25 */       if (country.region == INewsFilter.Region.Combined) {
/* 26 */         addItem(country);
/*    */       }
/*    */     }
/*    */ 
/* 30 */     refresh(newsFilter);
/*    */ 
/* 32 */     setAction(new AbstractAction()
/*    */     {
/*    */       public void actionPerformed(ActionEvent e) {
/* 35 */         CountriesComboBox.this.update();
/*    */       } } );
/*    */   }
/*    */ 
/*    */   public void refresh(INewsFilter newsFilter) {
/* 41 */     this.ignoreUpdate = true;
/* 42 */     this.newsFilter = newsFilter;
/*    */ 
/* 44 */     Set countries = newsFilter.getCountries();
/* 45 */     if ((countries != null) && (countries.size() == 1)) {
/* 46 */       INewsFilter.Country country = ((INewsFilter.Country[])countries.toArray(new INewsFilter.Country[1]))[0];
/* 47 */       if (country.region == INewsFilter.Region.Combined)
/* 48 */         setSelectedItem(country);
/*    */       else
/* 50 */         setSelectedItem(null);
/*    */     }
/*    */     else {
/* 53 */       setSelectedItem(null);
/*    */     }
/*    */   }
/*    */ 
/*    */   private void update() {
/* 58 */     if (this.ignoreUpdate) {
/* 59 */       this.ignoreUpdate = false;
/* 60 */       return;
/*    */     }
/*    */ 
/* 63 */     Set countries = this.newsFilter.getCountries();
/*    */ 
/* 65 */     countries.clear();
/*    */ 
/* 67 */     Object selectedItem = getSelectedItem();
/*    */ 
/* 69 */     if (selectedItem != null)
/* 70 */       countries.add((INewsFilter.Country)selectedItem);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.dowjones.CountriesComboBox
 * JD-Core Version:    0.6.0
 */