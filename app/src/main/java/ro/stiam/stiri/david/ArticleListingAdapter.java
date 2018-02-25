package ro.stiam.stiri.david;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.List;


public class ArticleListingAdapter extends ArrayAdapter<JSONObject> {
	private final Context context;

	public ArticleListingAdapter(Context context, List<JSONObject> values) {
		super(context, R.layout.activity_article_item, values);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.activity_article_item, parent, false);
		TextView titleView = (TextView) rowView.findViewById(R.id.article_title);
        TextView sourceView = (TextView) rowView.findViewById(R.id.source_info);
		TextView descriptionView = (TextView) rowView.findViewById(R.id.article_description);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.article_image);
		JSONObject article = getItem(position);
		
		String imageUrl = "";
		try {
			imageUrl = article.getString("thumbnail");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		if(imageUrl != null && !(imageUrl.length() == 0)){
			Picasso.with(context).load(imageUrl)
				.fit()
				.centerCrop()
				.into(imageView);
		} else {
			imageView.setVisibility(View.GONE);
		}
		
		try {
			titleView.setText(article.getString("title"));
		} catch (JSONException e) {
			titleView.setText("JSON ERROR!");
		}
		try {
			descriptionView.setText(article.getString("description"));
		} catch (JSONException e) {
			descriptionView.setText("JSON ERROR!");
		}

        String source = "";
        String json_date = "";
        try {
            source = article.getString("source");
            json_date = article.getString("date");
        } catch (JSONException e) {
            sourceView.setText("JSON ERROR!");
        }

        try {
            source += " - " + FormatRoDate.format(json_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        sourceView.setText(source);
		return rowView;
	}
}