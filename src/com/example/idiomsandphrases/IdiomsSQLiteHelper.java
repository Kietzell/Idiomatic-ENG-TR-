package com.example.idiomsandphrases;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.Vector;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class IdiomsSQLiteHelper extends SQLiteOpenHelper{
	private Activity mContext;
	private static int VERSION_INT = 1;
	public static String TABLE_LANGUAGES = "langtable";
	public static String COLUMN_LANGUAGE_NAME = "langname";
	
	public static String TABLE_CATEGORY = "cattable";
	public static String COLUMN_CATEGORY_NAME = "catname";

	public static String TABLE_IDIOMS = "idiomtable";
	public static String COLUMN_IDIOM_NAME = "idiomname";
	public static String COLUMN_IDIOM_MEANING = "idiommeaning";
	public static String COLUMN_IDIOM_EXAMPLE = "idiomexample";
	
	private Map catFileMap;
	
	IdiomsSQLiteHelper(Activity context){
		super(context, "langdb", null, VERSION_INT);
		this.mContext = context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// Dil tablosunda dil adý UNIQUE seçildi. Olan bir isim eklenmeye çalýþtýðýnda yenisiyle eskisi deðiþecek
		String createLanguageTable = "CREATE TABLE IF NOT EXISTS "+TABLE_LANGUAGES
				+"("+COLUMN_LANGUAGE_NAME+" TEXT(50) NOT NULL UNIQUE);";
		db.execSQL(createLanguageTable);
		
		

		// Kategori tablosunda dil adý, kategori adý ikiliisi UNIQUE seçildi. Olan bir dil, kategori ikilisi eklenmeye çalýþtýðýnda yenisiyle eskisi deðiþecek
		String createCategoryTable = "CREATE TABLE IF NOT EXISTS "+TABLE_CATEGORY
				+"("+COLUMN_LANGUAGE_NAME+" TEXT(50), " +COLUMN_CATEGORY_NAME +" TEXT(50), "
						+ "UNIQUE ("+COLUMN_LANGUAGE_NAME+","+ COLUMN_CATEGORY_NAME+") ON CONFLICT REPLACE);";
		db.execSQL(createCategoryTable);
		
		
		// Deyimler tablosunda dil adý, kategori adý, deyim adý üçlüsü UNIQUE seçildi. 
		// Olan bir dil, kategori, deyim üçlüsü eklenmeye çalýþtýðýnda yenisiyle eskisi deðiþecek
		String createidiomsTable = "CREATE TABLE IF NOT EXISTS "+TABLE_IDIOMS
				+"("+COLUMN_LANGUAGE_NAME+" TEXT(50), "+COLUMN_CATEGORY_NAME +" TEXT(50), "+COLUMN_IDIOM_NAME+" TEXT(50), "
				+ COLUMN_IDIOM_MEANING +" TEXT(100), " + COLUMN_IDIOM_EXAMPLE+" TEXT(200),"
				+ "UNIQUE ("+COLUMN_LANGUAGE_NAME+","+ COLUMN_CATEGORY_NAME+","+COLUMN_IDIOM_NAME+") ON CONFLICT REPLACE);";
		db.execSQL(createidiomsTable);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String deleteLanguageTable = "DROP TABLE IF EXISTS "+TABLE_LANGUAGES+";";
		db.execSQL(deleteLanguageTable);
		String deleteCategoryTable = "DROP TABLE IF EXISTS "+TABLE_CATEGORY+";";
		db.execSQL(deleteCategoryTable);
		String deleteidiomsTable = "DROP TABLE IF EXISTS "+TABLE_IDIOMS+";";
		db.execSQL(deleteidiomsTable);
		onCreate(db);
		
	}
	
	

}