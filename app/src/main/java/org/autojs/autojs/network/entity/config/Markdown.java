package org.autojs.autojs.network.entity.config;

import com.google.gson.annotations.SerializedName;

public class Markdown{

	@SerializedName("highlight")
	private int highlight;

	@SerializedName("theme")
	private String theme;

	public void setHighlight(int highlight){
		this.highlight = highlight;
	}

	public int getHighlight(){
		return highlight;
	}

	public void setTheme(String theme){
		this.theme = theme;
	}

	public String getTheme(){
		return theme;
	}

	@Override
 	public String toString(){
		return 
			"Markdown{" + 
			"highlight = '" + highlight + '\'' + 
			",theme = '" + theme + '\'' + 
			"}";
		}
}