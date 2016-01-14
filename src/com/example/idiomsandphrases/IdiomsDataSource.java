package com.example.idiomsandphrases;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class IdiomsDataSource {

	private SQLiteDatabase database;
	private IdiomsSQLiteHelper dbHelper;
	private static IdiomsDataSource instance = null;
	private Activity context = null;
	private String[] idiomCategoryNames = null;

	public IdiomsDataSource(Activity context) {
		dbHelper = new IdiomsSQLiteHelper(context);
		this.context = context;
		instance = this;
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}
	
	public SQLiteDatabase getDatabase(){
		return database;
	}

	public static IdiomsDataSource getInstance(Activity context) {
		if (instance == null) {
			instance = new IdiomsDataSource(context);
		}
		return instance;
	}

	public String[] getLanguageNames() {
		Cursor c = database.rawQuery("SELECT "
				+ IdiomsSQLiteHelper.COLUMN_LANGUAGE_NAME + " FROM "
				+ IdiomsSQLiteHelper.TABLE_LANGUAGES + " ORDER BY "
				+ IdiomsSQLiteHelper.COLUMN_LANGUAGE_NAME + " COLLATE NOCASE",
				null);
		String[] langNames = new String[c.getCount()];
		int counter = 0;
		while (c.moveToNext()) {
			langNames[counter] = c.getString(0);
			counter++;
		}
		c.close();
		return langNames;
	}

	public String[] getIdiomCategoryNames(String languageName) {
		Cursor c = database.rawQuery("SELECT "
				+ IdiomsSQLiteHelper.COLUMN_CATEGORY_NAME + " FROM "
				+ IdiomsSQLiteHelper.TABLE_CATEGORY + " WHERE "
				+ IdiomsSQLiteHelper.COLUMN_LANGUAGE_NAME + " = \"" + languageName
				+ "\" ORDER BY " + IdiomsSQLiteHelper.COLUMN_CATEGORY_NAME
				+ " COLLATE NOCASE", null);
		String[] categoryNames = new String[c.getCount()];
		int counter = 0;
		while (c.moveToNext()) {
			categoryNames[counter] = c.getString(0);
			counter++;
		}
		c.close();
		return categoryNames;
	}


	public List<IdiomPreview> getIdiomsForCategory(String languageName, String categoryName) {
		List<IdiomPreview> idiomPreviewList = new ArrayList<IdiomPreview>();
		Cursor c = database.rawQuery("SELECT "
				+ IdiomsSQLiteHelper.COLUMN_LANGUAGE_NAME + ","
				+ IdiomsSQLiteHelper.COLUMN_CATEGORY_NAME + ","
				+ IdiomsSQLiteHelper.COLUMN_IDIOM_NAME + ","
				+ IdiomsSQLiteHelper.COLUMN_IDIOM_MEANING + ","
				+ IdiomsSQLiteHelper.COLUMN_IDIOM_EXAMPLE + " FROM "
				+ IdiomsSQLiteHelper.TABLE_IDIOMS + " WHERE "
				+ IdiomsSQLiteHelper.COLUMN_LANGUAGE_NAME + " = \"" + languageName
				+ "\" AND " + IdiomsSQLiteHelper.COLUMN_CATEGORY_NAME + " = \""
				+ categoryName + "\" ORDER BY "
				+ IdiomsSQLiteHelper.COLUMN_IDIOM_NAME + " COLLATE NOCASE",
				null);
		int counter = 0;
		while (c.moveToNext()) {
			String[] data = { c.getString(2), c.getString(3), c.getString(4) };
			IdiomPreview idiomPreview = new IdiomPreview(data);
			idiomPreview.setLanguageName(c.getString(0));
			idiomPreview.setCategoryName(c.getString(1));
			idiomPreview.setIndex(counter);
			idiomPreviewList.add(idiomPreview);
			counter++;
		}
		c.close();

		return idiomPreviewList;
	}

	public void addLanguage(String languageName) {
		if (languageName == null || languageName.length() == 0) {
			return;
		}
		ContentValues values = new ContentValues();
		values.put(IdiomsSQLiteHelper.COLUMN_LANGUAGE_NAME, languageName);
		database.insert(IdiomsSQLiteHelper.TABLE_LANGUAGES, null, values);
		if(IdiomsNetworkManager.hasActiveInternetConnection(context)) {
			ParseQuery<ParseObject> languageQuery = ParseQuery.getQuery(IdiomsSQLiteHelper.TABLE_LANGUAGES);
			languageQuery.whereEqualTo(IdiomsSQLiteHelper.COLUMN_LANGUAGE_NAME, languageName);
			try {
				List<ParseObject>languages = languageQuery.find();
				ParseObject.deleteAll(languages);
			} catch (Exception e) {
				e.printStackTrace();
			}
			ParseObject languageObj = new ParseObject(IdiomsSQLiteHelper.TABLE_LANGUAGES);
			languageObj.put(IdiomsSQLiteHelper.COLUMN_LANGUAGE_NAME, languageName);
			try {
				languageObj.save();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}

	public void deleteLanguage(String languageName) {
		//Deleting language, categories under it and all idioms under all categories.
		database.delete(IdiomsSQLiteHelper.TABLE_LANGUAGES,
				IdiomsSQLiteHelper.COLUMN_LANGUAGE_NAME + "=\"" + languageName+"\"", null);
		database.delete(IdiomsSQLiteHelper.TABLE_CATEGORY,
				IdiomsSQLiteHelper.COLUMN_LANGUAGE_NAME + "=\"" + languageName+"\"", null);
		database.delete(IdiomsSQLiteHelper.TABLE_IDIOMS,
				IdiomsSQLiteHelper.COLUMN_LANGUAGE_NAME + "=\"" + languageName+"\"", null);
		//Syncing with cloud
		if(IdiomsNetworkManager.hasActiveInternetConnection(context)) {
			ParseQuery<ParseObject> languageQuery = ParseQuery.getQuery(IdiomsSQLiteHelper.TABLE_LANGUAGES);
			languageQuery.whereEqualTo(IdiomsSQLiteHelper.COLUMN_LANGUAGE_NAME, languageName);
			try {
				List<ParseObject>languages = languageQuery.find();
				ParseObject.deleteAll(languages);
			} catch (Exception e) {
				e.printStackTrace();
			}
			ParseQuery<ParseObject> categoryQuery = ParseQuery.getQuery(IdiomsSQLiteHelper.TABLE_CATEGORY);
			categoryQuery.whereEqualTo(IdiomsSQLiteHelper.COLUMN_LANGUAGE_NAME, languageName);
			try {
				List<ParseObject> categories = categoryQuery.find();
				ParseObject.deleteAll(categories);
			} catch (Exception e) {
				e.printStackTrace();
			}
			ParseQuery<ParseObject> idiomsQuery = ParseQuery.getQuery(IdiomsSQLiteHelper.TABLE_IDIOMS);
			idiomsQuery.whereEqualTo(IdiomsSQLiteHelper.COLUMN_LANGUAGE_NAME, languageName);
			try {
				List<ParseObject> objects = idiomsQuery.find();
				ParseObject.deleteAll(objects);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}

	public void addCategory(String languageName, String categoryName) {
		if (categoryName == null || categoryName.length() == 0) {
			return;
		}
		ContentValues values = new ContentValues();
		values.put(IdiomsSQLiteHelper.COLUMN_LANGUAGE_NAME, languageName);
		values.put(IdiomsSQLiteHelper.COLUMN_CATEGORY_NAME, categoryName);
		database.insert(IdiomsSQLiteHelper.TABLE_CATEGORY, null, values);
		
		ParseObject categoryObj = new ParseObject(IdiomsSQLiteHelper.TABLE_CATEGORY);
		categoryObj.put(IdiomsSQLiteHelper.COLUMN_LANGUAGE_NAME, languageName);
		categoryObj.put(IdiomsSQLiteHelper.COLUMN_CATEGORY_NAME, categoryName);
		if(IdiomsNetworkManager.hasActiveInternetConnection(context)) {
			ParseQuery<ParseObject> categoryQuery = ParseQuery.getQuery(IdiomsSQLiteHelper.TABLE_CATEGORY);
			categoryQuery.whereEqualTo(IdiomsSQLiteHelper.COLUMN_LANGUAGE_NAME, languageName);
			categoryQuery.whereEqualTo(IdiomsSQLiteHelper.COLUMN_CATEGORY_NAME, categoryName);
			try {
				List<ParseObject> categories = categoryQuery.find();
				ParseObject.deleteAll(categories);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				categoryObj.save();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}

	public void deleteCategory(String languageName, String categoryName) {
		//Deleting category and all idioms under it.
		database.execSQL("DELETE FROM "+IdiomsSQLiteHelper.TABLE_CATEGORY+" WHERE "
				+IdiomsSQLiteHelper.COLUMN_CATEGORY_NAME + "=\"" + categoryName+"\" AND "
				+IdiomsSQLiteHelper.COLUMN_LANGUAGE_NAME + "=\"" + languageName+"\";");
		database.execSQL("DELETE FROM "+IdiomsSQLiteHelper.TABLE_IDIOMS+" WHERE "
				+IdiomsSQLiteHelper.COLUMN_CATEGORY_NAME + "=\"" + categoryName+"\" AND "
				+IdiomsSQLiteHelper.COLUMN_LANGUAGE_NAME + "=\"" + languageName+"\";");
		//Syncing with cloud
		if(IdiomsNetworkManager.hasActiveInternetConnection(context)) {
			ParseQuery<ParseObject> categoryQuery = ParseQuery.getQuery(IdiomsSQLiteHelper.TABLE_CATEGORY);
			categoryQuery.whereEqualTo(IdiomsSQLiteHelper.COLUMN_LANGUAGE_NAME, languageName);
			categoryQuery.whereEqualTo(IdiomsSQLiteHelper.COLUMN_CATEGORY_NAME, categoryName);
			try {
				List<ParseObject> categories = categoryQuery.find();
				ParseObject.deleteAll(categories);
			} catch (Exception e) {
				e.printStackTrace();
			}
			ParseQuery<ParseObject> idiomsQuery = ParseQuery.getQuery(IdiomsSQLiteHelper.TABLE_IDIOMS);
			idiomsQuery.whereEqualTo(IdiomsSQLiteHelper.COLUMN_LANGUAGE_NAME, languageName);
			idiomsQuery.whereEqualTo(IdiomsSQLiteHelper.COLUMN_CATEGORY_NAME, categoryName);
			try {
				List<ParseObject> objects = idiomsQuery.find();
				ParseObject.deleteAll(objects);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}

	public void addIdiom(String languageName, String categoryName, String idiom,
			String meaning, String example) {
		if (idiom == null || idiom.length() == 0) {
			return;
		}
		if (meaning == null || meaning.length() == 0) {
			return;
		}
		if (example == null || example.length() == 0) {
			return;
		}
		ContentValues values = new ContentValues();
		values.put(IdiomsSQLiteHelper.COLUMN_LANGUAGE_NAME, languageName);
		values.put(IdiomsSQLiteHelper.COLUMN_CATEGORY_NAME, categoryName);
		values.put(IdiomsSQLiteHelper.COLUMN_IDIOM_NAME, idiom);
		values.put(IdiomsSQLiteHelper.COLUMN_IDIOM_MEANING, meaning);
		values.put(IdiomsSQLiteHelper.COLUMN_IDIOM_EXAMPLE, example);
		database.insert(IdiomsSQLiteHelper.TABLE_IDIOMS, null, values);
		if(IdiomsNetworkManager.hasActiveInternetConnection(context)) {
			ParseQuery<ParseObject> idiomsQuery = ParseQuery.getQuery(IdiomsSQLiteHelper.TABLE_IDIOMS);
			idiomsQuery.whereEqualTo(IdiomsSQLiteHelper.COLUMN_LANGUAGE_NAME, languageName);
			idiomsQuery.whereEqualTo(IdiomsSQLiteHelper.COLUMN_CATEGORY_NAME, categoryName);
			idiomsQuery.whereEqualTo(IdiomsSQLiteHelper.COLUMN_IDIOM_NAME, idiom);
	
			try {
				List<ParseObject> objects = idiomsQuery.find();
				ParseObject.deleteAll(objects);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			ParseObject idiomObj = new ParseObject(IdiomsSQLiteHelper.TABLE_IDIOMS);
			idiomObj.put(IdiomsSQLiteHelper.COLUMN_LANGUAGE_NAME, languageName);
			idiomObj.put(IdiomsSQLiteHelper.COLUMN_CATEGORY_NAME, categoryName);
			idiomObj.put(IdiomsSQLiteHelper.COLUMN_IDIOM_NAME, idiom);
			idiomObj.put(IdiomsSQLiteHelper.COLUMN_IDIOM_MEANING, meaning);
			idiomObj.put(IdiomsSQLiteHelper.COLUMN_IDIOM_EXAMPLE, example);
			try {
				idiomObj.save();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}

	public int editIdiom(String idiomToChange, String languageName, String categoryName,
			String idiom, String meaning, String example) {
		database.execSQL("UPDATE "+IdiomsSQLiteHelper.TABLE_IDIOMS+" SET "+IdiomsSQLiteHelper.COLUMN_IDIOM_NAME+" = \""+idiom+"\","
				+IdiomsSQLiteHelper.COLUMN_IDIOM_MEANING+" = \""+meaning+"\","+IdiomsSQLiteHelper.COLUMN_IDIOM_EXAMPLE+" = \""+example
				+"\" WHERE "+IdiomsSQLiteHelper.COLUMN_CATEGORY_NAME + "=\"" + categoryName+"\" AND "
				+IdiomsSQLiteHelper.COLUMN_IDIOM_NAME + "=\"" + idiomToChange+"\" AND "
				+IdiomsSQLiteHelper.COLUMN_LANGUAGE_NAME + "=\"" + languageName+"\";");
		int newIndex = 0;
		List<IdiomPreview> newList = getIdiomsForCategory(languageName, categoryName);
		for (int i = 0; i < newList.size(); i++) {
			IdiomPreview idiomPreview = newList.get(i);
			if(idiomPreview.getIdiomName().contentEquals(idiom)) {
				newIndex = i;
				break;
			}
		}
		deleteIdiom(languageName, categoryName, idiomToChange);
		addIdiom(languageName, categoryName, idiom, meaning, example);
		return newIndex;
	}

	public void deleteIdiom(String languageName, String categoryName, String idiom) {
		database.execSQL("DELETE FROM "+IdiomsSQLiteHelper.TABLE_IDIOMS+" WHERE "
				+IdiomsSQLiteHelper.COLUMN_CATEGORY_NAME + "=\"" + categoryName+"\" AND "
				+IdiomsSQLiteHelper.COLUMN_LANGUAGE_NAME + "= \"" + languageName+"\" AND "
				+IdiomsSQLiteHelper.COLUMN_IDIOM_NAME + "= \"" + idiom+"\";");
		if(IdiomsNetworkManager.hasActiveInternetConnection(context)) {
			ParseQuery<ParseObject> idiomsQuery = ParseQuery.getQuery(IdiomsSQLiteHelper.TABLE_IDIOMS);
			idiomsQuery.whereEqualTo(IdiomsSQLiteHelper.COLUMN_LANGUAGE_NAME, languageName);
			idiomsQuery.whereEqualTo(IdiomsSQLiteHelper.COLUMN_CATEGORY_NAME, categoryName);
			idiomsQuery.whereEqualTo(IdiomsSQLiteHelper.COLUMN_IDIOM_NAME, idiom);
	
			try {
				List<ParseObject> objects = idiomsQuery.find();
				ParseObject.deleteAll(objects);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}
}
