package com.lifeix.football.service.aggregation.common;

public class Constants {

    public static final class EmailType {
	/** 纯文本 **/
	public static final int TEXT = 0;
	/** html **/
	public static final int HTML = 1;
    }

    public static final class SendFlag {
	/** 待发送 **/
	public static final int UNSENDED = 0;
	/** 发送中 **/
	public static final int SENDING = 1;
	/** 发送完成 **/
	public static final int SENDED = 2;
    }

}
