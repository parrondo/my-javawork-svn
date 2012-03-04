//+------------------------------------------------------------------+
//|                                                  SpacedLines.mq4 |
//+------------------------------------------------------------------+

#property indicator_chart_window

extern double  StartPrice = 0;
extern int     NumLines   = 9;
extern int     Spacing    = 25;
extern color   LineColor  = Gray;
extern int     LineWidth  = 1;
extern int     LineStyle  = 0;
extern string  TimeFrames = "M1, M5, M15, M30, H1, H4, D1, W1, MN";

//+------------------------------------------------------------------+
int init()  {
//+------------------------------------------------------------------+
  del_obj();
  plot_obj();
  return(0);
}

//+------------------------------------------------------------------+
int deinit()  {
//+------------------------------------------------------------------+
  del_obj();
  return(0);
}

//+------------------------------------------------------------------+
int start()  {
//+------------------------------------------------------------------+
  return(0);
}

//+------------------------------------------------------------------+
int MathSign(double n)
//+------------------------------------------------------------------+
// Returns the sign of a number (i.e. -1, 0, +1)
// Usage:   int x=MathSign(-25)   returns x=-1
{
  if (n > 0) return(1);
  else if (n < 0) return (-1);
  else return(0);
}  

//+------------------------------------------------------------------+
double MathFix(double n, int d)
//+------------------------------------------------------------------+
// Returns N rounded to D decimals - works around a precision bug in MQL4
{
  return(MathRound(n*MathPow(10,d)+0.000000000001*MathSign(n))/MathPow(10,d));
}  


//+------------------------------------------------------------------+
void plot_obj()  {
//+------------------------------------------------------------------+

  TimeFrames = StringUpper(TimeFrames) + ",";
  int tf = 0;
  if (StringFind(TimeFrames,"M1,")  >= 0)    tf += 1;
  if (StringFind(TimeFrames,"M5,")  >= 0)    tf += 2;
  if (StringFind(TimeFrames,"M15,") >= 0)    tf += 4;
  if (StringFind(TimeFrames,"M30,") >= 0)    tf += 8;
  if (StringFind(TimeFrames,"H1,")  >= 0)    tf += 16;
  if (StringFind(TimeFrames,"H4,")  >= 0)    tf += 32;
  if (StringFind(TimeFrames,"D1,")  >= 0)    tf += 64;
  if (StringFind(TimeFrames,"W1,")  >= 0)    tf += 128;
  if (StringFind(TimeFrames,"MN,")  >= 0)    tf += 256;

  if (Digits >= 4)   {                            // non-JPY pair
     double mult = 0.0001;
     double prc  = MathFix(Close[0],2);
  }
  else  {                                         // JPY pair
     mult = 0.01;
     prc  = MathFix(Close[0],0);
  }   
  if (StartPrice > 0)
    prc = StartPrice;
  for (int i=0; i<NumLines; i++)   {
    string objname = "Lspc-"+i;
    ObjectCreate(objname,OBJ_HLINE,0,0,prc+Spacing*mult*i);
    ObjectSet(objname,OBJPROP_COLOR,LineColor);
    ObjectSet(objname,OBJPROP_WIDTH,LineWidth);
    ObjectSet(objname,OBJPROP_STYLE,LineStyle);
    ObjectSet(objname,OBJPROP_TIMEFRAMES,tf);
  }
  return(0);
}

//+------------------------------------------------------------------+
void del_obj()   {
//+------------------------------------------------------------------+
  int k=0;
  while (k<ObjectsTotal())   {
    string objname = ObjectName(k);
    //uncomment, after if condition fix for strings
    //if (StringSubstr(objname,0,5) == "Lspc-")  
      ObjectDelete(objname);
    //else
    //  k++;
  }    
  return(0);
}

//+------------------------------------------------------------------+
string StringUpper(string str)
//+------------------------------------------------------------------+
// Converts any lowercase characters in a string to uppercase
// Usage:    string x=StringUpper("The Quick Brown Fox")  returns x = "THE QUICK BROWN FOX"
{
  string outstr = "";
  string lower  = "abcdefghijklmnopqrstuvwxyz";
  string upper  = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  for(int i=0; i<StringLen(str); i++)  {
    int t1 = StringFind(lower,StringSubstr(str,i,1),0);
    if (t1 >=0)  
      outstr = outstr + StringSubstr(upper,t1,1);
    else
      outstr = outstr + StringSubstr(str,i,1);
  }
  return(outstr);
}  

