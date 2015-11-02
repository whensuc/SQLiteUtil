package cn.com.dyg.work.devicecheck.db;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 
          
        * 描述:  SQLite工具类
        *  
        * @author 作者名字  
        * @version 1.0  
        * @since 2015年10月30日 下午4:42:48
 */
@SuppressLint("DefaultLocale")
public class DataBaseUtil {
	private static DataBaseUtil dataBaseUtil;
	private static DataBaseHelper dataBaseHelper;
	private static SQLiteDatabase db;
	private static final String DATABASENAME="";
	private static final int DATAVERSION = 1;
	private DataBaseUtil(){
	}
	
	public static DataBaseUtil getInstance(Context context,List<String> sqls){
		if(dataBaseUtil==null){
			dataBaseUtil = new DataBaseUtil();
			dataBaseHelper = new DataBaseHelper(context,DATABASENAME,null,DATAVERSION,sqls,new UpdateVersion());
			db = dataBaseHelper.getWritableDatabase();
		}
		return dataBaseUtil;
	}
	/**
	 * 
	 	* 描述:  插入记录
	    * @param table ：表名
	    * @param values ： 插入字段键值对
	    * @author wzy 2015年10月30日 下午2:50:33
	 */
	public void insert(String table,ContentValues values){
		ifCloseOpen(db);
		db.insert(table, null, values);
	}
	/**
	 * 
	    * 描述:  更新记录
	    * @param table ：表名
	    * @param values ： 更新字段键值对
	    * @param whereClause ： where条件
	    * @param whereArgs  ： where条件值
	    * @author wzy 2015年10月30日 下午2:50:33
	    * 
	    * 例:update("Book",values,"author=? and version=?",new String[]{"xx","xx"})
	 */
	public void update(String table,ContentValues values, String whereClause, String[] whereArgs){
		ifCloseOpen(db);
		db.update(table, values, whereClause, whereArgs);
	}
	/**
	 * 
        * 描述:  删除记录
        * @param table : 表名
        * @param whereClause ：where条件
        * @param whereArgs  : where条件值
        * @author wzy 2015年10月30日 下午2:56:34
        * 
        * 例：delete("Book","price<?",new String[]{"100"})
	 */
	public void delete(	String table, String whereClause, String[] whereArgs){
		ifCloseOpen(db);
		db.delete(table, whereClause, whereArgs);
	}
	
	/**
	 * 
        * 描述:  插入记录方法
        * @param table ： 表名
        * @param object ： 实体类对象
        * @param vo  ：  Class对象
        * @author wzy 2015年11月2日 下午9:02:45
	 */
	public <E> void insert(String table,E object,Class<E> vo){
		ifCloseOpen(db);
		ContentValues values = new ContentValues();
		Field[] fields = vo.getDeclaredFields();
		for(Field field : fields){
			String name = field.getName();
			try {
				Method method = vo.getMethod("get"+name.substring(0,1).toUpperCase()+name.substring(1));
				String value = method.invoke(object).toString();
				values.put(name, value);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		db.insert(table, null, values);
	}
	/**
	 * 
        * 描述:  原生sql,用于增删改操作
        * @param sql ： sql语句
        * @param params  : 参数
        * @author wzy 2015年10月30日 下午3:03:18
        * 
        * 例：execSQL("insert into tablename(col1,col2) values(?,?)",new String[]{"xx","xx"})
	 */
	public void execSQL(String sql,String[] params){
		ifCloseOpen(db);
		db.execSQL(sql, params);	
	}
	/**
	 * 
        * 描述:  查询
        * @param table ： 表名
        * @param columns ：查询列名
        * @param selection ：where条件
        * @param selectionArgs : where条件参数
        * @param groupBy ： 排序
        * @param having ： having
        * @param orderBy : order by
        * @author wzy 2015年10月30日 下午4:36:40
	 */
	public void query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy){
		ifCloseOpen(db);
		Cursor cursor = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
		if(cursor.moveToFirst()){
			do{
				
			}while(cursor.moveToNext());
		}
		cursor.close();
	}
	
	/**
	 * 
        * 描述:  原生sql查询，查询结构自动填充实体类(目前只支持int,String,Long,double,float,short)
        * @param sql : 原生sql语句
        * @param params ： 参数
        * @param vo ： 实体类
        * @return  ： 返回实体类集合
        * @author wzy 2015年11月2日 下午2:14:54
	 */
	public <E> List<E> rawQuery(String sql,String[] params,Class<E> vo){
		ifCloseOpen(db);
		List<E> array = new ArrayList<E>();
		Cursor cursor = db.rawQuery(sql, params);
		Field[] fields = vo.getDeclaredFields();
		try {
			E le = vo.newInstance();
			if(cursor.moveToFirst()){
				do{
					for(Field field: fields){
						String name = field.getName();
						String type = field.getGenericType().toString();
						if(type.equals("class java.lang.String")){
							String value = cursor.getString(cursor.getColumnIndex(field.getName()));
							Method method = vo.getMethod("set"+name.substring(0,1).toUpperCase()+name.substring(1),new Class[]{String.class});
							method.invoke(le,value);
						}else if(type.equals("int")){
							int value = cursor.getInt(cursor.getColumnIndex(field.getName()));
							Method method = vo.getMethod("set"+name.substring(0,1).toUpperCase()+name.substring(1),new Class[]{int.class});
							method.invoke(le,value);
						}else if(type.equals("double")){
							double value = cursor.getDouble(cursor.getColumnIndex(field.getName()));
							Method method = vo.getMethod("set"+name.substring(0,1).toUpperCase()+name.substring(1),new Class[]{double.class});
							method.invoke(le,value);
						}else if(type.equals("float")){
							float value = cursor.getFloat(cursor.getColumnIndex(field.getName()));
							Method method = vo.getMethod("set"+name.substring(0,1).toUpperCase()+name.substring(1),new Class[]{float.class});
							method.invoke(le,value);
						}else if(type.equals("long")){
							long value = cursor.getLong(cursor.getColumnIndex(field.getName()));
							Method method = vo.getMethod("set"+name.substring(0,1).toUpperCase()+name.substring(1),new Class[]{long.class});
							method.invoke(le,value);
						}else if(type.equals("short")){
							short value = cursor.getShort(cursor.getColumnIndex(field.getName()));
							Method method = vo.getMethod("set"+name.substring(0,1).toUpperCase()+name.substring(1),new Class[]{short.class});
							method.invoke(le,value);
						}					
					}
					array.add(le);
				}while(cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		cursor.close();
		return array;
	}
	
	public SQLiteDatabase getDb(){
		return db;
	}
	
	private void ifCloseOpen(SQLiteDatabase db){
		if(db==null){
			db = dataBaseHelper.getWritableDatabase();
		}
	}
	//开启事务
	public void beginTransaction(){
		ifCloseOpen(db);
		db.beginTransaction();
	}
	//事务执行成功
	public void setTransactionSuccessful(){
		ifCloseOpen(db);
		db.setTransactionSuccessful();
	}
	//结束事务
	public void endTransaction(){
		ifCloseOpen(db);
		db.endTransaction();
	}
}
