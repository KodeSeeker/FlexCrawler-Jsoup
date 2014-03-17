package com.test;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.googlecode.flaxcrawler.CrawlerConfiguration;
import com.googlecode.flaxcrawler.CrawlerController;
import com.googlecode.flaxcrawler.CrawlerException;
import com.googlecode.flaxcrawler.DefaultCrawler;
import com.googlecode.flaxcrawler.download.DefaultDownloaderController;
import com.googlecode.flaxcrawler.model.CrawlerTask;
import com.googlecode.flaxcrawler.model.Page;
import com.googlecode.flaxcrawler.parse.DefaultParserController;

public class ExtraCredit {

	
    public static void main(String[] args) throws MalformedURLException, CrawlerException {
        // Setting up downloader controller
     try{
    	DefaultDownloaderController downloaderController = new DefaultDownloaderController();
        // Setting up parser controller
        DefaultParserController parserController = new DefaultParserController();
        	
        // Creating crawler configuration object
        CrawlerConfiguration configuration = new CrawlerConfiguration();

        // Creating five crawlers (to work with 5 threads)
     //   for (int i = 0; i < 5; i++) {
            // Creating crawler and setting downloader and parser controllers
            DefaultCrawler crawler = new ExampleCrawler();
            crawler.setDownloaderController(downloaderController);
            crawler.setParserController(parserController);
            // Adding crawler to the configuration object
            configuration.addCrawler(crawler);
       // }

        // Setting maximum parallel requests to a single site limit
        configuration.setMaxParallelRequests(1);
        // Setting http errors limits. If this limit violated for any
        // site - crawler will stop this site processing
        configuration.setMaxHttpErrors(HttpURLConnection.HTTP_CLIENT_TIMEOUT, 10);
        configuration.setMaxHttpErrors(HttpURLConnection.HTTP_BAD_GATEWAY, 10);
        // Setting period between two requests to a single site (in milliseconds)
        configuration.setPolitenessPeriod(500);
        configuration.setMaxParallelRequests(0);
        // Initializing crawler controller
        CrawlerController crawlerController = new CrawlerController(configuration);
        // Adding crawler seed
        
        List<String>  urllist= new ArrayList<String>();
      //  urllist.add("http://www.truthorfiction.com/index-new.htm");
       // urllist.add("http://www.truthorfiction.com/index-food.htm");
       // urllist.add("http://www.truthorfiction.com/index-pleasforhelp.htm");
        
       // urllist.add("http://www.truthorfiction.com/index-education.htm");
       // urllist.add("http://www.truthorfiction.com/index-household.htm");
        //urllist.add("http://www.truthorfiction.com/index-inspirational.htm");
      //  urllist.add("http://www.truthorfiction.com/index-tsunami.htm");
        //urllist.add("http://www.truthorfiction.com/index-warnings.htm");
        int urlCount=0;
   //     urllist.add("http://www.truthorfiction.com/index-animals.htm");
      //  urllist.add("http://www.truthorfiction.com/index-insects.htm");
       //urllist.add("http://www.truthorfiction.com/index-news.htm");
        //urllist.add("http://www.truthorfiction.com/index-internet.htm");
        //urllist.add("http://www.truthorfiction.com/index-prayers.htm");
        //urllist.add("http://www.truthorfiction.com/index-humor.htm");
        //urllist.add("http://www.truthorfiction.com/index-military.htm");
       // urllist.add("http://www.truthorfiction.com/index-celebrities.htm");
        //urllist.add("http://www.truthorfiction.com/index-politics.htm");
        
        //urllist.add("http://www.truthorfiction.com/index-hurricane-katrina.htm");
        //urllist.add("http://www.truthorfiction.com/index-war.htm");
        //urllist.add("http://www.truthorfiction.com/index-wtc.htm");
        //urllist.add("http://www.truthorfiction.com/index-promises.htm");
      //  urllist.add("http://www.truthorfiction.com/index-religious.htm");
       // urllist.add("http://www.truthorfiction.com/index-medical.htm");
       // urllist.add("http://www.truthorfiction.com/index-aviation.htm");
        //urllist.add("http://www.truthorfiction.com/index-government.htm");
        //urllist.add("http://www.truthorfiction.com/index-misc.htm");
        //urllist.add("http://www.truthorfiction.com/index-missing.htm");
        urllist.add("http://www.truthorfiction.com/index-redfaces.htm");
        for(String url : urllist)
        {
        	if(url==null||url.trim().isEmpty() )
        		continue;
        	crawlerController.addSeed(new URL(url));
        // Starting and joining our crawler
        	crawlerController.start();
        	urlCount++;
        	System.out.println(url+" "+urlCount);
        // Join crawler controller and wait for 60 seconds
        	crawlerController.join(60000);
        // Stopping crawler controller
       
        	 crawlerController.stop();
        }
       
        
     }
     catch(Exception e)
     {
    	 e.printStackTrace();
     }
        
        System.out.println("Done");
    }

    /**
     * Custom crawler. Extends {@link DefaultCrawler}.
     */
    private static class ExampleCrawler extends DefaultCrawler {

    	private  boolean seenOnce=false;//hack
    	/**
         * This method is called after each crawl attempt.
         * Warning - it does not matter if it was unsuccessful attempt or response was redirected.
         * So you should check response code before handling it.
         * @param crawlerTask
         * @param page
         */
        @Override
        protected void afterCrawl(CrawlerTask crawlerTask, Page page) {
            super.afterCrawl(crawlerTask, page);
           try{
            if(seenOnce==false)
            {
            	seenOnce=true;
            	Set <URL> urlSet = new HashSet<URL>();
            if (page == null) {
                System.out.println(crawlerTask.getUrl() + " violates crawler constraints (content-type or content-length or other)");
            } else if (page.getResponseCode() >= 300 && page.getResponseCode() < 400) {
                // If response is redirected - crawler schedules new task with new url
                System.out.println("Response was redirected from " + crawlerTask.getUrl());
            } else if (page.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Printing url crawled
                System.out.println(crawlerTask.getUrl() + ". Found " + (page.getLinks() != null ? page.getLinks().size() : 0) + " links.");
                //page.
      
            for( URL url  : page.getLinks())
                {
            		if (!url.toString().contains("index"))
            		{	//System.out.println(url);	
            			urlSet.add(url);// crawler contains all index URL's
            		}
            	}
          
            
              if(!urlSet.isEmpty())
            	getDatafromURL(urlSet);
              else
            	System.out.println("The set doesnt contain any URL's!");
            }
            }
           }
            catch(Exception e)
            {
            	e.printStackTrace();
            }
            
        }
        
        
        
        
        private void getDatafromURL(Set<URL> urlList)
        {
        	try
        	{
        	if(urlList==null)
        		return;
        	Iterator<URL> it= urlList.iterator();
        	while(it.hasNext())
        	{
        		URL url =it.next();
        		
        			parsewithJSoup(url);	
        		
        	///	NestedCrawler nestcrwl= new NestedCrawler(url);
        	//	nestcrwl.
        	}
        //	System.out.println(urlList.size());
        	
       
        	}
        	catch (Exception e)
        	{
        		e.printStackTrace();
        	}
        }
        
        public   Set <URL> getSecondPageLinks(URL url)
        {
        	
        	Set<URL> secondPageLinks= new HashSet<URL>();
        	String urlString= url.toString();
        	try {
				Document doc= Jsoup.connect(urlString).get();
				Elements ets= doc.select("a");
				System.out.println(ets);
        	
        	
        	} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 

        	
        	
        	return secondPageLinks;
        }
        
        private void parsewithJSoup(URL url)
        {
        	try
        	{
        	String  urlString= url.toString();
        	
        	String htmlText= Jsoup.connect(urlString).get().html(); 
        	Document htmlDoc= Jsoup.parse(htmlText);
        	String removedhtml=htmlDoc.text();
        	//System.out.println(removedhtml);
        	// defining indexes.
        	//System.out.println();
        	int navIndex= removedhtml.indexOf("Summary of the eRumor:");
        	if (navIndex==-1)
        		return;
        	int truIdx=removedhtml.indexOf("The Truth:");
        	 
       
        	String summary = removedhtml.substring(navIndex,truIdx);
        	String 	truth=removedhtml.substring(truIdx,removedhtml.indexOf(" ©"));
        
        	//System.out.println(summary);//put this in a file.
        	//System.out.println(truth); 
        	writeToFile(summary, truth);
        	
        	}
        	catch(Exception e)
        	{
        		
        		e.printStackTrace();
        		
        	}
        	}
        	
        
         void writeToFile(String summary,  String truth)
         {
        	 try
        	 	{
        		String pathToFile="F:\\extracreditFinalTruthOrFiction.txt";
        	    BufferedWriter out= new BufferedWriter(new FileWriter(pathToFile,true));
        	 	out.append("@@@begin_summary@@@");
        	 	out.append(summary);
        	 	out.append("@@@end_summary@@@");
        	 	out.append("\n");
        	 	out.append("@@@begin_truth@@@");
            	out.append(truth);
            	out.append("@@@end_truth@@@");
            	out.append("\n");
        	 	out.close();
        	 	}
        	 catch(Exception e)
        	 {
        		 e.printStackTrace();
        	 }
        	 }
        	 
         
        
     
        /**
         * You may check if you want to crawl next task
         * @param crawlerTask Task that is going to be crawled if you return {@code true}
         * @param parent parent.getUrl() page contain link to a crawlerTask.getUrl() or redirects to it
         * @return
         */
        @Override
        public boolean shouldCrawl(CrawlerTask crawlerTask, CrawlerTask parent) {
            // Default implementation returns true if crawlerTask.getDomainName() == parent.getDomainName()
            return super.shouldCrawl(crawlerTask, parent);
        }
    }
}