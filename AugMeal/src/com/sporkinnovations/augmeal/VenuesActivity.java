package com.sporkinnovations.augmeal;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sporkinnovations.augmeal.util.SpecialAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class VenuesActivity extends Activity {

	private static final String API_URL = "https://api.foursquare.com/v2/";
	private static final String CLIENT_ID = "Q51UZPWJYINNZ4X1WLYQCDDUWKYLCGPN3ZMM2NMKVIY25G5H";
	private static final String CLIENT_SECRET = "0P2QMRFKXKZWTBN2GVB3R2GSM20Q3R0VRWPXMIC1FOGUJXHA";
	
	private static final String searchPath = "venues/search";
	private static final String venuePath = "venues/";
	private static final String photoPath = "photos";
	private static final String version = "20140222";
	
	private static final String thumbSize = "50x50";
	private static final String GET = "GET";
	
	public static final String KEY_NAME = "NAME";
	public static final String KEY_LOCATION = "LOCATION";
	public static final String KEY_THUMB_URL ="THUMB_URL";
	public static final String KEY_ID = "ID";
	
	public static final int STATUS = 1;
	public static final String ID_MESSAGE = "ID_MESSAGE";
	
	private ListView list;
	private SpecialAdapter adapter;
	ArrayList<HashMap<String,String>> detailList;
	String coordinate;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_venues);

		// Show the Up button in the action bar.
		setupActionBar();


		//Get List of Names
		Intent intent = getIntent();
		coordinate = intent.getStringExtra(MainActivity.LOC_MESSAGE);
		new searchNearbyTask().execute(coordinate);
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.venues, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void initList(){
		//Find view on activity
		list = (ListView) findViewById(R.id.listView);
		
		//Retrieve Adapter
		adapter = new SpecialAdapter(this, detailList);
		list.setAdapter(adapter);
		
		list.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long id) {
				getDetailInfo(view,position);
			}
		});
	}
	private void getDetailInfo(View view, int ind){
		//taking latest coordinates
		HashMap<String, String> venueInfo = detailList.get(ind);
		System.out.println(ind);
		String id = venueInfo.get(KEY_ID);
		System.out.println(id);
		//create linkage
		Intent intent = new Intent(this, InfoActivity.class);

		intent.putExtra(ID_MESSAGE,id);
		startActivity(intent);
	}

	// Uses AsyncTask to create a task away from the main UI thread. This task takes a 
	// URL string and uses it to create an HttpUrlConnection. Once the connection
	// has been established, the AsyncTask downloads the contents of the webpage as
	// an InputStream. Finally, the InputStream is converted into a string, which is
	// displayed in the UI by the AsyncTask's onPostExecute method.
	private class searchNearbyTask extends AsyncTask<String, Integer, ArrayList<HashMap<String,String>>> {
		@Override
		protected ArrayList<HashMap<String,String>> doInBackground(String... coordinate) {
			ArrayList<HashMap<String,String>> infoList = new ArrayList<HashMap<String,String>>();
			
			StringBuilder urlBuilder = new StringBuilder(API_URL);
			urlBuilder.append(searchPath);
			urlBuilder.append('?');
			urlBuilder.append("ll=");
			urlBuilder.append(coordinate[0]);
			urlBuilder.append('&');

			//Authorizing
			urlBuilder.append("client_id=");
			urlBuilder.append(CLIENT_ID);
			urlBuilder.append("&client_secret=");
			urlBuilder.append(CLIENT_SECRET);

			urlBuilder.append("&v=" + version);
			String url = urlBuilder.toString();
			System.out.println(url);

			JSONObject responseObject = null;
			try{
				URL aUrl = new URL(url);
				HttpURLConnection connection = (HttpURLConnection) aUrl.openConnection();
				connection.setRequestMethod(GET);
				connection.connect();
				int responseCode = connection.getResponseCode();
				if (responseCode == 200){
					InputStream inputStream = connection.getInputStream();
					String response = readStream(inputStream);
					connection.disconnect();
					responseObject = new JSONObject(response);
				}
				else{
					System.out.println(responseCode);
				}

				JSONArray venues = responseObject.getJSONObject("response").getJSONArray("venues");
				int length = venues.length();
				JSONObject[] venueArray = new JSONObject[length];
				
				//initializing
				for (int i = 0; i < length; i ++){
					venueArray[i] = venues.getJSONObject(i);
					String id = venueArray[i].getString("id");

					//put into Hashmap;
					HashMap<String, String> info = new HashMap<String, String>();
					info.put(KEY_NAME, venueArray[i].getString("name"));
					info.put(KEY_LOCATION, venueArray[i].getJSONObject("location").getString("address"));
					info.put(KEY_ID, venueArray[i].getString("id"));
					info.put(KEY_THUMB_URL, getThumbUrl(id));
					infoList.add(info);
				}
			}
			catch(IOException e){
				System.out.println("IO Wrong");
			} catch (JSONException e) {
				System.out.print("JSON Wrong");
				e.printStackTrace();
			}
			return infoList;
		}
		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(ArrayList<HashMap<String,String>> infoList) {
			detailList = infoList;
			initList();
		}
		private String readStream(InputStream inputStream) throws IOException{
			StringWriter responseWriter = new StringWriter();

			char[] buf = new char[1024];
			int l = 0;

			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
			while ((l = inputStreamReader.read(buf)) > 0) {
				responseWriter.write(buf, 0, l);
			}

			responseWriter.flush();
			responseWriter.close();
			return responseWriter.getBuffer().toString();
		}
		private String getThumbUrl(String id){
			StringBuilder urlBuilder = new StringBuilder(API_URL);
			urlBuilder.append(venuePath);
			urlBuilder.append(id);
			urlBuilder.append('/');
			urlBuilder.append(photoPath);
			urlBuilder.append('?');

			//Authorizing
			urlBuilder.append("client_id=");
			urlBuilder.append(CLIENT_ID);
			urlBuilder.append("&client_secret=");
			urlBuilder.append(CLIENT_SECRET);

			urlBuilder.append("&v=" + version);
			String url = urlBuilder.toString();
			System.out.println(url);
			
			JSONObject responseObject = null;
			try{
				URL aUrl = new URL(url);
				HttpURLConnection connection = (HttpURLConnection) aUrl.openConnection();
				connection.setRequestMethod(GET);
				connection.connect();
				int responseCode = connection.getResponseCode();
				if (responseCode == 200){
					InputStream inputStream = connection.getInputStream();
					String response = readStream(inputStream);
					connection.disconnect();
					responseObject = new JSONObject(response);
				}
				else{
					System.out.println(responseCode);
				}
				
				JSONObject firstImage = responseObject.getJSONObject("response").getJSONObject("photos").getJSONArray("items").getJSONObject(0);
				String prefix = firstImage.getString("prefix");
				String suffix = firstImage.getString("suffix");
				return prefix + thumbSize + suffix;
				
			}
			catch(IOException e){
				System.out.println("IO Wrong");
				e.printStackTrace();
				return "";
			} catch (JSONException e) {
				System.out.print("JSON Wrong");
				e.printStackTrace();
				return "";
			}
			
			
			
			
		}
		
	}

}
