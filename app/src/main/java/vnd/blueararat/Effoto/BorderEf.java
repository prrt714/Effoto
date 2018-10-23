package vnd.blueararat.Effoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class BorderEf extends Effect implements
		WaveSettingsLayout.OnSettingsChangedListener {

	public static void scaleStrokeWidth(float scale) {
		sStrokeWidth *= scale;
	}

	public void setBackgroundColor(int backgroundColor) {
		mBackgroundColor = backgroundColor;
	}

	public int getBackgroundColor() {
		return mBackgroundColor;
	}

	public static final int WIDTH = 72;
	public static final int OFFSET = -36;
	public static final String BPOINT_NAME = "bpoint.png";
	private Paint mPaint, mBitmapPaint, mDrawPaint;
	private MaskFilter mEmboss;
	private MaskFilter mBlur;
	private int mColor;
	private static int sColor = 0xFF7777FF;
	private static float sStrokeWidth = 4;
	private float mStrokeWidth;
	private boolean isRainbow = false;
	private boolean adjustRainbow = false;
	private boolean isBlur = false;
	private boolean isNone = false;
	private float mCenterRainbowX;
	private float mCenterRainbowY;
	private float[] rainbow;
	static final int SELECT_FOLDER = 1;
	private int mMode1;
	private int mMode2;
	private final static int PADX = 0;
//	private final static int PADX2 = 2 * PADX;
	private int mBackgroundColor = -1;
	int[] mColors = new int[] { 0xFFFF0000, 0xFFFF00FF, 0xFF0000FF, 0xFF00FFFF,
			0xFF00FF00, 0xFFFFFF00, 0xFFFF0000 };
	private boolean mustFit = true;
	private boolean mustClip = false;
	private float mDx = 20, mDy = 20;
	private float mStartX = 10, mStartY = 10, mSmooth = 5;
	private boolean isCircle = false;
	private WaveSettingsLayout sd;
	private float[] mFitted = new float[6];
	private Drawable d1, d2;
	private Bitmap bpoint;
	private int frequency;
	public static final double RAD = 180.0 / Math.PI;
	public static final int BWIDTH = 400;

	public BorderEf(Context ctx) {
		this(ctx, Color.WHITE, 0, 0, sStrokeWidth, null);
	}

	public BorderEf(Context ctx, File serialized) {
		this(ctx, 0, 0, 0, 0, serialized);
		ib.setImageDrawable(d1);
	}

	public BorderEf(Context ctx, int color, int mode1, int mode2,
			float strokewidth, File serialized) {
		super(ctx);
		if (serialized == null || !load(serialized)) {
			this.mMode1 = mode1;
			this.mMode2 = mode2;
			this.mStrokeWidth = strokewidth;

			mBackgroundColor = ma.prefs.getInt(Prefs.KEY_COLOR, 0xFFFFFFFF);

			mDx = 10 + RANDOM.nextInt(20);
			mDy = 10 + RANDOM.nextInt(20);
			float m = mDx * 2;
			if (m > sBorderWidth)
				sBorderWidth = m;
			mStartX = 10;
			mStartY = 10;
			mSmooth = 5 - RANDOM.nextInt(10);

			float[] f = new float[3];
			Color.colorToHSV(sColor, f);

			f[0] = 360.f * RANDOM.nextFloat();
			mColor = Color.HSVToColor(Color.alpha(sColor), f);
			sColor = mColor;

			float[] f2 = new float[3];
			for (int i = 0; i < 7; i++) {
				Color.colorToHSV(mColors[i], f2);
				f2[1] = f[1];
				f2[2] = f[2];
				mColors[i] = Color.HSVToColor(f2);
			}

		}

		mPaint = new Paint() {
			{
				setAntiAlias(true);
				setDither(true);
				setColor(mColor);
				setStyle(Paint.Style.STROKE);
				setStrokeJoin(Paint.Join.ROUND);
				setStrokeCap(Paint.Cap.ROUND);
				setStrokeWidth(mStrokeWidth);
			}
		};

		mDrawPaint = new Paint();

		mBitmapPaint = new Paint(Paint.DITHER_FLAG);

		mEmboss = new EmbossMaskFilter(new float[] { 1, 1, 1 }, 0.4f, 6, 3.5f);

		mBlur = new BlurMaskFilter(mStrokeWidth != 0 ? mStrokeWidth : 1,
				BlurMaskFilter.Blur.NORMAL);

		d1 = mContext.getResources().getDrawable(R.drawable.bt_border_normal)
				.mutate();
		d2 = mContext.getResources().getDrawable(R.drawable.bt_border_pressed)
				.mutate();
		setHue(d1);
		setHue(d2);

		LayoutInflater inflater = LayoutInflater.from(ctx);
		View v = inflater.inflate(R.layout.sliding_drawer_2,
				ma.getParentLayout(), true);
		wsd = (WrappingSlidingDrawer) v.findViewById(R.id.slidingDrawer);
		sd = (WaveSettingsLayout) wsd.findViewById(R.id.content2);
		sd.setListener(this);

		Button bt = (Button) wsd.getHandle();
		bt.setText("" + index);

		MainActivity.setViewGroupFont(wsd, Typeface.MONOSPACE);
		sd.update(mStrokeWidth);
		if (serialized != null) {
			sd.update(rainbow, mMode1, mMode2);
		}
	}

	private class Draw extends AsyncTask<Void, Void, Void> {
		private final Paint paint = new Paint(mPaint);
		private final float lStrokeWidth = mStrokeWidth;
		private final float lDx = mDx;
		private final float lDy = mDy;
		private float lStartX = mStartX;
		private float lStartY = mStartY;
		private final float lSmooth = mSmooth;
		private final boolean lBlur = isBlur;
		private final boolean lNone = isNone;
		private final boolean lFit = mustFit;
		private final int lBackgroundColor = mBackgroundColor;
		private final boolean lCircle = isCircle;
		private final int lNumWaves = 2 * (int) ((mBitmapWidth + mBitmapHeight) / lDy);;
		double dangle = 2 * Math.PI / lNumWaves;

		@Override
		protected Void doInBackground(Void... params) {
			isLocked = true;
			iAmLocked = true;
			bmp3 = drawIntoBitmap();
			return null;
		}

		private synchronized Path drawFrameExport(Bitmap bitmap) {
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();

			int numberOfWavesX, numberOfWavesY;
			float dx2, dy2;

			Path path = new Path();

			float j = lDx;
			if (lCircle) {
				float adj = j + adjust(lNone, lBlur, lStrokeWidth);
				float cX = width / 2;
				float cY = height / 2;
				float radius = Math.min(cX, cY) - adj;
				float x = Math.max(j, j + cX - cY) + adj;

				if (!lFit) {
					cX += lStartX;
					cY += lStartY;
					x += lStartX;
				}

				float y = cY;

				double ang = 0;
				path.moveTo(x, y);
				float cos = (float) Math.cos(ang);
				float sin = (float) Math.sin(ang);
				float x1, x2, y1, y2;
				for (int n = 0; n < lNumWaves; n++) {
					x1 = x + lSmooth * sin;
					y1 = y + lSmooth * cos;
					ang += dangle;
					cos = (float) Math.cos(ang);
					sin = (float) Math.sin(ang);
					x = cX - (radius + j) * cos;
					y = cY + (radius + j) * sin;
					x2 = x - lSmooth * sin;
					y2 = y - lSmooth * cos;
					path.cubicTo(x1, y1, x2, y2, x, y);
					j = -j;
				}
				path.close();
			} else {
				if (lFit) {
					mFitted = fitPath(width, height, lDx, lDy, lSmooth,
							lStartX, lStartY, lStrokeWidth, lNone, lBlur);
					lStartX = mFitted[0];
					lStartY = mFitted[1];
					dx2 = mFitted[2];
					dy2 = mFitted[3];
					numberOfWavesX = (int) mFitted[4];
					numberOfWavesY = (int) mFitted[5];
				} else {
					numberOfWavesX = (int) ((width - lStartX * 2) / lDy);
					numberOfWavesY = (int) ((height - lStartY * 2) / lDy);
					if (numberOfWavesX % 2 == 0) {
						numberOfWavesX--;
					}
					if (numberOfWavesY % 2 == 0) {
						numberOfWavesY--;
					}
					dx2 = ((float) width - lStartX * 2) / numberOfWavesX;
					dy2 = ((float) height - lStartY * 2) / numberOfWavesY;
				}

				float x = lStartX;
				float y = lStartY;
				path.moveTo(lStartX, lStartY);
				for (int n = 1; n <= numberOfWavesY; n++) {
					y += dy2;
					path.cubicTo(x - j, y - dy2 / 2 - lSmooth, x - j, y - dy2
							/ 2 + lSmooth, x, y);
					j = -j;
				}
				for (int n = 1; n <= numberOfWavesX; n++) {
					x += dx2;
					path.cubicTo(x - dx2 / 2 - lSmooth, y - j, x - dx2 / 2
							+ lSmooth, y - j, x, y);
					j = -j;
				}
				j = -j;
				for (int n = 1; n <= numberOfWavesY; n++) {
					y -= dy2;
					path.cubicTo(x - j, y + dy2 / 2 + lSmooth, x - j, y + dy2
							/ 2 - lSmooth, x, y);
					j = -j;
				}
				for (int n = 1; n <= numberOfWavesX; n++) {
					x -= dx2;
					path.cubicTo(x + dx2 / 2 + lSmooth, y - j, x + dx2 / 2
							- lSmooth, y - j, x, y);
					j = -j;
				}
				path.close();
			}
			return path;
		}

		private Bitmap drawIntoBitmap() {
			Bitmap.Config g;
			if (MainActivity.isPNG) { // && bitmap.hasAlpha()
				g = Bitmap.Config.ARGB_8888;
			} else {
				g = Bitmap.Config.RGB_565;
			}
			Bitmap bitmap = bmp1;
			Bitmap bitmap2 = null;
			try {
				bitmap2 = Bitmap.createBitmap(bitmap.getWidth(),
						bitmap.getHeight(), g);
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				if (opts.inSampleSize < 2)
					opts.inSampleSize = 2;
				else
					opts.inSampleSize++;
				iAmLocked = false;
				return null;
			}
			Canvas canvas = new Canvas(bitmap2);
			if (!MainActivity.isPNG)
				canvas.drawColor(lBackgroundColor);

			Path path = drawFrameExport(bitmap);
			if (mustClip)
				canvas.clipPath(path);
			canvas.drawBitmap(bitmap, 0, 0, mBitmapPaint);

			if (!lFit && lStartX == 0 && lStartY == 0 && !lCircle) {
				float p = lStrokeWidth / 2;
				canvas.drawRect(p, p, bitmap.getWidth() - p, bitmap.getHeight()
						- p, paint);
			}

			canvas = new Canvas(bitmap2);
			canvas.drawPath(path, paint);

			if (bpoint != null) {
				PathMeasure pm = new PathMeasure(path, false);
				float coord[] = new float[2];
				float tan[] = new float[2];

				float l = pm.getLength() / frequency;

				for (int i = 0; i < frequency; i++) {
					pm.getPosTan(l * i, coord, tan);
					Matrix matrix = new Matrix();
					matrix.postTranslate(coord[0] + OFFSET, coord[1] + OFFSET);
					matrix.postRotate(
							(float) (RAD * Math.atan2(tan[1], tan[0])),
							coord[0], coord[1]);

					canvas.drawBitmap(bpoint, matrix, mDrawPaint);
				}
			}

			if (isCircle && lFit) {
				int l = 0, u = 0, dr;
				int w = bitmap2.getWidth();
				int h = bitmap2.getHeight();
				if (w > h) {
					l = (w - h) / 2;
					dr = h;
				} else if (w < h) {
					u = (h - w) / 2;
					dr = w;
				} else {
					return bitmap2;
				}
				bitmap2 = Bitmap.createBitmap(bitmap2, l, u, dr, dr);
			}

			return bitmap2;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (bmp3 == null) {
				ma.loadBitmap(true);
				return;
			}

			if (isMoving) {
				ma.updateMoving(bmp3);
				isLocked = false;
				iAmLocked = false;
			} else {
				isLocked = false;
				iAmLocked = false;
				ma.update(bmp3, index);
				if (!iAmLocked)
					freeMemory();
			}
		}
	}

	@Override
	public void rescale(float scale) {
		bmp1 = null;
		bmp3 = null;
		// Toast.makeText(mContext, "" + opts.inSampleSize, 0).show();
		mStrokeWidth *= scale;
		sd.update(mStrokeWidth);
		mDx *= scale;
		mDy *= scale;
		mCenterRainbowX *= scale;
		mCenterRainbowY *= scale;
		mStartX *= scale;
		mStartY *= scale;
		mSmooth *= scale;
		System.gc();
	}

	@Override
	public void invalidate() {
		if (!isLocked && bmp1 != null)
			new Draw().execute();
	}

	@Override
	public void activate() {
		super.activate();
		ib.setImageDrawable(d2);
	}

	@Override
	public void deactivate() {
		super.deactivate();
		ib.setImageDrawable(d1);
	}

	private int tw;
	private float mX, mY, adj1;
	float sD, sMx, sMy, mSmoothInitial;// = 5;

	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getActionMasked();
		int P = event.getPointerCount();
		if (action != MotionEvent.ACTION_MOVE) {
			if (action == MotionEvent.ACTION_DOWN) {
				isMoving = true;
				touch_start(event.getX(0), event.getY(0));
				return true;
			} else if (action == MotionEvent.ACTION_UP) {
				isMoving = false;
				if (CirclesEf.sOnlyBorderIndex > -1) {
					sBorderWidth = ma.getMaxWaveHeight();
					ma.update(null, 0);
				} else {
					invalidate();
				}
			}
			if (action == MotionEvent.ACTION_POINTER_DOWN) {
				if (P == 2) {
					float sX2 = event.getX(0) - event.getX(1);
					float sY2 = event.getY(0) - event.getY(1);
					sD = (float) Math.sqrt(sX2 * sX2 + sY2 * sY2);
					mSmoothInitial = mSmooth;
					return true;
				} else if (P == 3 && !mustFit) {
					sMx = mStartX - event.getX(2);
					sMy = mStartY - event.getY(2);
					return true;
				}
				return false;
			}
		} else {
			if (P == 1) {
				touch_move(event.getX(), event.getY());
			} else if (P == 2) {
				float sX2 = event.getX(0) - event.getX(1);
				float sY2 = event.getY(0) - event.getY(1);
				float sD2 = (float) Math.sqrt(sX2 * sX2 + sY2 * sY2);
				mSmooth = mSmoothInitial + sD2 - sD;
			} else if (P == 3 && !mustFit) {
				mStartX = (int) (sMx + event.getX(2));
				mStartY = (int) (sMy + event.getY(2));
			} else {
				return false;
			}
			invalidate();
		}
		return true;
	}

	public void setAdjustRainbow(boolean b) {
		adjustRainbow = b;
	}

	private void touch_move(float x, float y) {
		float dx = x - mX;
		float dy = y - mY;
		mDx = Math.abs(mDx + dx);
		mDy = Math.abs(mDy + dy);

		if (mDy < 2) {
			mDy = 2;
		} else {
			mSmooth = mSmooth + dy / 2;
		}
		if (mDx < 2)
			mDx = 2;
		mX = x;
		mY = y;
		// }
	}

	private void touch_start(float x, float y) {
		mX = x;
		mY = y;
		if (isRainbow) {
			if (adjustRainbow) {

			}
		}
	}

	private static float[] fitPath(float width, float height, float mDx,
			float mDy, float mSmooth, float startX, float startY,
			float strokewidth, boolean none, boolean blur) {
		float sX, sY;
		float dx2, dy2;
		float adjustStrokeWidth = adjust(none, blur, strokewidth);
		int numberOfWavesX, numberOfWavesY;
		Path p = new Path();
		RectF bounds = new RectF();
		// int k = 0;
		do {
			sX = startX;
			sY = startY;
			numberOfWavesX = (int) ((width - sX * 2) / mDy);
			numberOfWavesY = (int) ((height - sY * 2) / mDy);
			if (numberOfWavesX % 2 == 0) {
				numberOfWavesX--;
			}
			if (numberOfWavesY % 2 == 0) {
				numberOfWavesY--;
			}
			dy2 = ((float) height - sY * 2) / numberOfWavesY;
			dx2 = ((float) width - sX * 2) / numberOfWavesX;

			p.moveTo(0, 0);
			float j = mDx;
			p.cubicTo(j, dy2 / 2 - mSmooth, j, dy2 / 2 + mSmooth, 0, dy2);
			p.computeBounds(bounds, false);
			p.reset();
			startX = 3 * (bounds.right - bounds.left) / 4 + adjustStrokeWidth;

			p.moveTo(0, 0);
			p.cubicTo(dx2 / 2 - mSmooth, -j, dx2 / 2 + mSmooth, -j, dx2, 0);
			bounds = new RectF();
			p.computeBounds(bounds, true);
			p.reset();
			startY = 3 * (bounds.bottom - bounds.top) / 4 + adjustStrokeWidth;
			// k++;

		} while (Math.abs(sX - startX) > 0.01 || Math.abs(sY - startY) > 0.01);

		return new float[] { sX, sY, dx2, dy2, numberOfWavesX, numberOfWavesY };
	}

	private static float adjust(boolean none, boolean blur, float strokewidth) {
		float adjustStrokeWidth = strokewidth / 2;
		if (none && !blur) {
			adjustStrokeWidth = -adjustStrokeWidth;
		} else if (none && blur) {
			adjustStrokeWidth = 0;
		} else if (blur) {
			adjustStrokeWidth *= 2;
		}
		return adjustStrokeWidth;
	}

	@Override
	public void settingsChanged(int color, int mode1, int mode2,
			float strokewidth, float rainbow[]) {
		if (color != -1) {
			sColor = color;
			mColor = color;
			mPaint.setColor(color);
			setHue(d1);
			setHue(d2);

			float[] f1 = new float[3];
			float[] f = new float[3];
			Color.colorToHSV(mColor, f1);
			for (int i = 0; i < 7; i++) {
				Color.colorToHSV(mColors[i], f);
				f[1] = f1[1];
				f[2] = f1[2];
				mColors[i] = Color.HSVToColor(f);
			}
			if (isRainbow) {
				Shader s = new SweepGradient(mCenterRainbowX, mCenterRainbowY,
						mColors, null);
				mPaint.setShader(s);
			}
		}
		if (strokewidth != -1) {
			sStrokeWidth = strokewidth;
			mPaint.setStrokeWidth(strokewidth);
			if (strokewidth != 0)
				mBlur = new BlurMaskFilter(strokewidth,
						BlurMaskFilter.Blur.NORMAL);
			mStrokeWidth = strokewidth;
			if (isBlur) {
				mPaint.setMaskFilter(mBlur);
			}
		}
		if (mode1 != -1)
			mMode1 = mode1;
		if (mode2 != -1)
			mMode2 = mode2;
		switch (mode1) {
		case WaveSettingsLayout.NORMAL:
			mPaint.setMaskFilter(null);
			isBlur = false;
			break;
		case WaveSettingsLayout.BLUR:
			mPaint.setMaskFilter(mBlur);
			isBlur = true;
			break;
		case WaveSettingsLayout.EMBOSS:
			mPaint.setMaskFilter(mEmboss);
			isBlur = false;
			break;
		}
		switch (mode2) {
		case WaveSettingsLayout.COLOR:
			sd.setRainbow(false);
			mPaint.setXfermode(null);
			mPaint.setShader(null);
			isRainbow = false;
			isNone = false;
			break;
		case WaveSettingsLayout.RAINBOW:
			if (this.rainbow == null) {
				mCenterRainbowX = mBitmapWidth / 2;
				mCenterRainbowY = mBitmapHeight / 2;
			}
			sd.setRainbow(true);
			mPaint.setXfermode(null);
			if (!isRainbow) {
				if (mColors[0] == mColors[2]) {
					mColors = new int[] { 0xFFFF0000, 0xFFFF00FF, 0xFF0000FF,
							0xFF00FFFF, 0xFF00FF00, 0xFFFFFF00, 0xFFFF0000 };
				}
				Shader s = new SweepGradient(mCenterRainbowX, mCenterRainbowY,
						mColors, null);
				mPaint.setShader(s);
				isRainbow = true;
			}
			isNone = false;
			break;
		case WaveSettingsLayout.NONE:
			sd.setRainbow(false);
			isRainbow = false;
			mPaint.setShader(null);
			isNone = true;
			if (!MainActivity.isPNG) {
				if (isBlur) {
					mPaint.setColor(mColor);
					mPaint.setXfermode(new PorterDuffXfermode(
							PorterDuff.Mode.CLEAR));
				} else {
					mPaint.setColor(mBackgroundColor);
					mPaint.setXfermode(null);
				}
			} else {
				mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
			}
			break;
		}
		if (rainbow != null) {
			this.rainbow = rainbow;
			mCenterRainbowX = (bmp1.getWidth() / 2.f)
					* (1.f - rainbow[0] / sd.getRainbowScale());
			mCenterRainbowY = (bmp1.getHeight() / 2.f)
					* (1.f - rainbow[1] / sd.getRainbowScale());
			Shader s = new SweepGradient(mCenterRainbowX, mCenterRainbowY,
					mColors, null);
			mPaint.setShader(s);
		}
		invalidate();
	}

	@Override
	protected int getColor() {
		return mColor;
	}

	public void setFit(boolean mustFit) {
		this.mustFit = mustFit;
		invalidate();
	}

	public void setCircle(boolean isCircle) {
		this.isCircle = isCircle;
		invalidate();
	}

	public void setClip(boolean mustClip) {
		this.mustClip = mustClip;
		invalidate();
	}

	public void setPostmark() {
		if (!isCircle) {
			mustFit = false;
			mustClip = true;
			mStartX = 0;
			mStartY = 0;
			invalidate();
		}
	}

	public boolean getFit() {
		return mustFit;
	}

	public boolean getCircle() {
		return isCircle;
	}

	public boolean getClip() {
		return mustClip;
	}

	public float getWaveHeight() {
		if (mustFit) {
			return mFitted[0];
		} else if (!isCircle && mStartX == 0 && mStartY == 0) {
			return mDx / 2;
		}
		return mDx;
	}

	private void setHue(Drawable drawable) {
		if (drawable == null)
			return;
		final ColorMatrix matrixA = new ColorMatrix();
		matrixA.setSaturation(0);

		final ColorMatrix matrixB = new ColorMatrix();

		matrixB.setScale((float) Color.red(mColor) / 255,
				(float) Color.green(mColor) / 255,
				(float) Color.blue(mColor) / 255,
				(float) Color.alpha(mColor) / 255);
		matrixA.setConcat(matrixB, matrixA);

		final ColorMatrixColorFilter filter = new ColorMatrixColorFilter(
				matrixA);
		drawable.setColorFilter(filter);
	}

	public void setBitmapPoint(Bitmap bp, int freq) {
		if (bp == null) {
			bpoint = null;
		} else {
			bpoint = Bitmap.createScaledBitmap(bp, WIDTH, WIDTH, false);
			frequency = freq;
		}
	}

	public Bitmap getBitmapPoint() {
		if (bpoint == null)
			return null;
		Bitmap bp = Bitmap.createScaledBitmap(bpoint, BWIDTH, BWIDTH, false);
		return bp;
	}

	public boolean shouldDraw() {
		return (bpoint != null);
	}

	private static class SavedEffect implements Serializable {

		private static final long serialVersionUID = 2752188990081894671L;
		private int lindex;
		private int lmBackgroundColor;
		private int lmColor;
		private float lmDx;
		private float lmDy;
		private boolean lisBlur;
		private boolean lisRainbow;
		private float lmCenterRainbowX, lmCenterRainbowY;
		private float[] lrainbow;
		private int[] lmColors;

		private boolean lmustFit;
		private boolean lmustClip;
		private float lmStartX, lmStartY, lmSmooth, lmStrokeWidth;
		private boolean lisCircle;
		private int lmMode1;
		private int lmMode2;
		private int lfrequency;

		private SavedEffect(BorderEf ef) {
			lindex = ef.index;
			lmBackgroundColor = ef.mBackgroundColor;
			lmColor = ef.mColor;
			lmDx = ef.mDx;
			lmDy = ef.mDy;
			lisBlur = ef.isBlur;
			lisRainbow = ef.isRainbow;
			lrainbow = ef.rainbow;
			lmCenterRainbowX = ef.mCenterRainbowX;
			lmCenterRainbowY = ef.mCenterRainbowY;
			lmColors = ef.mColors;

			lmustFit = ef.mustFit;
			lmustClip = ef.mustClip;
			lmStartX = ef.mStartX;
			lmStartY = ef.mStartY;

			lmSmooth = ef.mSmooth;
			lmStrokeWidth = ef.mStrokeWidth;
			lisCircle = ef.isCircle;
			lmMode1 = ef.mMode1;
			lmMode2 = ef.mMode2;
			lfrequency = ef.frequency;
		}
	}

	@Override
	protected void save(File folder) {
		File f = new File(folder, index + ":" + getClass().getName());
		SavedEffect se = new SavedEffect(this);
		try {
			// FileOutputStream fileOut = new FileOutputStream(f);
			ObjectOutputStream out = new ObjectOutputStream(
					new FileOutputStream(f));
			out.writeObject(se);
			out.close();
			// fileOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (bpoint != null) {
			File imageFile = new File(folder, BPOINT_NAME + index);
			Bitmap.CompressFormat mCf = Bitmap.CompressFormat.PNG;
			int mQ = 100;
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bpoint.compress(mCf, mQ, stream);
			byte[] byteArray = stream.toByteArray();
			stream = null;
			BufferedOutputStream out = null;

			try {
				out = new BufferedOutputStream(new FileOutputStream(imageFile));
				out.write(byteArray);
			} catch (Exception e) {
			} finally {
				try {
					out.close();
				} catch (Exception e) {
				}
			}
			byteArray = null;
			System.gc();

		}
	}

	@Override
	protected boolean load(File serialized) {
		SavedEffect se = null;
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(
					serialized));
			se = (SavedEffect) in.readObject();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		mStrokeWidth = se.lmStrokeWidth;
		mDx = se.lmDx;
		mDy = se.lmDy;
		mSmooth = se.lmSmooth;
		mStartX = se.lmStartX;
		mStartY = se.lmStartY;
		mColor = se.lmColor;
		mBackgroundColor = se.lmBackgroundColor;
		isRainbow = se.lisRainbow;
		rainbow = se.lrainbow;
		mCenterRainbowX = se.lmCenterRainbowX;
		mCenterRainbowY = se.lmCenterRainbowY;

		mColors = se.lmColors;
		isCircle = se.lisCircle;
		mustFit = se.lmustFit;
		mustClip = se.lmustClip;
		mMode1 = se.lmMode1;
		mMode2 = se.lmMode2;
		frequency = se.lfrequency;
		String s_ind = serialized.getName().split(":")[0];
		File imageFile = new File(serialized.getParentFile(), BPOINT_NAME
				+ s_ind);
		if (imageFile.exists()) {
			bpoint = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
		}
		return true;
	}
}
