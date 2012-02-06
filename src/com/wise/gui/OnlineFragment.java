/*
 *
 * 	This file is part of ASage.
 *
 *    ASage is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    ASage is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with ASage.  If not, see <http://www.gnu.org/licenses/>.
 *
 *   Copyright 2012 wise 
 *
 *
 */

/**
onlineFragment.java
*/
package com.wise;

import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

/**
 * @author wise
 *
 */
public class OnlineFragment extends Fragment {

	public static String ERROR_MESSAGE[];
	public static final int ERROR_URL=0;
	public static final int ERROR_XML=1;
	public static final int ERROR_NETWORK=2;
	
	private static final int DURATION_TIME= 15;
	
	
	private Toast errorToast;
	//private TextView errorText;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(ERROR_MESSAGE==null){
			ERROR_MESSAGE=getResources().getStringArray(R.array.errorMessageString);
		}
		
		
		errorToast = Toast.makeText(this.getActivity(),"", DURATION_TIME);
		
	/*	errorToast = new Toast(this.getActivity());
		errorToast.setDuration(DURATION_TIME);
		errorText = new TextView(this.getActivity());
		errorText.setBackgroundColor(R.color.BackgroudColor);
		errorText.setTextColor(R.color.TextColor);
		errorText.setCompoundDrawables(getResources().
				getDrawable(android.R.drawable.stat_sys_warning),
				null, null, null);
		errorToast.setView(errorText); */
	}
	
	
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }
    
    public void showError(int messageId, Object... paramiter){
    	//errorText.setText(String.format(ERROR_MESSAGE[messageId],paramiter));
    	errorToast.setText(String.format(ERROR_MESSAGE[messageId],paramiter));
    	errorToast.show();
    }
}
