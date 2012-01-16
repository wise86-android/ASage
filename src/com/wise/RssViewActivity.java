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
RssViewActivity.java
*/
package com.wise;

import android.app.Activity;
import android.os.Bundle;

/**
 * @author wise
 *
 */
public class RssViewActivity extends Activity {
	
	public final static String RSS_URL = RssViewFragment.RSS_URL;
	public final static String TAG ="RssViewActivity";
	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.rss_view_activity);
		Bundle extras = getIntent().getExtras();
	    
	
		if (extras != null) {
			// Take the info from the intent and deliver it to the fragment so it can update
			String url = extras.getString(RSS_URL);
			RssViewFragment frag = (RssViewFragment) getFragmentManager().findFragmentById(R.id.rss_view_fragment);
			frag.viewRss(url);
      }
		
	}

}
