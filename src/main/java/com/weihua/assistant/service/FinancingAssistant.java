package com.weihua.assistant.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.gson.reflect.TypeToken;
import com.weihua.assistant.context.Context;
import com.weihua.assistant.context.Context.HistoryRecord;
import com.weihua.assistant.entity.alarm.AlarmInfo;
import com.weihua.assistant.entity.request.BaseRequest;
import com.weihua.assistant.entity.request.Request;
import com.weihua.assistant.entity.response.Response;
import com.weihua.assistant.service.annotation.ServiceLocation;
import com.weihua.database.dao.FinancingDao;
import com.weihua.database.dao.impl.FinancingDaoImpl;
import com.weihua.util.DateUtil;
import com.weihua.util.ExceptionUtil;
import com.weihua.util.GsonUtil;
import com.weihua.util.HttpUtil;

public class FinancingAssistant extends BaseAssistant {

	private static final Logger LOGGER = Logger.getLogger(FinancingAssistant.class);

	private static FinancingDao financingDao = new FinancingDaoImpl();

	@Override
	public Response getResponse(Request request) {
		Response response = null;
		try {
			BaseRequest baseRequest = (BaseRequest) request;
			if (baseRequest.isLocationPath() == null || baseRequest.isLocationPath() == false) {
				response = getSharesConfig((BaseRequest) request);
			} else {
				response = invokeLocationMethod((BaseRequest) request);
			}
		} catch (Exception e) {
			ExceptionUtil.propagate(LOGGER, e);
		}
		return response;
	}

	@ServiceLocation(value = "getSharesConfig")
	public Response getSharesConfig(BaseRequest request) {
		List<Map<String, Object>> result = financingDao.findAlarmSharesList();
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("sharesList", result);
		return response(model, "financing/shares");
	}

	@ServiceLocation(value = "getSharesChanges")
	public Response getSharesChanges(BaseRequest request) {
		HistoryRecord lastHistoryRecord = Context.findLastBackAssistantHistory(request.getAssistantType(),
				request.getOriginRequest());
		Map<String, String> timeConfig = GsonUtil.getMapFromJson(request.getExtraInfo());

		if (lastHistoryRecord == null || DateUtil.getDateDiff(DateUtil.getNowDateTime(),
				lastHistoryRecord.getCreateTime()) > Long.valueOf(timeConfig.get("rate"))) {
			String msg = getQuotChangesMsg();
			if (msg.length() > 0) {
				AlarmInfo model = new AlarmInfo();
				model.setTitle("Shares changes warnning");
				model.setContent(msg);
				// model.setIcon("icon");
				return responseJson(model);
			}
		}
		return null;
	}

	private static String getQuotChangesMsg() {

		StringBuilder sb = new StringBuilder();

		Map<String, String> map;
		Double temp;
		String alarmMsg = "";
		List<Double> alarmValue = new ArrayList<Double>();
		List<Map<String, Object>> result = financingDao.findAlarmSharesList();
		for (Map<String, Object> item : result) {
			map = getQuotInfo(item.get("share_code").toString());

			if (map.size() > 0) {
				ShareConfig shareConfig = GsonUtil.getEntityFromJson(String.valueOf(item.get("alarm_config")),
						new TypeToken<ShareConfig>() {
						});

				StringBuilder itemSb = new StringBuilder();

				temp = Double.parseDouble(map.get("quotPrice"));
				for (Double value : shareConfig.riseQuotPrice) {
					if (temp > value) {
						alarmMsg = "Price rised " + value + ",Current is " + temp + ".";
						alarmValue.add(value);
					}
				}
				if (alarmMsg != "") {
					itemSb.append(alarmMsg);
					alarmMsg = "";
					shareConfig.riseQuotPrice.removeAll(alarmValue);
				}

				temp = Double.parseDouble(map.get("quotMovement"));
				for (Double value : shareConfig.riseQuotMovement) {
					if (temp > value) {
						alarmMsg = "Movement rised " + value + "%,Current is " + temp + "%.";
						alarmValue.add(value);
					}
				}
				if (alarmMsg != "") {
					itemSb.append(alarmMsg);
					alarmMsg = "";
					shareConfig.riseQuotMovement.removeAll(alarmValue);
				}

				temp = Double.parseDouble(map.get("quotPrice"));
				for (Double value : shareConfig.downQuotPrice) {
					if (temp < value) {
						alarmMsg = "Price down " + value + ",Current is " + temp + ".";
						alarmValue.add(value);
					}
				}
				if (alarmMsg != "") {
					itemSb.append(alarmMsg);
					alarmMsg = "";
					shareConfig.downQuotPrice.removeAll(alarmValue);
				}

				temp = Double.parseDouble(map.get("quotMovement"));
				for (Double value : shareConfig.downQuotMovement) {
					if (temp < value) {
						alarmMsg = "Movement down " + value + "%,Current is " + temp + "%.";
						alarmValue.add(value);
					}
				}
				if (alarmMsg != "") {
					itemSb.append(alarmMsg);
					alarmMsg = "";
					shareConfig.downQuotMovement.removeAll(alarmValue);
				}

				if (itemSb.length() > 0) {
					sb.append(map.get("quotName")).append("[").append(map.get("quotCode")).append("]").append(":")
							.append(itemSb);
					financingDao.modifyAlarmShares(GsonUtil.toJson(shareConfig), 0, item.get("id"));
				}
			}
		}

		return sb.toString();

	}

	private static Map<String, String> getQuotInfo(String shareCode) {
		Map<String, String> map = new HashMap<String, String>();
		Calendar cal = Calendar.getInstance();
		double hour = cal.get(Calendar.HOUR_OF_DAY) + cal.get(Calendar.MINUTE) / 60.0;
		if ((hour >= 9.5 && hour < 11.5) || (hour >= 13 && hour <= 15)) {
			String content = HttpUtil.get("http://web.sqt.gtimg.cn/q=" + shareCode + "?r=0.6450336522583517", null, 5000,
					5000, "GBK");
			String result = content.substring(content.indexOf("\"") + 1, content.length() - 2);
			if (result.contains("|")) {
				String[] array = result.split("\\|");

				String[] temp = array[0].split("~");
				map.put("quotName", temp[1]);
				map.put("quotCode", temp[2]);

				String lastPrice = array[0].substring(array[0].lastIndexOf("~") + 1);
				temp = lastPrice.split("\\/");
				map.put("quotTime", temp[0]);
				map.put("quotPrice", temp[1]);

				String lastMovement = array[array.length - 1].substring(array[array.length - 1].indexOf("~") + 1);
				temp = lastMovement.split("~");
				map.put("quotMovement", temp[2]);
			}
		}
		return map;
	}

	private static class ShareConfig {
		public List<Double> riseQuotPrice;
		public List<Double> riseQuotMovement;
		public List<Double> downQuotPrice;
		public List<Double> downQuotMovement;
	}

}
