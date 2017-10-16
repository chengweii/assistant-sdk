package com.weihua.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * 日期工具类
 * 
 */
public class DateUtil {

	// 默认日期格式
	public static final String DATE_DEFAULT_FORMAT = "yyyy-MM-dd";

	// 默认时间格式
	public static final String DATETIME_DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

	public static final String TIME_DEFAULT_FORMAT = "HH:mm:ss";

	// 日期格式化
	private static ThreadLocal<DateFormat> dateFormat = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat(DATE_DEFAULT_FORMAT);
		}
	};

	// 时间格式化
	private static ThreadLocal<DateFormat> dateTimeFormat = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat(DATETIME_DEFAULT_FORMAT);
		}
	};

	private static ThreadLocal<DateFormat> timeFormat = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat(TIME_DEFAULT_FORMAT);
		}
	};

	public static final String TIME_HH_MM_FORMAT = "HH:mm";

	private static ThreadLocal<DateFormat> timeHHMMFormat = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat(TIME_HH_MM_FORMAT);
		}
	};

	private static ThreadLocal<GregorianCalendar> gregorianCalendar = new ThreadLocal<GregorianCalendar>();

	public static void main(String[] args) throws Exception {
	}

	/**
	 * 日期格式化yyyy-MM-dd
	 * 
	 * @param date
	 * @return
	 */
	public static String getDateFormat(Date date) {
		return dateFormat.get().format(date);
	}

	/**
	 * 日期格式化yyyy-MM-dd HH:mm:ss
	 * 
	 * @param date
	 * @return
	 */
	public static String getDateTimeFormat(Date date) {
		return dateTimeFormat.get().format(date);
	}

	/**
	 * 时间格式化
	 * 
	 * @param date
	 * @return HH:mm:ss
	 */
	public static String getTimeFormat(Date date) {
		return timeFormat.get().format(date);
	}

	/**
	 * 日期格式化
	 * 
	 * @param date
	 * @param 格式类型
	 * @return
	 */
	public static String getDateFormat(Date date, String formatStr) {
		if (!StringUtil.isEmpty(formatStr)) {
			return new SimpleDateFormat(formatStr).format(date);
		}
		return null;
	}

	/**
	 * 日期格式化
	 * 
	 * @param date
	 * @return
	 */
	public static Date getDateFormat(String date) {
		try {
			return dateFormat.get().parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 时间格式化
	 * 
	 * @param date
	 * @return
	 */
	public static Date getDateTimeFormat(String date) {
		try {
			return dateTimeFormat.get().parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Date getDateTimeHHMMFormat(String date) {
		try {
			return timeHHMMFormat.get().parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取当前日期(yyyy-MM-dd)
	 * 
	 * @return
	 */
	public static Date getNowDate() {
		return DateUtil.getDateFormat(dateFormat.get().format(new Date()));
	}

	/**
	 * 获取当前日期(yyyy-MM-dd HH:mm:ss )
	 * 
	 * @return
	 */
	public static Date getNowDateTime() {
		return new Date();
	}

	/**
	 * 获取当前日期星期一日期
	 * 
	 * @return date
	 */
	public static Date getFirstDayOfWeek() {
		gregorianCalendar.get().setFirstDayOfWeek(Calendar.MONDAY);
		gregorianCalendar.get().setTime(new Date());
		gregorianCalendar.get().set(Calendar.DAY_OF_WEEK, gregorianCalendar.get().getFirstDayOfWeek()); // Monday
		return gregorianCalendar.get().getTime();
	}

	/**
	 * 获取当前日期星期日日期
	 * 
	 * @return date
	 */
	public static Date getLastDayOfWeek() {
		gregorianCalendar.get().setFirstDayOfWeek(Calendar.MONDAY);
		gregorianCalendar.get().setTime(new Date());
		gregorianCalendar.get().set(Calendar.DAY_OF_WEEK, gregorianCalendar.get().getFirstDayOfWeek() + 6); // Monday
		return gregorianCalendar.get().getTime();
	}

	/**
	 * 获取日期星期一日期
	 * 
	 * @param 指定日期
	 * @return date
	 */
	public static Date getFirstDayOfWeek(Date date) {
		if (date == null) {
			return null;
		}
		gregorianCalendar.get().setFirstDayOfWeek(Calendar.MONDAY);
		gregorianCalendar.get().setTime(date);
		gregorianCalendar.get().set(Calendar.DAY_OF_WEEK, gregorianCalendar.get().getFirstDayOfWeek()); // Monday
		return gregorianCalendar.get().getTime();
	}

	/**
	 * 获取日期星期一日期
	 * 
	 * @param 指定日期
	 * @return date
	 */
	public static Date getLastDayOfWeek(Date date) {
		if (date == null) {
			return null;
		}
		gregorianCalendar.get().setFirstDayOfWeek(Calendar.MONDAY);
		gregorianCalendar.get().setTime(date);
		gregorianCalendar.get().set(Calendar.DAY_OF_WEEK, gregorianCalendar.get().getFirstDayOfWeek() + 6); // Monday
		return gregorianCalendar.get().getTime();
	}

	/**
	 * 获取当前月的第一天
	 * 
	 * @return date
	 */
	public static Date getFirstDayOfMonth() {
		gregorianCalendar.get().setTime(new Date());
		gregorianCalendar.get().set(Calendar.DAY_OF_MONTH, 1);
		return gregorianCalendar.get().getTime();
	}

	/**
	 * 获取当前月的最后一天
	 * 
	 * @return
	 */
	public static Date getLastDayOfMonth() {
		gregorianCalendar.get().setTime(new Date());
		gregorianCalendar.get().set(Calendar.DAY_OF_MONTH, 1);
		gregorianCalendar.get().add(Calendar.MONTH, 1);
		gregorianCalendar.get().add(Calendar.DAY_OF_MONTH, -1);
		return gregorianCalendar.get().getTime();
	}

	/**
	 * 获取指定月的第一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date getFirstDayOfMonth(Date date) {
		gregorianCalendar.get().setTime(date);
		gregorianCalendar.get().set(Calendar.DAY_OF_MONTH, 1);
		return gregorianCalendar.get().getTime();
	}

	/**
	 * 获取指定月的最后一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date getLastDayOfMonth(Date date) {
		gregorianCalendar.get().setTime(date);
		gregorianCalendar.get().set(Calendar.DAY_OF_MONTH, 1);
		gregorianCalendar.get().add(Calendar.MONTH, 1);
		gregorianCalendar.get().add(Calendar.DAY_OF_MONTH, -1);
		return gregorianCalendar.get().getTime();
	}

	/**
	 * 获取日期前一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date getDayBefore(Date date) {
		gregorianCalendar.get().setTime(date);
		int day = gregorianCalendar.get().get(Calendar.DATE);
		gregorianCalendar.get().set(Calendar.DATE, day - 1);
		return gregorianCalendar.get().getTime();
	}

	/**
	 * 获取日期后一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date getDayAfter(Date date) {
		gregorianCalendar.get().setTime(date);
		int day = gregorianCalendar.get().get(Calendar.DATE);
		gregorianCalendar.get().set(Calendar.DATE, day + 1);
		return gregorianCalendar.get().getTime();
	}

	/**
	 * 获取当前年
	 * 
	 * @return
	 */
	public static int getNowYear() {
		Calendar d = Calendar.getInstance();
		return d.get(Calendar.YEAR);
	}

	/**
	 * 获取当前月份
	 * 
	 * @return
	 */
	public static int getNowMonth() {
		Calendar d = Calendar.getInstance();
		return d.get(Calendar.MONTH) + 1;
	}

	/**
	 * 获取当月天数
	 * 
	 * @return
	 */
	public static int getNowMonthDay() {
		Calendar d = Calendar.getInstance();
		return d.getActualMaximum(Calendar.DATE);
	}

	/**
	 * 获取时间段的每一天
	 * 
	 * @param 开始日期
	 * @param 结算日期
	 * @return 日期列表
	 */
	public static List<Date> getEveryDay(Date startDate, Date endDate) {
		if (startDate == null || endDate == null) {
			return null;
		}
		// 格式化日期(yy-MM-dd)
		startDate = DateUtil.getDateFormat(DateUtil.getDateFormat(startDate));
		endDate = DateUtil.getDateFormat(DateUtil.getDateFormat(endDate));
		List<Date> dates = new ArrayList<Date>();
		gregorianCalendar.get().setTime(startDate);
		dates.add(gregorianCalendar.get().getTime());
		while (gregorianCalendar.get().getTime().compareTo(endDate) < 0) {
			// 加1天
			gregorianCalendar.get().add(Calendar.DAY_OF_MONTH, 1);
			dates.add(gregorianCalendar.get().getTime());
		}
		return dates;
	}

	/**
	 * 获取提前多少个月
	 * 
	 * @param monty
	 * @return
	 */
	public static Date getFirstMonth(int monty) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -monty);
		return c.getTime();
	}

	/**
	 * 比较日期大小
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int compareDate(Date date1, Date date2) {
		if (date1.getTime() > date2.getTime()) {
			return 1;
		} else if (date1.getTime() < date2.getTime()) {
			return -1;
		} else {
			return 0;
		}
	}

	/**
	 * 获取时间差（秒）
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static long getDateDiff(Date date1, Date date2) {
		return (date1.getTime() - date2.getTime()) / 1000;
	}

	public static TimePeriod getTimePeriodByDate(Date date) {
		double hour = Double.valueOf(date.getHours()) + Double.valueOf(date.getMinutes()) / 60d;
		if (hour >= 5 && hour < 8) {
			return TimePeriod.MORNING;
		} else if (hour >= 8 && hour < 11) {
			return TimePeriod.BEFORENOON;
		} else if (hour >= 11 && hour < 13) {
			return TimePeriod.NOON;
		} else if (hour >= 13 && hour < 18) {
			return TimePeriod.AFTERNOON;
		} else if (hour >= 18 && hour < 21) {
			return TimePeriod.NIGHT;
		} else if (hour >= 21 || hour < 5) {
			return TimePeriod.DEEPNIGHT;
		}
		return null;
	}

	public static enum TimePeriod {

		/**
		 * 早晨
		 */
		MORNING("MORNING", "早晨"),

		/**
		 * 上午
		 */
		BEFORENOON("BEFORENOON", "上午"),

		/**
		 * 中午
		 */
		NOON("NOON", "中午"),

		/**
		 * 下午
		 */
		AFTERNOON("AFTERNOON", "下午"),

		/**
		 * 晚上
		 */
		NIGHT("NIGHT", "晚上"),

		/**
		 * 深夜
		 */
		DEEPNIGHT("DEEPNIGHT", "深夜");

		private TimePeriod(String code, String value) {
			this.code = code;
			this.value = value;
		}

		private String code;
		private String value;

		public String getCode() {
			return code;
		}

		public String getValue() {
			return value;
		}

		public static TimePeriod fromCode(String code) {
			for (TimePeriod entity : TimePeriod.values()) {
				if (entity.getCode().equals(code)) {
					return entity;
				}
			}
			return null;
		}
	}

}