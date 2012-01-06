package com.wise;

import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.transform.Result;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class ASageActivity extends Activity {
	
	final static String ASAGE_URL = "url"; 
	
	private WebView mBrowser;
	private TextView mTitle;
	private URL feedXml;
	private Transformer mRss2Html;
	
    /** Called when the activity is first created. */
  	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_sage);
        
        mBrowser = (WebView) this.findViewById(R.id.browser);
        mBrowser.setWebViewClient(new WebClientManager());
        
        mTitle = (TextView) this.findViewById(R.id.title);
        
        
        
        if(savedInstanceState == null)
        	savedInstanceState = this.getIntent().getExtras();
        
        try {
        	feedXml = new URL(savedInstanceState.getString(ASAGE_URL));
        	//feedXml = new URL("http://www.comicsblog.it/rss2.xml");	
        	
		} catch (MalformedURLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
        
        
        
        try {
			mRss2Html = TransformerFactory.newInstance().newTransformer( 
					new StreamSource(this.getResources().openRawResource(R.raw.rss2html)));
		} catch (TransformerConfigurationException e) {
			Log.println(Log.DEBUG, "xmlTrans", e.toString());
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			Log.println(Log.DEBUG, "xmlTrans", e.toString());
			e.printStackTrace();
		}
       
        /*Cursor c = new CursorLoader(this,android.provider.Browser.BOOKMARKS_URI,
        		null, null, null, null).loadInBackground();
        
        int nameIndex = c.getColumnIndex(Browser.BookmarkColumns.TITLE);
        int isBookMarkIndex = c.getColumnIndex(Browser.BookmarkColumns.BOOKMARK);
        int urlIndex = c.getColumnIndex(Browser.BookmarkColumns.URL);
     
        Log.d("bookMarksCol","number: "+c.getColumnCount());
        for(String name : c.getColumnNames()){
        	Log.d("bookMarksCol","name: "+name);
        }
        c.moveToFirst();
        while (!c.isAfterLast()){
        	if(c.getInt(isBookMarkIndex)==1){
        		Log.d("bookMarks","name: "+c.getString(nameIndex));
        		Log.d("bookMarks","Url: "+c.getString(urlIndex));
        	}
        	c.moveToNext();
        }
        */
      
        new LoadRss().execute(feedXml);
        
	}//onCreate
	
  	@Override
  	protected void onSaveInstanceState (Bundle outState){
  		//save the feed
  		outState.putString("url",feedXml.toString());
  	}
  	
	private class LoadRss extends AsyncTask<URL,Void,Void>{

		private ByteArrayOutputStream page = new ByteArrayOutputStream();
		
		@Override
		protected Void doInBackground(URL... arg0) {			
			Result html = new StreamResult(page);
	       // StreamSource s = new StreamSource(this.getResources().openRawResource(R.raw.rss2));
	        StreamSource s=null;
			try {
				s = new StreamSource(feedXml.openStream());
			} catch (IOException e1) {
				e1.printStackTrace();
			} 
	        try {
				mRss2Html.transform(s,html);
			} catch (TransformerException e) {
				Log.println(Log.WARN, "xmlTrans", e.toString());
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void notUsed){
			mTitle.setText("Finito!!");
			mBrowser.loadData(page.toString(), "text/html", "utf8");
			
		}
				
	}
	
	private class WebClientManager extends  WebViewClient{
		
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url){
			Log.d("LoadUrl", "Open: "+url);
			
			Intent i = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
			view.getContext().startActivity(i);
			
			return true;
		}
		
	}
	
}