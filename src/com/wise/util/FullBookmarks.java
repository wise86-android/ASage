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
Bookmarks.java
*/
package com.wise.util;

import android.content.Context;
import android.content.CursorLoader;

import android.provider.BrowserContract.Bookmarks;

/**
 * @author wise
 *
 */
public class FullBookmarks extends CursorLoader{

  //TODO: gestire le subdirectory
  //TODO: la data modificata probabilemente e un altra, guardare su history..
  

	public static final String FAVICON =Bookmarks.FAVICON;
	public static final String TITLE = Bookmarks.TITLE;
	public static final String URL = Bookmarks.URL;
	public static final String LAST_VISIT = Bookmarks.DATE_MODIFIED;
	
	public FullBookmarks(Context c){
		super(c,Bookmarks.CONTENT_URI_DEFAULT_FOLDER,
				new String[] {
					Bookmarks._ID,
					Bookmarks.FAVICON,
					Bookmarks.DATE_MODIFIED,
					Bookmarks.TITLE,
					Bookmarks.URL }, 
				//show only the bookmarks
				null,null,
				//order by title
				Bookmarks.TITLE);
	}
	
	public FullBookmarks(Context c,long folderId){
		super(c,Bookmarks.CONTENT_URI_DEFAULT_FOLDER,
				new String[] {
					Bookmarks._ID,
					Bookmarks.FAVICON,
					Bookmarks.DATE_MODIFIED,
					Bookmarks.TITLE,
					Bookmarks.URL }, 
				//show only the bookmarks
				"("+Bookmarks.PARENT +"="+folderId+")",null,
				//order by title
				Bookmarks.TITLE);
	}
	
	
}
