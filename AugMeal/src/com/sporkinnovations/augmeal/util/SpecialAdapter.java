package com.sporkinnovations.augmeal.util;

import java.util.ArrayList;
import java.util.HashMap;

import com.sporkinnovations.augmeal.R;
import com.sporkinnovations.augmeal.VenuesActivity;
import com.sporkinnovations.augmeal.R.id;
import com.sporkinnovations.augmeal.R.layout;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SpecialAdapter extends BaseAdapter{

	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private static LayoutInflater inflater = null;
	public ImageLoader imageLoader;
	
	public SpecialAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
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
		TextView venu = (TextView)vi.findViewById(R.id.venue); // title
		TextView description = (TextView)vi.findViewById(R.id.description); // artist name
		ImageView thumb_image= (ImageView)vi.findViewById(R.id.list_image); // thumb image

		HashMap<String, String> venueInfo = new HashMap<String, String>();
		venueInfo = data.get(position);

		// Setting all values in listview
		venu.setText(venueInfo.get(VenuesActivity.KEY_NAME));
		description.setText(venueInfo.get(VenuesActivity.KEY_LOCATION));
		imageLoader.DisplayImage(venueInfo.get(VenuesActivity.KEY_THUMB_URL), thumb_image);
		return vi;


	}

}
