package digital_signage_new.com;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class TwoSizeVideo extends Activity implements SurfaceHolder.Callback,
		OnCompletionListener, OnPreparedListener {

	private static final String TAG = "MediaPlayerX";
	// private Animation marqueeAnimation;
	// private TransitionDrawable transition;
	private AbsoluteLayout ln;
	private JSONArray arrJson;
	private JSONArray arrJsonInfo;
	private Integer[] videoMute;// ={1,0};//1 for mp3

	/*
	 * private Handler backgroundHandler = new Handler() {public void
	 * handleMessage(Message msg){ if(x%2==0){transition.startTransition(5000);}
	 * else{transition.reverseTransition(5000);} x=x+1;}};
	 */
	private Handler handlerCompanyInfo = new Handler() {
		public void handleMessage(Message msg) {
			setCompanyInfo(i);
			i++;// if(i==arrJsonInfo.length()-1){i=0;}
			if (i == companyInfoDescription.length) {
				i = 0;
			}
		}
	};
	private Handler handlerTicker = new Handler() {
		public void handleMessage(Message msg) {
			newsroll.setText("• " + tickerHeadLines[tickerIndex] + ".");
			tickerIndex++;
			if (tickerIndex == tickerHeadLines.length) {
				tickerIndex = 0;
			}
		}
	};

	private String[] companyInfoHead, companyInfoDescription;// ={"a","b","c","d"},companyInfoDescription={"aa","bb","cc","dd"};
	private String[] tickerHeadLines;
	private TextView company1, company2, newsroll, dateText, tagLine;
	private VideoView videoview, videoviewsmall;
	private SurfaceHolder holder;
	private MediaPlayer player, mediaPlayerAudio;
	private String[] videoLinks;// ={"http://192.168.0.98/android/dsn/cheetah.3gp","http://192.168.0.98/android/dsn/ironman.mp4"};
	private String[] tempPath;
	private File file;
	private int videoIncrementer = 0;
	private int mVideoWidth, mVideoHeight, i = 0, tickerIndex;
	private Integer[] videoSizeController;// ={0,1};
	private int videoSizeIndex, x = 0;
	private boolean mp3Play = true, jsonVideoOnlyOnce = true;
	private boolean bool = true, releasePlayerDecide = false;
	private Thread threadTicker;
	private ProgressDialog dialog;
	int idForThisCompany;
	private Drawable drawable;
	private ImageView compLogo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// read the login id frm LogIn activity
		{
			Bundle b = getIntent().getExtras();
			tempPath = b.getStringArray("VideoPaths");
			idForThisCompany = b.getInt("ID");
		}
		Log.d("TwoSizeVideo", "onCreate");
		ln = (AbsoluteLayout) findViewById(R.id.LinearLayout01);
		jsonBackground();
		setTagLogo();
		videoviewsmall = (VideoView) findViewById(R.id.videoviewsmall);
		holder = videoviewsmall.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		jsonCompanyInfo();
		// videoSizeControl();

		// transition= (TransitionDrawable) ln.getBackground();

		company1 = (TextView) findViewById(R.id.compHead);
		company2 = (TextView) findViewById(R.id.compDescription);
		dateText = (TextView) findViewById(R.id.dateText);
		newsroll = (TextView) findViewById(R.id.marqee);
		setAllTypefaces();
		setDate();
		// marqueeAnimation=AnimationUtils.loadAnimation(getApplicationContext(),R.anim.marquee);

		/*
		 * Thread background = new Thread (new Runnable() { public void run() {
		 * try { while (true) { Thread.sleep(5000);
		 * backgroundHandler.sendMessage(backgroundHandler.obtainMessage()); } }
		 * catch (java.lang.InterruptedException e) {} } }); background.start();
		 */

		Thread threadCompanyInfo;
		threadCompanyInfo = new Thread(new Runnable() {

			@Override
			public void run() {
				int j = 0;
				while (j < 3) {

					handlerCompanyInfo.sendMessage(handlerCompanyInfo
							.obtainMessage());
					try {
						Thread.sleep(1000 * 30);

					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});

		threadCompanyInfo.start();

		threadTicker = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					handlerTicker.sendMessage(handlerTicker.obtainMessage());
					try {
						Thread.sleep(1000 * 10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});

		mediaPlayerAudio = MediaPlayer.create(getApplicationContext(),
				R.raw.somebodyisme);
		jsonTicker();
	}// onCreate end

	void videoSizeControl() {
		Log.d("TwoSizeVideo", "videoSizeControl");

		// if(videoSizeController[videoSizeIndex]==0)
		{
			videoSmallSize();
		}
		/*
		 * else{ Intent i=new Intent(this,FullVideo.class); Bundle b=new
		 * Bundle(); b.putString("VideoLink", tempPath[videoIncrementer]);
		 * b.putInt("VideoMute", videoMute[videoIncrementer]); i.putExtras(b);
		 * //if(mediaPlayerAudio.isPlaying()){mediaPlayerAudio.release();}
		 * /*if(videoIncrementer
		 * <videoLinks.length-1&&videoSizeIndex<videoLinks.length-1){
		 * videoIncrementer++;videoSizeIndex++; }
		 * else{videoIncrementer=0;videoSizeIndex=0;}
		 */
		/*
		 * if(releasePlayerDecide) {
		 * player.release();player=null;doCleanUp();releasePlayerDecide=true;}
		 */
		// startActivity(i);
		// startActivityForResult(i, 1);
		// videoFullSize();

		// }
	}

	/*
	 * void videoFullSize() { videoview.setVisibility(View.VISIBLE); //-
	 * activity prefered as the background activity is paused.
	 * videoviewsmall.setVisibility(View.INVISIBLE);
	 * holder=videoview.getHolder(); holder.addCallback(this); playVideo(); }
	 */

	void videoSmallSize() {

		Log.d("TwoSizeVideo", "videoSmallSize");

		// Toast.makeText(getApplicationContext(),
		// "Insmal",Toast.LENGTH_LONG).show();
		// holder=videoviewsmall.getHolder();
		// holder.addCallback(this);
		/*
		 * if(releasePlayerDecide) { Log.d("TwoSizeVideo"
		 * ,"videoSmallSize playerrelease");
		 * player.release();releasePlayerDecide=true;}
		 */
		playVideo();
	}

	void videoIncrement() {
		Log.d("TwoSizeVideo", "videoIncrement");
		if (videoIncrementer < tempPath.length - 1) {
			videoIncrementer++;
		}
		/*
		 * if(videoIncrementer<videoLinks.length-1&&videoSizeIndex<videoLinks.length
		 * -1){ videoIncrementer++;videoSizeIndex++; }
		 * else{videoIncrementer=0;videoSizeIndex=0;}
		 */
		videoSizeControl();
	}

	public void videoMute() {
		Log.d("MediaPlayerDemo", "videoMute called");

		player.setVolume(0, 0);
		// if(mediaPlayerAudio)
		if (mp3Play) {
			playMp3();
			mp3Play = false;
		} else {
			mediaPlayerAudio.start();
		}
		return;
	}

	public void videoAudioPlay() {
		Log.d("MediaPlayerDemo", "videoAudioPlay called");
		// if(mediaPlayerAudio!=null){
		if (mediaPlayerAudio.isPlaying()) {
			mediaPlayerAudio.pause();
		}// }
		player.setVolume(1, 1);
		return;
	}

	void playVideo() {
		try {
			Log.d("TwoSizeVideo", "playVideo");

			player = new MediaPlayer();
			// if(videoMute[videoIncrementer]==0)videoAudioPlay();
			// else if(videoMute[videoIncrementer]==1)videoMute();
			player.setDataSource(tempPath[videoIncrementer]);
			// player.setDataSource(videoLinks[videoIncrementer]);
			player.setDisplay(holder);
			player.prepareAsync();
			player.setOnCompletionListener(this);
			player.setOnPreparedListener(this);

		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void startVideoPlayback() {

		Log.d("TwoSizeVideo", "startVideoPlayback");

		holder.setFixedSize(mVideoWidth, mVideoHeight);
		player.start();
	}

	private void playMp3() {
		Log.d("TwoSizeVideo", "playMp3");

		try {// audioStreamer.startStreaming("http://91.82.85.71:9216",5208,
				// 216);
				// mediaPlayerAudio=new MediaPlayer();
				// mediaPlayerAudio.setDataSource("http://download400.mediafire.com/69jx5slifpgg/t4mjmrynlrm/Michael+Jackson+-+Shout.mp3");
				// mediaPlayerAudio.prepare();
			mediaPlayerAudio = MediaPlayer.create(getApplicationContext(),
					R.raw.somebodyisme);
			mediaPlayerAudio.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayerAudio.start();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * private void jsonVideo() {if(jsonVideoOnlyOnce) { Log.d("TwoSizeVideo"
	 * ,"jsonVideo");
	 * 
	 * Json1 jsonObj = new Json1(); // arrJson = jsonObj.parseJsonData(
	 * "http://bytmediacenter.com/android/php/data.php?id=8&action=VIDEO");
	 * arrJson = jsonObj.parseJsonData("http://bytmediacenter.com/" +
	 * "android/php/data.php?id="+idForThisCompany+"&action=VIDEO");
	 * videoLinks=new String[arrJson.length()]; videoMute=new
	 * Integer[arrJson.length()]; videoSizeController=new
	 * Integer[arrJson.length()]; for(int j=0;j<arrJson.length();j++){
	 * JSONObject json_data; try { json_data = arrJson.getJSONObject(j);
	 * videoLinks[j] = json_data.getString("video_link");
	 * videoMute[j]=Integer.parseInt(json_data.getString("music"));
	 * videoLinks[j]=videoLinks[j].trim().replaceAll(" ", "%20");
	 * videoSizeController
	 * [j]=Integer.parseInt(json_data.getString("fullscreen")); } catch
	 * (JSONException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } }
	 * 
	 * tempPath=new String[arrJson.length()];
	 * 
	 * for(int i=0;i<arrJson.length();i++){ try { storeSDCard(videoLinks[i],i);
	 * } catch (IOException e) {e.printStackTrace();} }
	 * }jsonVideoOnlyOnce=false;
	 * 
	 * }
	 * 
	 * private void storeSDCard(String path,int arrayIndex) throws IOException {
	 * bool=false; try{ Log.d("TwoSizeVideo" ,"storeSDCard"); URLConnection cn =
	 * new URL(path).openConnection(); cn.connect(); InputStream istream =
	 * cn.getInputStream(); // try { file =
	 * File.createTempFile("downloadingMediaFile",".dat"); tempPath[arrayIndex]=
	 * file.getAbsolutePath(); // }catch(Exception e){ // Intent i=new
	 * Intent(this,InsertSD.class); //startActivity(i); //finish();}
	 * FileOutputStream out = new FileOutputStream(file); byte buf[] = new
	 * byte[16384]; do { int numread = istream.read(buf); if (numread <= 0)
	 * break; out.write(buf, 0, numread); // ... } while (true); out.close();
	 * istream.close(); file.deleteOnExit(); } catch(Exception e){
	 * Toast.makeText(getApplicationContext(),"", Toast.LENGTH_LONG).show(); } }
	 */

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			try {
				if (mediaPlayerAudio.isPlaying()) {
					mediaPlayerAudio.release();
				}
				if (player.isPlaying()) {
					player.release();
				}

				/*
				 * for(int i=0;i<tempPath.length;i++) { File f=new
				 * File(tempPath[i]); f.delete(); }
				 */
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), e.toString(),
						Toast.LENGTH_LONG).show();
			}
			finish();
		}
		return true;
	}

	/*
	 * private void storeSDCard(String path,int arrayIndex) throws IOException {
	 * bool=false; try{ URLConnection cn = new URL(path).openConnection();
	 * cn.connect(); InputStream istream = cn.getInputStream(); // try { file =
	 * File.createTempFile("downloadingMediaFile",".dat"); tempPath[arrayIndex]=
	 * file.getAbsolutePath(); // }catch(Exception e){ // Intent i=new
	 * Intent(this,InsertSD.class); //startActivity(i); //finish();}
	 * FileOutputStream out = new FileOutputStream(file); byte buf[] = new
	 * byte[16384]; do { int numread = istream.read(buf); if (numread <= 0)
	 * break; out.write(buf, 0, numread); // ... } while (true); out.close();
	 * istream.close(); file.deleteOnExit(); } catch(Exception e){
	 * Toast.makeText(getApplicationContext(),"", Toast.LENGTH_LONG).show(); } }
	 */

	private void jsonCompanyInfo() {
		Log.d("TwoSizeVideo", "jsonCompanyInfo");

		Json1 jsonObj = new Json1();
		arrJsonInfo = jsonObj.parseJsonData("http://bytmediacenter.com/"
				+ "android/php/data.php?id=" + idForThisCompany
				+ "&action=COMP");
		companyInfoHead = new String[arrJsonInfo.length()];
		companyInfoDescription = new String[arrJsonInfo.length()];
		for (int j = 0; j < arrJsonInfo.length(); j++) {
			JSONObject json_data;
			try {
				json_data = arrJsonInfo.getJSONObject(j);
				companyInfoHead[j] = json_data.getString("title");
				companyInfoDescription[j] = json_data.getString("desc");
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

		Log.d("TwoSizeVideo", "surfaceChanged");

		if (width == 0 || height == 0) {
			Log.e(TAG, "invalid video width(" + width + ") or height(" + height
					+ ")");
			return;
		}

		mVideoWidth = width;
		mVideoHeight = height;
		// callbackWidth=width;
		// callbackHeight=height;

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		Log.d("TwoSizeVideo", "surfaceCreated");
		if (bool) {
			new DownloadFileAsync().execute();
			// jsonVideo();
			// playVideo();
		}

		// jsonVideo();
		// holder.setFixedSize(mVideoWidth, mVideoHeight);
		holder.setFixedSize(650, 300);
		// videoSizeControl();
		// playVideo();
		// Toast.makeText(getApplicationContext(),
		// "surfaceCreated",Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.d("TwoSizeVideo", "onRestart");

		videoIncrement();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {

			Log.d("TwoSizeVideo", Integer.toString(requestCode));
			// finish();
		}
		Log.d("TwoSizeVideo", "onActivityResult");

	}

	/*
	 * @Override protected void onResume(){ super.onResume(); videoIncrement();
	 * }
	 */

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

		Log.d("TwoSizeVideo", "surfaceDestroyed");
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		// TODO Auto-generated method stub
		Log.d("TwoSizeVideo", "onPrepared");
		startVideoPlayback();

	}

	@Override
	public void onCompletion(MediaPlayer mp) {

		Log.d("TwoSizeVideo", "onCompletion");
		releasePlayerDecide = true;
		if (mp.equals(player)) {
			player.release();
			player = null;
			doCleanUp();
			videoIncrement();
		}
		if (mp.equals(mediaPlayerAudio)) {
			mediaPlayerAudio.release();
			mediaPlayerAudio = MediaPlayer.create(getApplicationContext(),
					R.raw.somebodyisme);
			playMp3();
		}

	}

	private void setCompanyInfo(int i) {
		Log.d("TwoSizeVideo", "setCompanyInfo");

		company1.setText(companyInfoHead[i]);
		company2.setText(companyInfoDescription[i]);
	}

	private void jsonTicker() {
		Log.d("TwoSizeVideo", "jsonTicker");

		Json1 jsonObj = new Json1();
		String tickerLink = "";
		arrJson = jsonObj.parseJsonData("http://bytmediacenter.com/"
				+ "android/php/data.php?id=" + idForThisCompany
				+ "&action=TICKER");
		for (int j = 0; j < arrJson.length(); j++) {
			JSONObject json_data;
			try {
				json_data = arrJson.getJSONObject(j);
				tickerLink = json_data.getString("link");
				tickerLink = tickerLink.trim().replaceAll(" ", "%20");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		try {
			tickerHeadLines = new String[new NewsRoll().yo(tickerLink).length];
			tickerHeadLines = new NewsRoll().yo(tickerLink);

			threadTicker.start();
			// newsroll.setText(new NewsRoll().yo(tickerLink));
			// newsroll.startAnimation(marqueeAnimation);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// get the link to background image using json
	private void jsonBackground() {
		Log.d("TwoSizeVideo", "jsonBackground");

		Json1 jsonObj = new Json1();
		String backgroundURL = "";
		Log.d("idForThisCompany", Integer.toString(idForThisCompany));
		arrJson = jsonObj
				.parseJsonData("http://bytmediacenter.com/"
						+ "android/php/data.php?id=" + idForThisCompany
						+ "&action=UID");

		for (int j = 0; j < arrJson.length(); j++) {
			JSONObject json_data;
			try {
				json_data = arrJson.getJSONObject(j);
				backgroundURL = json_data.getString("theme");
				backgroundURL = backgroundURL.trim().replaceAll(" ", "%20");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		// set background from input stream
		{
			Log.d("jsonBackground", backgroundURL);
			URLConnection cn;
			try {
				cn = new URL(backgroundURL).openConnection();
				cn.connect();
				InputStream istream = cn.getInputStream();
				drawable = Drawable.createFromStream(istream, "abc");

				ln.setBackgroundDrawable(drawable);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	// get the TagLine set tag to the text, get Url of Logo
	private void setTagLogo() {
		Log.d("TwoSizeVideo", "setTagLogo");
		{
			tagLine = (TextView) findViewById(R.id.tagline);
			compLogo = (ImageView) findViewById(R.id.compLogo);
		}
		Json1 jsonObj = new Json1();
		String logoURL = "", tagLineText = "";

		Log.d("idForThisCompany", Integer.toString(idForThisCompany));
		arrJson = jsonObj
				.parseJsonData("http://www.bytmediacenter.com/android/"
						+ "php/data.php?action=UID&id=" + idForThisCompany);

		for (int j = 0; j < arrJson.length(); j++) {
			JSONObject json_data;
			try {
				json_data = arrJson.getJSONObject(j);
				logoURL = json_data.getString("logo_img");
				tagLineText = json_data.getString("tag_line");
				logoURL = logoURL.trim().replaceAll(" ", "%20");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		// set background from input stream
		{
			Log.d("jsonBackground", logoURL);
			URLConnection cn;
			try {
				cn = new URL(logoURL).openConnection();
				cn.connect();
				InputStream istream = cn.getInputStream();
				drawable = Drawable.createFromStream(istream, "abc");
				compLogo.setImageDrawable(drawable);
				// ln.setBackgroundDrawable(drawable);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		tagLine.setText(tagLineText);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d("TwoSizeVideo", "onDestroy");
		releaseMediaPlayer();
	}

	private void releaseMediaPlayer() {
		Log.d("TwoSizeVideo", "releaseMediaPlayer");

		if (player != null) {
			player.release();
			player = null;
		}
	}

	private void doCleanUp() {
		mVideoWidth = 0;
		mVideoHeight = 0;

	}

	public MediaPlayer getMediaPlayer() {
		Log.d("TwoSizeVideo", "getMediaPlayer");

		return mediaPlayerAudio;
	}

	public void pauseMediaPlayer() {

		Log.d("TwoSizeVideo", "pauseMediaPlayer");

		if (mediaPlayerAudio.isPlaying()) {
			mediaPlayerAudio.pause();
		}
	}

	private void setDate() {
		String[] month = { "JANUARY", "FERBRUARY", "MARCH", "APRIL", "MAY",
				"JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOV",
				"DECEMBER" };
		String[] day = { "SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY",
				"THURSDAY", "FRIDAY", "SATURDAY" };
		Calendar c = Calendar.getInstance();
		String date = day[c.get(Calendar.DAY_OF_WEEK) - 1] + ", "
				+ month[c.get(Calendar.MONTH)] + " "
				+ c.get(Calendar.DAY_OF_MONTH) + ", " + c.get(Calendar.YEAR);
		dateText.setText(date);
	}

	private void setAllTypefaces() {
		Typeface font = Typeface
				.createFromAsset(getAssets(), "fonts/arial.ttf");
		newsroll.setTypeface(font);
		/*
		 * font = Typeface.createFromAsset(getAssets(),
		 * "fonts/ChaparralProBold.otf"); company1.setTypeface(font); font =
		 * Typeface.createFromAsset(getAssets(),
		 * "fonts/ChaparralProRegular.otf"); company2.setTypeface(font);
		 */
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		dialog = new ProgressDialog(this);
		dialog.setMessage("Loading Please Wait...");
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		return dialog;
	}

	class DownloadFileAsync extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			showDialog(RESULT_OK);
		}

		@Override
		protected String doInBackground(String... unused) {
			// jsonVideo();
			return null;
		}

		protected void onProgressUpdate(String... progress) {
			dialog.setProgress(Integer.parseInt(progress[0]));
		}

		@Override
		protected void onPostExecute(String unused) {
			videoSizeControl();
			// playVideo();
			dialog.dismiss();
		}
	}
}