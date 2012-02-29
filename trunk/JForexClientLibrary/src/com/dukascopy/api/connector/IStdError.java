package com.dukascopy.api.connector;

public abstract interface IStdError
{
  public static final int ERR_NO_ERROR = 0;
  public static final int ERR_NO_RESULT = 1;
  public static final int ERR_COMMON_ERROR = 2;
  public static final int ERR_INVALID_TRADE_PARAMETERS = 3;
  public static final int ERR_SERVER_BUSY = 4;
  public static final int ERR_OLD_VERSION = 5;
  public static final int ERR_NO_CONNECTION = 6;
  public static final int ERR_NOT_ENOUGH_RIGHTS = 7;
  public static final int ERR_TOO_FREQUENT_REQUESTS = 8;
  public static final int ERR_MALFUNCTIONAL_TRADE = 9;
  public static final int ERR_ACCOUNT_DISABLED = 64;
  public static final int ERR_INVALID_ACCOUNT = 65;
  public static final int ERR_TRADE_TIMEOUT = 128;
  public static final int ERR_INVALID_PRICE = 129;
  public static final int ERR_INVALID_STOPS = 130;
  public static final int ERR_INVALID_TRADE_VOLUME = 131;
  public static final int ERR_MARKET_CLOSED = 132;
  public static final int ERR_TRADE_DISABLED = 133;
  public static final int ERR_NOT_ENOUGH_MONEY = 134;
  public static final int ERR_PRICE_CHANGED = 135;
  public static final int ERR_OFF_QUOTES = 136;
  public static final int ERR_BROKER_BUSY = 137;
  public static final int ERR_REQUOTE = 138;
  public static final int ERR_ORDER_LOCKED = 139;
  public static final int ERR_LONG_POSITIONS_ONLY_ALLOWED = 140;
  public static final int ERR_TOO_MANY_REQUESTS = 141;
  public static final int ERR_TRADE_MODIFY_DENIED = 145;
  public static final int ERR_TRADE_CONTEXT_BUSY = 146;
  public static final int ERR_TRADE_EXPIRATION_DENIED = 147;
  public static final int ERR_TRADE_TOO_MANY_ORDERS = 148;
  public static final int ERR_TRADE_HEDGE_PROHIBITED = 149;
  public static final int ERR_TRADE_PROHIBITED_BY_FIFO = 150;
  public static final int ERR_NO_MQLERROR = 4000;
  public static final int ERR_WRONG_FUNCTION_POINTER = 4001;
  public static final int ERR_ARRAY_INDEX_OUT_OF_RANGE = 4002;
  public static final int ERR_NO_MEMORY_FOR_CALL_STACK = 4003;
  public static final int ERR_RECURSIVE_STACK_OVERFLOW = 4004;
  public static final int ERR_NOT_ENOUGH_STACK_FOR_PARAM = 4005;
  public static final int ERR_NO_MEMORY_FOR_PARAM_STRING = 4006;
  public static final int ERR_NO_MEMORY_FOR_TEMP_STRING = 4007;
  public static final int ERR_NOT_INITIALIZED_STRING = 4008;
  public static final int ERR_NOT_INITIALIZED_ARRAYSTRING = 4009;
  public static final int ERR_NO_MEMORY_FOR_ARRAYSTRING = 4010;
  public static final int ERR_TOO_LONG_STRING = 4011;
  public static final int ERR_REMAINDER_FROM_ZERO_DIVIDE = 4012;
  public static final int ERR_ZERO_DIVIDE = 4013;
  public static final int ERR_UNKNOWN_COMMAND = 4014;
  public static final int ERR_WRONG_JUMP = 4015;
  public static final int ERR_NOT_INITIALIZED_ARRAY = 4016;
  public static final int ERR_DLL_CALLS_NOT_ALLOWED = 4017;
  public static final int ERR_CANNOT_LOAD_LIBRARY = 4018;
  public static final int ERR_CANNOT_CALL_FUNCTION = 4019;
  public static final int ERR_EXTERNAL_CALLS_NOT_ALLOWED = 4020;
  public static final int ERR_NO_MEMORY_FOR_RETURNED_STR = 4021;
  public static final int ERR_SYSTEM_BUSY = 4022;
  public static final int ERR_INVALID_FUNCTION_PARAMSCNT = 4050;
  public static final int ERR_INVALID_FUNCTION_PARAMVALUE = 4051;
  public static final int ERR_STRING_FUNCTION_INTERNAL = 4052;
  public static final int ERR_SOME_ARRAY_ERROR = 4053;
  public static final int ERR_INCORRECT_SERIESARRAY_USING = 4054;
  public static final int ERR_CUSTOM_INDICATOR_ERROR = 4055;
  public static final int ERR_INCOMPATIBLE_ARRAYS = 4056;
  public static final int ERR_GLOBAL_VARIABLES_PROCESSING = 4057;
  public static final int ERR_GLOBAL_VARIABLE_NOT_FOUND = 4058;
  public static final int ERR_FUNC_NOT_ALLOWED_IN_TESTING = 4059;
  public static final int ERR_FUNCTION_NOT_CONFIRMED = 4060;
  public static final int ERR_SEND_MAIL_ERROR = 4061;
  public static final int ERR_STRING_PARAMETER_EXPECTED = 4062;
  public static final int ERR_INTEGER_PARAMETER_EXPECTED = 4063;
  public static final int ERR_DOUBLE_PARAMETER_EXPECTED = 4064;
  public static final int ERR_ARRAY_AS_PARAMETER_EXPECTED = 4065;
  public static final int ERR_HISTORY_WILL_UPDATED = 4066;
  public static final int ERR_TRADE_ERROR = 4067;
  public static final int ERR_END_OF_FILE = 4099;
  public static final int ERR_SOME_FILE_ERROR = 4100;
  public static final int ERR_WRONG_FILE_NAME = 4101;
  public static final int ERR_TOO_MANY_OPENED_FILES = 4102;
  public static final int ERR_CANNOT_OPEN_FILE = 4103;
  public static final int ERR_INCOMPATIBLE_FILEACCESS = 4104;
  public static final int ERR_NO_ORDER_SELECTED = 4105;
  public static final int ERR_UNKNOWN_SYMBOL = 4106;
  public static final int ERR_INVALID_PRICE_PARAM = 4107;
  public static final int ERR_INVALID_TICKET = 4108;
  public static final int ERR_TRADE_NOT_ALLOWED = 4109;
  public static final int ERR_LONGS_NOT_ALLOWED = 4110;
  public static final int ERR_SHORTS_NOT_ALLOWED = 4111;
  public static final int ERR_OBJECT_ALREADY_EXISTS = 4200;
  public static final int ERR_UNKNOWN_OBJECT_PROPERTY = 4201;
  public static final int ERR_OBJECT_DOES_NOT_EXIST = 4202;
  public static final int ERR_UNKNOWN_OBJECT_TYPE = 4203;
  public static final int ERR_NO_OBJECT_NAME = 4204;
  public static final int ERR_OBJECT_COORDINATES_ERROR = 4205;
  public static final int ERR_NO_SPECIFIED_SUBWINDOW = 4206;
  public static final int ERR_SOME_OBJECT_ERROR = 4207;
  public static final String ERR_NO_ERROR_MSG = "ERR_NO_ERROR_MSG";
  public static final String ERR_NO_RESULT_MSG = "ERR_NO_RESULT_MSG";
  public static final String ERR_COMMON_ERROR_MSG = "ERR_COMMON_ERROR_MSG";
  public static final String ERR_INVALID_TRADE_PARAMETERS_MSG = "ERR_INVALID_TRADE_PARAMETERS_MSG";
  public static final String ERR_SERVER_BUSY_MSG = "ERR_SERVER_BUSY_MSG";
  public static final String ERR_OLD_VERSION_MSG = "ERR_OLD_VERSION_MSG";
  public static final String ERR_NO_CONNECTION_MSG = "ERR_NO_CONNECTION_MSG";
  public static final String ERR_NOT_ENOUGH_RIGHTS_MSG = "ERR_NOT_ENOUGH_RIGHTS_MSG";
  public static final String ERR_TOO_FREQUENT_REQUESTS_MSG = "ERR_TOO_FREQUENT_REQUESTS_MSG";
  public static final String ERR_MALFUNCTIONAL_TRADE_MSG = "ERR_MALFUNCTIONAL_TRADE_MSG";
  public static final String ERR_ACCOUNT_DISABLED_MSG = "ERR_ACCOUNT_DISABLED_MSG";
  public static final String ERR_INVALID_ACCOUNT_MSG = "ERR_INVALID_ACCOUNT_MSG";
  public static final String ERR_TRADE_TIMEOUT_MSG = "ERR_TRADE_TIMEOUT_MSG";
  public static final String ERR_INVALID_PRICE_MSG = "ERR_INVALID_PRICE_MSG";
  public static final String ERR_INVALID_STOPS_MSG = "ERR_INVALID_STOPS_MSG";
  public static final String ERR_INVALID_TRADE_VOLUME_MSG = "ERR_INVALID_TRADE_VOLUME_MSG";
  public static final String ERR_MARKET_CLOSED_MSG = "ERR_MARKET_CLOSED_MSG";
  public static final String ERR_TRADE_DISABLED_MSG = "ERR_TRADE_DISABLED_MSG";
  public static final String ERR_NOT_ENOUGH_MONEY_MSG = "ERR_NOT_ENOUGH_MONEY_MSG";
  public static final String ERR_PRICE_CHANGED_MSG = "ERR_PRICE_CHANGED_MSG";
  public static final String ERR_OFF_QUOTES_MSG = "ERR_OFF_QUOTES_MSG";
  public static final String ERR_BROKER_BUSY_MSG = "ERR_BROKER_BUSY_MSG";
  public static final String ERR_REQUOTE_MSG = "ERR_REQUOTE_MSG";
  public static final String ERR_ORDER_LOCKED_MSG = "ERR_ORDER_LOCKED_MSG";
  public static final String ERR_LONG_POSITIONS_ONLY_ALLOWED_MSG = "ERR_LONG_POSITIONS_ONLY_ALLOWED_MSG";
  public static final String ERR_TOO_MANY_REQUESTS_MSG = "ERR_TOO_MANY_REQUESTS_MSG";
  public static final String ERR_TRADE_MODIFY_DENIED_MSG = "ERR_TRADE_MODIFY_DENIED_MSG";
  public static final String ERR_TRADE_CONTEXT_BUSY_MSG = "ERR_TRADE_CONTEXT_BUSY_MSG";
  public static final String ERR_TRADE_EXPIRATION_DENIED_MSG = "ERR_TRADE_EXPIRATION_DENIED_MSG";
  public static final String ERR_TRADE_TOO_MANY_ORDERS_MSG = "ERR_TRADE_TOO_MANY_ORDERS_MSG";
  public static final String ERR_TRADE_HEDGE_PROHIBITED_MSG = "ERR_TRADE_HEDGE_PROHIBITED_MSG";
  public static final String ERR_TRADE_PROHIBITED_BY_FIFO_MSG = "ERR_TRADE_PROHIBITED_BY_FIFO_MSG";
  public static final String ERR_NO_MQLERROR_MSG = "ERR_NO_MQLERROR_MSG";
  public static final String ERR_WRONG_FUNCTION_POINTER_MSG = "ERR_WRONG_FUNCTION_POINTER_MSG";
  public static final String ERR_ARRAY_INDEX_OUT_OF_RANGE_MSG = "ERR_ARRAY_INDEX_OUT_OF_RANGE_MSG";
  public static final String ERR_NO_MEMORY_FOR_CALL_STACK_MSG = "ERR_NO_MEMORY_FOR_CALL_STACK_MSG";
  public static final String ERR_RECURSIVE_STACK_OVERFLOW_MSG = "ERR_RECURSIVE_STACK_OVERFLOW_MSG";
  public static final String ERR_NOT_ENOUGH_STACK_FOR_PARAM_MSG = "ERR_NOT_ENOUGH_STACK_FOR_PARAM_MSG";
  public static final String ERR_NO_MEMORY_FOR_PARAM_STRING_MSG = "ERR_NO_MEMORY_FOR_PARAM_STRING_MSG";
  public static final String ERR_NO_MEMORY_FOR_TEMP_STRING_MSG = "ERR_NO_MEMORY_FOR_TEMP_STRING_MSG";
  public static final String ERR_NOT_INITIALIZED_STRING_MSG = "ERR_NOT_INITIALIZED_STRING_MSG";
  public static final String ERR_NOT_INITIALIZED_ARRAYSTRING_MSG = "ERR_NOT_INITIALIZED_ARRAYSTRING_MSG";
  public static final String ERR_NO_MEMORY_FOR_ARRAYSTRING_MSG = "ERR_NO_MEMORY_FOR_ARRAYSTRING_MSG";
  public static final String ERR_TOO_LONG_STRING_MSG = "ERR_TOO_LONG_STRING_MSG";
  public static final String ERR_REMAINDER_FROM_ZERO_DIVIDE_MSG = "ERR_REMAINDER_FROM_ZERO_DIVIDE_MSG";
  public static final String ERR_ZERO_DIVIDE_MSG = "ERR_ZERO_DIVIDE_MSG";
  public static final String ERR_UNKNOWN_COMMAND_MSG = "ERR_UNKNOWN_COMMAND_MSG";
  public static final String ERR_WRONG_JUMP_MSG = "ERR_WRONG_JUMP_MSG";
  public static final String ERR_NOT_INITIALIZED_ARRAY_MSG = "ERR_NOT_INITIALIZED_ARRAY_MSG";
  public static final String ERR_DLL_CALLS_NOT_ALLOWED_MSG = "ERR_DLL_CALLS_NOT_ALLOWED_MSG";
  public static final String ERR_CANNOT_LOAD_LIBRARY_MSG = "ERR_CANNOT_LOAD_LIBRARY_MSG";
  public static final String ERR_CANNOT_CALL_FUNCTION_MSG = "ERR_CANNOT_CALL_FUNCTION_MSG";
  public static final String ERR_EXTERNAL_CALLS_NOT_ALLOWED_MSG = "ERR_EXTERNAL_CALLS_NOT_ALLOWED_MSG";
  public static final String ERR_NO_MEMORY_FOR_RETURNED_STR_MSG = "ERR_NO_MEMORY_FOR_RETURNED_STR_MSG";
  public static final String ERR_SYSTEM_BUSY_MSG = "ERR_SYSTEM_BUSY_MSG";
  public static final String ERR_INVALID_FUNCTION_PARAMSCNT_MSG = "ERR_INVALID_FUNCTION_PARAMSCNT_MSG";
  public static final String ERR_INVALID_FUNCTION_PARAMVALUE_MSG = "ERR_INVALID_FUNCTION_PARAMVALUE_MSG";
  public static final String ERR_STRING_FUNCTION_INTERNAL_MSG = "ERR_INVALID_FUNCTION_PARAMVALUE_MSG";
  public static final String ERR_SOME_ARRAY_ERROR_MSG = "ERR_SOME_ARRAY_ERROR_MSG";
  public static final String ERR_INCORRECT_SERIESARRAY_USING_MSG = "ERR_INCORRECT_SERIESARRAY_USING_MSG";
  public static final String ERR_CUSTOM_INDICATOR_ERROR_MSG = "ERR_CUSTOM_INDICATOR_ERROR_MSG";
  public static final String ERR_INCOMPATIBLE_ARRAYS_MSG = "ERR_INCOMPATIBLE_ARRAYS_MSG";
  public static final String ERR_GLOBAL_VARIABLES_PROCESSING_MSG = "ERR_GLOBAL_VARIABLES_PROCESSING_MSG";
  public static final String ERR_GLOBAL_VARIABLE_NOT_FOUND_MSG = "ERR_GLOBAL_VARIABLE_NOT_FOUND_MSG";
  public static final String ERR_FUNC_NOT_ALLOWED_IN_TESTING_MSG = "ERR_FUNC_NOT_ALLOWED_IN_TESTING_MSG";
  public static final String ERR_FUNCTION_NOT_CONFIRMED_MSG = "ERR_FUNCTION_NOT_CONFIRMED_MSG";
  public static final String ERR_SEND_MAIL_ERROR_MSG = "ERR_SEND_MAIL_ERROR_MSG";
  public static final String ERR_STRING_PARAMETER_EXPECTED_MSG = "ERR_STRING_PARAMETER_EXPECTED_MSG";
  public static final String ERR_INTEGER_PARAMETER_EXPECTED_MSG = "ERR_INTEGER_PARAMETER_EXPECTED_MSG";
  public static final String ERR_DOUBLE_PARAMETER_EXPECTED_MSG = "ERR_DOUBLE_PARAMETER_EXPECTED_MSG";
  public static final String ERR_ARRAY_AS_PARAMETER_EXPECTED_MSG = "ERR_ARRAY_AS_PARAMETER_EXPECTED_MSG";
  public static final String ERR_HISTORY_WILL_UPDATED_MSG = "ERR_HISTORY_WILL_UPDATED_MSG";
  public static final String ERR_TRADE_ERROR_MSG = "ERR_TRADE_ERROR_MSG";
  public static final String ERR_END_OF_FILE_MSG = "ERR_END_OF_FILE_MSG";
  public static final String ERR_SOME_FILE_ERROR_MSG = "ERR_SOME_FILE_ERROR_MSG";
  public static final String ERR_WRONG_FILE_NAME_MSG = "ERR_WRONG_FILE_NAME_MSG";
  public static final String ERR_TOO_MANY_OPENED_FILES_MSG = "ERR_TOO_MANY_OPENED_FILES_MSG";
  public static final String ERR_CANNOT_OPEN_FILE_MSG = "ERR_CANNOT_OPEN_FILE_MSG";
  public static final String ERR_INCOMPATIBLE_FILEACCESS_MSG = "ERR_INCOMPATIBLE_FILEACCESS_MSG";
  public static final String ERR_NO_ORDER_SELECTED_MSG = "ERR_NO_ORDER_SELECTED_MSG";
  public static final String ERR_UNKNOWN_SYMBOL_MSG = "ERR_UNKNOWN_SYMBOL_MSG";
  public static final String ERR_INVALID_PRICE_PARAM_MSG = "ERR_INVALID_PRICE_PARAM_MSG";
  public static final String ERR_INVALID_TICKET_MSG = "ERR_INVALID_TICKET_MSG";
  public static final String ERR_TRADE_NOT_ALLOWED_MSG = "ERR_TRADE_NOT_ALLOWED_MSG";
  public static final String ERR_LONGS_NOT_ALLOWED_MSG = "ERR_LONGS_NOT_ALLOWED_MSG";
  public static final String ERR_SHORTS_NOT_ALLOWED_MSG = "ERR_SHORTS_NOT_ALLOWED_MSG";
  public static final String ERR_OBJECT_ALREADY_EXISTS_MSG = "ERR_OBJECT_ALREADY_EXISTS_MSG";
  public static final String ERR_UNKNOWN_OBJECT_PROPERTY_MSG = "ERR_UNKNOWN_OBJECT_PROPERTY_MSG";
  public static final String ERR_OBJECT_DOES_NOT_EXIST_MSG = "ERR_OBJECT_DOES_NOT_EXIST_MSG";
  public static final String ERR_UNKNOWN_OBJECT_TYPE_MSG = "ERR_UNKNOWN_OBJECT_TYPE_MSG";
  public static final String ERR_NO_OBJECT_NAME_MSG = "ERR_NO_OBJECT_NAME_MSG";
  public static final String ERR_OBJECT_COORDINATES_ERROR_MSG = "ERR_OBJECT_COORDINATES_ERROR_MSG";
  public static final String ERR_NO_SPECIFIED_SUBWINDOW_MSG = "ERR_NO_SPECIFIED_SUBWINDOW_MSG";
  public static final String ERR_SOME_OBJECT_ERROR_MSG = "ERR_SOME_OBJECT_ERROR_MSG";
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.api.connector.IStdError
 * JD-Core Version:    0.6.0
 */