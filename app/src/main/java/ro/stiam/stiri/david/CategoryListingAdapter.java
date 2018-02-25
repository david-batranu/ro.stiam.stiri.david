package ro.stiam.stiri.david;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;


public class CategoryListingAdapter extends ArrayAdapter<String> {
    private final Context context;

    public CategoryListingAdapter(Context context, List<String> values) {
        super(context, R.layout.activity_article_item, values);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.category_layout, parent, false);
        CheckBox checkbox = (CheckBox) rowView.findViewById(R.id.checkBox);
        String categoryTitle = getItem(position);
        checkbox.setText(categoryTitle);
        final MainActivity mainActivity = (MainActivity) context;


        String encodedCategoryTitle = "";
        try {
            encodedCategoryTitle = URLEncoder.encode(categoryTitle, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        if(mainActivity.selectedCategories.contains(encodedCategoryTitle)){
            checkbox.setChecked(true);
        }
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String text = (String) buttonView.getText();
                try {
                    text = URLEncoder.encode(text, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                if (isChecked){
                    mainActivity.selectCategory(text);
                } else {
                    mainActivity.deselectCategory(text);
                }
            }
        });

        return rowView;
    }
}