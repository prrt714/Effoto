package vnd.blueararat.Effoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.widget.ToggleButton;

public class CustomToggleButton extends ToggleButton {

	public static int index = 1;
	private int mPosition = 0;
	private StateListDrawable mStateListDrawable;
	private static Bitmap bitmap;
	private final static int COLOR_COUNT = 100;

	public CustomToggleButton(Context context) {
		this(context, null);
	}

	public CustomToggleButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (bitmap == null)
			makeBitmap();
	}

	public CustomToggleButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		if (bitmap == null)
			makeBitmap();

		// mStateListDrawable = setgetBackground()
	}

	public int getPosition() {
		return mPosition;
	}

	public void setPosition(int position) {
		mPosition = position;
	}

	@Override
	public void setChecked(boolean checked) {
		if (checked) {
			mPosition = index;
			index++;
		} else {
			mPosition = 0;
		}
		super.setChecked(checked);
		// Toast.makeText(getContext(), "" + mPosition, 0).show();
		// ColorDrawable dr = new ColorDrawable(mColors[index]);
		if (checked) {
			// setBackgroundColor(bitmap.getPixel(mPosition % COLOR_COUNT, 0));
			BitmapDrawable bd = new BitmapDrawable(getContext().getResources(),
					bitmap);
			setBackgroundDrawable(bd);
		} else {
			setBackgroundColor(Color.TRANSPARENT);
		}
	}

	int[] mColors = new int[] { 0xFFFF0000, 0xFFFF00FF, 0xFF0000FF, 0xFF00FFFF,
			0xFF00FF00, 0xFFFFFF00, 0xFFFF0000 };

	// @Override
	// public void setBackgroundDrawable(Drawable d) {
	// //ColorDrawable dr = new ColorDrawable(mColors[index]);
	// super.setBackgroundDrawable(d);
	// setBackgroundColor(mColors[index]);
	// }

	private static void makeBitmap() {
		int[] colors = new int[] { 0xFFFF0000, 0xFFFF00FF, 0xFF0000FF,
				0xFF00FFFF, 0xFF00FF00, 0xFFFFFF00 };

		LinearGradient gradient = new LinearGradient(0, 0, COLOR_COUNT, 0,
				colors, null, android.graphics.Shader.TileMode.CLAMP);
		Paint p = new Paint();
		// p.setDither(true);
		p.setShader(gradient);

		bitmap = Bitmap.createBitmap(COLOR_COUNT, 1, Config.RGB_565);
		Canvas c = new Canvas(bitmap);
		c.drawLine(0, 0, COLOR_COUNT, 0, p);
	}

}
