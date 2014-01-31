package com.actimust.simplecontacts;

import java.util.regex.Pattern;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.widget.RadioButton;

public class SimpleAccountManager {
	
	public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern
			.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
					+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
					+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");

	public static final String GOOGLE_TYPE = "com.google";
	public static final String ACCOUNT_NOT_FOUND = "accountNotFound";
	
	public static String getFirstGoogleAccount(Context ctx){
		Pattern emailPattern = VALID_EMAIL_ADDRESS_REGEX; // API level 8+
		Account[] accounts = AccountManager.get(ctx).getAccounts();
		for (Account account : accounts)
			if (account.type.equals(GOOGLE_TYPE)
					&& emailPattern.matcher(account.name).matches())
				return  account.name;
		
		return ACCOUNT_NOT_FOUND;
	}
	
	public static RadioButton[] getAccountsChoices(Context ctx){
		return new RadioButton[1];
	}

}
