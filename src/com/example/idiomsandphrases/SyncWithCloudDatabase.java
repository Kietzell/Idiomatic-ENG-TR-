package com.example.idiomsandphrases;

import java.util.List;

import android.database.sqlite.SQLiteDatabase;

import com.parse.ParseObject;
import com.parse.ParseQuery;

public class SyncWithCloudDatabase {

	public static void setUpDatabase(SQLiteDatabase database){
		ParseQuery<ParseObject> languageQuery = ParseQuery.getQuery(IdiomsSQLiteHelper.TABLE_LANGUAGES);
		languageQuery.orderByAscending(IdiomsSQLiteHelper.COLUMN_LANGUAGE_NAME);
		List<ParseObject> languages = null;
		try {
			languages = languageQuery.find();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(languages != null) {
			setUpLanguageTable(database, languages);
		}

		ParseQuery<ParseObject> categoryQuery = ParseQuery.getQuery(IdiomsSQLiteHelper.TABLE_CATEGORY);
		categoryQuery.orderByAscending(IdiomsSQLiteHelper.COLUMN_CATEGORY_NAME);
		List<ParseObject> categories = null;
		try {
			categories = categoryQuery.find();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(categories != null) {
			setUpCategoryTable(database, categories);
		}
		
		ParseQuery<ParseObject> idiomsQuery = ParseQuery.getQuery(IdiomsSQLiteHelper.TABLE_IDIOMS);
		idiomsQuery.orderByAscending(IdiomsSQLiteHelper.COLUMN_IDIOM_NAME);
		idiomsQuery.setLimit(Integer.MAX_VALUE);
		List<ParseObject> idioms = null;
		try {
			idioms = idiomsQuery.find();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(idioms != null) {
			setUpIdiomsTable(database, idioms);
		}
		
	}
	
	private static void setUpLanguageTable(SQLiteDatabase database, List<ParseObject> languages) {
		String[] columns = {IdiomsSQLiteHelper.COLUMN_LANGUAGE_NAME};
		for (int i = 0; i < languages.size(); i++) {
			ParseObject parseObject = languages.get(i);
			String languageName = parseObject.getString(IdiomsSQLiteHelper.COLUMN_LANGUAGE_NAME);
			String[] values = {languageName};
			String sql = generateInsertQuery(IdiomsSQLiteHelper.TABLE_LANGUAGES, columns, values);
			database.execSQL(sql);
		}
	}
	private static void setUpCategoryTable(SQLiteDatabase database, List<ParseObject> categories) {
		String[] columns = {IdiomsSQLiteHelper.COLUMN_LANGUAGE_NAME, IdiomsSQLiteHelper.COLUMN_CATEGORY_NAME};
		for (int i = 0; i < categories.size(); i++) {
			ParseObject parseObject = categories.get(i);
			String languageName = parseObject.getString(IdiomsSQLiteHelper.COLUMN_LANGUAGE_NAME);
			String categoryName = parseObject.getString(IdiomsSQLiteHelper.COLUMN_CATEGORY_NAME);
			String[] values = {languageName, categoryName};
			String sql = generateInsertQuery(IdiomsSQLiteHelper.TABLE_CATEGORY, columns, values);
			database.execSQL(sql);
		}
	}
	private static void setUpIdiomsTable(SQLiteDatabase database, List<ParseObject> idioms) {
		System.out.println("--------------------------------- idiom table: "+idioms.size());
		String[] columns = {IdiomsSQLiteHelper.COLUMN_LANGUAGE_NAME, IdiomsSQLiteHelper.COLUMN_CATEGORY_NAME, 
				IdiomsSQLiteHelper.COLUMN_IDIOM_NAME, IdiomsSQLiteHelper.COLUMN_IDIOM_MEANING, IdiomsSQLiteHelper.COLUMN_IDIOM_EXAMPLE};
		for (int i = 0; i < idioms.size(); i++) {
			ParseObject parseObject = idioms.get(i);
			String languageName = parseObject.getString(IdiomsSQLiteHelper.COLUMN_LANGUAGE_NAME);
			String categoryName = parseObject.getString(IdiomsSQLiteHelper.COLUMN_CATEGORY_NAME);
			String idiomName = parseObject.getString(IdiomsSQLiteHelper.COLUMN_IDIOM_NAME);
			String idiomMeaning = parseObject.getString(IdiomsSQLiteHelper.COLUMN_IDIOM_MEANING);
			String idiomExample = parseObject.getString(IdiomsSQLiteHelper.COLUMN_IDIOM_EXAMPLE);
			String[] values = {languageName, categoryName, idiomName, idiomMeaning, idiomExample};
			String sql = generateInsertQuery(IdiomsSQLiteHelper.TABLE_IDIOMS, columns, values);
			database.execSQL(sql);
		}
	}
	
	
	private static String generateInsertQuery(String tableName, String[] columns, String[] values) {
		if(columns.length < values.length) {
			return ";";
		}
		String query = "INSERT OR REPLACE INTO "+tableName+" (";
		for (int i = 0; i < columns.length; i++) {
			query += columns[i];
			if(i == columns.length - 1) {
				query += ") ";
			} else {
				query += ", ";
			}
		}
		query += "VALUES(";
		for (int i = 0; i < values.length; i++) {
			query += "\""+values[i].replace("\"","")+"\"";
			if(i == values.length - 1) {
				query += ");";
			} else {
				query += ", ";
			}
		}
		return query;
	}
	
}
