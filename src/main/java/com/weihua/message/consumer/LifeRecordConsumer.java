package com.weihua.message.consumer;

import org.apache.log4j.Logger;

import com.google.gson.reflect.TypeToken;
import com.weihua.database.dao.FamilyDao;
import com.weihua.database.dao.impl.FamilyDaoImpl;
import com.weihua.message.MessageConsumer;
import com.weihua.util.GsonUtil;

public class LifeRecordConsumer  implements MessageConsumer {

	private static Logger LOGGER = Logger.getLogger(LifeRecordConsumer.class);
	
	private static FamilyDao familyDao = new FamilyDaoImpl();
	
	@Override
	public void doHandle(String message) {
		LOGGER.info("Recieved message:" + message);
		String[] records = GsonUtil.getEntityFromJson(message, new TypeToken<String[]>() {
		});
		String[][] result = new String[records.length][5];
		for (int i = 0; i < records.length; i++) {
			String[] params = records[i].split("##");
			result[i][0] = params[0];
			result[i][1] = params[2];
			result[i][2] = params[2];
			result[i][3] = params[1];
			result[i][4] = params[3];
		}
		familyDao.syncAllRecord(result);
	}
}
