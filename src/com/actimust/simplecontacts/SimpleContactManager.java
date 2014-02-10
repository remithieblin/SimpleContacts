package com.actimust.simplecontacts;

import java.util.ArrayList;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.provider.ContactsContract;
import android.widget.Toast;

public class SimpleContactManager {
	
	public static void addContact(Context ctx, String accountName, String name, String phone){
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
						SimpleAccountManager.GOOGLE_TYPE)
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

			ctx.getContentResolver()
					.applyBatch(ContactsContract.AUTHORITY, ops);

			Toast.makeText(ctx, "Contact saved",
					Toast.LENGTH_SHORT).show();
		} catch (Exception e) {

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
