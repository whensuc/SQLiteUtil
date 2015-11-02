package cn.com.dyg.work.devicecheck.db;



import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 
          
        * ����:  DataBaseHelper��
        *  
        * @author wzy 
        * @version 1.0  
        * @since 2015��10��30�� ����11:50:09
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
	 * �ص��ӿ�update����
	 * update�������ʵ��
	 * switch(oldVersion){
	 * 		case 1:
	 * 			db.execSQL() �汾2�Ķ���sql
	 * 		case 2:
	 * 			db.execSQL() �汾3�Ķ���sql
	 * 		��������
	 * }
	 */
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		iupdate.update(oldVersion);
	}

}
