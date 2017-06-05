package com.weihua.database.dao.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import com.weihua.database.dao.HolidayArrangementDao;
import com.weihua.util.DateUtil;

public class HolidayArrangementDaoImpl extends BaseDao implements HolidayArrangementDao {

	private static Logger LOGGER = Logger.getLogger(HolidayArrangementDaoImpl.class);

	@Override
	public boolean findIsHoliday(String holidayDate) {
		String sql = "select * from holiday_arrangement where holiday_date = ?";
		Map<String, Object> result = dBHelper.queryMap(LOGGER, sql, holidayDate);

		if (result != null) {
			return "0".equals(result.get("is_workday"));
		} else {
			Date input = DateUtil.formatDate(holidayDate, "yyyy-MM-dd");
			Calendar cal = Calendar.getInstance();
			cal.setTime(input);
			return cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
					|| cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
		}
	}
}
