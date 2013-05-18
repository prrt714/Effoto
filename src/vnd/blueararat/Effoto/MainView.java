package vnd.blueararat.Effoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class MainView extends ImageView {

	public MainView(Context context) {
		super(context);
	}

	public MainView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MainView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
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

}