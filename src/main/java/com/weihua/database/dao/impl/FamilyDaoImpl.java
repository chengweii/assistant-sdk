package com.weihua.database.dao.impl;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.weihua.database.dao.FamilyDao;

public class FamilyDaoImpl extends BaseDao implements FamilyDao {

	private static Logger LOGGER = Logger.getLogger(FamilyDaoImpl.class);

	@Override
	public List<Map<String, Object>> findRecordListByWord(String word) {
		String sql = "select * from life_record where record_title like ?";
		List<Map<String, Object>> result = dBHelper.queryMapList(LOGGER, sql,"%"+word+"%");
		return result;
	}

	@Override
	public Map<String, Object> findRecordById(Integer id) {
		String sql = "select * from life_record where id=?";
		Map<String, Object> result = dBHelper.queryMap(LOGGER, sql,id);
		return result;
	}

	@Override
	public int modifyRecord(Object... params) {
		String sql = "insert into life_record (type_name,record_title,record_content,record_time,optimization) values(?,?,?,?,?)";
		if(params.length > 5){
			sql = "update life_record set type_name=?,record_title=?,record_content=?,record_time=?,optimization=? where id=?";
		}
		return dBHelper.queryUpdate(LOGGER, sql, params);
	}
	
	@Override
	public List<Map<String, Object>> findRecordListByTime(String timeBegin,String timeEnd) {
		String sql = "select * from life_record where record_time >= ? and record_time <= ? order by record_time asc";
		List<Map<String, Object>> result = dBHelper.queryMapList(LOGGER, sql,timeBegin,timeEnd);
		return result;
	}

}
