/*    */ package com.dukascopy.charts.data.datacache;
/*    */ 
/*    */ import com.dukascopy.transport.util.Bits;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.OutputStream;
/*    */ import java.io.StreamCorruptedException;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Arrays;
/*    */ 
/*    */ public class OrdersChunkData
/*    */ {
/*    */   public static final int VERSION = 1;
/*    */   private static final String HEADER = "Orders";
/*    */   public long from;
/*    */   public long to;
/*    */   public boolean full;
/*    */   public ArrayList<OrderHistoricalData> orders;
/*    */   public ArrayList<String> openGroupsIds;
/*    */ 
/*    */   public void writeObject(OutputStream os)
/*    */     throws IOException
/*    */   {
/* 31 */     os.write("Orders".getBytes());
/* 32 */     os.write(1);
/* 33 */     os.write(Bits.longBytes(this.from));
/* 34 */     os.write(Bits.longBytes(this.to));
/* 35 */     os.write(Bits.booleanBytes(this.full));
/* 36 */     Bits.writeObject(os, this.orders);
/* 37 */     Bits.writeObject(os, this.openGroupsIds);
/*    */   }
/*    */ 
/*    */   public void readObject(InputStream is) throws IOException {
/* 41 */     byte[] header = Bits.read(is, new byte["Orders".length()]);
/* 42 */     if (!Arrays.equals(header, "Orders".getBytes())) {
/* 43 */       throw new StreamCorruptedException("Deserialization error, unknown header [" + new String(header, "UTF-8") + "]");
/*    */     }
/* 45 */     int version = is.read();
/* 46 */     if (version != 1) {
/* 47 */       throw new StreamCorruptedException("Versions doesn't match, stream version [" + version + "], class version [" + 1 + "]");
/*    */     }
/* 49 */     this.from = Bits.getLong(Bits.read(is, new byte[8]));
/* 50 */     this.to = Bits.getLong(Bits.read(is, new byte[8]));
/* 51 */     this.full = Bits.getBoolean((byte)is.read());
/* 52 */     this.orders = ((ArrayList)Bits.readObject(is, ArrayList.class, OrderHistoricalData.class));
/* 53 */     this.openGroupsIds = ((ArrayList)Bits.readObject(is, ArrayList.class, String.class));
/*    */   }
/*    */ 
/*    */   private ArrayList readArray(InputStream is, Class clazz) throws IOException {
/* 57 */     int size = is.read();
/* 58 */     ArrayList array = new ArrayList(size);
/* 59 */     for (int i = 0; i < size; i++) {
/* 60 */       Object o = Bits.readObject(is, clazz);
/* 61 */       array.add(o);
/*    */     }
/* 63 */     return array;
/*    */   }
/*    */ 
/*    */   public boolean equals(Object o)
/*    */   {
/* 68 */     if (this == o) return true;
/* 69 */     if ((o == null) || (getClass() != o.getClass())) return false;
/*    */ 
/* 71 */     OrdersChunkData that = (OrdersChunkData)o;
/*    */ 
/* 73 */     if (this.from != that.from) return false;
/* 74 */     if (this.full != that.full) return false;
/* 75 */     if (this.to != that.to) return false;
/* 76 */     if (this.openGroupsIds != null ? !this.openGroupsIds.equals(that.openGroupsIds) : that.openGroupsIds != null)
/* 77 */       return false;
/* 78 */     return this.orders != null ? this.orders.equals(that.orders) : that.orders == null;
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 85 */     int result = (int)(this.from ^ this.from >>> 32);
/* 86 */     result = 31 * result + (int)(this.to ^ this.to >>> 32);
/* 87 */     result = 31 * result + (this.full ? 1 : 0);
/* 88 */     result = 31 * result + (this.orders != null ? this.orders.hashCode() : 0);
/* 89 */     result = 31 * result + (this.openGroupsIds != null ? this.openGroupsIds.hashCode() : 0);
/* 90 */     return result;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.OrdersChunkData
 * JD-Core Version:    0.6.0
 */