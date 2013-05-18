package vnd.blueararat.Effoto;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class WaveSettingsLayout extends RelativeLayout {

	public interface OnSettingsChangedListener {
		void settingsChanged(int color, int mode1, int mode2, float strokewidth, float rainbow[]);
	}

	private OnSettingsChangedListener mListener;
	ColorPickerView cpv;
	private int mInitialColor;
	private Context mContext;
	private int mColor;
	// private static boolean isBlur, isEmpty, isEmboss;
	static final int NORMAL = 0;
	static final int BLUR = 1;
	static final int EMBOSS = 2;
	static final int COLOR = 0;
	static final int RAINBOW = 1;
	static final int NONE = 2;
	int mMode1 = NORMAL;
	int mMode2 = COLOR;
	float mStrokeWidth = 4;
	private SeekBar sb;

	public WaveSettingsLayout(Context context) {
		this(context, null, 0);
	}

	public WaveSettingsLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}

	public WaveSettingsLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// int initialColor
		// mListener = (OnSettingsChangedListener) context;
		LayoutInflater.from(context)
				.inflate(R.layout.wave_settings, this, true);

		// mInitialColor = initialColor;
		mContext = context;
		//
		// // +++++++++++++++++++++++++

	}
	
	public void setRainbow(boolean isRainbow){
		cpv.setRainbow(isRainbow);
	}

	public void setListener(OnSettingsChangedListener listener) {
		mListener = listener;

		// OnSettingsChangedListener l = new OnSettingsChangedListener() {
		// @Override
		// public void settingsChanged(int color, int mode1, int mode2,
		// float strokewidth) {
		// mColor = color;
		// if (mode1 == -1) {
		// RadioGroup rd = (RadioGroup) findViewById(R.id.radiogroup1);
		// sMode1 = rd.indexOfChild(findViewById(rd
		// .getCheckedRadioButtonId()));
		// } else {
		// sMode1 = mode1;
		// }
		// if (mode2 == -1) {
		// RadioGroup rd = (RadioGroup) findViewById(R.id.radiogroup2);
		// sMode2 = rd.indexOfChild(findViewById(rd
		// .getCheckedRadioButtonId()));
		// } else {
		// sMode2 = mode2;
		// }
		// EditText et = (EditText) findViewById(R.id.strokewidth);
		// try {
		// sStrokeWidth = Float.parseFloat(et.getText().toString()
		// .replace(",", "."));
		// } catch (NumberFormatException e) {
		// // TODO
		// }
		// mListener.settingsChanged(mColor, sMode1, sMode2, sStrokeWidth);
		// }
		// };

		cpv = (ColorPickerView) findViewById(R.id.ColorPickerView);
		// cpv.setInitialColor(mInitialColor);
		cpv.setInitialColor(((BorderEf) mListener).getColor());
		cpv.setSettingsChangedListener(mListener);

		// +++++++++++++++++++++++

		// ---------------------

		final RadioGroup rd1 = (RadioGroup) findViewById(R.id.radiogroup1);
		rd1.check(rd1.getChildAt(mMode1).getId());
		final RadioGroup rd2 = (RadioGroup) findViewById(R.id.radiogroup2);
		rd2.check(rd2.getChildAt(mMode2).getId());
		final EditText et = (EditText) findViewById(R.id.strokewidth);
		et.setText(String.format("%.1f", mStrokeWidth));

		OnCheckedChangeListener cl = new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (group == rd1) {
					mMode1 = group.indexOfChild(findViewById(checkedId));
				} else if (group == rd2) {
					mMode2 = group.indexOfChild(findViewById(checkedId));
				}
				mListener.settingsChanged(-1, mMode1, mMode2, -1, null);
			}

		};
		rd1.setOnCheckedChangeListener(cl);
		rd2.setOnCheckedChangeListener(cl);

		sb = (SeekBar) findViewById(R.id.strokewidth_seekBar);
		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (fromUser) {
					mStrokeWidth = (float) progress;
				}
				et.setText(String.format("%.1f", mStrokeWidth));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				mListener.settingsChanged(-1, -1, -1, mStrokeWidth, null);
			}
		});
		if (mStrokeWidth > 20) {
			sb.setMax((int) mStrokeWidth);
		}
		sb.setProgress((int) mStrokeWidth);

		et.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {

				try {
					mStrokeWidth = Float.parseFloat(et.getText().toString()
							.replace(",", "."));
				} catch (NumberFormatException e) {
					et.setText(String.format("%.1f", mStrokeWidth));
				}
				if (mStrokeWidth > 20) {
					sb.setMax((int) mStrokeWidth);
				}
				sb.setProgress((int) mStrokeWidth);
				mListener.settingsChanged(-1, -1, -1, mStrokeWidth, null);
				return false;
			}
		});
	}
	
	public float getRainbowScale() {
		return cpv.getRainbowScale();
	}

	public void update(float strokewidth) {
		mStrokeWidth = strokewidth;
		sb.setProgress((int) strokewidth);
	}
}