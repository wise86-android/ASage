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

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebViewFragment;

public class RssViewFragment extends WebViewFragment {
	
	final static String RSS_URL = "url"; 
	
	private URL feedXml;
	private Transformer mRss2Html;
	private WebView browser;

	/** Called when the activity is first created. */
  	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            
        if(savedInstanceState == null)
        	savedInstanceState = this.getActivity().getIntent().getExtras();
        
        try {
        	//feedXml = new URL(savedInstanceState.getString(RSS_URL));
        	feedXml = new URL("http://www.comicsblog.it/rss2.xml");	
        	
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
       
    
        
	}//onCreate
  	
  	
  	public void onStart(){
  		super.onStart();
  		Log.d("LoadUrl", "Open: ");
  	    browser = this.getWebView();
        browser.setWebViewClient(new WebClientManager());
        new LoadRss().execute(feedXml);
  	}
	
  	@Override
  	public void onSaveInstanceState(Bundle outState){
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
			browser.loadData(page.toString(), "text/html", "utf8");
			Log.d("LoadUrl", "Open: ");
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