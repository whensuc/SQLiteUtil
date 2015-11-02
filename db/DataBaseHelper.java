package cn.com.dyg.work.devicecheck.db;



import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 
          
        * 描述:  DataBaseHelper类
        *  
        * @author wzy 
        * @version 1.0  
        * @since 2015年10月30日 上午11:50:09
        * 
 */
public class DataBaseHelper extends SQLiteOpenHelper{
	
	List<String> sqls = new ArrayList<String>();
	private IUpdate iupdate;
	
	public DataBaseHelper(Context context, String name, CursorFactory factory,
			int version,List<String> sqls,IUpdate iupdate) {
		this(context, name, factory, version);
		this.sqls = sqls;
		this.iupdate = iupdate;
	}
	public DataBaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}
	

	@Override
	public void onCreate(SQLiteDatabase db) {
		for(String sql : sqls){
			db.execSQL(sql);
		}
	}
	
	
	/**
	 * 回调接口update方法
	 * update方法最佳实现
	 * switch(oldVersion){
	 * 		case 1:
	 * 			db.execSQL() 版本2改动的sql
	 * 		case 2:
	 * 			db.execSQL() 版本3改动的sql
	 * 		依次类推
	 * }
	 */
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		iupdate.update(oldVersion);
	}

}
