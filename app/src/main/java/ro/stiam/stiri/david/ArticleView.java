package ro.stiam.stiri.david;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ArticleView extends Activity {
	private String bodyText = new String();
	private JSONObject article = new JSONObject();
	private StiamApp application = (StiamApp) getApplication();
	private GestureDetector mDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_article_view);
		mDetector = new GestureDetector(this, new ActivityListener());

		Bundle b = getIntent().getExtras();
		try {
			article = new JSONObject(b.getString("articleJSON"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		final HandlerClass mHandler = new HandlerClass(this);

		// Start lengthy operation in a background thread
		new Thread(new Runnable() {
			public void run() {
				try {
					bodyText = getArticleBody(article.getString("url"),
							article.getString("original")).getString("text");
					bodyText = bodyText.replaceAll("\n", "\n\n");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mHandler.sendEmptyMessage(0);
			}

		}).start();

		loadContent(article);

	}

	public class ActivityListener extends OnSwipeTouchListener {
		@Override
		public void onSwipeRight() {
			finish();
			overridePendingTransition(R.anim.slide_in_left,
					R.anim.slide_out_right);
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		this.mDetector.onTouchEvent(event);
		return super.dispatchTouchEvent(event);
	}

	public void loadBody() {
		ProgressBar mProgress = (ProgressBar) findViewById(R.id.progress_bar);
		TextView bodyView = (TextView) this.findViewById(R.id.article_body);
		bodyView.setText(bodyText);
		mProgress.setVisibility(View.GONE);
	}

	public void loadContent(JSONObject article) {
		ImageView imageView = (ImageView) this.findViewById(R.id.article_image);
		TextView titleView = (TextView) this.findViewById(R.id.article_title);
		TextView descriptionView = (TextView) this
				.findViewById(R.id.article_description);
		TextView bodyView = (TextView) this.findViewById(R.id.article_body);
		bodyView.setText(bodyText);

		try {
			titleView.setText(article.getString("title"));
			descriptionView.setText(article.getString("description"));

			String imageUrl = "";
			try {
				imageUrl = article.getString("thumbnail");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			if (imageUrl != null && !(imageUrl.length() == 0)) {
				Picasso.with(application).load(imageUrl).fit().centerCrop()
						.into(imageView);
			} else {
				imageView.setVisibility(View.GONE);
			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public JSONObject getArticleBody(String url, String original) {
		StiamApp application = (StiamApp) getApplication();
		String[] splitUrl = url.split("/");
		String baseUrl = new String();
		for (int i = 0; i < splitUrl.length - 1; i++) {
			String delimiter = new String();
			if (i != splitUrl.length - 2) {
				delimiter = "/";
			} else {
				delimiter = "";
			}
			baseUrl = baseUrl.concat(splitUrl[i] + delimiter);
		}
		String finalUrl = baseUrl.concat("/diffbot.json?url=");
		finalUrl = finalUrl.concat(original);
		return application.getJSON(finalUrl);
	}

	private static class HandlerClass extends Handler {
		private final WeakReference<ArticleView> mTarget;

		public HandlerClass(ArticleView context) {
			mTarget = new WeakReference<ArticleView>((ArticleView) context);
		}

		@Override
		public void handleMessage(Message msg) {
			ArticleView target = mTarget.get();
			if (target != null) {
				target.loadBody();
			}
		}
	}

	private class GetArticleImageTask extends AsyncTask<String, Void, String> {
		public ImageView imageView;
		public Bitmap image;
		public Drawable thumb_d;

		public GetArticleImageTask(ImageView imageView, Bitmap image) {
			super();
			this.imageView = imageView;
			this.image = image;
		}

		@Override
		protected String doInBackground(String... urls) {
			try {
				URL thumb_u = new URL(urls[0]);
				image = BitmapFactory.decodeStream(thumb_u.openStream());
				thumb_d = new BitmapDrawable(getResources(), image);
			} catch (IOException e) {
				return "500";
			}
			return "200";
		}

		@Override
		protected void onPostExecute(String result) {
			if (result.equals("200")) {
				imageView.setImageDrawable(thumb_d);

				imageView
						.measure(MeasureSpec.makeMeasureSpec(0,
								MeasureSpec.UNSPECIFIED), MeasureSpec
								.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
				int targetWidth = imageView.getMeasuredWidth() * 2; // TODO:
																	// bad, need
																	// to change

				Drawable thumb_d = new BitmapDrawable(getResources(), image);
				imageView.setImageDrawable(thumb_d);

				final int imageHeight = image.getHeight();
				final int imageWidth = image.getWidth();

				imageView.setLayoutParams(new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.MATCH_PARENT,
						RelativeLayout.LayoutParams.MATCH_PARENT));

				float scale = (float) targetWidth / imageWidth;
				imageView.getLayoutParams().height = (int) Math.round(imageHeight * scale);
			} else {
				imageView.setVisibility(View.GONE);
			}

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.article_view, menu);
		return true;
	}

}
