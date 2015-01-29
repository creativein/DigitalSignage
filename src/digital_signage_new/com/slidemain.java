package digital_signage_new.com;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class slidemain extends Activity {
	Animation fadein, fadeout, shake;
	static int current_image = 0;
	ImageView first_image, second_image;
	RelativeLayout layoutmain;
	boolean thread_running_flag = true;

	Integer[] imagelist = { R.drawable.tour_01, R.drawable.tour_01,
			R.drawable.tour_02, R.drawable.tour_03, R.drawable.tour_04,
			R.drawable.tour_05, R.drawable.tour_06, R.drawable.tour_07,
			R.drawable.tour_08, R.drawable.tour_09, R.drawable.tour_10,
			R.drawable.tour_11, R.drawable.tour_12, R.drawable.tour_13,
			R.drawable.tour_14, R.drawable.tour_15, R.drawable.tour_18,
			R.drawable.tour_19, R.drawable.tour_20, R.drawable.tour_21,
			R.drawable.tour_22, R.drawable.tour_23, R.drawable.tour_24,
			R.drawable.tour_25, R.drawable.tour_26, R.drawable.tour_27,
			R.drawable.tour_28, R.drawable.tour_29, R.drawable.tour_29 };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.main);

		setContentView(R.layout.splashscreen);

		layoutmain = (RelativeLayout) findViewById(R.id.splash_Layout01);
		first_image = (ImageView) findViewById(R.id.splash_imageView1);
		second_image = (ImageView) findViewById(R.id.splash_imageView2);
		fadein = AnimationUtils.loadAnimation(this, R.anim.fadein);
		fadeout = AnimationUtils.loadAnimation(this, R.anim.fadeout);
		shake = AnimationUtils.loadAnimation(this, R.anim.shake);

		fadeout.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub

				second_image.startAnimation(fadein);
			}
		});
		shake.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				first_image.startAnimation(shake);
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				first_image.startAnimation(shake);
			}
		});

		// Here is the heavy-duty thread
		Thread t = new Thread() {

			public void run() {
				while (thread_running_flag) {
					// Send update to the main thread
					messageHandler.sendMessage(Message.obtain(messageHandler,
							current_image));

					current_image++;
					System.out.println(current_image);

					try {
						Thread.sleep(20000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
			}
		};
		if (imagelist.length != 0) {
			second_image.startAnimation(fadeout);
			t.start();
		} else {
			second_image.setImageResource(imagelist[0]);
		}
	}

	/********************** hander ******************/
	Handler messageHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (current_image <= imagelist.length) {
				// first_image.startAnimation(fadeout);
				Log.e("imagelist.lenth", Integer.toString(imagelist.length));
				if (current_image > 1 && current_image < imagelist.length - 2) {
					first_image.setBackgroundResource(R.drawable.tour_bmt_logo);
					first_image.setVisibility(View.VISIBLE);
					// first_image.startAnimation(shake);
				}

				// first_image.setImageResource(imgarr[current_image]);
				second_image.setImageResource(imagelist[current_image]);
				Log.e("image assigned", "");
				second_image.startAnimation(fadeout);

			}
			/*
			 * else { thread_running_flag=false;
			 * first_image.setVisibility(View.INVISIBLE); Intent login=new
			 * Intent(getApplicationContext(),LogIn.class);
			 * startActivity(login); }
			 */
			if (current_image == imagelist.length - 2) {
				thread_running_flag = false;
				first_image.setVisibility(View.INVISIBLE);
				Log.e("you are in", "intenet");
				Intent login_intent = new Intent(getApplicationContext(),
						LogIn.class);
				startActivity(login_intent);
				// finish();
			}
		}
	};// end of handler

	/**** key events *****************/

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == event.KEYCODE_DPAD_UP) {
			current_image++;
			if (current_image > 1 && current_image < imagelist.length - 2) {
				first_image.setBackgroundResource(R.drawable.tour_bmt_logo);
				first_image.setVisibility(View.VISIBLE);
				// first_image.startAnimation(shake);
			}
			second_image.setImageResource(imagelist[current_image]);
			second_image.startAnimation(fadeout);

			Log.e("up keycode", Integer.toString(keyCode));
			Log.e("index at up", Integer.toString(current_image));
		}
		if (keyCode == event.KEYCODE_DPAD_DOWN) {
			current_image--;
			if (current_image > 1 && current_image < imagelist.length - 2) {
				first_image.setBackgroundResource(R.drawable.tour_bmt_logo);
				first_image.setVisibility(View.VISIBLE);
				// first_image.startAnimation(shake);
			}

			second_image.setImageResource(imagelist[current_image]);
			second_image.startAnimation(fadeout);

			Log.e("down keycode", Integer.toString(keyCode));
			Log.e("index at down", Integer.toString(current_image));
		}
		if (keyCode == event.KEYCODE_DPAD_RIGHT) {
			current_image++;
			if (current_image > 1 && current_image < imagelist.length - 2) {
				first_image.setBackgroundResource(R.drawable.tour_bmt_logo);
				first_image.setVisibility(View.VISIBLE);
				// first_image.startAnimation(shake);
			}

			second_image.setImageResource(imagelist[current_image]);
			second_image.startAnimation(fadeout);

			Log.e("right keycode", Integer.toString(keyCode));
			Log.e("index at right", Integer.toString(current_image));
		}
		if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			current_image--;
			if (current_image > 1 && current_image < imagelist.length - 2) {
				first_image.setBackgroundResource(R.drawable.tour_bmt_logo);
				first_image.setVisibility(View.VISIBLE);
				// first_image.startAnimation(shake);
			}

			second_image.setImageResource(imagelist[current_image]);
			second_image.startAnimation(fadeout);

			Log.e("left keycode", Integer.toString(keyCode));
			Log.e("index at left", Integer.toString(current_image));
		}
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return true;
	}// End Of Key Event

}
