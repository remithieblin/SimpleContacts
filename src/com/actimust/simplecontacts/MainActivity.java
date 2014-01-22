package com.actimust.simplecontacts;

import java.util.ArrayList;
import java.util.regex.Pattern;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class MainActivity extends SherlockActivity {

	ActionMode mMode;
	boolean actionModeUp;

	public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern
			.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
					+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
					+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");

	private final String accountType = "com.google";
	private String accountName;
	private boolean isInitialized = true;
	private boolean mActionModeIsActive;
	private boolean mBackWasPressedInActionMode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Intent intent = AccountPicker.newChooseAccountIntent(null, null, new
		// String[]{"com.google"},
		// false, null, null, null, null);
		// startActivityForResult(intent, SOME_REQUEST_CODE);

		Pattern emailPattern = VALID_EMAIL_ADDRESS_REGEX; // API level 8+
		Account[] accounts = AccountManager.get(this).getAccounts();
		for (Account account : accounts)
			if (account.type.equals(accountType)
					&& emailPattern.matcher(account.name).matches())
				accountName = account.name;

		setContentView(R.layout.activity_main);

		initializeForm();

		final EditText nameET = ((EditText) findViewById(R.id.nameET));
		nameET.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (!actionModeUp) {
					mMode = startActionMode(new AnActionModeOfEpicProportions());
					actionModeUp = true;
				}
				getReadyForInput(nameET);
				return false;
			}
		});

		nameET.requestFocus();

		final EditText phoneET = ((EditText) findViewById(R.id.phoneET));
		phoneET.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (!actionModeUp) {
					mMode = startActionMode(new AnActionModeOfEpicProportions());
					actionModeUp = true;
				}
				getReadyForInput(phoneET);
				return false;
			}
		});
	}

	private void getReadyForInput(final EditText editText) {
		editText.setText("");
		editText.setTextColor(Color.WHITE);
		isInitialized = false;
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
			initializeForm();
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

		if (name != null && name != "" && !isInitialized) {

			/*
			 * Prepares the batch operation for inserting a new raw contact and
			 * its data. Even if the Contacts Provider does not have any data
			 * for this person, you can't add a Contact, only a raw contact. The
			 * Contacts Provider will then add a Contact automatically.
			 */

			// Creates a new array of ContentProviderOperation objects.
			ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

			/*
			 * Creates a new raw contact with its account type (server type) and
			 * account name (user's account). Remember that the display name is
			 * not stored in this row, but in a StructuredName data row. No
			 * other data is required.
			 */
			ContentProviderOperation.Builder op = ContentProviderOperation
					.newInsert(ContactsContract.RawContacts.CONTENT_URI)
					.withValue(ContactsContract.RawContacts.ACCOUNT_TYPE,
							accountType)
					.withValue(ContactsContract.RawContacts.ACCOUNT_NAME,
							accountName);

			// Builds the operation and adds it to the array of operations
			ops.add(op.build());

			// Creates the display name for the new raw contact, as a
			// StructuredName data row.
			op = ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					/*
					 * withValueBackReference sets the value of the first
					 * argument to the value of the ContentProviderResult
					 * indexed by the second argument. In this particular call,
					 * the raw contact ID column of the StructuredName data row
					 * is set to the value of the result returned by the first
					 * operation, which is the one that actually adds the raw
					 * contact row.
					 */
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID, 0)

					// Sets the data row's MIME type to StructuredName
					.withValue(
							ContactsContract.Data.MIMETYPE,
							ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)

					// Sets the data row's display name to the name in the UI.
					.withValue(
							ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
							name);

			// Builds the operation and adds it to the array of operations
			ops.add(op.build());

			// Inserts the specified phone number and type as a Phone data row
			op = ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					/*
					 * Sets the value of the raw contact id column to the new
					 * raw contact ID returned by the first operation in the
					 * batch.
					 */
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID, 0)

					// Sets the data row's MIME type to Phone
					.withValue(
							ContactsContract.Data.MIMETYPE,
							ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)

					// Sets the phone number and type
					.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,
							phone);
			// .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
			// phoneType);

			// Builds the operation and adds it to the array of operations
			ops.add(op.build());

			/*
			 * Demonstrates a yield point. At the end of this insert, the batch
			 * operation's thread will yield priority to other threads. Use
			 * after every set of operations that affect a single contact, to
			 * avoid degrading performance.
			 */
			op.withYieldAllowed(true);

			// Builds the operation and adds it to the array of operations
			ops.add(op.build());

			// Ask the Contacts Provider to create a new contact
			// Log.d(TAG,"Selected account: " + mSelectedAccount.getName() +
			// " (" +
			// mSelectedAccount.getType() + ")");
			// Log.d(TAG,"Creating contact: " + name);

			/*
			 * Applies the array of ContentProviderOperation objects in batch.
			 * The results are discarded.
			 */
			try {

				getContentResolver()
						.applyBatch(ContactsContract.AUTHORITY, ops);

				Toast.makeText(MainActivity.this, "Contact saved",
						Toast.LENGTH_SHORT).show();
			} catch (Exception e) {

				// Display a warning
				Context ctx = getApplicationContext();
				// CharSequence txt =
				// getString(R.string.contactCreationFailure);
				int duration = Toast.LENGTH_SHORT;
				Toast toast = Toast.makeText(ctx, "Could not create contact: "
						+ e.getMessage(), duration);
				toast.show();

				// Log exception
				// Log.e(TAG, "Exception encountered while inserting contact: "
				// + e);
			}
		}
	}

	private void initializeForm() {
		EditText nameET = ((EditText) findViewById(R.id.nameET));
		initialiseEditText(nameET, "Name");
		EditText phoneET = ((EditText) findViewById(R.id.phoneET));
		initialiseEditText(phoneET, "415 415 6969");
		isInitialized = true;
	}

	private void initialiseEditText(EditText nameET, String text) {
		nameET.setText(text);
		nameET.setTextColor(Color.GRAY);
	}
}
