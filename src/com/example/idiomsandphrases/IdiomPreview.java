package com.example.idiomsandphrases;

public class IdiomPreview {
	private String[] idiomData = null;
	private int index;
	private String languageName;
	private String categoryName;

	public String getLanguageName() {
		return languageName;
	}

	public void setLanguageName(String languageName) {
		this.languageName = languageName;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public IdiomPreview(String[] paramArrayOfString) {
		this.idiomData = paramArrayOfString;
	}

	public String getIdiomName() {
		return this.idiomData[0];
	}

	public int getIndex() {
		return this.index;
	}

	public String getMeaning() {
		return this.idiomData[1];
	}

	public String getSample() {
		return this.idiomData[2];
	}

	public void setIndex(int paramInt) {
		this.index = paramInt;
	}

	public String toString() {
		return this.idiomData[0];
	}
	
	
}

/*
 * Location: C:\Users\selcuk.yayla\Desktop\decompile\ornekidiomlar.jar
 * 
 * Qualified Name: com.ani.apps.idioms.and.phrases.IdiomPreview
 * 
 * JD-Core Version: 0.7.0.1
 */