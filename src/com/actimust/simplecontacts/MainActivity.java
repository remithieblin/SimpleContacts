package com.actimust.simplecontacts;

import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MainActivity extends SherlockActivity {

	private static final String NO_ACCOUNT_CHOSEN = "NO_ACCOUNT_CHOSEN";
	private static final String ACCOUNT_CHOOSEN = "ACCOUNT_CHOOSEN";
	ActionMode mMode;
	boolean actionModeUp;

	private String accountName;
	private boolean mBackWasPressedInActionMode;
	private SlidingMenu menu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		String name = intent.getStringExtra(ContactsContract.Intents.Insert.NAME);
		String number = intent.getStringExtra(ContactsContract.Intents.Insert.PHONE);

		setAccount();

		setContentView(R.layout.activity_main);

		initMenu();

		initSettings();

		GestureManager gestureManager = new GestureManager();
		gestureManager.manage(menu);

		final EditText nameET = ((EditText) findViewById(R.id.nameET));
		nameET.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (!actionModeUp) {
					mMode = startActionMode(new AnActionModeOfEpicProportions());
					actionModeUp = true;
				}
				return false;
			}
		});
		nameET.requestFocus();
		if (name != null && name != "")
			nameET.setText(name);

		final EditText phoneET = ((EditText) findViewById(R.id.phoneET));
		phoneET.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (!actionModeUp) {
					mMode = startActionMode(new AnActionModeOfEpicProportions());
					actionModeUp = true;
				}
				return false;
			}
		});
		if(number != null && number != "")
			phoneET.setText(number);
	}

	private void setAccount() {
		SharedPreferences settings = getSharedPreferences("pref", 0);
		accountName = settings.getString(ACCOUNT_CHOOSEN, NO_ACCOUNT_CHOSEN);
		if (accountName.equals(NO_ACCOUNT_CHOSEN))
			accountName = SimpleAccountManager.getFirstGoogleAccount(this);
	}

	private void initSettings() {
		final RadioGroup group = ((RadioGroup) findViewById(R.id.radio_selection_group));
		List<String> accountsChoices = SimpleAccountManager
				.getAccountsChoices(this);
		int id = 0;
		for (String account : accountsChoices) {
			final RadioButton radioButton = new RadioButton(this);
			radioButton.setText(account);
			radioButton.setId(Integer.MAX_VALUE - id);
			id++;
			if(accountName.equals(account)){
				group.check(radioButton.getId());
				radioButton.setChecked(true);
			}
			
			radioButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					SharedPreferences settings = getSharedPreferences("pref",0);
					SharedPreferences.Editor editor = settings.edit();
					editor.putString(ACCOUNT_CHOOSEN, (String)radioButton.getText());
					editor.commit();
					
					accountName = (String) radioButton.getText();
				}
			});
			group.addView(radioButton);
		}
	}

	private void initMenu() {
		menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.RIGHT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
		menu.setMenu(R.layout.settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add("Settings").setIcon(R.drawable.ic_launcher_settings)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		menu.toggle();
		return super.onOptionsItemSelected(item);
	}

	private final class AnActionModeOfEpicProportions implements
			ActionMode.Callback {

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {

			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			Toast.makeText(MainActivity.this, "Got click: " + item,
					Toast.LENGTH_SHORT).show();
			mode.finish();
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {

			if (!mBackWasPressedInActionMode)
				saveContact();
			EditText nameET = ((EditText) findViewById(R.id.nameET));
			nameET.setText("");
			EditText phoneET = ((EditText) findViewById(R.id.phoneET));
			phoneET.setText("");

			mBackWasPressedInActionMode = false;
			actionModeUp = false;
		}

	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		mBackWasPressedInActionMode = actionModeUp
				&& event.getKeyCode() == KeyEvent.KEYCODE_BACK;
		return super.dispatchKeyEvent(event);
	}

	private void saveContact() {

		EditText nameET = ((EditText) findViewById(R.id.nameET));
		String name = nameET.getText().toString();
		EditText phoneET = ((EditText) findViewById(R.id.phoneET));
		String phone = phoneET.getText().toString();

		if (name != null && name != "") 
			SimpleContactManager.addContact(this, accountName, name, phone);
	}

}
