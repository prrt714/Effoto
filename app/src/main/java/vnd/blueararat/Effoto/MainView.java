package vnd.blueararat.Effoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;

import java.util.Random;

public class MainView extends android.support.v7.widget.AppCompatImageView {

	private static final int IMAGE_RESOURCE = getRandomImageResource();

	public MainView(Context context) {
		super(context);
		setImageResource(IMAGE_RESOURCE);
	}

	public MainView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MainView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setImageResource(IMAGE_RESOURCE);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		RectF viewRect = new RectF(0, 0, w, h);
		((MainActivity) getContext()).OnSizeChanged(viewRect);
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		BitmapDrawable bd = new BitmapDrawable(getContext().getResources(), bm);
		bd.setFilterBitmap(false);
		setImageDrawable(bd);
	}

	public static int getImageResource() {
		return IMAGE_RESOURCE;
	}

	private static int getRandomImageResource() {
		int[] array = new int[]{R.drawable.df1, R.drawable.df2};
		return array[new Random().nextInt(array.length)];
	}
}