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
          
        * ����:  SQLite������
        *  
        * @author ��������  
        * @version 1.0  
        * @since 2015��10��30�� ����4:42:48
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
	 	* ����:  �����¼
	    * @param table ������
	    * @param values �� �����ֶμ�ֵ��
	    * @author wzy 2015��10��30�� ����2:50:33
	 */
	public void insert(String table,ContentValues values){
		ifCloseOpen(db);
		db.insert(table, null, values);
	}
	/**
	 * 
	    * ����:  ���¼�¼
	    * @param table ������
	    * @param values �� �����ֶμ�ֵ��
	    * @param whereClause �� where����
	    * @param whereArgs  �� where����ֵ
	    * @author wzy 2015��10��30�� ����2:50:33
	    * 
	    * ��:update("Book",values,"author=? and version=?",new String[]{"xx","xx"})
	 */
	public void update(String table,ContentValues values, String whereClause, String[] whereArgs){
		ifCloseOpen(db);
		db.update(table, values, whereClause, whereArgs);
	}
	/**
	 * 
        * ����:  ɾ����¼
        * @param table : ����
        * @param whereClause ��where����
        * @param whereArgs  : where����ֵ
        * @author wzy 2015��10��30�� ����2:56:34
        * 
        * ����delete("Book","price<?",new String[]{"100"})
	 */
	public void delete(	String table, String whereClause, String[] whereArgs){
		ifCloseOpen(db);
		db.delete(table, whereClause, whereArgs);
	}
	
	/**
	 * 
        * ����:  �����¼����
        * @param table �� ����
        * @param object �� ʵ�������
        * @param vo  ��  Class����
        * @author wzy 2015��11��2�� ����9:02:45
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
        * ����:  ԭ��sql,������ɾ�Ĳ���
        * @param sql �� sql���
        * @param params  : ����
        * @author wzy 2015��10��30�� ����3:03:18
        * 
        * ����execSQL("insert into tablename(col1,col2) values(?,?)",new String[]{"xx","xx"})
	 */
	public void execSQL(String sql,String[] params){
		ifCloseOpen(db);
		db.execSQL(sql, params);	
	}
	/**
	 * 
        * ����:  ��ѯ
        * @param table �� ����
        * @param columns ����ѯ����
        * @param selection ��where����
        * @param selectionArgs : where��������
        * @param groupBy �� ����
        * @param having �� having
        * @param orderBy : order by
        * @author wzy 2015��10��30�� ����4:36:40
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
        * ����:  ԭ��sql��ѯ����ѯ�ṹ�Զ����ʵ����(Ŀǰֻ֧��int,String,Long,double,float,short)
        * @param sql : ԭ��sql���
        * @param params �� ����
        * @param vo �� ʵ����
        * @return  �� ����ʵ���༯��
        * @author wzy 2015��11��2�� ����2:14:54
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
	//��������
	public void beginTransaction(){
		ifCloseOpen(db);
		db.beginTransaction();
	}
	//����ִ�гɹ�
	public void setTransactionSuccessful(){
		ifCloseOpen(db);
		db.setTransactionSuccessful();
	}
	//��������
	public void endTransaction(){
		ifCloseOpen(db);
		db.endTransaction();
	}
}
