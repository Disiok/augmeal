package com.sporkinnovations.augmeal.util;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sporkinnovations.augmeal.R;
import com.sporkinnovations.augmeal.VenueActivity;
import com.squareup.picasso.Picasso;

public class SpecialAdapter extends BaseAdapter{

	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private static LayoutInflater inflater = null;

	public Context context;

	public SpecialAdapter(Activity activity, ArrayList<HashMap<String, String>> data) {
		this.activity = activity;
		this.data=data;
		inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		context = activity.getApplicationContext();
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (convertView == null){
			view = inflater.inflate(R.layout.list_row, null);
		}

		//Locating all views
		TextView venue = (TextView)view.findViewById(R.id.venue); // title
		TextView description = (TextView)view.findViewById(R.id.description); // artist name
		ImageView thumb_image= (ImageView)view.findViewById(R.id.list_image); // thumb image

		//Resolving hashmap
		HashMap<String, String> venueInfo = new HashMap<String, String>();
		venueInfo = data.get(position);

		//Retrieving data
		String name = venueInfo.get(VenueActivity.KEY_NAME);
		String descript = venueInfo.get(VenueActivity.KEY_LOCATION);
		String url = venueInfo.get(VenueActivity.KEY_THUMB_URL);

		// Setting all values in list view
		venue.setText(name);
		description.setText(descript);
		
		//Using Picasso Library
		Picasso.with(context)
			.load(url)
			.placeholder(R.drawable.stub)
			.error(R.drawable.stub)
			.into(thumb_image);

		return view;
	}

}
