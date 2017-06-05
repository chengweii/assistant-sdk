package com.weihua.message.consumer;

import org.apache.log4j.Logger;

import com.google.gson.reflect.TypeToken;
import com.weihua.database.dao.LifeMotoDao;
import com.weihua.database.dao.impl.LifeMotoDaoImpl;
import com.weihua.message.MessageConsumer;
import com.weihua.util.GsonUtil;

public class LifeMotoConsumer  implements MessageConsumer {

	private static Logger LOGGER = Logger.getLogger(LifeMotoConsumer.class);
	
	private static LifeMotoDao lifeMotoDao = new LifeMotoDaoImpl();
	
	@Override
	public void doHandle(String message) {
		LOGGER.info("Recieved message:" + message);
		String[] records = GsonUtil.getEntityFromJson(message, new TypeToken<String[]>() {
		});
		String[][] result = new String[records.length][1];
		for (int i = 0; i < records.length; i++) {
			String[] params = records[i].split("##");
			result[i][0] = params[0];
		}
		lifeMotoDao.syncAllRecord(result);
	}
}
