package com.weihua.database.dao.impl;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.weihua.database.dao.InterviewDao;

public class InterviewDaoImpl extends BaseDao implements InterviewDao {

	private static Logger LOGGER = Logger.getLogger(InterviewDaoImpl.class);

	@Override
	public List<Map<String, Object>> findSubjectList(int beginRowNum, int rowCount) {
		String sql = "select * from interview_subject limit ?,?";
		List<Map<String, Object>> result = dBHelper.queryMapList(LOGGER, sql, beginRowNum, rowCount);
		return result;
	}

	@Override
	public Map<String, Object> findSubjectById(Integer id) {
		String sql = "select * from interview_subject where id=?";
		Map<String, Object> result = dBHelper.queryMap(LOGGER, sql, id);
		return result;
	}

}
