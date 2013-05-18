package vnd.blueararat.Effoto;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.SlidingDrawer;

public class WrappingSlidingDrawer extends SlidingDrawer {

	private boolean mVertical;
	private int mTopOffset;
	private static final String ADDITIONAL = "vnd.blueararat.Efoto";
	private int mAlign;

	public WrappingSlidingDrawer(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public WrappingSlidingDrawer(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		int orientation = attrs.getAttributeIntValue("android", "orientation",
				ORIENTATION_VERTICAL);
		mTopOffset = attrs.getAttributeIntValue("android", "topOffset", 0);
		mVertical = (orientation == SlidingDrawer.ORIENTATION_VERTICAL);
		mAlign = attrs.getAttributeIntValue(ADDITIONAL, "align", 1);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);

		int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

		if (widthSpecMode == MeasureSpec.UNSPECIFIED
				|| heightSpecMode == MeasureSpec.UNSPECIFIED) {
			throw new RuntimeException(
					"SlidingDrawer cannot have UNSPECIFIED dimensions");
		}

		final View handle = getHandle();
		final View content = getContent();

		measureChild(handle, widthMeasureSpec, heightMeasureSpec);

		if (mVertical) {
			int height = heightSpecSize - handle.getMeasuredHeight()
					- mTopOffset;
			content.measure(widthMeasureSpec,
					MeasureSpec.makeMeasureSpec(height, heightSpecMode));
			heightSpecSize = handle.getMeasuredHeight() + mTopOffset
					+ content.getMeasuredHeight();
			if (handle.getMeasuredWidth() <= widthSpecSize)
				widthSpecSize = content.getMeasuredWidth();
			else
				widthSpecSize = handle.getMeasuredWidth();
		} else {
			int width = widthSpecSize - handle.getMeasuredWidth() - mTopOffset;
			content.measure(MeasureSpec.makeMeasureSpec(width, widthSpecMode),
					heightMeasureSpec);
			widthSpecSize = handle.getMeasuredWidth() + mTopOffset
					+ content.getMeasuredWidth();
			if (handle.getMeasuredHeight() <= heightSpecSize)
				heightSpecSize = content.getMeasuredHeight();
			else
				heightSpecSize = handle.getMeasuredHeight();
		}

		setMeasuredDimension(widthSpecSize, heightSpecSize);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (mAlign == 0) {
			final View handle = getHandle();
			handle.layout(0, handle.getTop(), handle.getWidth(),
					handle.getBottom());
		}
	}
}
