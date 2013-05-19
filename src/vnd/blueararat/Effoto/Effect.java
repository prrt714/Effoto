package vnd.blueararat.Effoto;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer;

public abstract class Effect {

	protected static int count = 0;
	protected static Options opts = new Options();
	protected Bitmap bmp1, bmp3;
	protected int mBitmapWidth, mBitmapHeight;

	protected static volatile boolean isLocked = false;
	protected static float sBorderWidth;
	protected int index;

	protected Effect ef;
	protected ImageButton ib;
	protected WrappingSlidingDrawer wsd;
	protected Context mContext;
	protected boolean active;
	protected MainActivity ma;
	protected ImageView mImageView;
	protected volatile boolean isMoving = false;

	public Effect(Context ctx) {
		ef = this;
		mContext = ctx;
		count++;
		index = count;
		ma = (MainActivity) ctx;
		mImageView = ma.getImageView();
		ib = new ImageButton(ctx);
		ib.setPadding(2, 2, 2, 2);
		ib.setBackgroundResource(R.drawable.ef);
		ib.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				activate();
			}
		});

		final Animation animation1 = AnimationUtils.loadAnimation(mContext,
				R.anim.disappear);
		animation1.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {

				ma.getButtonLayout().post(new Runnable() {
					public void run() {
						// it works without the runOnUiThread, but all UI
						// updates must
						// be done on the UI thread
						ma.runOnUiThread(new Runnable() {
							public void run() {
								ma.getButtonLayout().removeView(ib);
								ma.getParentLayout().removeView(wsd);
							}
						});
					}
				});
			}
		});

		ib.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				/*
				 * Toast.makeText(mContext, "67 "+v, 0).show();
				 * ma.getButtonLayout().removeView(ib);
				 */final CharSequence[] items = { "Remove" };
				new AlertDialog.Builder(mContext)// .setTitle(R.string.pick_color);
						.setItems(items, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								if (which == 0) {
									if (ma.removeEffect(ef))
										ib.startAnimation(animation1);
									// Effect.count--;

								} else {
									ib.startAnimation(animation1);
								}
							}
						}).show();

				return true;
			}
		});

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(0, 0, 2, 0);
		ma.getButtonLayout().addView(ib, layoutParams);
		View vw = ma.getParentLayout().findViewById(R.id.slidingDrawer);
		if (vw != null) {
			ma.getParentLayout().removeView(vw);
			vw = null;
		}
	}

	//
	// @Override
	// protected String toString() {
	// return getName();
	// }
	//
	// protected abstract String getName();
	//
	protected void setActive(boolean active) {
		this.active = active;
	}

	protected void setBitmap(Bitmap bmp1) {
		this.bmp1 = bmp1;
		mBitmapHeight = bmp1.getHeight();
		mBitmapWidth = bmp1.getWidth();
	}

	protected Bitmap getBitmap() {
		return bmp3;
	}

	protected abstract void invalidate();

	protected SlidingDrawer getSlidingDrawer() {
		return wsd;
	}

	protected void activate() {
		if (!active) {
			active = true;
			if (ma.getCurrentEffect() != null)
				ma.getCurrentEffect().deactivate();

			ma.setCurrentEffect(this);
			WrappingSlidingDrawer vw = (WrappingSlidingDrawer) ma
					.getParentLayout().findViewById(R.id.slidingDrawer);
			if (vw != wsd) {
				if (vw != null) {
					ma.getParentLayout().removeView(vw);
					vw = null;
				}
				ma.getParentLayout().addView(wsd);
			}
			if (bmp1 == null) {
				ma.update(null, 0);
			}
		}
	}

	protected void deactivate() {
		active = false;
		freeMemory();
	}

	protected void setIndex(int i) {
		index = i;
		((Button) wsd.getHandle()).setText("" + i);
	}

	protected Drawable getIcon() {
		return null;
	}

	// protected abstract String getString();

	protected abstract void rescale(float scale);

	protected void freeMemory() {
		if (!active) {
			bmp1 = null;
			bmp3 = null;
			System.gc();
		}
	}

	protected boolean isActive() {
		return active;
	}
}