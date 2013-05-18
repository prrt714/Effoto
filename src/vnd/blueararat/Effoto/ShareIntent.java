package vnd.blueararat.Effoto;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.Drawable;

public class ShareIntent {

	public ComponentName getCn() {
		return cn;
	}

	public Drawable getIcon() {
		return icon;
	}

//	public Intent getIntent() {
//		return intent;
//	}

	private String name;
	//private Intent intent;
	private Drawable icon;
	private ComponentName cn;
	
//	public ShareIntent(){
//		this.name = null;
//		this.intent = null;
//		this.icon = null;
//	}
	
	public ShareIntent(String name, ComponentName cn, Drawable icon){
		this.name = name;
		this.cn = cn;
		this.icon = icon;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return name;
	}

}
