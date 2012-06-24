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
RssFeeds.java
*/
package com.wise.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author wise
 *
 */
public class RssFeedsDB{
	/**
	 * Contains logic to return specific words from the dictionary, and
	 * load the dictionary table when it needs to be created.
	 */
	    private static final String TAG = "FeedDB";
	    
	    private static final String DATABASE_NAME = "FeedDB";
	    private static final int DATABASE_VERSION = 8;

	    private static final String FEED_TABLE ="Feed";
	    private static final String FOLDER_TABLE ="Folder";
	    
	    private final FeedDBOpenHelper mDatabaseOpenHelper;
	    
	    public static final String FEED_ID="_id";
	    public static final String FEED_NAME="Name";
	    public static final String FEED_URL="Url";
	    public static final String FEED_FAVICON="FavIcon";
	    public static final String FEED_LASTACCESS="LastVisitDate";
	    public static final String FEED_FOLDER="folderId";

	    public static final String ELEMENT_IS_FEED="isFeed";
	    public static final String ELEMENT_ID="_id";
	    public static final String ELEMENT_NAME="Name";
	    
	    public static final String FOLDER_ID="_id";
	    public static final String FOLDER_NAME="FolderName";
	    public static final String FOLDER_PARENT="ParentId";
	    
	    public static final int FOLDER_ROOT_ID =1;
	    

	    private static final String SelectElement =
	    		"SELECT 1 AS "+ELEMENT_IS_FEED+","+ FEED_ID+" AS "+ELEMENT_ID+","+ FEED_NAME+" AS "+ELEMENT_NAME+","+ FEED_URL+","+ FEED_FAVICON+","+ FEED_LASTACCESS +","+FEED_FOLDER + 
				" FROM "+FEED_TABLE+
				" WHERE "+FEED_FOLDER +"= ?"+
			" UNION "+
				" SELECT 0 AS "+ELEMENT_IS_FEED +","+FOLDER_ID +" AS "+ ELEMENT_ID+", "+FOLDER_NAME +" AS "+ELEMENT_NAME+", "+FOLDER_PARENT+
				" AS "+FEED_FOLDER+", NULL AS "+FEED_URL+",NULL AS "+FEED_FAVICON+", NULL AS "+FEED_LASTACCESS +
				" FROM "+FOLDER_TABLE+
				" WHERE "+FOLDER_PARENT+"= ? AND "+FOLDER_ID +"<>"+FOLDER_PARENT+
			" ORDER BY "+ELEMENT_IS_FEED+", "+ELEMENT_NAME;
	    
	    private static final String getAllFolder =
	    		"SELECT "+FOLDER_ID+","+FOLDER_NAME+","+FOLDER_PARENT +
	    		"  FROM "+FOLDER_TABLE+" WHERE "+FOLDER_PARENT+"=? AND "+FOLDER_ID+"<>"+FOLDER_ROOT_ID +
	    		"  ORDER BY "+FOLDER_NAME;
	    
	    private static final String getFeed =
	    		" SELECT "+FEED_ID+","+FEED_NAME+","+FEED_URL+","+FEED_FAVICON+","+FEED_LASTACCESS+
	    		" FROM "+FEED_TABLE+
	    		" WHERE "+FEED_ID+"=?" +
	    		" ORDER BY "+FEED_NAME;
	    
	    private static final String getAllFeedQuery =
	    		" SELECT "+FEED_ID+","+FEED_NAME+","+FEED_URL+","+FEED_FAVICON+","+FEED_LASTACCESS+
	    		" FROM "+FEED_TABLE+
	    		" ORDER BY "+FEED_NAME;

	/**
	 * Constructor
	 * 
	 * @param context
	 *            The Context within which to work, used to create the DB
	 */
	public RssFeedsDB(Context context) {
		mDatabaseOpenHelper = new FeedDBOpenHelper(context);
	}

	/**
	 * @return all the feed in the database
	 */
	public Cursor getAllFeed() {

		SQLiteDatabase db = mDatabaseOpenHelper.getReadableDatabase();
		Log.d(TAG, "Db Exec:\n" + getAllFeedQuery);
		return db.rawQuery(getAllFeedQuery, null);
	}

	/**
	 * 
	 * @param groupId
	 *            group number
	 * @return all the feed in the same group
	 */
	public Cursor getFeed(long groupId) {

		SQLiteDatabase db = mDatabaseOpenHelper.getReadableDatabase();
		Log.d(TAG, "Db Exec:\n" + getFeed + "\ngrupId:" + groupId);
		return db.rawQuery(getFeed, new String[] { "" + groupId });
	}

	/**
	 * @return the feed in the root group
	 */
	public Cursor getAllFolder() {
		return getAllFolder(FOLDER_ROOT_ID);
	}

	/**
	 * @param parentID group id
	 * @return return all subgroup/folder inside a group
	 */
	public Cursor getAllFolder(long parentID) {

		SQLiteDatabase db = mDatabaseOpenHelper.getReadableDatabase();
		Log.d(TAG, "Db Exec:\n" + getAllFolder);
		return db.rawQuery(getAllFolder, new String[] { "" + parentID });
	}


	/**
	 * return all the elements inside a group id,
	 * is the union of folder/group and feed
	 * @return all the elements inside a group id
	 */
	public Cursor getAllElement() {
		return getAllElement(FOLDER_ROOT_ID);
	}

	public Cursor getAllElement(long parentID) {

		SQLiteDatabase db = mDatabaseOpenHelper.getReadableDatabase();
		Log.d(TAG, "Db Exec:\n" + SelectElement);
		Log.d(TAG, "Db parent:\n" + parentID);
		return db.rawQuery(SelectElement, new String[] { "" + parentID,
				"" + parentID });
	}
	
	public long insertFolder(String name){
		return insertFolder(name,FOLDER_ROOT_ID);
	}
	    
	public long insertFolder(String name, long parentId){
		SQLiteDatabase db = mDatabaseOpenHelper.getWritableDatabase();
		ContentValues row = new ContentValues(2);
		row.put(FOLDER_NAME,name);
		row.put(FOLDER_PARENT,parentId);
		return db.insert(FOLDER_TABLE, null, row);
	}
	    
	
	public long insertFeed(long parentId,String name,String url){
		SQLiteDatabase db = mDatabaseOpenHelper.getWritableDatabase();
		ContentValues row = new ContentValues(2);
		row.put(FEED_FOLDER, parentId);
		row.put(FEED_NAME,name);
		row.put(FEED_URL, url);
		return db.insert(FEED_TABLE,null, row);
		
		
	}
	    
	    /**
	     * This creates/opens the database.
	     */
	    private static class FeedDBOpenHelper extends SQLiteOpenHelper {

	        private SQLiteDatabase mDatabase;

	        private static final String CREATE_FOLDER_TABLE =
	                    "CREATE TABLE "+FOLDER_TABLE+"(" +
	                    	FOLDER_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT,"+
	                    	FOLDER_NAME+" TEXT NOT NULL,"+
	                    	FOLDER_PARENT+" INTEGER REFERENCES "+FOLDER_TABLE+"("+FOLDER_ID+") " +
	                    			"ON DELETE CASCADE"+
	                     ");";
	        private static final String DEFAULT_FOLDER_VALUE = "INSERT INTO "+FOLDER_TABLE+
	        		"("+FOLDER_ID+","+FOLDER_NAME+","+FOLDER_PARENT+") VALUES (1,'',1);";

	        private static final String CREATE_FEED_TABLE =
                    "CREATE TABLE "+FEED_TABLE+ "(" +
                    		FEED_ID+" INTEGER PRIMARY KEY AUTOINCREMENT," +
                    		FEED_NAME+" TEXT NOT NULL,"+
                    		FEED_URL+" TEXT NOT NULL,"+
                    		FEED_FAVICON+" BLOB,"+
                    		FEED_LASTACCESS+" INTEGER,"+
                    		FEED_FOLDER+" INTEGER NOT NULL REFERENCES "+FOLDER_TABLE+"("+FOLDER_ID+")" +
                    				"ON DELETE CASCADE" +
                     ");";
	        
	        private static final String DELETE_FEED_TABLE = "DROP TABLE IF EXISTS " + FEED_TABLE+";";
	        private static final String DELETE_FOLTER_TABLE = "DROP TABLE IF EXISTS " + FOLDER_TABLE +";";
	        
	        FeedDBOpenHelper(Context context) {
	            super(context, DATABASE_NAME, null, DATABASE_VERSION);
	        }

	        @Override
	        public void onCreate(SQLiteDatabase db) {
	            mDatabase = db;
	            Log.d(TAG,"Db Exec:\n"+CREATE_FOLDER_TABLE);
	            mDatabase.execSQL(CREATE_FOLDER_TABLE);
	            Log.d(TAG,"Db Exec:\n"+DEFAULT_FOLDER_VALUE);
	            mDatabase.execSQL(DEFAULT_FOLDER_VALUE);
	            Log.d(TAG,"Db Exec:\n"+CREATE_FEED_TABLE);
	            mDatabase.execSQL(CREATE_FEED_TABLE);
	            
	            //Debug only
	            Log.d(TAG,"Db Exec:\n"+"INSERT INTO "+FEED_TABLE+"(NAME,URL,folderId) VALUES ('blogDown','http://www.downloadblog.it/rss2.xml',1);");
	            mDatabase.execSQL("INSERT INTO "+FEED_TABLE+"(NAME,URL,folderId,LastVisitDate) VALUES ('blogDown','http://www.downloadblog.it/rss2.xml',1,strftime('%s','2010-01-01'));");
	            mDatabase.execSQL("INSERT INTO "+FOLDER_TABLE+"("+FOLDER_ID+","+FOLDER_NAME+","+FOLDER_PARENT+") VALUES (2,'group1',1);");
	            mDatabase.execSQL("INSERT INTO "+FEED_TABLE+"(NAME,URL,folderId,LastVisitDate) VALUES ('blogDown2','http://www.downloadblog.it/rss2.xml',2,strftime('%s','2010-01-01'));");
	            mDatabase.execSQL("INSERT INTO "+FOLDER_TABLE+"("+FOLDER_ID+","+FOLDER_NAME+","+FOLDER_PARENT+") VALUES (3,'group2',1);");
	            mDatabase.execSQL("INSERT INTO "+FEED_TABLE+"(NAME,URL,folderId,LastVisitDate) VALUES ('blogDown3','http://www.downloadblog.it/rss2.xml',3,strftime('%s','2010-01-01'));");
                
	        }

	        @Override
	        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
	                    + newVersion + ", which will destroy all old data");
	            Log.d(TAG,"Db Exec:\n"+DELETE_FOLTER_TABLE);
	            db.execSQL(DELETE_FOLTER_TABLE);
	            Log.d(TAG,"Db Exec:\n"+DELETE_FEED_TABLE);
	            db.execSQL(DELETE_FEED_TABLE);	       
	            onCreate(db);
	        }
	    }

	}