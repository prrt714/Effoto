package vnd.blueararat.Effoto;

import java.io.File;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.widget.Toast;

public class Prefs extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {

	static final String KEY_FOLDER = "folder";
//	static final String KEY_RETAIN_EXIF = "exif";//getString(R.string.exif);
	static final String KEY_FORMAT = "format";
	static final String KEY_COLOR = "background_color";
	static final String KEY_JPEG_Q = "jpeg_quality";
	static final int SELECT_FILE = 1;
	private Preference mSeekbarPrefJ;
	private Preference mColorPref;
	private ListPreference mSaveFormat;
	private FolderPref mPrefFolder;
//	private Preference mRetainExif;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		mSeekbarPrefJ = findPreference(KEY_JPEG_Q);
		mSaveFormat = (ListPreference) findPreference(KEY_FORMAT);
		mSaveFormat.setSummary(getString(R.string.pictures_will_be_saved) + " "
				+ mSaveFormat.getValue());
		boolean b = mSaveFormat.getValue().equals("JPEG");
		mSeekbarPrefJ.setEnabled(b);
		mColorPref = findPreference(KEY_COLOR);
		mColorPref.setEnabled(b);
//		mRetainExif = findPreference(KEY_RETAIN_EXIF);
//		mRetainExif.setEnabled(b);
		// Preference media_scanner = findPreference("media_scanner");
		// media_scanner
		// .setOnPreferenceClickListener(new OnPreferenceClickListener() {
		// public boolean onPreferenceClick(Preference preference) {
		// searchMedia();
		// return true;
		// }
		// });
		mPrefFolder = (FolderPref) findPreference(KEY_FOLDER);
		mPrefFolder
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						Intent intent = new Intent(getBaseContext(),
								FileDialog.class);
						File f = new File(mPrefFolder.getString());
						if (f != null && f.getParentFile() != null
								&& f.getParentFile().exists()) {
							intent.putExtra(FileDialog.START_PATH,
									f.getParent());
						} else {
							String sf = "/sdcard/Pictures";
							f = new File(sf);
							if (f.exists()) {
								intent.putExtra(FileDialog.START_PATH, sf);
							} else {
								intent.putExtra(FileDialog.START_PATH,
										"/sdcard");
							}
						}
						intent.putExtra(FileDialog.CAN_SELECT_DIR, true);
						// intent.putExtra(FileDialog.SELECTION_MODE,
						// SelectionMode.MODE_CREATE);
						// intent.putExtra(FileDialog.FORMAT_FILTER, new
						// String[] { "ttf",
						// "otf" });
						startActivityForResult(intent, SELECT_FILE);
						return true;
					}
				});
		if (mPrefFolder.getString().length() == 0)
			mPrefFolder.setSummary(R.string.folder_select);

//		Preference media_scanner = findPreference("media_scanner");
//		media_scanner
//				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
//					public boolean onPreferenceClick(Preference preference) {
//						searchMedia();
//						return true;
//					}
//				});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == SELECT_FILE) {
			if (resultCode == RESULT_OK) {
				String sFile = data.getStringExtra(FileDialog.RESULT_PATH);
				mPrefFolder.setString(sFile);
				// MainActivity.setTypeface(sFile);
			}
		}
	}

//	private void searchMedia() {
//		sendBroadcast(new Intent(
//				Intent.ACTION_MEDIA_MOUNTED,
//				Uri.parse("file://" + Environment.getExternalStorageDirectory())));
//		Toast.makeText(this, R.string.media_scanning, Toast.LENGTH_LONG).show();
//	}

	@Override
	protected void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {
		// if (arg1.equals("reset_settings")) {
		//
		// } else if (arg1.equals(KView.KEY_NUMBER_OF_MIRRORS)) {
		//
		// } else if (arg1.equals(MainActivity.KEY_IMAGE_URI)) {
		//
		// } else
		if (arg1.equals(KEY_FORMAT)) {
			boolean b = mSaveFormat.getValue().equals("JPEG");
			MainActivity.isPNG = !b;
			mSeekbarPrefJ.setEnabled(b);
			mColorPref.setEnabled(b);
//			mRetainExif.setEnabled(b);
			
			mSaveFormat.setSummary(getString(R.string.pictures_will_be_saved)
					+ " " + mSaveFormat.getValue());
		}
		// else if (arg1.equals(KEY_COLOR)) {
		// Context ctx =
		// getCallingActivity();//getParent();//ApplicationContext();
		// if (ctx instanceof MainActivity) {
		// Effect ef = ((MainActivity) ctx).getCurrentEffect();
		// if (ef instanceof BorderEf) {
		// ((BorderEf) ef).setBackgroundColor(mColorPref.getColor());
		// }
		// }
		// }
	}
}
