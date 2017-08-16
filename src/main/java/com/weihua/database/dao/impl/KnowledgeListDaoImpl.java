package com.weihua.database.dao.impl;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.weihua.database.dao.KnowledgeListDao;

public class KnowledgeListDaoImpl extends BaseDao implements KnowledgeListDao {

	private static Logger LOGGER = Logger.getLogger(KnowledgeListDaoImpl.class);

	@Override
	public List<Map<String, Object>> findRecordList() {
		String sql = "select * from knowledge_list order by important_level desc";
		List<Map<String, Object>> result = dBHelper.queryMapList(LOGGER, sql);
		return result;
	}

}
