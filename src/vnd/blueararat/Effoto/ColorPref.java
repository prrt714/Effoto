package vnd.blueararat.Effoto;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class ColorPref extends DialogPreference {

	private ColorPickerView mColorPickerView;

	private int mColor;
	private ImageView mImageView;

	public ColorPref(Context context, AttributeSet attrs) {
		super(context, attrs);
		setDialogTitle("");
	}

	@Override
	protected void onBindView(View view) {
		super.onBindView(view);
		mImageView = (ImageView) view.findViewById(R.id.ImageView);
		mImageView.setImageDrawable(getDrawable());
	}

	@Override
	protected View onCreateDialogView() {
		ColorPickerView.setBackColor(mColor);
		View v = super.onCreateDialogView();
		try {
			mColorPickerView = (ColorPickerView) v
					.findViewById(R.id.ColorPickerView);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return v;
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);

		if (positiveResult) {
			int value = mColorPickerView.getColor();
			if (callChangeListener(value)) {
				setColor(value);
				mImageView.setImageDrawable(getDrawable());
			}
		}
	}

	public ColorPickerView getColorPickerView() {
		return mColorPickerView;
	}

	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
		setColor(restoreValue ? getPersistedInt(mColor)
				: (Integer) defaultValue);
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		return a.getInt(index, 0xFFFFFFFF);
	}

	public void setColor(int color) {
		final boolean wasBlocking = shouldDisableDependents();

		mColor = color;

		persistInt(color);
		setSummary("ARGB: #"+Integer.toHexString(color).toUpperCase());
		//BorderEf.sBackgroundColor = mColor;
		final boolean isBlocking = shouldDisableDependents();
		if (isBlocking != wasBlocking) {
			notifyDependencyChange(isBlocking);
		}
	}

	public int getColor() {
		return mColor;
	}

	private Drawable getDrawable() {
		PaintDrawable drawable = new PaintDrawable(mColor);
		drawable.setIntrinsicHeight(32);
		drawable.setIntrinsicWidth(32);
		drawable.setCornerRadius(4);
		return drawable;
	}
}
