package digital_signage_new.com;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;

public class Json1 {

	String result = "";
	public InputStream is;
	public JSONArray jArray;

	public JSONArray parseJsonData(String sUrl) {
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("year", "1990"));
		// http post
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(sUrl);
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
			Log.e("log_tag", "connection success ");
		} catch (Exception e) {
			Log.e("log_tag", "Error in http connection " + e.toString());
		} // convert response to string
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder stringbuilder = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				stringbuilder.append(line + "\n");
			}
			is.close();
			result = stringbuilder.toString();
		} catch (Exception e) {
			Log.e("log_tag", "Error converting result " + e.toString());
		} // parseJsonData end
		try {
			jArray = new JSONArray(result);
		} catch (JSONException e) {
			Log.e("log_tag", "Error parsing data " + e.toString());
		}
		return jArray;
	}
}
