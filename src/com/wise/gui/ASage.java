/*
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
 *   Copyright 2012 Giovanni Visentini 
 */

package com.wise.gui;

import com.wise.R;
import com.wise.util.BookmarkFolder;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Main class of the project
 * @author Giovanni Visentini
 */
public class ASage extends CachedActivity {
	
	private MenuItem preference;
	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.a_sage);
		this.getFragmentManager().beginTransaction().add(R.id.feed_list_contaner, new FeedExplorerFragment()).commit();
				
	}
	
	public boolean onOptionsItemSelected (MenuItem item){
		if(item.equals(preference)){
			startPreferenceActivity();
			return true;
		}//else
		return false;
	}
	
	public boolean onCreateOptionsMenu (Menu menu){
		
		preference= menu.add(R.string.preferenceMenuItem);
		
		return true;
		
	}
	
	private void startPreferenceActivity(){
		Intent i = new Intent(this,Preference.class);
		startActivity(i);
	}
}
