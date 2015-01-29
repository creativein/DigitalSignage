package digital_signage_new.com;

//Music completeness.
import java.io.IOException;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.Toast;
import android.widget.VideoView;
import android.app.Activity;
import android.content.Intent;

public class FullVideo extends Activity implements SurfaceHolder.Callback,
		OnCompletionListener, OnPreparedListener {

	private int videoMute;
	private boolean mp3Play = true;

	VideoView videoview;
	SurfaceHolder holder;
	MediaPlayer player, mediaPlayerAudio;
	int callbackWidth, callbackHeight;
	String videolink;
	Bundle bundle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main2);
		videoview = (VideoView) findViewById(R.id.videoviewfullscreen);

		{
			bundle = getIntent().getExtras();
			videolink = bundle.getString("VideoLink");
			videoMute = bundle.getInt("VideoMute");
		}
		holder = videoview.getHolder();
		holder.addCallback(this);
		if (mediaPlayerAudio == null) {
			Toast.makeText(getApplicationContext(), "Null", Toast.LENGTH_LONG)
					.show();
		}

	}

	public void videoMute() {
		Log.d("MediaPlayerDemo", "videoMute called");
		Toast.makeText(getApplicationContext(), "videoMute", Toast.LENGTH_LONG)
				.show();
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
		Toast.makeText(getApplicationContext(), "videoAudioPlay",
				Toast.LENGTH_LONG).show();
		Log.d("MediaPlayerDemo", "videoAudioPlay called");
		// if(mediaPlayerAudio!=null){
		// if(mediaPlayerAudio.isPlaying()){mediaPlayerAudio.pause();}//}
		// if(videoMute==0){new TwoSizeVideo().pauseMediaPlayer();}
		player.setVolume(1, 1);
		return;
	}

	void playVideo() {
		try {
			player = new MediaPlayer();
			if (videoMute == 0)
				videoAudioPlay();
			else if (videoMute == 1)
				videoMute();
			player.setDataSource(videolink);
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

	private void playMp3() {

		try {// audioStreamer.startStreaming("http://91.82.85.71:9216",5208,
				// 216);
				// mediaPlayerAudio=new MediaPlayer();
				// mediaPlayerAudio.setDataSource("http://download400.mediafire.com/69jx5slifpgg/t4mjmrynlrm/Michael+Jackson+-+Shout.mp3");
				// mediaPlayerAudio.prepare();
			mediaPlayerAudio = MediaPlayer.create(getApplicationContext(),
					R.raw.somebodyisme);
			mediaPlayerAudio.setOnCompletionListener(this);
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

	void startVideoPlayback() {
		holder.setFixedSize(callbackWidth, callbackHeight);
		player.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		callbackWidth = width;
		callbackHeight = height;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		playVideo();
		Toast.makeText(getApplicationContext(), "surfaceCreated",
				Toast.LENGTH_LONG).show();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		// TODO Auto-generated method stub
		startVideoPlayback();
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		if (mp.equals(player)) {
			player.release();
			finish();
		}
		if (mp.equals(mediaPlayerAudio)) {
			mediaPlayerAudio.release();
			mediaPlayerAudio = MediaPlayer.create(getApplicationContext(),
					R.raw.somebodyisme);
			playMp3();
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		releaseMediaPlayer();
		/*
		 * if(!player.isPlaying()){ Intent i=new
		 * Intent(this,TwoSizeVideo.class); setResult(1);}
		 */
	}

	private void releaseMediaPlayer() {
		if (player != null) {
			player.release();
			player = null;
		}
		try {
			if (mediaPlayerAudio.isPlaying()) {
				mediaPlayerAudio.release();
			}
			if (player.isPlaying()) {
				player.release();
			}
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.toString(),
					Toast.LENGTH_LONG).show();
		}

	}
}