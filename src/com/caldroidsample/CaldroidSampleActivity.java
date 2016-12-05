package com.caldroidsample;

import hirondelle.date4j.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

@SuppressLint("SimpleDateFormat")
public class CaldroidSampleActivity extends FragmentActivity {
	private static final String TAG = CaldroidSampleActivity.class.getSimpleName();
	/** 比赛周期多少天 */
	public static final int MATCH_PERIOD_DAYS = 90;
	private CaldroidFragment caldroidFragment;
	private SimpleDateFormat formatter;
	private TextView textView;
	private SimpleDateFormat yearMonthformatter;
	private String prevYearMonth;//记录当前选择的年月
	private String txtRedColor;
	private String txtBlackColor;
	private Handler matchhandler = new Handler() {
		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Calendar cal = Calendar.getInstance();

			// Min date is last 7 days
			// cal.add(Calendar.DATE, -7);
			Date minDate = cal.getTime();

			// Max date is next 7 days
			cal = Calendar.getInstance();
			cal.add(Calendar.DATE, MATCH_PERIOD_DAYS);
			Date maxDate = cal.getTime();

			// // Set selected dates
			// // From Date
			// cal = Calendar.getInstance();
			// cal.add(Calendar.DATE, 2);
			// Date fromDate = cal.getTime();
			//
			// // To Date
			// cal = Calendar.getInstance();
			// cal.add(Calendar.DATE, 3);
			// Date toDate = cal.getTime();

			// Set disabled dates
			ArrayList<Date> disabledDates = new ArrayList<Date>();
			for (int i = 0; i < MATCH_PERIOD_DAYS; i++) {
				cal = Calendar.getInstance();
				cal.add(Calendar.DATE, i);
				if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
					disabledDates.add(cal.getTime());
				}
			}

			// Customize
			caldroidFragment.setMinDate(minDate);
			caldroidFragment.setMaxDate(maxDate);
			caldroidFragment.setDisableDates(disabledDates);
//			 caldroidFragment.setSelectedDates(fromDate, toDate);
			// caldroidFragment.setShowNavigationArrows(false);
			// caldroidFragment.setEnableSwipe(false);

			caldroidFragment.refreshView();

			// Move to date
			// cal = Calendar.getInstance();
			// cal.add(Calendar.MONTH, 12);
			// caldroidFragment.moveToDate(cal.getTime());

			String text = "Today: " + formatter.format(new Date()) + "\n";
			text += "Min Date: " + formatter.format(minDate) + "\n";
			text += "Max Date: " + formatter.format(maxDate) + "\n";
			// text += "Select From Date: " + formatter.format(fromDate)
			// + "\n";
			// text += "Select To Date: " + formatter.format(toDate) + "\n";
			for (Date date : disabledDates) {
				text += "Disabled Date: " + formatter.format(date) + "\n";
			}

			textView.setText(text);

		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		formatter = new SimpleDateFormat("dd MMM yyyy");
		yearMonthformatter = new SimpleDateFormat("yyyy-MM");
	    txtRedColor = "#fc6278";
		txtBlackColor = "#000000";
		// Setup caldroid fragment
		// **** If you want normal CaldroidFragment, use below line ****
		caldroidFragment = new CaldroidFragment();

		// Setup arguments

		// If Activity is created after rotation
		if (savedInstanceState != null) {
			caldroidFragment.restoreStatesFromKey(savedInstanceState, "CALDROID_SAVED_STATE");
		}
		// If activity is created from fresh
		else {
			Bundle args = new Bundle();
			Calendar cal = Calendar.getInstance();
			args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
			args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
			args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
			args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, false);// 5行
			args.putBoolean(CaldroidFragment.IS_SHOW_TODAT_TXT, true);// 显示当天时间为“今天”

			// Uncomment this to customize startDayOfWeek
			// args.putInt(CaldroidFragment.START_DAY_OF_WEEK,
			// CaldroidFragment.TUESDAY); // Tuesday

			// Uncomment this line to use Caldroid in compact mode
			// args.putBoolean(CaldroidFragment.SQUARE_TEXT_VIEW_CELL, false);

			// Uncomment this line to use dark theme
			// args.putInt(CaldroidFragment.THEME_RESOURCE,
			// com.caldroid.R.style.CaldroidDefaultDark);

			caldroidFragment.setArguments(args);
		}

		// setCustomResourceForDates();

		// Attach to the activity
		FragmentTransaction t = getSupportFragmentManager().beginTransaction();
		t.replace(R.id.calendar1, caldroidFragment);
		t.commit();

		// Setup listener
				final CaldroidListener listener = new CaldroidListener() {

					@Override
					public void onSelectDate(Date date, View view) {
						String curentSelectYearMonth = yearMonthformatter.format(date);
						if(curentSelectYearMonth.equals(prevYearMonth)){
							if (caldroidFragment.isSelectedDate(date)) {
								caldroidFragment.clearSelectedDate(date);
							} else {
								caldroidFragment.setSelectedDate(date);
							}
							caldroidFragment.refreshView();
							updateSelectedView(caldroidFragment.getSelectedDates());
						}else{
							caldroidFragment.moveToDate(date);
							caldroidFragment.refreshView();
						}
						Log.i(TAG, "date=="+formatter.format(date)+";curentSelectYearMonth=="+curentSelectYearMonth+";prevYearMonth=="+prevYearMonth);
					}

					@Override
					public void onChangeMonth(int month, int year) {
						prevYearMonth = year+"-"+(month>9?month:("0"+month))+"";
						Log.i(TAG, "prevYearMonth=="+prevYearMonth+";");
					}

					@Override
					public void onLongClickDate(Date date, View view) {
						Log.i(TAG, "Long click " + formatter.format(date));
					}

					@Override
					public void onCaldroidViewCreated() {
						if (caldroidFragment.getLeftArrowButton() != null) {
							Log.i(TAG, "Caldroid view is created");
						}
					}

				};


		// Setup Caldroid
		caldroidFragment.setCaldroidListener(listener);

		textView = (TextView) findViewById(R.id.textview);

		matchhandler.sendEmptyMessageDelayed(1, 500);

	}
	/**
	 * 设置已选择
	 */
	private void updateSelectedView(ArrayList<DateTime> selectedDates) {
		StringBuilder builder = new StringBuilder();
		for (DateTime time : selectedDates) {
			builder.append(time.format("YYYY.MM.DD")).append("、");
		}
		if (builder.length() > 0) {
			String selecteddate = builder.substring(0, builder.length() - 1);
			textView.setText(Html.fromHtml("<font color=\"" + txtBlackColor + "\">已选:</font>" + "<font color=\"" + txtRedColor + "\">" + selecteddate + "</font>"));
		} else {
			textView.setText(Html.fromHtml("<font color=\"" + txtBlackColor + "\">已选:</font>" + "<font color=\"" + txtRedColor + "\"></font>"));
		}

	}
	/**
	 * Save current states of the Caldroid here
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);

		if (caldroidFragment != null) {
			caldroidFragment.saveStatesToKey(outState, "CALDROID_SAVED_STATE");
		}

	}

}
