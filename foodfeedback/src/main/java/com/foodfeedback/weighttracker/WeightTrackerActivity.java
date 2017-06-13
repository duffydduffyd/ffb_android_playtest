package com.foodfeedback.weighttracker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.TreeMap;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.LineChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer.FillOutsideLine;
import org.achartengine.tools.PanListener;
import org.achartengine.tools.ZoomEvent;
import org.achartengine.tools.ZoomListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewManager;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.foodfeedback.cachemanager.FetchGalleryCacheManager;
import com.foodfeedback.cachemanager.FetchWeightCacheManager;
import com.foodfeedback.cachemanager.LoginDetailsCacheManager;
import com.foodfeedback.cachemanager.UserDetailsCacheManager;
import com.foodfeedback.cachemanager.UserPreferencesCacheManager;
import com.foodfeedback.myfood.ImageLoader;
import com.foodfeedback.networking.ServiceKeys;
import com.foodfeedback.networking.TrackerNetworkingServices;
import com.foodfeedback.onboarding.Controller;
import com.foodfeedback.onboarding.R;
import com.foodfeedback.settings.SettingsActivity;
import com.foodfeedback.util.IabHelper;
import com.foodfeedback.util.IabResult;
import com.foodfeedback.util.Inventory;
import com.foodfeedback.util.Purchase;
import com.foodfeedback.utilities.ActionItem;
import com.foodfeedback.utilities.DownloadImageTask;
import com.foodfeedback.utilities.ProgressHUD;
import com.foodfeedback.utilities.QuickAction;
import com.foodfeedback.utilities.RecommendAnimationHelper;
import com.foodfeedback.utilities.Utilities;
import com.foodfeedback.valueobjects.AccountOperations;
import com.foodfeedback.valueobjects.FetchGallery;
import com.foodfeedback.valueobjects.FetchWeight;
import com.foodfeedback.valueobjects.FoodImagePost;
import com.foodfeedback.valueobjects.UserDetails;
import com.foodfeedback.valueobjects.UserPreferences;
import com.localytics.android.LocalyticsSession;

public class WeightTrackerActivity extends Activity {
	private static final String SKU_TRACKER = "weight_tracker";
	// private static final String SKU_TRACKER = "tracker_purchase";
	String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAk9fFQRUCBxEQwMaEhvlp2oqH4Zkp4wZmgxgtm3jRoWqVGdXxmmi7+WOGY76NB4xA5GQWvHMn95pk3klGNjNMryeFlazUszaPseFyzQDmS8X3XDZOcHYFGT9f5eM1TKSwPHPXMMLD3+gUMLkoit/MEVpziIR4ArBjFmiOsig59/8uczDb3Te16w9mUMF7i5ndGDE2haEIqSpzjB6urDbANzST2P73GV7JFiqeNiq2i5KhvluUUSUed74k12M6p+YVHEabksJUG7FJWKHlTO5Id/q4oflywcWl+VLjM1B/jO/QE4L/sLpnYHw66wJOju2dsrFINyC67mSgM6JiYDo5xQIDAQAB";
	String someInfoToIdentifyTheUserMakingThePurchase = "UNKNOWN USER"; // Default
																		// value
	private final static String TAG_PURCHASED_TRACKER = "Purchased Tracker";
	private final static String TAG_TRACKER_VIEWED = "Viewed Tracker";
	private static final int ID_ADD = 1;
	float xPosition, yPosition;

	Long minAvailableTimestamp = (long) 0, maxAvailableTimestamp = (long) 0,
			minAvailableWeightTimestamp = (long) 0;
	Double maxWeightEntered = (double) 0;
	IabHelper mHelper;
	private LocalyticsSession localyticsSession;
	Typeface tfNormal, tfSpecial;

	TextView headerTitle, introText, recordWeightTitle, progressChartTitle;
	TextView kgLBSChanger, kgLBSLabel;

	private RelativeLayout deleteDialog, chartHolderLayout;;
	private TextView deleteOkButton, deleteCancelButton, textDialog;
	public QuickAction mQuickAction;

	EditText editTextWeightEntry;
	Button saveNewWeight, purchaseButton;
	LinearLayout settingButton;
	ViewFlipper viewflipper;
	UserDetails userDetails;
	Map<Long, Double> myDataForPlot = new HashMap<Long, Double>();
	ImageLoader imgLoader;
	ArrayList<GroupDefinition> calculatedGroups;
	ArrayList<MyCustomLine> allLinesDrawn = new ArrayList<WeightTrackerActivity.MyCustomLine>();
	LinearLayout imageMarkerLayout;
	int maxPhotosInAllGroups = 0;

	/** The main dataset that includes all the series that go into a chart. */
	private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	/** The main renderer that includes all the renderers customizing a chart. */
	private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
	private XYSeries blackSeries, whiteSeries;
	private GraphicalView mChartView;

	private Boolean galleryNeedsUpdate = false,
			fingerisDownForLongEngouh = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tracker_viewflipper);
		imgLoader = new ImageLoader(getApplicationContext());
		// Context used to access device resources
		this.localyticsSession = new LocalyticsSession(
				this.getApplicationContext());
		this.localyticsSession.open(); // open the session
		this.localyticsSession.tagScreen("Weight Tracker");
		this.localyticsSession.upload();

		setupViewsandFonts();
		initializeBasic();

	}

	@Override
	protected void onResume() {
		super.onResume();
		this.localyticsSession.open();
		if (deleteDialog.getVisibility() == View.VISIBLE) {
			mQuickAction.dismiss();
		}
	}

	private void setupViewsandFonts() {
		tfNormal = Typeface.createFromAsset(this.getAssets(), this
				.getResources().getString(R.string.font_standard));
		tfSpecial = Typeface.createFromAsset(this.getAssets(), this
				.getResources().getString(R.string.app_font_style_medium));

		// Intro Screen
		viewflipper = (ViewFlipper) findViewById(R.id.trackerViewflipper);
		settingButton = (LinearLayout) findViewById(R.id.rightbutton);

		headerTitle = (TextView) findViewById(R.id.tracker_txt);
		headerTitle.setTypeface(tfSpecial);
		purchaseButton = (Button) findViewById(R.id.purchaseButton);
		purchaseButton.setTypeface(tfNormal);
		introText = (TextView) findViewById(R.id.tracker_intro_txt);
		introText.setTypeface(tfNormal);

		// tracker screen
		editTextWeightEntry = (EditText) findViewById(R.id.weight);
		editTextWeightEntry.setTypeface(tfNormal);
		recordWeightTitle = (TextView) findViewById(R.id.label_recordweight);
		recordWeightTitle.setTypeface(tfSpecial);
		progressChartTitle = (TextView) findViewById(R.id.label_progress_chart);
		progressChartTitle.setTypeface(tfSpecial);
		saveNewWeight = (Button) findViewById(R.id.add);
		saveNewWeight.setTypeface(tfSpecial);
		kgLBSChanger = (TextView) findViewById(R.id.kglbs);
		kgLBSChanger.setTypeface(tfSpecial);
		kgLBSLabel = (TextView) findViewById(R.id.txt_kglbs);
		kgLBSLabel.setTypeface(tfSpecial);

		deleteDialog = (RelativeLayout) findViewById(R.id.delete_weight);
		deleteOkButton = (TextView) findViewById(R.id.logout_okbutton);
		deleteCancelButton = (TextView) findViewById(R.id.logout_cancelbutton);
		textDialog = (TextView) findViewById(R.id.textDialog);

		imageMarkerLayout = (LinearLayout) findViewById(R.id.imageMarkerLayout);

		if (ServiceKeys.INDEMOMODE) {
			System.out.println("Currently in demo mode - Moving on to tracker");
			purchaseButton.setText(getResources().getString(
					R.string.purchase_amount)
					+ " ( Demo Mode )");
			purchaseButton.setEnabled(true);
			purchaseButton.setBackgroundResource(R.drawable.redbar_button);

		} else {
			System.out.println("Live mode - Initialize payment set up");
			initializePayment();
		}
		
		
	}

	protected void checkConnectivityForAddWeight(Context context,
			String weightValueToAdd) {

		if (Utilities.isConnectingToInternet(context.getApplicationContext())) {
			addWeightBtnClick();// Add it to the current screen view ASAP
			if (kgLBSChanger.getText().toString()
					.equals(getResources().getString(R.string.kg))) {
				new AddWeightAsync(context, weightValueToAdd).execute();
			} else {
				try {
					double k = Double.parseDouble(weightValueToAdd);
					k = k * 0.453592;
					weightValueToAdd = String.valueOf(k);
					new AddWeightAsync(context, weightValueToAdd).execute();

				} catch (Exception e) {
					System.out.println("Can't fetch weight");
				}
			}

		} else {
			Utilities.showErrorToast(
					getResources().getString(R.string.internet_connection),
					this);
		}
	}

	public void addWeightBtnClick() {
		long x = 0;
		double y = 0.0;
		try {
			y = Double.parseDouble(editTextWeightEntry.getText().toString());
		} catch (NumberFormatException e) {
			editTextWeightEntry.requestFocus();
			return;
		}
		editTextWeightEntry.setText("");
		// add a new data point to the current series
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

		long secondsSinceEpoch = calendar.getTimeInMillis() / 1000L;
		x = secondsSinceEpoch;
		myDataForPlot.put(x, y);
		setUpSeriesWithData();
		updateGraphAndRelatedViews();

	}

	class AddWeightAsync extends AsyncTask<Void, Void, Integer> {

		private Context ctx;
		private String kilo;

		public AddWeightAsync(Context context, String kilo) {
			this.ctx = context;
			this.kilo = kilo;
		}

		@Override
		protected void onPreExecute() {
		}

		protected void onPostExecute(Integer result) {
			if (result == 1) {

			}

		}

		@Override
		protected Integer doInBackground(Void... params) {
			try {

				TrackerNetworkingServices.getInstance()
						.addWeightHttpPostRequest(userDetails.getUserID(),
								userDetails.getPassword(), kilo, ctx);
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			return 1;
		}
	}

	private void initializePayment() {
		// compute your public key and store it in base64EncodedPublicKey
		mHelper = new IabHelper(this, base64EncodedPublicKey);

		
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(IabResult result) {
				if (!result.isSuccess()) {
					// Oh no, there was a problem.
					System.out.println("Problem setting up In-app Billing: "
							+ result);
					Utilities.showErrorToast(
							"Could not set up Tracker Purchase",
							WeightTrackerActivity.this);
					return;
				} else {
					System.out.println(" Hooray, IAB is fully set up! ");

					// Hooray, IAB is fully set up!
					try {
						ArrayList<String> additionalSkuList = new ArrayList<String>();
						additionalSkuList.add(SKU_TRACKER);
						IabHelper.QueryInventoryFinishedListener mQueryFinishedListener = new IabHelper.QueryInventoryFinishedListener() {
							public void onQueryInventoryFinished(
									IabResult result, Inventory inventory) {
								if (result.isFailure()) {
									// handle error

									purchaseButton
											.setText(getResources().getString(
													R.string.purchase_error));
									return;
								}

								try {
									if (inventory.getSkuDetails(SKU_TRACKER) != null) {
										// First check if already purchased
										if (inventory.hasPurchase(SKU_TRACKER)) {
											// Go ahead and move directly to the
											// next screen
											moveToGraphScreen();

										} else {
											purchaseButton.setEnabled(true);
											String applePrice = inventory
													.getSkuDetails(SKU_TRACKER)
													.getPrice();
											purchaseButton
													.setText(getResources()
															.getString(
																	R.string.purchase_amount)
															+ " ("
															+ applePrice
															+ ")");
											purchaseButton
													.setBackgroundResource(R.drawable.redbar_button);
										}

									} else {
										// Could NOT find the specific SKU to
										// make
										// the purchase.
										purchaseButton
												.setText(getResources()
														.getString(
																R.string.purchase_error));
										purchaseButton.setEnabled(false);
										return;
									}
								} catch (Exception e) {
									e.printStackTrace();
									purchaseButton
											.setText(getResources().getString(
													R.string.purchase_error));
								}
							}
						};
						mHelper.queryInventoryAsync(true, additionalSkuList,
								mQueryFinishedListener);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

	}

	private void initializeBasic() {
		try {
			userDetails = UserDetailsCacheManager.getObject(Controller
					.getAppBackgroundContext());
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		
		saveNewWeight.setBackgroundResource(R.drawable.unfocus_redimage);
		saveNewWeight.setEnabled(false);

		saveNewWeight.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(
						editTextWeightEntry.getWindowToken(),
						InputMethodManager.RESULT_UNCHANGED_SHOWN);
				checkConnectivityForAddWeight(v.getContext(),
						editTextWeightEntry.getText().toString());

			}
		});
		
		editTextWeightEntry.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (editTextWeightEntry.getText().toString().length() == 0) {
					saveNewWeight
							.setBackgroundResource(R.drawable.unfocus_redimage);
					saveNewWeight.setEnabled(false);
				} else if (editTextWeightEntry.getText().toString().length() > 0) {
					saveNewWeight
							.setBackgroundResource(R.drawable.redbar_button);
					saveNewWeight.setEnabled(true);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		settingButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),
						SettingsActivity.class)
						.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			}
		});

		purchaseButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ServiceKeys.INDEMOMODE) {
					System.out
							.println("Currently in demo mode - Moving on to tracker");
					moveToGraphScreen();
				} else {
					System.out.println("purchase active");
					start_payment();
				}

			}
		});

		kgLBSChanger.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (kgLBSChanger.getText().toString()
						.equals(getResources().getString(R.string.kg))) {
					try {
						UserPreferences fetchUserPref = UserPreferencesCacheManager
								.getObject(Controller.getAppBackgroundContext());
						if (fetchUserPref == null) {
							fetchUserPref = new UserPreferences();
						}
						fetchUserPref.setWeightUnitPreference(getResources()
								.getString(R.string.lbs));
						UserPreferencesCacheManager.saveObject(
								Controller.getAppBackgroundContext(),
								fetchUserPref);
						kgLBSChanger.setText(getResources().getString(
								R.string.lbs));
						kgLBSLabel.setText(getResources().getString(
								R.string.lbs));
						fetchAllWeightsInLBS();
						setUpSeriesWithData();
						updateGraphAndRelatedViews();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					try {
						UserPreferences fetchUserPref = UserPreferencesCacheManager
								.getObject(Controller.getAppBackgroundContext());

						if (fetchUserPref == null) {
							fetchUserPref = new UserPreferences();
						}
						fetchUserPref.setWeightUnitPreference(getResources()
								.getString(R.string.kg));
						UserPreferencesCacheManager.saveObject(
								Controller.getAppBackgroundContext(),
								fetchUserPref);
						kgLBSChanger.setText(getResources().getString(
								R.string.kg));
						kgLBSLabel.setText(getResources()
								.getString(R.string.kg));
						fetchAllWeightsInKG();
						setUpSeriesWithData();
						updateGraphAndRelatedViews();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

	}

	private void initializeGraph() {

		try {
			UserPreferences fetchflash = UserPreferencesCacheManager
					.getObject(Controller.getAppBackgroundContext());
			if (fetchflash.getWeightUnitPreference().equals(
					getResources().getString(R.string.lbs))) {
				kgLBSChanger.setText(getResources().getString(R.string.lbs));
				kgLBSLabel.setText(getResources().getString(R.string.lbs));
				fetchAllWeightsInLBS();
			} else {
				kgLBSChanger.setText(getResources().getString(R.string.kg));
				kgLBSLabel.setText(getResources().getString(R.string.kg));
				fetchAllWeightsInKG();

			}
		} catch (Exception e2) {
			e2.printStackTrace();
			kgLBSChanger.setText(getResources().getString(R.string.lbs));
			kgLBSLabel.setText(getResources().getString(R.string.lbs));
			fetchAllWeightsInLBS();

		}

		// create black series data
		String seriesTitle = "Series " + (mDataset.getSeriesCount() + 1);
		blackSeries = new XYSeries(seriesTitle);
		// create white series data
		String seriesWhite = "Series " + (2);
		whiteSeries = new XYSeries(seriesWhite);
		// populate the data into the series
		setUpSeriesWithData();

		// Add the series to the dataset
		mDataset.addSeries(blackSeries);
		mDataset.addSeries(whiteSeries);

		FillOutsideLine fill = new FillOutsideLine(
				FillOutsideLine.Type.BOUNDS_ALL);
		fill.setColor(getResources().getColor(R.color.graph_green));

		// create a new renderer for the BLACK series
		XYSeriesRenderer blackRenderer = new XYSeriesRenderer();
		blackRenderer.setColor(Color.BLACK);
		blackRenderer.setPointStyle(PointStyle.CIRCLE);
		blackRenderer.setPointStrokeWidth(6);
		blackRenderer.setShowLegendItem(false);
		blackRenderer.setFillPoints(true);
		blackRenderer.setLineWidth(1);
		blackRenderer.addFillOutsideLine(fill);
		blackRenderer.setDisplayChartValuesDistance(20);

		XYSeriesRenderer whiteRenderer = new XYSeriesRenderer();
		whiteRenderer.setColor(Color.WHITE);
		whiteRenderer.setPointStyle(PointStyle.CIRCLE);
		whiteRenderer.setPointStrokeWidth(1);
		whiteRenderer.setShowLegendItem(false);
		whiteRenderer.setFillPoints(true);
		whiteRenderer.setLineWidth(2);
		whiteRenderer.addFillOutsideLine(fill);
		whiteRenderer.setDisplayChartValuesDistance(20);

		// add the series set some renderer properties
		mRenderer.addSeriesRenderer(blackRenderer);
		mRenderer.addSeriesRenderer(whiteRenderer);
		mRenderer.setClickEnabled(true);
		mRenderer.setSelectableBuffer(30);
		mRenderer.setAxisTitleTextSize(16);
		mRenderer.setPanEnabled(true, false);
		mRenderer.setZoomEnabled(true, false);
		mRenderer.setYAxisAlign(Align.LEFT, 0);
		mRenderer.setLabelsTextSize(22);
		mRenderer.setTextTypeface(tfNormal);
		mRenderer.setYLabelsAlign(Align.RIGHT, 0);
		mRenderer.setXLabelsColor(Color.WHITE);
		mRenderer.setApplyBackgroundColor(true);
		mRenderer.setBackgroundColor(Color.WHITE);
		mRenderer.setMarginsColor(Color.WHITE);
		mRenderer.setAxesColor(Color.GRAY);
		mRenderer.setLabelsColor(Color.BLACK);
		mRenderer.setXLabelsPadding(50);
		mRenderer.setMargins(new int[] { 20, 45, -20, 20 });
		mRenderer.setYLabelsPadding(5);

		mRenderer.setPointSize(10);

		chartHolderLayout = (RelativeLayout) findViewById(R.id.chart);
		chartHolderLayout.setEnabled(true);
		// XYChart chart = new LineChart(mDataset, mRenderer);

		// Specifying chart types to be drawn in the graph
		// Number of data series and number of types should be same
		// Order of data series and chart type will be same
		String[] types = new String[] { LineChart.TYPE, LineChart.TYPE };

		// Creating a combined chart with the chart types specified in types
		// array
		mChartView = (GraphicalView) ChartFactory.getCombinedXYChartView(
				getBaseContext(), mDataset, mRenderer, types);

		// mChartView = new GraphicalView(this, chart);
		if (android.os.Build.VERSION.SDK_INT >= 11) {
			mChartView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}

		initializeGraphActions();
		alignGraph();
		updateGraphAndRelatedViews();

	}

	private void initializeGraphActions() {

		mChartView.setOnClickListener(new View.OnClickListener() {
			@SuppressLint("SimpleDateFormat")
			public void onClick(View v) {
				// handle the click event on the chart
				SeriesSelection pointClicked = mChartView
						.getCurrentSeriesAndPoint();
				if (pointClicked == null) {
					System.out.println("No datapoints clicked");
				} else {
					Date showDatePresent = new Date((long) (pointClicked
							.getXValue() * 1000));
					SimpleDateFormat sdf = new SimpleDateFormat("MMM d");
					String dateOfShow = sdf.format(showDatePresent);
					ActionItem addItem;

					addItem = new ActionItem(ID_ADD, dateOfShow, ((double) Math
							.round(pointClicked.getValue() * 10))
							/ 10
							+ " "
							+ kgLBSChanger.getText().toString(), null,
							(int) (xPosition), (int) yPosition);

					addItem.setSticky(true);
					mQuickAction = new QuickAction(getApplicationContext());
					mQuickAction.addActionItem(addItem);

					if (deleteDialog.getVisibility() == View.VISIBLE)
						mQuickAction.dismiss();
					else
						mQuickAction.show(v);

				}
			}

		});

		mChartView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				fingerisDownForLongEngouh = true;
				mChartView.postDelayed(longClickRun, 500);
				return false;
			}
		});

		deleteOkButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				deleteDialog.setVisibility(View.GONE);
				chartHolderLayout.setEnabled(true);
				imageMarkerLayout.setVisibility(LinearLayout.VISIBLE);
				checkConnectivityForDeleteWeight(v.getContext());

			}
		});

		deleteCancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				deleteDialog.setVisibility(View.GONE);
				chartHolderLayout.setEnabled(true);
				imageMarkerLayout.setVisibility(LinearLayout.VISIBLE);
				startDrawingGallery();
			}
		});

		mChartView.addPanListener(new PanListener() {
			public void panApplied() {
				System.out.println("Panning");
				fingerisDownForLongEngouh = false;
				double minx = mRenderer.getXAxisMin();
				double maxx = mRenderer.getXAxisMax();

				long currentXValueMin = (long) minx;
				long currentXValueMax = (long) maxx;
				HashMap<String, Double> map = getExtremeWeightWithinTimeSpan(
						currentXValueMin, currentXValueMax);

				setUpGraphViewPort(currentXValueMax, currentXValueMin,
						map.get("maxWeightFound"), map.get("minWeightFound"));
				galleryNeedsUpdate = true;
			}

		});

		mChartView.addZoomListener(new ZoomListener() {
			@Override
			public void zoomReset() {
			}

			@Override
			public void zoomApplied(ZoomEvent e) {
				fingerisDownForLongEngouh = false;
				System.out.println("Zooming");
				double minx = mRenderer.getXAxisMin();
				double maxx = mRenderer.getXAxisMax();

				long currentXValueMin = (long) minx;
				long currentXValueMax = (long) maxx;
				HashMap<String, Double> map = getExtremeWeightWithinTimeSpan(
						currentXValueMin, currentXValueMax);

				setUpGraphViewPort(currentXValueMax, currentXValueMin,
						map.get("maxWeightFound"), map.get("minWeightFound"));
				galleryNeedsUpdate = true;

			}
		}, true, true);
		mChartView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent me) {
				if (me.getAction() == MotionEvent.ACTION_DOWN) {
					fingerisDownForLongEngouh = false;
					xPosition = me.getRawX();
					yPosition = me.getRawY();
				}
				if (me.getAction() == MotionEvent.ACTION_UP) {
					xPosition = me.getRawX();
					yPosition = me.getRawY();
					startDrawingGallery();

				}
				return false;
			}
		});

	}

	private void startDrawingGallery() {
		if (galleryNeedsUpdate) {
			long minx = (long) mRenderer.getXAxisMin();
			long maxx = (long) mRenderer.getXAxisMax();
			new DrawAllNecessaryGallery(minx, maxx).execute();
			galleryNeedsUpdate = false;
		}
	}

	@SuppressLint("NewApi")
	private void drawImageMarkers(
			final ArrayList<GroupDefinition> calculatedGroups) {
		final int statusBarHeight = getStatusBarHeight();
		if (allLinesDrawn.size() > 0) {
			// Remove off these lines
			for (int viewIndex = 0; viewIndex < allLinesDrawn.size(); viewIndex++) {
				try {
					allLinesDrawn.get(viewIndex).setVisibility(View.GONE);
					((ViewManager) allLinesDrawn.get(viewIndex).getParent())
							.removeView(allLinesDrawn.get(viewIndex));
				} catch (Exception ex) {
					ex.printStackTrace();
				}

			}
		}
		allLinesDrawn.clear();
		imageMarkerLayout = (LinearLayout) findViewById(R.id.imageMarkerLayout);
		imageMarkerLayout.removeAllViews();

		// Draw a point in the first point of the line and the last position

		long lowestTimeStampsInView = (long) mRenderer.getXAxisMin();
		long biggestTimeStampsInView = (long) mRenderer.getXAxisMax();
		long totalTimeStampInView = biggestTimeStampsInView
				- lowestTimeStampsInView;
		// What is the total length available
		// What does that correspond to in timestamp values currently shown
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		final int totalWidth = size.x;

		int OnepxinTS = (int) (totalTimeStampInView / (totalWidth - 150));
		// Width
		// Margin - left only for first
		// margin - for remaining
		int myOffsetIs = 0;

		int totalWidthUsed = 0;
		maxPhotosInAllGroups = 0;
		if (calculatedGroups != null && calculatedGroups.size() > 0) {
			for (int i = 0; i < calculatedGroups.size(); i++) {
				if (maxPhotosInAllGroups < calculatedGroups.get(i)
						.getImagesInGroup().size()) {
					maxPhotosInAllGroups = calculatedGroups.get(i)
							.getImagesInGroup().size();
				}

			}
			for (int i = 0; i < calculatedGroups.size(); i++) {

				// How much distance is my start from the previous - offset
				if (i == 0) {
					myOffsetIs = (int) ((calculatedGroups.get(i)
							.getGroupStartTime() - lowestTimeStampsInView) / OnepxinTS);
				} else {
					myOffsetIs = (int) ((calculatedGroups.get(i)
							.getGroupStartTime() - calculatedGroups.get(i - 1)
							.getGroupEndTime()) / OnepxinTS);
				}
				// How much timestamp is my width
				// How much is my width in distance points
				long imageWidthInTimestamp = calculatedGroups.get(i)
						.getGroupEndTime()
						- calculatedGroups.get(i).getGroupStartTime();

				int imageWidthToDisplay = (int) (imageWidthInTimestamp / OnepxinTS);
				if (imageWidthToDisplay < 18) {
					imageWidthToDisplay = 18;
				}

				if (myOffsetIs < 10) {
					myOffsetIs = 10;
				}

				totalWidthUsed = totalWidthUsed + imageWidthToDisplay
						+ myOffsetIs;
				final ImageView imageView = new ImageView(
						getApplicationContext());
				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
						(int) (imageWidthToDisplay), 22);

				layoutParams.setMargins(myOffsetIs, 0, 0, 0);

				imageMarkerLayout.addView(imageView, layoutParams);
				imageView.setBackgroundResource(R.drawable.circle);
				imageView.setTag(i);

				imageView.getViewTreeObserver().addOnGlobalLayoutListener(
						new OnGlobalLayoutListener() {

							@SuppressWarnings("deprecation")
							@Override
							public void onGlobalLayout() {
								int[] l = new int[2];
								imageView.getLocationInWindow(l);
								int x = l[0];
								// int y = l[1];
								int w = imageView.getWidth();
								int h = imageView.getHeight();

								LinearLayout customGalleryLayout = (LinearLayout) findViewById(R.id.myCustomGalleryView);
								int[] layoutView1 = new int[2];
								customGalleryLayout
										.getLocationOnScreen(layoutView1);

								int[] layoutView = new int[2];
								imageMarkerLayout
										.getLocationOnScreen(layoutView);

								MyCustomLine view = new MyCustomLine(
										getApplicationContext());
								view.setStartX(x + (w / 2));
								int endX = (Integer) imageView.getTag();
								if (calculatedGroups.get(endX)
										.getImagesInGroup().size() == 1) {
									view.setBottomWidth(5);
								} else {
									System.out.println("maxphotos = "
											+ maxPhotosInAllGroups);
									System.out.println("thisgroup = "
											+ calculatedGroups.get(endX)
													.getImagesInGroup().size());
									int widthcalculation = calculatedGroups
											.get(endX).getImagesInGroup()
											.size()
											* (80 / maxPhotosInAllGroups);
									System.out.println("widthcalculation = "
											+ widthcalculation);
									if (widthcalculation < 10) {
										view.setBottomWidth(10);
									} else {
										view.setBottomWidth(widthcalculation);
									}

								}
								int endXPosition = getXValueOfGalleryImage(
										endX, totalWidth,
										calculatedGroups.size(), 6);
								view.setEndX(endXPosition);
								view.setStartY(layoutView[1] - statusBarHeight
										+ h / 2 + 5);
								view.setEndY(layoutView1[1] - statusBarHeight);
								if (w < 15) {
									view.setTopWidth(10);
								} else {
									view.setTopWidth(w - 5);
								}

								ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
										LayoutParams.MATCH_PARENT,
										LayoutParams.MATCH_PARENT);

								if (imageView.isShown()) {

									addContentView(view, params);
									allLinesDrawn.add(view);
								}

								if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
									imageView.getViewTreeObserver()
											.removeGlobalOnLayoutListener(this);
								} else {
									imageView.getViewTreeObserver()
											.removeOnGlobalLayoutListener(this);
								}

							}
						});

			}
		}

	}

	private int getXValueOfGalleryImage(int locationNumber,
			int widthOfGalleryLayout, int numberOfGroups,
			int totalNumberOfGroupsPossible) {
		int xPositionOfSpecificImage = 0;
		try {
			int sizeOfMargin = 0;
			int sizeOfEachGalleryImage = (widthOfGalleryLayout / (totalNumberOfGroupsPossible))
					- (totalNumberOfGroupsPossible * 2 * sizeOfMargin);
			int sizeOfallGroupsWithinGalleryLayout = numberOfGroups
					* (sizeOfEachGalleryImage + sizeOfMargin * 2);
			int sizeOfallGroupsWithinGalleryLayoutPossible = totalNumberOfGroupsPossible
					* (sizeOfEachGalleryImage + sizeOfMargin * 2);
			int remainingSpaceOnLeft = (sizeOfallGroupsWithinGalleryLayoutPossible - sizeOfallGroupsWithinGalleryLayout) / 2;

			xPositionOfSpecificImage = (int) (remainingSpaceOnLeft
					+ (locationNumber * (sizeOfEachGalleryImage + sizeOfMargin * 2)) + sizeOfEachGalleryImage / 2);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return xPositionOfSpecificImage;
	}

	public int getStatusBarHeight() {
		int result = 0;
		int resourceId = getResources().getIdentifier("status_bar_height",
				"dimen", "android");
		if (resourceId > 0) {
			result = getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	public class PointValue {
		int xValue;
		int yValue;

	}

	private ArrayList<GroupDefinition> calculateTheGroup(
			long currentHighestTimeDifference,
			TreeMap<Long, FoodImagePost> foodImageList) {

		ArrayList<GroupDefinition> calculatedGroups = new ArrayList<GroupDefinition>();
		Long previousImageTimestamp = (long) 0;
		int currentIndex = 0;
		boolean anotherGroupCreated = false;
		for (Long eachImageTimestamp : foodImageList.keySet()) {

			GroupDefinition gdNew = null;
			if (previousImageTimestamp == (long) 0) {
				// First image directly add to the first group
				// New to create the group first
				gdNew = new GroupDefinition();
				gdNew.setGroupStartTime(eachImageTimestamp);
				anotherGroupCreated = false;
				calculatedGroups.add(gdNew);
			} else {
				if (eachImageTimestamp - previousImageTimestamp > currentHighestTimeDifference) {
					// Difference is greater - so it needs to create a new group
					gdNew = new GroupDefinition();
					gdNew.setGroupStartTime(eachImageTimestamp);
					calculatedGroups.add(gdNew);
					anotherGroupCreated = true;
				} else {
					anotherGroupCreated = false;
					// Difference is not greater - so we need to just add it to
					// the same group
					gdNew = calculatedGroups.get(currentIndex);
				}
			}

			if (anotherGroupCreated) {
				currentIndex++; // Increment the current index
			}

			if (gdNew != null) {
				// It will probably never be null at this point but check
				// nevertheless
				gdNew.setGroupEndTime(eachImageTimestamp);
				if (gdNew.getImagesInGroup() == null) {
					ArrayList<FoodImagePost> foodImagesWithinTheGroup = new ArrayList<FoodImagePost>();
					gdNew.setImagesInGroup(foodImagesWithinTheGroup);
				}

				ArrayList<FoodImagePost> foodImagesWithinTheGroup = gdNew
						.getImagesInGroup();
				foodImagesWithinTheGroup.add(foodImageList
						.get(eachImageTimestamp));
				gdNew.setImagesInGroup(foodImagesWithinTheGroup);

				calculatedGroups.set(currentIndex, gdNew);

			} else {
				System.out
						.println("#Groups# Group has not even been created?? Blasphemy");
			}
			previousImageTimestamp = eachImageTimestamp;

		}
		return calculatedGroups;
	}

	private ArrayList<GroupDefinition> getGroups(long startTimeStamp,
			long endTimeStamp, int numberOfGroups) throws Exception {

		// I received the Start and End Timestamp that the graph is currently
		// showing in the view
		// First get the list of Photos that fall into this list
		// Second - in a second arrayList - get the different in timestamp
		// between each photo -
		// so that will be totalphotos in the set - 1 count (2 photos will have
		// one value of diff between them
		// In this arraylist - sort the results
		// Now try to find the best value for separating into six sets
		// Once we have the separater logic - group the phots by separation and
		// make it available for drawing the groups

		HashMap<Long, FoodImagePost> foodImageList = new HashMap<Long, FoodImagePost>();

		// Get the list of photos within the start and end timestamp
		FetchGallery fetchGalleryObj = FetchGalleryCacheManager
				.getObject(Controller.getAppBackgroundContext());
		if (fetchGalleryObj != null && fetchGalleryObj.getData() != null
				&& fetchGalleryObj.getData().size() > 0) {
			for (int i = 0; i < fetchGalleryObj.getData().size(); i++) {
				Long imageTimeStamp = Long.parseLong(fetchGalleryObj.getData()
						.get(i).getTimestamp());
				if (imageTimeStamp >= startTimeStamp
						&& imageTimeStamp < endTimeStamp) {
					// Yes the image is a part of the full set of the graph in
					// view
					foodImageList.put(imageTimeStamp, fetchGalleryObj.getData()
							.get(i));
				}
			}
		}

		// need to sort the foodImageList - first photo first

		TreeMap<Long, FoodImagePost> sorted_map = new TreeMap<Long, FoodImagePost>(
				foodImageList);

		// Now i have the list of photos in a hashmap - i need to traverse this
		// and find the difference of timestamp between each photo
		ArrayList<Long> listOfTimeDifferences = new ArrayList<Long>();
		Long previousImageTimestamp = (long) 0;
		for (Long eachImageTimestamp : sorted_map.keySet()) {
			if (previousImageTimestamp != (long) 0) {
				// If its not the first image then add the difference from the
				// previous image
				listOfTimeDifferences.add(eachImageTimestamp
						- previousImageTimestamp);
			}
			previousImageTimestamp = eachImageTimestamp;
		}

		// Now i have all timestamp differences. Lets sort this out
		Collections.sort(listOfTimeDifferences);
		Collections.reverse(listOfTimeDifferences); // Highest difference first

		ArrayList<GroupDefinition> calculatedGroups = null;
		if (listOfTimeDifferences.size() > 0) {
			Boolean iHaveAchivedSuccess = false;
			int currentIndex = 0;

			while (!iHaveAchivedSuccess) {
				long currentHighestValueOfTimeDifference = listOfTimeDifferences
						.get(currentIndex);

				calculatedGroups = calculateTheGroup(
						currentHighestValueOfTimeDifference, sorted_map);
				if (calculatedGroups != null
						&& calculatedGroups.size() <= numberOfGroups) {
					if (calculatedGroups.size() < numberOfGroups) {
						for (int numberOfCalculatedGroups = 0; numberOfCalculatedGroups < calculatedGroups
								.size(); numberOfCalculatedGroups++) {
							if (calculatedGroups.get(0).getImagesInGroup()
									.size() > 1) {
								// This should not happen. We cannot have less
								// than 6 groups and all in one group
								iHaveAchivedSuccess = false;
								break;
							}
						}

					} else {
						// So we got 6 groups. So thats cool
						iHaveAchivedSuccess = true;
					}

				}
				currentIndex++;
				if (currentIndex >= listOfTimeDifferences.size()) {
					iHaveAchivedSuccess = true;
				}
			}
		}

		// Time to print and check what the final results were
		// printCalculatedGroups(calculatedGroups);
		this.calculatedGroups = calculatedGroups;
		return calculatedGroups;
	}

	class DrawAllNecessaryGallery extends AsyncTask<Void, Void, Void> {

		long minX, maxX;

		public DrawAllNecessaryGallery(long minXvalue, long maxXvalue) {
			this.minX = minXvalue;
			this.maxX = maxXvalue;
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				getGroups(this.minX, this.maxX, 6);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			drawLayoutForGallery(calculatedGroups);
			drawImageMarkers(calculatedGroups);

		}
	}

	@SuppressLint("NewApi")
	private void drawLayoutForGallery(
			final ArrayList<GroupDefinition> calculatedGroups) {
		LinearLayout customGalleryLayout = (LinearLayout) findViewById(R.id.myCustomGalleryView);
		customGalleryLayout.removeAllViews();
		if (calculatedGroups != null && calculatedGroups.size() > 0) {

			for (int i = 0; i < calculatedGroups.size(); i++) {
				String imageURLToDisplay = "";
				RelativeLayout imageLayout = (RelativeLayout) View.inflate(
						getApplicationContext(), R.layout.gallery_image_item,
						null);

				final ImageView imageView = (ImageView) imageLayout
						.findViewById(R.id.imgHolder);
				if (calculatedGroups.get(i).getImagesInGroup() != null
						&& calculatedGroups.get(i).getImagesInGroup().size() > 0) {
					imageURLToDisplay = calculatedGroups.get(i)
							.getImagesInGroup().get(0).getImage_thumb_url();
					if (!imageURLToDisplay.equals("")) {
						new DownloadImageTask(imageView, getApplicationContext()).execute(imageURLToDisplay);
						//imgLoader.DisplayImage(imageURLToDisplay, R.drawable.placeholder_gallery, imageView, true);
						
						imageView.setTag(i);
						imageView.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								// Click - should open gallery
								Intent intent = new Intent(
										getApplicationContext(),
										PhotoFullScaleViewer.class);
								int indexValue = (Integer) v.getTag();
								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								String[] listOfURLSToDisplay = new String[calculatedGroups
										.get(indexValue).getImagesInGroup()
										.size()];
								for (int k = 0; k < calculatedGroups
										.get(indexValue).getImagesInGroup()
										.size(); k++) {
									listOfURLSToDisplay[k] = calculatedGroups
											.get(indexValue).getImagesInGroup()
											.get(k).getImage_url();
								}

								Bundle b = new Bundle();
								b.putInt("clickedPicture", 1);
								b.putStringArray("listOfURLS",
										listOfURLSToDisplay);
								intent.putExtras(b);
								getApplicationContext().startActivity(intent);

							}
						});

					}
				}
				android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(
						android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
						android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
				params.setMargins(2, 0, 2, 0);
				customGalleryLayout.addView(imageLayout, params);

			}
		}

	}

	private class MyCustomLine extends View {
		private int startX, startY, endX, endY, topWidth, bottomWidth;

		public void setTopWidth(int topWidth) {
			this.topWidth = topWidth;
		}

		public void setBottomWidth(int bottomWidth) {
			this.bottomWidth = bottomWidth;
		}

		public MyCustomLine(Context context) {
			super(context);
		}

		@Override
		public void draw(Canvas canvas) {

			ScreenPoint[] points = new ScreenPoint[] {
					new ScreenPoint(startX - topWidth / 2, startY),
					new ScreenPoint((startX + topWidth / 2) - 5, startY),
					new ScreenPoint(endX + bottomWidth / 2, endY),
					new ScreenPoint(endX - bottomWidth / 2, endY) };
			drawPoly(canvas, R.color.graph_line_grey, points);
			// Paint paint = new Paint();
			// paint.setColor(Color.GRAY);
			// paint.setStrokeWidth(2);
			// paint.setAntiAlias(true);
			// canvas.drawRect(startX, startY, endX, endY, paint);
		}

		public void setEndY(int endY) {
			this.endY = endY;
		}

		public void setEndX(int endX) {
			this.endX = endX;
		}

		public void setStartY(int startY) {
			this.startY = startY;
		}

		public void setStartX(int startX) {
			this.startX = startX;
		}
	}

	private void drawPoly(Canvas canvas, int color, ScreenPoint[] points) {
		// line at minimum...
		if (points.length < 2) {
			return;
		}

		// paint
		Paint polyPaint = new Paint();
		polyPaint.setAntiAlias(true);
		polyPaint.setColor(getResources().getColor(R.color.graph_line_grey));
		polyPaint.setStyle(Style.FILL);

		// path
		Path polyPath = new Path();
		polyPath.moveTo(points[0].x, points[0].y);
		int i, len;
		len = points.length;
		for (i = 0; i < len; i++) {
			polyPath.lineTo(points[i].x, points[i].y);
		}
		polyPath.lineTo(points[0].x, points[0].y);

		// draw
		canvas.drawPath(polyPath, polyPaint);
	}

	private class ScreenPoint {

		public float x = 0;
		public float y = 0;

		public ScreenPoint(float x, float y) {
			this.x = x;
			this.y = y;
		}
	}

	// Delete Weight start
	final Runnable longClickRun = new Runnable() {
		public void run() {

			if (fingerisDownForLongEngouh) {
				deleteLast();
			}

		}
	};

	public void deleteLast() {

		imageMarkerLayout.setVisibility(LinearLayout.INVISIBLE);
		// removing the current lines
		try {
			// asking for 6 groups now
			if (allLinesDrawn.size() > 0) {
				// Remove off these lines
				for (int viewIndex = 0; viewIndex <= allLinesDrawn.size(); viewIndex++) {
					((ViewGroup) allLinesDrawn.get(viewIndex).getParent())
							.removeView(allLinesDrawn.get(viewIndex));
				}
			}
			allLinesDrawn.clear();
		} catch (Exception e) {
			System.out
					.println("#Groups# - Exception happened" + e.getMessage());
			e.printStackTrace();
		}

		textDialog.setText(getResources().getString(R.string.are_sure_delete));
		Animation slideUpInAnim;
		chartHolderLayout.setEnabled(false);
		slideUpInAnim = AnimationUtils.loadAnimation(this,
				R.anim.slide_upin_fast);

		deleteDialog.setVisibility(View.VISIBLE);
		deleteDialog.startAnimation(slideUpInAnim);

	}

	protected void checkConnectivityForDeleteWeight(Context context) {

		if (Utilities.isConnectingToInternet(context.getApplicationContext())) {
			new DeleteWeight(context).execute();
		} else {
			Utilities.showErrorToast(
					getResources().getString(R.string.internet_connection),
					this);
		}

	}

	class DeleteWeight extends AsyncTask<Void, Void, String> {
		private Context ctx;
		private int idOfWeightTobeDeleted = 0;
		ProgressHUD mProgressHUD;
		long lastValue;

		public DeleteWeight(Context context) {
			this.ctx = context;
			FetchWeight fetchWeight = null;
			try {
				fetchWeight = FetchWeightCacheManager.getObject(Controller
						.getAppBackgroundContext());
				if (fetchWeight != null && fetchWeight.getData() != null
						&& fetchWeight.getData().size() > 0) {
					idOfWeightTobeDeleted = fetchWeight.getData()
							.get(fetchWeight.getData().size() - 1).getId();
					fetchWeight.getData().remove(
							fetchWeight.getData().size() - 1);
					FetchWeightCacheManager.saveObject(
							Controller.getAppBackgroundContext(), fetchWeight);

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		@Override
		protected void onPreExecute() {

			mProgressHUD = ProgressHUD.show(ctx, "", true, false);

		}

		@Override
		protected String doInBackground(Void... params) {
			if (idOfWeightTobeDeleted != 0) {
				TrackerNetworkingServices.getInstance()
						.deleteWeightHttpPostRequest(userDetails.getUserID(),
								userDetails.getPassword(),
								Integer.toString(idOfWeightTobeDeleted), ctx);// addWeightHttpPostRequest(userDetails.getUserID(),
			}

			return "";
		}

		@Override
		protected void onPostExecute(String result) {
			mProgressHUD.dismiss();
			if (kgLBSChanger.getText().toString()
					.equals(getResources().getString(R.string.kg))) {
				fetchAllWeightsInKG();
			} else {
				fetchAllWeightsInLBS();
			}
			setUpSeriesWithData();
			updateGraphAndRelatedViews();

		}
	}

	// Delete Weight END

	private void alignGraph() {
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		params.leftMargin = 10;
		params.rightMargin = 10;
		chartHolderLayout.setPadding(0, 0, 0, 0);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		chartHolderLayout.addView(mChartView, params);
	}

	private void setUpSeriesWithData() {
		whiteSeries.clear();
		blackSeries.clear();
		minAvailableTimestamp = (long) 0;
		maxAvailableTimestamp = (long) 0;
		minAvailableWeightTimestamp = (long) 0;
		
		if (myDataForPlot.size() > 0) {
			for (Entry<Long, Double> entry : myDataForPlot.entrySet()) {
				if (maxAvailableTimestamp == 0) {
					maxAvailableTimestamp = entry.getKey();
				}
				if (maxAvailableTimestamp < entry.getKey()) {
					maxAvailableTimestamp = entry.getKey();

				}
				if (minAvailableTimestamp == 0) {
					minAvailableTimestamp = entry.getKey();
				}
				if (minAvailableTimestamp > entry.getKey()) {
					minAvailableTimestamp = entry.getKey();
				}

				if (maxWeightEntered < entry.getValue()) {
					maxWeightEntered = entry.getValue();

				}
				whiteSeries.add(entry.getKey(), entry.getValue());
				blackSeries.add(entry.getKey(), entry.getValue());
			}

		}

		minAvailableWeightTimestamp = minAvailableTimestamp;
		// Also check against the photos to get the max and minimum available
		// timestamp

		FetchGallery fetchGalleryObj;
		try {
			fetchGalleryObj = FetchGalleryCacheManager.getObject(Controller
					.getAppBackgroundContext());
			if (fetchGalleryObj != null && fetchGalleryObj.getData() != null
					&& fetchGalleryObj.getData().size() > 0) {
				for (int i = 0; i < fetchGalleryObj.getData().size(); i++) {
					Long imageTimeStamp = Long.parseLong(fetchGalleryObj
							.getData().get(i).getTimestamp());
					if (maxAvailableTimestamp < imageTimeStamp) {
						maxAvailableTimestamp = imageTimeStamp;
					}

					if (maxAvailableTimestamp == 0) {
						maxAvailableTimestamp = imageTimeStamp;
					}

					if (minAvailableTimestamp == 0) {
						minAvailableTimestamp = imageTimeStamp;
					}
					if (minAvailableTimestamp > imageTimeStamp) {
						minAvailableTimestamp = imageTimeStamp;
					}

				}
			}

			if (myDataForPlot != null && !myDataForPlot.isEmpty()) {
				// Now add the lowest value for 2 days ahead so the left of
				// graph to here will have a fill
				whiteSeries.add((minAvailableTimestamp - 2 * 24 * 60 * 60),
						myDataForPlot.get(minAvailableWeightTimestamp));
				blackSeries.add((minAvailableTimestamp - 2 * 24 * 60 * 60),
						myDataForPlot.get(minAvailableWeightTimestamp));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void updateGraphAndRelatedViews() {
		mChartView.repaint();
		setUpGraphViewPort(0, 0, 0.0, 0.0);
		galleryNeedsUpdate = true;
		startDrawingGallery();

	}

	private void setUpGraphViewPort(long maxXValueToShow, long minXValueToShow,
			double maxYValue, double minYValue) {
		System.out.println("Setting up graph viewport");
		double marginWeightDisplay = 0.0;
		if (kgLBSChanger.getText().toString()
				.equals(getResources().getString(R.string.kg))) {
			marginWeightDisplay = 0.5;
		} else {
			marginWeightDisplay = 2;
		}

		// To ensure the graph does not pan on the left more than what is
		// available

		if (minXValueToShow < (minAvailableTimestamp - (2 * 24 * 60 * 60))) {
			minXValueToShow = (minAvailableTimestamp - (2 * 24 * 60 * 60));
		}

		// Set the X & Y axis that we need
		if (maxXValueToShow == 0) {
			// Added 2 days
			maxXValueToShow = maxAvailableTimestamp + (2 * 24 * 60 * 60);

			// Show maximum of X days to the past unless the eaarliest available
			// time is less than that
			if (minAvailableTimestamp < maxXValueToShow - (60 * 24 * 60 * 60)) {
				minXValueToShow = maxXValueToShow - (60 * 24 * 60 * 60);
			}

			HashMap<String, Double> extremeWeightsWithinView = getExtremeWeightWithinTimeSpan(
					minXValueToShow, maxXValueToShow);
			// Find the highest and lowest weight that is visible in the current
			// view

			mRenderer.setRange(new double[] {
					minXValueToShow,
					maxXValueToShow,
					extremeWeightsWithinView.get("minWeightFound")
							- marginWeightDisplay,
					extremeWeightsWithinView.get("maxWeightFound")
							+ marginWeightDisplay });
		} else {
			if (maxYValue == 0) {
				System.out.println("No max weight found ===== " + maxYValue);
				// There are no weights within the timestamp - so retain the
				// existing ranges
				double minx = mRenderer.getXAxisMin();
				double maxx = mRenderer.getXAxisMax();
				double miny = mRenderer.getYAxisMin();
				double maxy = mRenderer.getYAxisMax();
				mRenderer.setRange(new double[] { minx, maxx, miny, maxy });
			} else {
				System.out.println("There was a max weight found ===== "
						+ maxYValue);
				mRenderer.setRange(new double[] { minXValueToShow,
						maxXValueToShow, minYValue - marginWeightDisplay,
						maxYValue + marginWeightDisplay });
			}

		}

	}

	private HashMap<String, Double> getExtremeWeightWithinTimeSpan(
			long minXValueToShow, long maxXValueToShow) {
		HashMap<String, Double> map = new HashMap<String, Double>();
		Double maxWeightFound = 0.0;
		Double minWeightFound = 5000.0;// Some high number we know will be
										// higher than the weights
		if (myDataForPlot.size() > 0) {
			for (Entry<Long, Double> entry : myDataForPlot.entrySet()) {
				if (minXValueToShow < entry.getKey()
						&& maxXValueToShow > entry.getKey()) {
					if (maxWeightFound < entry.getValue()) {
						maxWeightFound = entry.getValue();
					}
					if (minWeightFound > entry.getValue()) {
						minWeightFound = entry.getValue();
					}

				}
			}
		}
		map.put("maxWeightFound", maxWeightFound);
		map.put("minWeightFound", minWeightFound);
		return map;
	}

	private void fetchAllWeightsInLBS() {
		double double_kilo;
		long weight_date;
		double w_lbs;
		// Clear all previous values
		myDataForPlot.clear();
		try {
			FetchWeight fetchWeight = FetchWeightCacheManager
					.getObject(Controller.getAppBackgroundContext());
			if (fetchWeight != null && fetchWeight.getData() != null
					&& fetchWeight.getData().size() != 0) {
				for (int i = 0; i < fetchWeight.getData().size(); i++) {
					double_kilo = fetchWeight.getData().get(i).getKilos();
					weight_date = (long) fetchWeight.getData().get(i)
							.getTimestamp();
					w_lbs = double_kilo * (double) 2.20462;
					myDataForPlot.put(weight_date, w_lbs);

				}
			}

		} catch (Exception e1) {
			e1.printStackTrace();

		}

	}

	private void fetchAllWeightsInKG() {
		double double_kilo;
		long weight_date;
		// Clear all previous values
		myDataForPlot.clear();
		try {
			FetchWeight fetchWeight = FetchWeightCacheManager
					.getObject(Controller.getAppBackgroundContext());
			if (fetchWeight != null && fetchWeight.getData() != null
					&& fetchWeight.getData().size() != 0) {
				for (int i = 0; i < fetchWeight.getData().size(); i++) {
					double_kilo = fetchWeight.getData().get(i).getKilos();
					weight_date = (long) fetchWeight.getData().get(i)
							.getTimestamp();
					myDataForPlot.put(weight_date, double_kilo);
				}
			}

		} catch (Exception e1) {
			e1.printStackTrace();

		}

	}

	protected void moveToGraphScreen() {
		viewflipper.setInAnimation(RecommendAnimationHelper
				.inFromRightAnimation());
		viewflipper.setOutAnimation(RecommendAnimationHelper
				.outToLeftAnimation());
		viewflipper.setDisplayedChild(1);

		initializeGraph();

		localyticsSession.tagEvent(WeightTrackerActivity.TAG_TRACKER_VIEWED);
	}

	protected void start_payment() {
		IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
			@Override
			public void onIabPurchaseFinished(IabResult result, Purchase info) {
				if (result.isFailure()) {
					System.err.println("Error purchasing: " + result);

					if (result.getResponse() == 7) {
						// Item is already purchased
						localyticsSession
								.tagEvent(WeightTrackerActivity.TAG_TRACKER_VIEWED);
						moveToGraphScreen();
						return;
					} else {
						Utilities.showErrorToast("Purchase not successful",
								WeightTrackerActivity.this);
					}
					return;
				} else if (info.getSku().equals(SKU_TRACKER)) {
					// Successfully purchased move to the next screen
					System.err.println("Purchased: " + info);
					localyticsSession
							.tagEvent(WeightTrackerActivity.TAG_PURCHASED_TRACKER);
					moveToGraphScreen();

				}
			}
		};
		try {
			AccountOperations accountOperationsObj = LoginDetailsCacheManager
					.getObject(Controller.getAppBackgroundContext());
			someInfoToIdentifyTheUserMakingThePurchase = accountOperationsObj
					.getData().getUser().getEmail();
		} catch (Exception e) {

			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mHelper.launchPurchaseFlow(this, SKU_TRACKER, 10001,
				mPurchaseFinishedListener,
				someInfoToIdentifyTheUserMakingThePurchase);

	}

}
