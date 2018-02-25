package ro.stiam.stiri.david;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends ListActivity {
	// private String url =
	// "http://stiam.ro/revista-presei-romanesti/query.json?c4=Monden&_=1384206233772";
    private String baseUrl = "http://stiam.ro/revista-presei-romanesti/query.json";
	private String url = "";
    private String categoryParam = "";
    public List<String> selectedCategories = new ArrayList<String>();
	private int nextPageStart = 0;
	private final List<JSONObject> articleList = new ArrayList<JSONObject>();
    private final List<String> categoryList = new ArrayList<String>();
	private boolean loadingMore = false;
	private GestureDetector mDetector;
	private Intent nextActivitiyIntent;
    private ListView mCategoryList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		mDetector = new GestureDetector(this, new ActivityListener());
        initUrl();
        initDrawer();
        getListView().setOverScrollMode(View.OVER_SCROLL_ALWAYS);
		View footerView = ((LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.list_footer, null, false);
		getListView().addFooterView(footerView);

		ArticleListingAdapter adapter = new ArticleListingAdapter(this, articleList);
		setListAdapter(adapter);
		OnScrollListener listener = getOnScrollListener();
		getListView().setOnScrollListener(listener);
	}


    private void initUrl(){
        Calendar calendar = Calendar.getInstance();
        url = baseUrl + "?_=" + calendar.getTimeInMillis() + "";
    }

    private Runnable loadCategoryItems = new Runnable() {
        @Override
        public void run() {
            StiamApp application = (StiamApp) getApplication();
            String pageUrl = "http://stiam.ro/revista-presei-romanesti/app.json?_=1384702391547";
            JSONObject json = application.getJSON(pageUrl);
            try {
                updateCategoryList(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            runOnUiThread(categoryReturnRes);
        }
    };

    private Runnable categoryReturnRes = new Runnable() {
        @Override
        public void run() {
            ((CategoryListingAdapter) mCategoryList.getAdapter()).notifyDataSetChanged();
        }
    };

    public void initDrawer(){
        mCategoryList = (ListView) findViewById(R.id.category_list);
        mCategoryList.setAdapter(new CategoryListingAdapter(this, categoryList));
        Thread thread = new Thread(null, loadCategoryItems);
        thread.start();

    }

    public void updateCategoryList(JSONObject json) throws JSONException {
        JSONObject categories = json.getJSONObject("categories");
        JSONObject properties = categories.getJSONObject("properties");
        categoryParam = properties.getString("name");
        JSONArray items;
        items = categories.getJSONArray("items");
        for (int i = 0; i < items.length(); i++) {
            String title = items.getString(i);
            categoryList.add(title);
        }
    }

	public void updateList(JSONObject json) {
		JSONArray items;
		try {
			items = json.getJSONArray("items");
			for (int i = 0; i < items.length(); i++) {
				JSONObject object = items.getJSONObject(i);
				articleList.add(object);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    private String joinList(List list, String on){
        String result = "";

        for (int i = 0; i < list.size(); i++){
            result += (list.get(i) + on);
        }

        return result;
    }

	private Runnable loadMoreListItems = new Runnable() {
		@Override
		public void run() {
			loadingMore = true;
			StiamApp application = (StiamApp) getApplication();
			String pageUrl = url + "&b_start=" + nextPageStart;
            if (selectedCategories.size() > 0){
                pageUrl = pageUrl + "&" + categoryParam + "=" + joinList(selectedCategories, "&" + categoryParam + "=");
            }
            JSONObject json = application.getJSON(pageUrl);
			updateList(json);
			runOnUiThread(returnRes);
		}
	};

	private Runnable returnRes = new Runnable() {
		@Override
		public void run() {
			((ArticleListingAdapter) getListAdapter()).notifyDataSetChanged();
			loadingMore = false;
		};
	};

	public class ActivityListener extends OnSwipeTouchListener {
		@Override
		public void onSwipeLeft() {
			nextActivityStart();
		}
	}

	public void nextActivityStart() {
		if (nextActivitiyIntent != null) {
			startActivity(nextActivitiyIntent);
			overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		this.mDetector.onTouchEvent(event);
		return super.dispatchTouchEvent(event);
	}

	public OnScrollListener getOnScrollListener() {
		return new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				int lastInScreen = firstVisibleItem + visibleItemCount;
				if ((lastInScreen == totalItemCount) && !(loadingMore)) {
					nextPageStart = articleList.size();
					Thread thread = new Thread(null, loadMoreListItems);
					thread.start();
				}
			}
		};
	}

    public void refreshListing(){
        initUrl();
        articleList.clear();
        ((ArticleListingAdapter) getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_refresh:
                refreshListing();
                return true;
            case R.id.action_settings:
                //openSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		JSONObject item = (JSONObject) l.getItemAtPosition(position);

		Bundle b = new Bundle();
		b.putString("articleJSON", item.toString());
		nextActivitiyIntent = new Intent(MainActivity.this, ArticleView.class);
		nextActivitiyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		nextActivitiyIntent.putExtras(b);
		nextActivityStart();

	}

    public void selectCategory(String title){
        selectedCategories.add(title);
        articleList.clear();
        ((ArticleListingAdapter) getListAdapter()).notifyDataSetChanged();

    }

    public void deselectCategory(String title){
        selectedCategories.remove(title);
        articleList.clear();
        ((ArticleListingAdapter) getListAdapter()).notifyDataSetChanged();
    }


}