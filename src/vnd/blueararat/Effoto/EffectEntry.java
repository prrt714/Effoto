package vnd.blueararat.Effoto;

import android.graphics.drawable.Drawable;

public class EffectEntry {

	public Class<? extends Effect> getEffect() {
		return ef;
	}

	public Drawable getIcon() {
		return icon;
	}

	private String name;
	private Drawable icon;
	private Class<? extends Effect> ef;

	public EffectEntry(String name, Class ef, Drawable icon) {
		this.name = name;
		this.ef = ef;
		this.icon = icon;
	}

	@Override
	public String toString() {
		return name;
	}
}
