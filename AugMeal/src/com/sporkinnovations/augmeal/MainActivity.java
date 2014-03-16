package com.sporkinnovations.augmeal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity{
	public final static String LOC_MESSAGE = "com.disiok.myfirstapp.LOC_MESSAGE";
	
	private LocationManager locationManager;
	private LocationListener locationListener;
	
	private double longitude = 0;
	private double latitude = 0;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null){
			//Setup Location Objects
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			locationListener = new MyLocationListener();
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		}
		else{
			longitude = savedInstanceState.getDouble("longitude");
			latitude = savedInstanceState.getDouble("latitude");
			
			//Change Button
			TextView t = (TextView) findViewById(R.id.text);
			t.setText(R.string.nearby);
			ImageView b = (ImageView) findViewById(R.id.imageView);
			b.setImageResource(R.drawable.loc_pin1);
			b.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					sendCoordinate(v);
				}
			});
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void sendCoordinate(View view){
		//taking latest coordinates
		String loc = latitude +"," + longitude;

		//create linkage
		Intent intent = new Intent(this, VenueActivity.class);

		intent.putExtra(LOC_MESSAGE,loc);
		startActivity(intent);
	}

	private class MyLocationListener implements LocationListener 
	{
		@Override
		public void onLocationChanged(Location location) {
			latitude = location.getLatitude();
			longitude = location.getLongitude();
			String text = "Location Updated";
			Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
			
			//Change Button
			TextView t = (TextView) findViewById(R.id.text);
			t.setText(R.string.nearby);
			ImageView b = (ImageView) findViewById(R.id.imageView);
			b.setImageResource(R.drawable.loc_pin1);
			b.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					sendCoordinate(v);
				}
			});
			
			// Remove the listener you previously added
			locationManager.removeUpdates(this);
		}

		@Override
		public void onProviderDisabled(String provider) {
			Toast.makeText( getApplicationContext(),"GPS Disabled",Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onProviderEnabled(String provider) {
			Toast.makeText( getApplicationContext(),"GPS Enabled",Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Toast.makeText( getApplicationContext(),"Status Changed",Toast.LENGTH_SHORT).show();
		}
	}

}
