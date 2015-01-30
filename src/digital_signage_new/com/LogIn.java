package digital_signage_new.com;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.animation.ScaleAnimation;
import android.widget.AbsoluteLayout;
import android.widget.AbsoluteLayout.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LogIn extends Activity implements OnFocusChangeListener {
	SharedPreferences logInPreferences;
	SharedPreferences.Editor editlogInPreferences;
	String userName, password;
	private JSONArray arrJson;
	int logInId;
	Button signin, close, tour;
	EditText etxt_user, etxt_pass;
	AbsoluteLayout layout1, layout2;
	int returnId;
	ScaleAnimation imgbtnAnim;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginpage);
		preferences();
		init();
		startLogIn();
		etxt_user.setOnFocusChangeListener(this);
		etxt_pass.setOnFocusChangeListener(this);
		signin.setOnFocusChangeListener(this);
		close.setOnFocusChangeListener(this);
		tour.setOnFocusChangeListener(this);
	}

	public void init() {
		etxt_user = (EditText) findViewById(R.id.txt_username);
		etxt_pass = (EditText) findViewById(R.id.txt_password);
		signin = (Button) findViewById(R.id.btn_sign_in);
		close = (Button) findViewById(R.id.btn_close);
		tour = (Button) findViewById(R.id.btn_tour);
		layout1 = (AbsoluteLayout) findViewById(R.id.LinearLayout01);
		layout2 = (AbsoluteLayout) findViewById(R.id.LinearLayout02);

		close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.exit(1);
			}
		});

		tour.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent tour = new Intent(getApplicationContext(),
						slidemain.class);
				startActivity(tour);
				layout2.setVisibility(View.INVISIBLE);
				layout1.setLayoutParams(new AbsoluteLayout.LayoutParams(0, 20,
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			}
		});
	}

	public void startLogIn() {

		if (checkLogInInfo() == 0) {
			signin.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					getUserNamePassworEditText();
					logInNow();
				}
			});

		} else {
			// get ID fron shar prefs and start signage.
			alreadyLoggedIn();
		}
	}

	public void getUserNamePassworEditText() {

		userName = etxt_user.getText().toString();
		password = etxt_pass.getText().toString();
	}

	public void preferences() {
		logInPreferences = getSharedPreferences("LogIn", MODE_PRIVATE);
		editlogInPreferences = logInPreferences.edit();
	}

	public void setPrferencesAfterLogIn() {

		editlogInPreferences.putInt("LogIn-ID", logInId);
		editlogInPreferences.commit(); // Very important
		checkLogInInfo();
		alreadyLoggedIn();
		// TextView text = (TextView) findViewById(R.id.myEditText);
		// text.setText("This is the first time logging in and ur id is "+returnId);
	}

	public int checkLogInInfo() {
		returnId = logInPreferences.getInt("LogIn-ID", 0);
		return returnId;
	}

	public void alreadyLoggedIn() {

		Intent intent = new Intent(getApplicationContext(),
				SelectDownload.class);
		Bundle b = new Bundle();
		b.putInt("ID", returnId);
		intent.putExtras(b);
		startActivity(intent);
		finish();

	}

	// send username, password in url using json, read the id received then call
	// setPrferencesAfterLogIn()
	// and save the ID
	public void logInNow() {

		Json1 jsonObj = new Json1();
//		arrJson = jsonObj.parseJsonData("http://www.boxmytv.com/mytv/webservice/config.php?&un=" + userName + "&psw="
//				+ password);
		arrJson = jsonObj.parseJsonData("http://www.boxmytv.com/mytv/webservice/config.php?un=" + userName + "&psw="+ password+"&action=LOGIN");
		Log.d("json data", arrJson.toString());
		 // http:bytmediacenter.com/android/php/data.php?action=UID&id=9
		for (int j = 0; j < arrJson.length(); j++) {
			JSONObject json_data;
			try {
				json_data = arrJson.getJSONObject(j);
				logInId = Integer.parseInt(json_data.getString("id"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// setPrefs now
		setPrferencesAfterLogIn();
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub
		if (hasFocus) {
			imgbtnAnim = new ScaleAnimation(1.0f, 1.3f, 1.0f, 1.3f, 90.0f,
					50.0f);
			imgbtnAnim.setDuration(1000);
			imgbtnAnim.setFillBefore(true);
			imgbtnAnim.setFillAfter(true);
			imgbtnAnim.setStartOffset(0);
			setEvent(v, v.getId(), 1);
		} else {
			imgbtnAnim = new ScaleAnimation(1.3f, 1.0f, 1.3f, 1.0f, 90.0f,
					50.0f);
			imgbtnAnim.setDuration(1000);
			imgbtnAnim.setFillBefore(true);
			imgbtnAnim.setFillAfter(true);
			imgbtnAnim.setStartOffset(0);
			setEvent(v, v.getId(), 1);
		}
	}

	private void setEvent(View v, int id, int event) {
		v.setAnimation(imgbtnAnim);
		imgbtnAnim.start();
	}

}