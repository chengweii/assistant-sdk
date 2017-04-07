package com.weihua.database.dao.impl;

import com.weihua.util.DBUtil;
import com.weihua.util.DBUtil.DBHelper;

public class BaseDao {
	protected DBHelper dBHelper = DBUtil.getDBHelper();
}
