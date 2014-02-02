package com.actimust.simplecontacts;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

public class SimpleAccountManager {

	public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern
			.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
					+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
					+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");

	public static final String GOOGLE_TYPE = "com.google";
	public static final String ACCOUNT_NOT_FOUND = "accountNotFound";

	public static String getFirstGoogleAccount(Context ctx) {
		List<String> googleAccounts = getGoogleAccounts(ctx);
		if (googleAccounts.size() != 0)
			return googleAccounts.get(0);

		return ACCOUNT_NOT_FOUND;
	}

	public static List<String> getAccountsChoices(Context ctx) {
		return getGoogleAccounts(ctx);
	}

	private static List<String> getGoogleAccounts(Context ctx) {
		Pattern emailPattern = VALID_EMAIL_ADDRESS_REGEX; // API level 8+
		Account[] accounts = AccountManager.get(ctx).getAccounts();
		List<String> result = new ArrayList<String>();
		for (Account account : accounts)
			if (account.type.equals(GOOGLE_TYPE)
					&& emailPattern.matcher(account.name).matches())
				result.add(account.name);

		return result;
	}

}
