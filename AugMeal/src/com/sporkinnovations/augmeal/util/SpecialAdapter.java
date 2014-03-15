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
import com.sporkinnovations.augmeal.VenuesActivity;
import com.squareup.picasso.Picasso;

public class SpecialAdapter extends BaseAdapter{

	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private static LayoutInflater inflater = null;
	//public ImageLoader imageLoader;
	public Context context;
	
	public SpecialAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //imageLoader=new ImageLoader(activity.getApplicationContext());
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
		View vi = convertView;
		if (convertView == null){
			vi = inflater.inflate(R.layout.list_row, null);
		}
		
		//Locating all views
		TextView venue = (TextView)vi.findViewById(R.id.venue); // title
		TextView description = (TextView)vi.findViewById(R.id.description); // artist name
		ImageView thumb_image= (ImageView)vi.findViewById(R.id.list_image); // thumb image

		//Resolving hashmap
		HashMap<String, String> venueInfo = new HashMap<String, String>();
		venueInfo = data.get(position);
		
		//Retrieving data
		String name = venueInfo.get(VenuesActivity.KEY_NAME);
		String descript = venueInfo.get(VenuesActivity.KEY_LOCATION);
		String url = venueInfo.get(VenuesActivity.KEY_THUMB_URL);

		// Setting all values in listview
		venue.setText(name);
		description.setText(descript);
		Picasso.with(context).load(url).into(thumb_image);
		
		//imageLoader.DisplayImage(venueInfo.get(VenuesActivity.KEY_THUMB_URL), thumb_image);
		return vi;


	}

}
