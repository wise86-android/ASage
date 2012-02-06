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
BookmarkFolder.java
*/
package com.wise.util;

import android.provider.BrowserContract.Bookmarks;
import android.content.Context;
import android.content.CursorLoader;


/**
 * A Cursor for scan the Bookmarks folders
 * 
 * @author Giovanni Visentini
 *
 */
public class BookmarkFolder extends CursorLoader{
	
	public static final String FOLDER_NAME=Bookmarks.TITLE;
	public static final String FOLDER_ID=Bookmarks._ID;
	
	public BookmarkFolder(Context c){
		super(c,Bookmarks.CONTENT_URI_DEFAULT_FOLDER,
				new String[]{
					Bookmarks._ID,
					Bookmarks.TITLE },
			"(" + Bookmarks.IS_FOLDER +"=1)",
			null,Bookmarks.TITLE);
	}
	

}
