/*    */ package com.dukascopy.transport.common.protocol.http;
/*    */ 
/*    */ public enum HttpStatusCode
/*    */ {
/* 10 */   INFO_CONTINUE(100, "Continue"), 
/* 11 */   INFO_SWITCHINGPROTOCOLS(101, "Switching Protocols"), 
/*    */ 
/* 13 */   SUCCESS_OK(200, "OK"), 
/* 14 */   SUCCESS_CREATED(201, "Created"), 
/* 15 */   SUCCESS_ACCEPTED(202, "Accepted"), 
/* 16 */   SUCCESS_NONAUTHORITATIVEINFO(203, "Non-Authoritative Information"), 
/* 17 */   SUCCESS_NOCONTENT(204, "No Content"), 
/* 18 */   SUCCESS_RESETCONTENT(205, "Reset Content"), 
/* 19 */   SUCCESS_PARTIALCONTENT(206, "Partial Content"), 
/*    */ 
/* 21 */   REDIR_MULTIPLECHOICES(300, "Multiple Choices"), 
/* 22 */   REDIR_MOVEDPERMANENTLY(301, "Moved Permanently"), 
/* 23 */   REDIR_FOUND(302, "Found"), 
/* 24 */   REDIR_SEEOTHER(303, "See Other"), 
/* 25 */   REDIR_NOTMODIFIED(304, "Not Modified"), 
/* 26 */   REDIR_USEPROXY(305, "Use Proxy"), 
/* 27 */   REDIR_RESERVED306(306, "Reserved"), 
/* 28 */   REDIR_TEMPORARYREDIRECT(307, "Temporary Redirect"), 
/*    */ 
/* 30 */   CLIENTERR_BADREQUEST(400, "Bad Request"), 
/* 31 */   CLIENTERR_UNAUTHORIZED(401, "Unauthorized"), 
/* 32 */   CLIENTERR_PAYMENTREQUIRED(402, "Payment Required"), 
/* 33 */   CLIENTERR_FORBIDDEN(403, "Forbidden"), 
/* 34 */   CLIENTERR_NOTFOUND(404, "Not Found"), 
/* 35 */   CLIENTERR_METHODNOTALLOWED(405, "Method Not Allowed"), 
/* 36 */   CLIENTERR_NOTACCEPTABLE(406, "Not Acceptable"), 
/* 37 */   CLIENTERR_PROXYAUTHORIZATIONREQUIRED(407, "Proxy Authorization Required"), 
/* 38 */   CLIENTERR_REQUESTTIMEOUT(408, "Request Timeout"), 
/* 39 */   CLIENTERR_CONFLICT(409, "Conflict"), 
/* 40 */   CLIENTERR_GONE(410, "Gone"), 
/* 41 */   CLIENTERR_LENGTHREQUIRED(411, "Length Required"), 
/* 42 */   CLIENTERR_PRECONDITIONFAILED(412, "Precondition Failed"), 
/* 43 */   CLIENTERR_REQUESTENTITYTOOLARGE(413, "Request Entity Too Large"), 
/* 44 */   CLIENTERR_REQUESTURITOOLONG(414, "Request URI Too Long"), 
/* 45 */   CLIENTERR_UNSUPPORTEDMEDIATYPE(415, "Unsupported Media Type"), 
/* 46 */   CLIENTERR_REQUESTRANGENOTSATISFIABLE(416, "Request Range Not Satisfiable"), 
/* 47 */   CLIENTERR_EXPECTATIONFAILED(417, "Expectation Failed"), 
/*    */ 
/* 49 */   SERVERERR_INTERNAL(500, "Internal"), 
/* 50 */   SERVERERR_NOTIMPLEMENTED(501, "Not Implemented"), 
/* 51 */   SERVERERR_BADGATEWAY(502, "Bad Gateway"), 
/* 52 */   SERVERERR_SERVICEUNAVAILABLE(503, "Service Unavailable"), 
/* 53 */   SERVERERR_GATEWAYTIMEOUT(504, "Gateway Timeout"), 
/* 54 */   SERVERERR_HTTPVERSIONNOTSUPPORTED(505, "HTTP Version Not Supported");
/*    */ 
/*    */   private int statusCode;
/*    */   private String message;
/*    */ 
/*    */   private HttpStatusCode(int statusCode, String message)
/*    */   {
/* 66 */     this.statusCode = statusCode;
/* 67 */     this.message = message;
/*    */   }
/*    */ 
/*    */   public void setStatusCode(int statusCode) {
/* 71 */     this.statusCode = statusCode;
/*    */   }
/*    */ 
/*    */   public int getStatusCode() {
/* 75 */     return this.statusCode;
/*    */   }
/*    */ 
/*    */   public void setMessage(String message) {
/* 79 */     this.message = message;
/*    */   }
/*    */ 
/*    */   public String getMessage() {
/* 83 */     return this.message;
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 87 */     return this.message;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.protocol.http.HttpStatusCode
 * JD-Core Version:    0.6.0
 */