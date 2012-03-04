#property indicator_separate_window
#property indicator_buffers 2
#property indicator_color1 Blue
#property indicator_color2 Red
#property indicator_width1 4
#property indicator_width2 4
 
 
 
 
 
//---- input parameters
extern int    CCIPeriod = 2;
extern int    CCIPrice  = 5;
extern int    TimeFrame = 5;
 
int    UpperTriggerLevel  = 100;
int    LowerTriggerLevel  = -100;
int    CriticalLevel      = 250;
int    MaxBarsToCount     = 1500;
 
//---- indicator buffers
 
double UpBufferM5[];
double DnBufferM5[];
 
 
//+------------------------------------------------------------------+
//| Custom indicator initialization function                         |
//+------------------------------------------------------------------+
  int init()
  {
   IndicatorBuffers(2);
   SetIndexStyle(0,DRAW_HISTOGRAM,STYLE_SOLID);
   SetIndexStyle(1,DRAW_HISTOGRAM,STYLE_SOLID);
   SetIndexBuffer(0,UpBufferM5);
   SetIndexBuffer(1,DnBufferM5);
 
 
    
   IndicatorDigits(MarketInfo(Symbol(),MODE_DIGITS));
    
      switch(TimeFrame)
     {
      case 1: string TimeFrameStr= "M1";  break;
      case 5:        TimeFrameStr= "M5";  break;
      case 15:       TimeFrameStr= "M15"; break;
      case 30:       TimeFrameStr= "M30"; break;
      case 60:       TimeFrameStr= "H1";  break;
      case 240:      TimeFrameStr= "H4";  break;
      case 1440:     TimeFrameStr= "D1";  break;
      case 10080:    TimeFrameStr= "W1";  break;
      case 43200:    TimeFrameStr= "MN1"; break;
      default :      TimeFrameStr= "CurrTF";
     }
 
 
 
   return(0);
  }
 
//+------------------------------------------------------------------+
//| CCIFilter                                                        |
//+------------------------------------------------------------------+
int start()
  {
   int shift;
   int trend;
   datetime TimeArray[];
   ArrayCopySeries(TimeArray,MODE_TIME,Symbol(),TimeFrame); 
 
    
   double CCI0M5;
   double CCI1M5;
   double UpDnZero;
   double UpDnBuffer;
    
   if (UpperTriggerLevel<0) UpperTriggerLevel=0;
   if (LowerTriggerLevel>0) UpperTriggerLevel=0;
 
   int    limit,y=0,counted_bars=IndicatorCounted();
   limit= Bars-counted_bars;
   limit= MathMax(limit,TimeFrame/Period());
   limit= MathMin(limit,MaxBarsToCount);
 
   for(shift=0,y=0;shift<limit;shift++)
   {
   if (Time[shift]<TimeArray[y]) y++;
         CCI0M5=iCCI(NULL,TimeFrame,CCIPeriod,CCIPrice,y);
         CCI1M5=iCCI(NULL,TimeFrame,CCIPeriod,CCIPrice,y+1);
             UpDnZero=0;
             UpDnBuffer=1;
              
             UpBufferM5[shift]=UpDnZero;
             DnBufferM5[shift]=UpDnZero;
 
 
      if (CCI0M5>UpperTriggerLevel)  UpBufferM5[shift]=UpDnBuffer;
      if (CCI0M5<LowerTriggerLevel)  DnBufferM5[shift]=UpDnBuffer;
      if (CCI0M5>0 && CCI0M5<=UpperTriggerLevel) UpBufferM5[shift]=UpDnBuffer;
      if (CCI0M5<0 && CCI0M5>=LowerTriggerLevel) DnBufferM5[shift]=UpDnBuffer;
       
 
    }
 
    return(0);    
 }