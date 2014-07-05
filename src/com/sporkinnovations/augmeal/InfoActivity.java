package com.sporkinnovations.augmeal;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sporkinnovations.augmeal.util.MenuListAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class InfoActivity extends Activity {

	//Locu api constants
	private static final String LOCU_KEY = "7145f301442f045b26c41bf86a8d181c26c96ebb";
	private static final String LOCU_URL ="https://api.locu.com/v1_0/venue/";
	private static final String SEARCH_PATH = "search/";
	public static final String GET = "GET";


	private ListView list;
	private MenuListAdapter adapter;
	ArrayList<HashMap<String,String>> detailList;


	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);

		// Show the Up button in the action bar.
		setupActionBar();

		//Retrieving venue information
		Intent intent = getIntent();
		HashMap<String, String> map = (HashMap<String, String>) intent.getSerializableExtra(VenueActivity.HASH_MESSAGE);

		String name = map.get(VenueActivity.KEY_NAME);
		String coordinate = intent.getStringExtra(MainActivity.LOC_MESSAGE);

		//Display name of restaurant
		TextView t = (TextView) findViewById(R.id.textView);
		t.setText(name);

		//Execute AsyncTask to retrieve information and inflate list view
		new displayMenuTask().execute(name, coordinate);
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
		getMenuInflater().inflate(R.menu.info, menu);
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

	public void getDish(View view, int position){
		Intent intent = new Intent(this, DishActivity.class);
		startActivity(intent);
	}

	private void initList(){
		//Find view on activity
		list = (ListView) findViewById(R.id.listViewMenu);

		//Retrieve Adapter
		adapter = new MenuListAdapter(this, detailList);
		list.setAdapter(adapter);

		list.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long id) {
				getDish(view,position);
			}
		});
	}

	// Uses AsyncTask to download list information and images
	private class displayMenuTask extends AsyncTask<String, Integer, ArrayList<HashMap<String,String>>> {
		@Override
		protected ArrayList<HashMap<String,String>> doInBackground(String... info) {

			ArrayList<HashMap<String,String>> infoList = new ArrayList<HashMap<String,String>>();

			String name = info[0];
			String coordinate = info[1];
			//Encoding name
			String encodedName = "";
			try {
				encodedName = URLEncoder.encode(name, "UTF-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}

			//Build GET request URL
			String url = LOCU_URL + SEARCH_PATH +
					"?location=" + coordinate + 
					"&name=" + encodedName +
					"&api_key=" + LOCU_KEY;


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
				//TO-DO: parse JSON data into menu data and add to hashmap

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
