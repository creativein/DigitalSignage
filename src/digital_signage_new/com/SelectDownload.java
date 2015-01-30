package digital_signage_new.com;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import digital_signage_new.com.TwoSizeVideo.DownloadFileAsync;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Path;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AnalogClock;
import android.widget.DigitalClock;
import android.widget.TextView;

public class SelectDownload extends Activity {

	public InputStream iStream;
	FileOutputStream fOutStream;
	URLConnection connection;
	File file;
	Path p;
	AnalogClock AL;
	DigitalClock DL;
	TextView date;
	private ProgressDialog dialog;

	// for jsonvideo method
	public boolean jsonVideoOnlyOnce = true;
	public JSONArray arrJsonVideoLinks;
	// public String[] videoLinks;
	private int idCompanySelectDownload;

	// urls should be unique
	String url[]; /*
				 * ={"http://10.0.2.2/test/gamecore.mp4",
				 * "http://10.0.2.2/test/gamecore2.mp4",
				 * "http://10.0.2.2/test/cheetah.3gp",
				 * "http://10.0.2.2/test/cheetah2.3gp",
				 * "http://10.0.2.2/test/cheetah3.3gp" };
				 */// ,"http://10.0.2.2/test/gamecore.mp4"
	// String
	// url[]={"http://bytmediacenter.com/android/boxmytvchannel/gamecore.mp4",
	// "http://bytmediacenter.com/android/boxmytvchannel/box.mp4"};
	String finalURLnPath[][];

	String filePath[];
	byte buffer[] = new byte[16384];

	SharedPreferences sharedPreferences;
	SharedPreferences.Editor editPreferences;
	int urlIndex;

	String choose[][];
	String playPaths[];
	String newUrls[];
	String playURLs[];
	String deletePaths[];

	private boolean downloadFirst = false, downloadOtherThanFirst = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		preferences();
		start();

		AL = (AnalogClock) findViewById(R.id.ALClock);
		DL = (DigitalClock) findViewById(R.id.dc);
		date = (TextView) findViewById(R.id.dateText);
	} // onCreate end

	public void getCompanyId() {
		Bundle b = getIntent().getExtras();
		idCompanySelectDownload = b.getInt("ID");
	}

	// start the logic.
	public void start() {
		getCompanyId();
		jsonVideo(); // first fill url[]
		if (checkLogInInfo()) {
			downloadFirst = true;
			// for(int i=0;i<url.length;i++){downloadInSdcardFirst(url[i]);}
		} else {
			downloadOtherThanFirst = true;
			// chooseURL();
		}
		new DownloadFileAsync().execute();
	}

	class DownloadFileAsync extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			showDialog(RESULT_OK);
		}

		@Override
		protected String doInBackground(String... unused) {
			if (downloadFirst) {
				filePath = new String[url.length]; // cos downloadInSdcardFirst
													// is callled in a loop
				for (int i = 0; i < url.length; i++) {
					downloadInSdcardFirst(url[i]);
				}
				// callGraphicalActivity(); //as it is first time log in
				// afterdownloading all videos
				// directly call callGraphicalActivity
			} else if (downloadOtherThanFirst) {
				chooseURL();
			}
			// jsonVideo();
			return null;
		}

		protected void onProgressUpdate(String... progress) {
			dialog.setProgress(Integer.parseInt(progress[0]));
		}

		@Override
		protected void onPostExecute(String unused) {
			callGraphicalActivity();
			// videoSizeControl();
			// playVideo();
			dialog.dismiss();
			DL.setVisibility(View.VISIBLE);
			AL.setVisibility(View.VISIBLE);
			date.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		dialog = new ProgressDialog(this);
		dialog.setMessage("Loading Please Wait...");
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		return dialog;
	}

	// get all the links to videos on server using json
	private void jsonVideo() {
		if (jsonVideoOnlyOnce) {
			Log.d("TwoSizeVideo", "jsonVideo");

			Json1 jsonObj = new Json1();
			// arrJson =
			// jsonObj.parseJsonData("http://bytmediacenter.com/android/php/data.php?id=8&action=VIDEO");
			//http://www.boxmytv.com/mytv/webservice/config.php?id=8&action=VIDEO
			arrJsonVideoLinks = jsonObj
					.parseJsonData("http://www.boxmytv.com/"
							+ "mytv/webservice/config.php?id="
							+ idCompanySelectDownload + "&action=VIDEO");
			url = new String[arrJsonVideoLinks.length()];
			// videoMute=new Integer[arrJsonVideoLinks.length()];
			// videoSizeController=new Integer[arrJsonVideoLinks.length()];
			for (int j = 0; j < arrJsonVideoLinks.length(); j++) {
				JSONObject json_data;
				try {
					json_data = arrJsonVideoLinks.getJSONObject(j);
					url[j] = json_data.getString("video_link");
					// videoMute[j]=Integer.parseInt(json_data.getString("music"));
					url[j] = url[j].trim().replaceAll(" ", "%20");
					// videoSizeController[j]=Integer.parseInt(json_data.getString("fullscreen"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			/*
			 * for(int i=0;i<arrJsonVideoLinks.length();i++){ try {
			 * storeSDCard(videoLinks[i],i); } catch (IOException e)
			 * {e.printStackTrace();} }
			 */
		}
		jsonVideoOnlyOnce = false;

	}

	/*
	 * choose video urls to be downloaded, delete videos from sdcard of which
	 * urls are absent, make a new sharedpreference
	 */
	public void chooseURL() {
		int noOfUrlsPref;
		// read no. of video paths already present and thus no. of videos in
		// sdcard.
		{
			noOfUrlsPref = sharedPreferences.getInt("URLCount", 0);
			// Log.d("noOfUrlsPref",Integer.toString(noOfUrlsPref));
			choose = new String[2][noOfUrlsPref]; // 0/1 -path/url
		}
		for (int i = 0; i < noOfUrlsPref; i++) {

			choose[0][i] = sharedPreferences.getString("FilePath" + i,
					"nothing present");
			choose[1][i] = sharedPreferences.getString("URL" + i,
					"nothing present");
			// Log.d("choose0"+i,choose[0][i]);
			// Log.d("choose1"+i,choose[1][i]);
			/*
			 * editPreferences.remove("FilePath"+length2D);
			 * editPreferences.commit();
			 */

		}

		int noOfPlayPaths = 0;
		int y = url.length;
		for (int j = 0; j < y; j++) {
			for (int i = 0; i < choose[1].length; i++) {
				if (url[j].equals(choose[1][i])) {
					noOfPlayPaths++;
				}
			}
		}
		// Log.d("noOfPlayPaths",Integer.toString(noOfPlayPaths));

		playPaths = new String[noOfPlayPaths];
		playURLs = new String[noOfPlayPaths];
		newUrls = new String[y - noOfPlayPaths];
		deletePaths = new String[noOfUrlsPref - noOfPlayPaths];
		int deleteIndex = 0;
		int playIndex = 0;
		int didNotMatch = 0;
		for (int i = 0; i < choose[1].length; i++) {
			didNotMatch = 0;
			for (int j = 0; j < y; j++) { // may be y needs to be changed to no.
											// of urls which are in the shared
											// prefs
				// Log.d("url"+j,url[j]);
				if (url[j].equals(choose[1][i])) {
					// Log.d("url"+j,url[j]);
					// Log.d("choose[1])"+i,choose[1][i]);
					playPaths[playIndex] = choose[0][i];
					playURLs[playIndex] = choose[1][i];
					// Log.d("playPaths"+playIndex,playPaths[playIndex]);
					// Log.d("playURLs"+playIndex,playURLs[playIndex]);
					playIndex++;

				} else {
					didNotMatch++;
					if (didNotMatch == y) { // here (y= url length) is required
						// Log.d("didNotMatch"+i,Integer.toString(didNotMatch));
						// Log.d("choose0"+i,choose[0][i]);
						deletePaths[deleteIndex] = choose[0][i];
						// Log.d("deletePaths"+deleteIndex,deletePaths[deleteIndex]);
						String s = url[j];
						deleteIndex++;
					}
				}
			}
		}

		int newUrlIndex = 0;
		for (int j = 0; j < y; j++) {
			didNotMatch = 0;
			for (int i = 0; i < choose[1].length; i++) { // may be y needs to be
															// changed to no. of
															// urls which are in
															// the shared prefs
				// Log.d("url"+j,url[j]);
				if (url[j].equals(choose[1][i])) {
				} else {
					didNotMatch++;
					if (didNotMatch == choose[1].length) {
						newUrls[newUrlIndex] = url[j];
						Log.d("newUrls" + newUrlIndex, newUrls[newUrlIndex]);
						newUrlIndex++;
					}
				}
			}
		}

		int finalFilled = 0;
		finalURLnPath = new String[2][newUrls.length + noOfPlayPaths]; // path/url
																		// 0/1
		for (int i = 0; i < playPaths.length; i++) {
			String s = playPaths[i];
			finalURLnPath[0][i] = playPaths[i];
			finalURLnPath[1][i] = playURLs[i];
			finalFilled++;
		}

		// calling download, declaring length of filepath before as calling
		// download in a loop
		// length of file path should be newUrls.length however url length is
		// always greater for equal to newurls.length
		{
			filePath = new String[url.length];
			for (int i = 0; i < newUrls.length; i++) {
				downloadInSdcard(newUrls[i], i);
			}
		}

		// after download call set prefernce in a loop
		if (filePath != null) { // cos if urls are less in no. than the shared
								// prefs urls
			int itShouldStartFromZero = 0;
			for (int i = finalFilled; i < finalURLnPath[1].length; i++) {
				Log.d("i", Integer.toString(i));
				Log.d("itShouldStartFromZero",
						Integer.toString(itShouldStartFromZero));
				Log.d("filePath" + itShouldStartFromZero,
						filePath[itShouldStartFromZero]);
				finalURLnPath[0][i] = filePath[itShouldStartFromZero]; // filepath
																		// is
																		// actually
																		// the
																		// new
																		// path
				finalURLnPath[1][i] = newUrls[itShouldStartFromZero];
				itShouldStartFromZero++;

			}
		}

		{
			editPreferences.clear();
			editPreferences.commit();
		}

		Log.d("finalURLnPath0 length",
				Integer.toString(finalURLnPath[0].length));
		Log.d("finalURLnPath1 length",
				Integer.toString(finalURLnPath[1].length));
		for (int i = 0; i < finalURLnPath[1].length; i++) {
			Log.d("finalURLnPath0" + i, finalURLnPath[0][i]);
			Log.d("finalURLnPath1" + i, finalURLnPath[1][i]);
			preferencesSet(i);
		}

		for (int i = 0; i < deletePaths.length; i++) {
			Log.d("DeletPath", deletePaths[i]);
			Log.d("DeletFile", Integer.toString(i));

			File f = new File(deletePaths[i]);
			f.delete();
		}

		// on constructing the paths of all videos.
		// callGraphicalActivity();
	}// chooseURL ends

	// get the app preference and its editor.
	public void preferences() {

		sharedPreferences = getSharedPreferences("TrackDownload",
				MODE_WORLD_WRITEABLE);
		editPreferences = sharedPreferences.edit();
	}

	// set key values in preferences.
	public void preferencesSet(int i) {
		editPreferences.putInt("URLCount", url.length);
		editPreferences.putString("FilePath" + i, finalURLnPath[0][i]);
		editPreferences.putString("URL" + i, finalURLnPath[1][i]);
		editPreferences.commit();
		urlIndex++;

	}

	public void preferencesSetFirst() {
		editPreferences.putInt("URLCount", url.length);
		editPreferences.putString("FilePath" + urlIndex, filePath[urlIndex]);
		editPreferences.putString("URL" + urlIndex, url[urlIndex]);
		editPreferences.commit();
		urlIndex++;
	}

	// check if there are any entries in the shared preferences.
	public boolean checkLogInInfo() {

		int x = sharedPreferences.getInt("URLCount", 0);
		Log.d("checkLogInInfo", "URLCount=" + x);
		if (x == 0)
			return true;
		else
			return false;
		/* boolean n=sharedPreferences.contains("FilePath1"); */
	}

	// download videos in sdcard.
	public void downloadInSdcard(String urlMethod, int filePathIndex) {

		// first clear the preferences so that it will be set completely a new
		// after downloads.

		try {
			connection = new URL(urlMethod).openConnection();
			connection.connect();
			iStream = connection.getInputStream();

		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			file = File.createTempFile("myFile", ".mp4");
			filePath[urlIndex] = file.getAbsolutePath();

			Log.d("Down filePat" + urlIndex, filePath[urlIndex]);
			urlIndex++;
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			fOutStream = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {

			do {
				int byteRead = iStream.read(buffer);
				if (byteRead <= 0)
					break;
				fOutStream.write(buffer, 0, byteRead);
			} while (true);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				iStream.close();
				fOutStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// preferencesSet();
	}

	public void downloadInSdcardFirst(String urlMethod) {

		// first clear the preferences so that it will be set completely anew
		// after downloads.

		// filePath=new String[url.length];

		try {
			connection = new URL(urlMethod).openConnection();
			connection.connect();
			iStream = connection.getInputStream();

		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			file = File.createTempFile("myFile", ".mp4");
			filePath[urlIndex] = file.getAbsolutePath();
			Log.d("filePath.length", Integer.toString(filePath.length));
			Log.d("Down filePat" + urlIndex, filePath[urlIndex]);
			// urlIndex++;
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			fOutStream = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {

			do {
				int byteRead = iStream.read(buffer);
				if (byteRead <= 0)
					break;
				fOutStream.write(buffer, 0, byteRead);
			} while (true);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				iStream.close();
				fOutStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		preferencesSetFirst();

	}

	public void callGraphicalActivity() {
		Intent intent = new Intent(getApplicationContext(), TwoSizeVideo.class);
		Bundle b = new Bundle();
		b.putInt("ID", idCompanySelectDownload);
		if (downloadFirst) {
			b.putStringArray("VideoPaths", filePath);
		} else if (downloadOtherThanFirst) {
			b.putStringArray("VideoPaths", finalURLnPath[0]);
		}
		intent.putExtras(b);
		startActivity(intent);
		finish();
		DL.setVisibility(View.VISIBLE);
		AL.setVisibility(View.VISIBLE);
		date.setVisibility(View.VISIBLE);

	}
}

/*
 * BufferedReader in = null; BufferedWriter out = null; try { File f=new
 * File("mnt/sdcard/a.txt"); File ff=new File("mnt/sdcard/LazyList/af.txt"); in
 * = new BufferedReader(new FileReader(f)); out = new BufferedWriter(new
 * FileWriter(ff)); int c;
 * 
 * while ((c = in.read()) != -1) { Toast.makeText(getApplicationContext(),
 * Integer.toString(c), Toast.LENGTH_LONG).show(); out.write(c); }
 * 
 * } catch (FileNotFoundException e) { // TODO Auto-generated catch block
 * e.printStackTrace(); } catch (IOException e) { // TODO Auto-generated catch
 * block e.printStackTrace(); } finally { if (in != null) { try { in.close(); if
 * (out != null) { out.close(); } }catch (IOException e) { // TODO
 * Auto-generated catch block e.printStackTrace(); } }
 */