package com.ctbri.utils;

import java.util.HashMap;
import java.util.Map;


public class ISO8583Util {
	
	/**
	 * pos中心返回应答码
	 */
	private final static Map<String,String[]> responseCodeMap = new HashMap<String,String[]>();
	private final static String SUCCESS  = "00";
	//private final static String OPERAT = "D";
	
	static{
		//初始化值
		responseCodeMap.put(SUCCESS, new String[]{"A",	"承兑或交易成功",					"交易成功"});
		responseCodeMap.put("01", new String[]{"C",	"查发卡行",							"交易失败，请联系发卡行"});
		responseCodeMap.put("02", new String[]{"C",	"可电话向发卡行查询",					"交易失败，请联系发卡行"});
		responseCodeMap.put("03", new String[]{"C",	"商户需要在银行或中心登记",			"商户未登记"});
		responseCodeMap.put("04", new String[]{"D",	"操作员没收卡",						"没收卡，请联系收单行"});
		responseCodeMap.put("05", new String[]{"C",	"发卡不予承兑",						"交易失败，请联系发卡行"});
		responseCodeMap.put("06", new String[]{"E",	"发卡行故障",						"交易失败，请联系发卡行"});
		responseCodeMap.put("07", new String[]{"D",	"特殊条件下没收卡",					"没收卡，请联系收单行"});
		responseCodeMap.put("09", new String[]{"B",	"重新提交交易请求",					"交易失败，请重试"});
		responseCodeMap.put("12", new String[]{"C",	"发卡行不支持的交易",					"交易失败，请重试"});
		responseCodeMap.put("13", new String[]{"B",	"金额为0 或太大",						"交易金额超限，请重试"});
		responseCodeMap.put("14", new String[]{"B",	"卡种未在中心登记或读卡号有误",		"无效卡号，请联系发卡行"});
		responseCodeMap.put("15", new String[]{"C",	"此发卡行未与中心开通业务",			"此卡不能受理"});
		responseCodeMap.put("19", new String[]{"C",	"刷卡读取数据有误，可重新刷卡",		"交易失败，请联系发卡行"});
		responseCodeMap.put("20", new String[]{"C",	"无效应答",							"交易失败，请联系发卡行"});
		responseCodeMap.put("21", new String[]{"C",	"不做任何处理",						"交易失败，请联系发卡行"});
		responseCodeMap.put("22", new String[]{"C",	"POS 状态与中心不符，可重新签到",		"操作有误，请重试"});
		responseCodeMap.put("23", new String[]{"C",	"不可接受的交易费",					"交易失败，请联系发卡行"});
		responseCodeMap.put("25", new String[]{"C",	"发卡行未能找到有关记录",				"交易失败，请联系发卡行"});
		responseCodeMap.put("30", new String[]{"C",	"格式错误",							"交易失败，请重试"});
		responseCodeMap.put("31", new String[]{"C",	"此发卡方未与中心开通业务",			"此卡不能受理"});
		responseCodeMap.put("33", new String[]{"D",	"过期的卡，操作员可以没收",			"过期卡，请联系发卡行"});
		responseCodeMap.put("34", new String[]{"D",	"有作弊嫌疑的卡，操作员可以没收",		"没收卡，请联系收单行"});
		responseCodeMap.put("35", new String[]{"D",	"有作弊嫌疑的卡，操作员可以没收",		"没收卡，请联系收单行"});
		responseCodeMap.put("36", new String[]{"D",	"有作弊嫌疑的卡，操作员可以没收",		"此卡有误，请换卡重试"});
		responseCodeMap.put("37", new String[]{"D",	"有作弊嫌疑的卡，操作员可以没收",		"没收卡，请联系收单行"});
		responseCodeMap.put("38", new String[]{"D",	"密码错次数超限，操作员可以没收",		"密码错误次数超限"});
		responseCodeMap.put("39", new String[]{"C",	"可能刷卡操作有误",					"交易失败，请联系发卡行"});
		responseCodeMap.put("40", new String[]{"C",	"发卡行不支持的交易类型",				"交易失败，请联系发卡行"});
		responseCodeMap.put("41", new String[]{"D",	"挂失的卡， 操作员可以没收",			"没收卡，请联系收单行"});
		responseCodeMap.put("42", new String[]{"B",	"发卡行找不到此账户",					"交易失败，请联系发卡方"});
		responseCodeMap.put("43", new String[]{"D",	"被窃卡， 操作员可以没收",			"没收卡，请联系收单行"});
		responseCodeMap.put("44", new String[]{"C",	"可能刷卡操作有误",					"交易失败，请联系发卡行"});
		responseCodeMap.put("51", new String[]{"C",	"账户内余额不足",					"余额不足，请查询"});
		responseCodeMap.put("52", new String[]{"C",	"无此支票账户",						"交易失败，请联系发卡行"});
		responseCodeMap.put("53", new String[]{"C",	"无此储蓄卡账户",					"交易失败，请联系发卡行"});
		responseCodeMap.put("54", new String[]{"C",	"过期的卡",							"过期卡，请联系发卡行"});
		responseCodeMap.put("55", new String[]{"C",	"密码输错",							"密码错，请重试"});
		responseCodeMap.put("56", new String[]{"C",	"发卡行找不到此账户",					"交易失败，请联系发卡行"});
		responseCodeMap.put("57", new String[]{"C",	"不允许持卡人进行的交易",				"交易失败，请联系发卡行"});
		responseCodeMap.put("58", new String[]{"C",	"该商户不允许进行的交易",				"终端无效，请联系收单行或银联"});
		responseCodeMap.put("59", new String[]{"C",	"",									"交易失败，请联系发卡行"});
		responseCodeMap.put("60", new String[]{"C",	"",									"交易失败，请联系发卡行"});
		responseCodeMap.put("61", new String[]{"C",	"一次交易的金额太大",					"金额太大"});
		responseCodeMap.put("62", new String[]{"C",	"",									"交易失败，请联系发卡行"});
		responseCodeMap.put("63", new String[]{"C",	"违反安全保密规定",					"交易失败，请联系发卡行"});
		responseCodeMap.put("64", new String[]{"C",	"原始金额不正确",					"交易失败，请联系发卡行"});
		responseCodeMap.put("65", new String[]{"C",	"超出取款次数限制",					"超出取款次数限制"});
		responseCodeMap.put("66", new String[]{"C",	"受卡方呼受理方安全保密部门",			"交易失败，请联系收单行或银联"});
		responseCodeMap.put("67", new String[]{"C",	"捕捉（没收卡）",					"没收卡"});
		responseCodeMap.put("68", new String[]{"C",	"发卡行规定时间内没有回答",			"交易超时，请重试"});
		responseCodeMap.put("75", new String[]{"C",	"允许的输入PIN 次数超限",				"密码错误次数超限"});
		responseCodeMap.put("77", new String[]{"D",	"POS 批次与网络中心不一致",			"请向网络中心签到"});
		responseCodeMap.put("79", new String[]{"C",	"POS 终端上传的脱机数据对账不平",		"POS 终端重传脱机数据"});
		responseCodeMap.put("90", new String[]{"C",	"日期切换正在处理",					"交易失败，请稍后重试"});
		responseCodeMap.put("91", new String[]{"C",	"电话查询发卡方或银联，可重作",		"交易失败，请稍后重试"});
		responseCodeMap.put("92", new String[]{"C",	"电话查询发卡方或网络中心，可重作",	"交易失败，请稍后重试"});
		responseCodeMap.put("93", new String[]{"C",	"交易违法、不能完成",					"交易失败，请联系发卡行"});
		responseCodeMap.put("94", new String[]{"C",	"查询网络中心，可重新签到作交易",		"交易失败，请稍后重试"});
		responseCodeMap.put("95", new String[]{"C",	"调节控制错",						"交易失败，请稍后重试"});
		responseCodeMap.put("96", new String[]{"C",	"发卡方或网络中心出现故障",			"交易失败，请稍后重试"});
		responseCodeMap.put("97", new String[]{"D",	"终端未在中心或银行登记",				"终端未登记，请联系收单行或银联"});
		responseCodeMap.put("98", new String[]{"E",	"银联收不到发卡行应答",				"交易超时，请重试"});
		responseCodeMap.put("99", new String[]{"B",	"可重新签到作交易",					"校验错，请重新签到"});
		responseCodeMap.put("A0", new String[]{"B",	"可重新签到作交易",					"校验错，请重新签到"});
	}
	
	public static String getTransMessage(String code) {
		String[] result = responseCodeMap.get(code);
		if(result!=null) {
			return result[1];
		}else {
			return null;
		}
	}
	
}
