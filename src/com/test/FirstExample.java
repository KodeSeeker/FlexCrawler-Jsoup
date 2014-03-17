package com.test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.googlecode.flaxcrawler.CrawlerConfiguration;
import com.googlecode.flaxcrawler.CrawlerController;
import com.googlecode.flaxcrawler.CrawlerException;
import com.googlecode.flaxcrawler.DefaultCrawler;
import com.googlecode.flaxcrawler.download.DefaultDownloaderController;
import com.googlecode.flaxcrawler.model.CrawlerTask;
import com.googlecode.flaxcrawler.model.Page;
import com.googlecode.flaxcrawler.parse.DefaultParserController;

public class FirstExample {

	public static void main(String[] args) throws MalformedURLException,
			CrawlerException {
		// Setting up downloader controller
		DefaultDownloaderController downloaderController = new DefaultDownloaderController();
		// Setting up parser controller
		DefaultParserController parserController = new DefaultParserController();

		// Creating crawler configuration object
		CrawlerConfiguration configuration = new CrawlerConfiguration();

		// Creating five crawlers (to work with 5 threads)
		for (int i = 0; i < 2; i++) {
		// Creating crawler and setting downloader and parser controllers
		DefaultCrawler crawler = new ExampleCrawler();
		crawler.setDownloaderController(downloaderController);
		crawler.setParserController(parserController);
		// Adding crawler to the configuration object
		configuration.addCrawler(crawler);
		 }

		// Setting maximum parallel requests to a single site limit
		configuration.setMaxParallelRequests(0);
		// Setting http errors limits. If this limit violated for any
		// site - crawler will stop this site processing
		configuration.setMaxHttpErrors(HttpURLConnection.HTTP_CLIENT_TIMEOUT,
				10);
		configuration.setMaxHttpErrors(HttpURLConnection.HTTP_BAD_GATEWAY, 10);
		// Setting period between two requests to a single site (in
		// milliseconds)
		configuration.setPolitenessPeriod(500);
		configuration.setMaxParallelRequests(0);
		// Initializing crawler controller
		CrawlerController crawlerController = new CrawlerController(
				configuration);
		// Adding crawler seed
		crawlerController
				.addSeed(new URL(
						"http://search.atomz.com/search/?sp-q=katrina&sp-a=00062d45-sp00000000&sp-advanced=1&sp-p=all&sp-w-control=1&sp-w=alike&sp-date-range=-1&sp-x=any&sp-c=100&sp-m=0&sp-s=0"));
		crawlerController
				.addSeed(new URL(
						"http://search.atomz.com/search/?sp-q=hurricane+katrina&x=0&y=0&sp-a=00062d45-sp00000000&sp-advanced=1&sp-p=all&sp-w-control=1&sp-w=alike&sp-date-range=-1&sp-x=any&sp-c=100&sp-m=1&sp-s=0"));

		
		// Starting and joining our crawler
		crawlerController.start();
		// Join crawler controller and wait for 60 seconds
		crawlerController.join(60000);
		// Stopping crawler controller
		crawlerController.stop();
		System.out.println("Done");
	}

	/**
	 * Custom crawler. Extends {@link DefaultCrawler}.
	 */
	private static class ExampleCrawler extends DefaultCrawler {

		private boolean seenOnce = false;// hack

		/**
		 * This method is called after each crawl attempt. Warning - it does not
		 * matter if it was unsuccessful attempt or response was redirected. So
		 * you should check response code before handling it.
		 * 
		 * @param crawlerTask
		 * @param page
		 */
		@Override
		protected void afterCrawl(CrawlerTask crawlerTask, Page page) {
			super.afterCrawl(crawlerTask, page);
			try {
				if (seenOnce == false) {
					seenOnce = true;
					Set<URL> urlSet = new HashSet<URL>();
					if (page == null) {
						System.out
								.println(crawlerTask.getUrl()
										+ " violates crawler constraints (content-type or content-length or other)");
					} else if (page.getResponseCode() >= 300
							&& page.getResponseCode() < 400) {
						// If response is redirected - crawler schedules new
						// task with new url
						System.out.println("Response was redirected from "
								+ crawlerTask.getUrl());
					} else if (page.getResponseCode() == HttpURLConnection.HTTP_OK) {
						// Printing url crawled
						System.out.println(crawlerTask.getUrl()
								+ ". Found "
								+ (page.getLinks() != null ? page.getLinks()
										.size() : 0) + " links.");
						// page.
						// URL urlNew= new
						// URL("http://www.snopes.com/inboxer/trivia/billions.asp");
						// parsewithJSoup(urlNew);
						for (URL url : page.getLinks()) {
							urlSet.add(url);// crawler contains all URL's
						}
						if (!urlSet.isEmpty())
							getDatafromURL(urlSet);
						else
							System.out
									.println("The set doesnt contain any URL's!");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		private void getDatafromURL(Set<URL> urlList) {
			try {
				if (urlList == null)
					return;
				Iterator<URL> it = urlList.iterator();
				while (it.hasNext()) {
					URL url = it.next();
					// need to crawl each through URrawler
					// System.out.println(url.toString());
					parsewithJSoup(url);
				}
				System.out.println(urlList.size());

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void parsewithJSoup(URL url) {
			String urlString = url.toString();
			try {

				// Element body=htmlDoc.body();
				String htmlText = Jsoup.connect(urlString).get().html();
				Document htmlDoc = Jsoup.parse(htmlText);
				String removedhtml = htmlDoc.text();
				// System.out.println(removedhtml);
				// defining indexes.
				/*
				 * writeHTML( urlString.substring(22).replace("/", "_")
				 * .replace(".", "_"), removedhtml);
				 */

				int claimIndex = removedhtml.indexOf("Claim: ");
				if (claimIndex == -1)
					claimIndex = removedhtml.indexOf("Legend: ");
				int statusIndex = removedhtml.indexOf("Status: ");
				if (statusIndex == -1)
					statusIndex = removedhtml.indexOf("Mixture: ");
				int exIndex = removedhtml.indexOf("Example: ");
				int orgIndex = removedhtml.indexOf("Origins: ");
				int srcIndex = removedhtml.indexOf("Sources: ");

				urlTagsLog(urlString, claimIndex, statusIndex, exIndex,
						orgIndex, srcIndex);

				if (claimIndex == -1)
					return;
				// String claim =
				// removedhtml.substring(removedhtml.indexOf("Claim: "),removedhtml.indexOf("Status: "));

				// cutting html string.
				String status = "";
				String example = "";
				String source = "";
				String origin = "";

				// +num -- hack to get rid of word
				String claim = removedhtml
						.substring(claimIndex + 6,
								statusIndex != -1 ? statusIndex
										: (exIndex != -1 ? exIndex
												: (orgIndex != -1 ? orgIndex
														: srcIndex)));
				// If the status gets included in claim
				if (claim.toLowerCase().contains("mixture")) {
					claim = claim.substring(0,
							claim.toLowerCase().indexOf("mixture"));
					status = "Mixture";
				}
				else if (claim.toLowerCase().contains("true")) {
					claim = claim.substring(0,
							claim.toLowerCase().indexOf("true"));
					status = "True";
				}
				else if (claim.toLowerCase().contains("false")) {
					claim = claim.substring(0,
							claim.toLowerCase().indexOf("false"));
					status = "False";
				}
				else {
					if (statusIndex != -1)
						status = removedhtml
								.substring(statusIndex + 7,
										exIndex != -1 ? exIndex
												: (orgIndex != -1 ? orgIndex
														: srcIndex));
				}
				if (exIndex != -1)
					example = removedhtml.substring(exIndex + 8,
							orgIndex != -1 ? orgIndex
									: (srcIndex != 1 ? srcIndex : null));
				if (orgIndex != -1)
					origin = removedhtml
							.substring(orgIndex != -1 ? orgIndex + 8 : srcIndex);
				if (srcIndex != -1)
					source = removedhtml.substring(srcIndex);
				// System.out.println(claim+ status+ example+origin+ source);
				// sentence.replace("and", "")
				writeToFile(
						urlString.substring(22).replace("/", "_")
								.replace(".", "_"), claim, status, example,
						origin, source);

			} 
			catch(StringIndexOutOfBoundsException s)
			{
				return;
			}
			catch (Exception e) {
				e.printStackTrace();
				System.out.println("hello " + urlString);
			}
		}

		void urlTagsLog(String url, int claimIndex, int statusIndex,
				int exIndex, int orgIndex, int srcIndex) {
			try {
				String pathToFile = "F:\\DataMining.csv";
				BufferedWriter out = new BufferedWriter(new FileWriter(
						pathToFile, true));
				out.append(url + "," + claimIndex + "," + statusIndex + ","
						+ exIndex + "," + orgIndex + "," + srcIndex + "\n");
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		void writeHTML(String file, String content) {
			try {
				String pathToFile = "F:\\"
						+ file + ".txt";
				BufferedWriter out = new BufferedWriter(new FileWriter(
						pathToFile, true));
				out.append(content);
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		void writeToFile(String file, String claim, String status,
				String example, String origin, String source) {
			try {
				// String pathToFile="F:\\outputData.txt";
				String pathToFile = "F:\\"
						+ file + ".txt";
				BufferedWriter out = new BufferedWriter(new FileWriter(
						pathToFile, true));
				out.append("@@@begin_claim@@@");
				out.append(claim);
				out.append("@@@end_claim@@@");
				out.append("\n");

				out.append("@@@begin_status@@@");
				status = status.toLowerCase().contains("mixture") ? "Mixture"
						: status.trim();
				out.append(status);
				out.append("@@@end_status@@@");
				out.append("\n");

				out.append("@@@begin_example@@@");
				out.append(example.trim());
				out.append("@@@end_example@@@");
				out.append("\n");

				out.append("@@@begin_origins@@@");
				out.append(origin.trim());
				out.append("@@@end_origins@@@");
				out.append("\n");

				out.append("@@@begin_sources@@@");
				out.append(source.trim());
				out.append("@@@end_sources@@@");
				out.append("\n");

				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * You may check if you want to crawl next task
		 * 
		 * @param crawlerTask
		 *            Task that is going to be crawled if you return
		 *            {@code true}
		 * @param parent
		 *            parent.getUrl() page contain link to a
		 *            crawlerTask.getUrl() or redirects to it
		 * @return
		 */
		@Override
		public boolean shouldCrawl(CrawlerTask crawlerTask, CrawlerTask parent) {
			// Default implementation returns true if
			// crawlerTask.getDomainName() == parent.getDomainName()
			return super.shouldCrawl(crawlerTask, parent);
		}
	}
}