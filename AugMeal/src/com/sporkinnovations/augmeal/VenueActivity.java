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

import com.sporkinnovations.augmeal.util.SpecialAdapter;

public class VenueActivity extends Activity {

	private static final String API_URL = "https://api.foursquare.com/v2/";
	private static final String CLIENT_ID = "Q51UZPWJYINNZ4X1WLYQCDDUWKYLCGPN3ZMM2NMKVIY25G5H";
	private static final String CLIENT_SECRET = "0P2QMRFKXKZWTBN2GVB3R2GSM20Q3R0VRWPXMIC1FOGUJXHA";
	
	private static final String EXPLORE_PATH = "venues/explore";
	private static final String VERSION = "20140222";
	private static final String SECTION = "food";
	private static final String DOWNLOAD_PHOTO = "1";
	
	private static final String THUMB_SIZE = "100x100";
	private static final String GET = "GET";
	
	public static final String KEY_NAME = "NAME";
	public static final String KEY_LOCATION = "LOCATION";
	public static final String KEY_THUMB_URL ="THUMB_URL";
	public static final String KEY_ID = "KEY_ID";
	
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
		
		//Execute AsyncTask to retrieve information and inflate list view
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

	// Uses AsyncTask to download list information and images
	private class searchNearbyTask extends AsyncTask<String, Integer, ArrayList<HashMap<String,String>>> {
		@Override
		protected ArrayList<HashMap<String,String>> doInBackground(String... coordinate) {
			
			ArrayList<HashMap<String,String>> infoList = new ArrayList<HashMap<String,String>>();
			
			//Build GET request URL
			String url = API_URL + EXPLORE_PATH +
					"?ll=" + coordinate[0] + 
					"&venuePhotos=" + DOWNLOAD_PHOTO +
					"&section=" + SECTION +
					"&client_id=" + CLIENT_ID +
					"&client_secret=" +CLIENT_SECRET +
					"&v=" + VERSION;

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

				JSONArray groups = responseObject.getJSONObject("response").getJSONArray("groups");
				int groupLength = groups.length();
				
				//Initializing
				for (int i = 0; i < groupLength; i ++){
					JSONArray items = groups.getJSONObject(i).getJSONArray("items");
					
					int itemLength = items.length();
					
					for (int j = 0; j < itemLength; j++){
						//Get venue object
						JSONObject venue = items.getJSONObject(j).getJSONObject("venue");
						
						//Resolve image url
						JSONObject firstImage = venue.getJSONObject("photos")
								.getJSONArray("groups").getJSONObject(0)
								.getJSONArray("items").getJSONObject(0);
						String prefix = firstImage.getString("prefix");
						String suffix = firstImage.getString("suffix");
						String imageUrl = prefix + THUMB_SIZE + suffix;
						
						HashMap<String,String> info = new HashMap<String, String>();
						info.put(KEY_ID, venue.getString("id"));
						info.put(KEY_NAME, venue.getString("name"));
						info.put(KEY_LOCATION, venue.getJSONObject("location").getString("address"));
						info.put(KEY_THUMB_URL, imageUrl);
						
						//Add hashmap of venue into arrayList
						infoList.add(info);
					}
				}
			}
			catch(IOException e){
				System.out.println("IO Wrong");
			} 
			catch (JSONException e) {
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
	}

}
