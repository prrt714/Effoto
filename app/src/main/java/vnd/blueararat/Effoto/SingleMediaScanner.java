package vnd.blueararat.Effoto;

import java.io.File;
import java.io.FilenameFilter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.widget.Toast;

public class SingleMediaScanner implements MediaScannerConnectionClient {

	private MediaScannerConnection mMs;
	private final File mFile;
	private Context mContext;
	private final boolean isDir;
	//private volatile int i = 0;
	private int j;
	private Uri mUri;
	
	Uri getUri() {
		return mUri;
	}

	public SingleMediaScanner(Context context, File f) {
		isDir = f.isDirectory();
		mContext = context;
		mFile = f;
		mMs = new MediaScannerConnection(context, this);
		mMs.connect();
	}

	@Override
	public void onMediaScannerConnected() {

		if (!isDir) {
			mMs.scanFile(mFile.getAbsolutePath(), null);
		} else {
			File[] files = mFile.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String filename) {
					return (filename.endsWith(".jpg") || filename
							.endsWith(".png"));
				}
			});
			if (files != null) {
				j = files.length;
			}
			if (files == null || j == 0) {
				Toast.makeText(mContext, R.string.nothing_to_open,
						Toast.LENGTH_SHORT).show();
				return;
			}
			File file = files[j - 1];
			mMs.scanFile(file.getAbsolutePath(), null);
		}
	}

	@Override
	public void onScanCompleted(String path, Uri uri) {
		mUri = uri;
		if (!isDir) {
			mMs.disconnect();
			mMs = null;
			return;
		}
		try {
			if (uri != null) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(uri);
				try {
					mContext.startActivity(intent);
				} catch (ActivityNotFoundException e) {
					mUri = null;
				}
			}
		} finally {
			mMs.disconnect();
			mMs = null;
		}
	}
}