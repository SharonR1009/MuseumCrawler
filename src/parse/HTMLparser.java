package parse;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Document;

public class HTMLparser {
	public static void main(String args[]) throws IOException{
		 String totalXml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><artworks>";
		 Document doc = Jsoup.connect("https://www.nationalgalleries.org/collection/subjects/").get();
		 Element list = doc.getElementById("content");
		 Elements links = list.getElementsByTag("a");
		 /*for(Element link:links)
		 {
			 String typeUrl = link.attr("href");
			 totalXml =  totalXml + parseLists(typeUrl);			 
			 //System.out.println(typeUrl);			 
		 }*/
		 totalXml =  totalXml + parseLists("https://www.nationalgalleries.org/collection/subjects/abstract");
		 totalXml =  totalXml + parseLists("https://www.nationalgalleries.org/collection/subjects/collage");
		 totalXml =  totalXml + parseLists("https://www.nationalgalleries.org/collection/subjects/colourists");
		 totalXml =  totalXml + parseLists("https://www.nationalgalleries.org/collection/subjects/cubism");
		 totalXml =  totalXml + parseLists("https://www.nationalgalleries.org/collection/subjects/dada");
		 totalXml =  totalXml + parseLists("https://www.nationalgalleries.org/collection/subjects/documentary");
		 totalXml =  totalXml + parseLists("https://www.nationalgalleries.org/collection/subjects/glasgow-school-glasgow-boys");
		 totalXml =  totalXml + parseLists("https://www.nationalgalleries.org/collection/subjects/history");
		 //
		 totalXml = totalXml + "</artworks>";
		 totalXml = totalXml.trim().replaceAll("&", "&amp;");
		 //totalXml = totalXml.trim().replaceAll("<", "&lt;");
		 //totalXml = totalXml.trim().replaceAll(">", "&gt;");
		 //totalXml = totalXml.trim().replaceAll("'", "&apos;");
		 //totalXml = totalXml.trim().replaceAll("\\\"", "&quot;");
		 writeFile(totalXml);
	}
	//example:https://www.nationalgalleries.org/collection/subjects/abstract
	public static String parseLists(String typeUrl) throws IOException{
		int count = 0;
		String linkXml = "";
		Document doc = Jsoup.connect(typeUrl).get();
		//colloct all artworks in current page
		parseItem(typeUrl);
		//collect all pages' url
		Element list = doc.getElementById("work-text");
		Element page = list.getElementsByClass("paging").first();
		Elements links = page.getElementsByTag("a");
		String numb = page.getElementsByTag("strong").last().text();
		int num = Integer.parseInt(numb) - 1;
		//System.out.println(num);
		for(Element link:links)
		{
			String pageUrl = link.attr("href");
			count++;
			if(count > num)
				break;
			//System.out.println(pageUrl);
			linkXml = linkXml + parseItem(pageUrl);
		}
		return linkXml;
	}
	public static String parseItem(String url) throws IOException{
		Document doc = Jsoup.connect(url).get();
		//colloct all artworks in current page
		Element row0 = doc.getElementById("row_0");
		Elements row0items = row0.getElementsByClass("ciDetails");
		String curXml = "";
		
		for(Element row0item:row0items)
		{
			String itemUrl = row0item.getElementsByTag("a").first().attr("href");
			//System.out.println(itemUrl);
			String xml = extract(itemUrl);
			curXml = curXml + xml;
			System.out.println(xml);
		}		
		Element row1 = doc.getElementById("row_1");		
		Elements row1items = row1.getElementsByClass("ciDetails");
		for(Element row1item:row1items)
		{
			String itemUrl = row1item.getElementsByTag("a").first().attr("href");
			//System.out.println(itemUrl);
			String xml = extract(itemUrl);
			curXml = curXml + xml;
			System.out.println(xml);
		}
		return curXml;
	}
	//example:https://www.nationalgalleries.org/collection/subjects/abstract/artist/martin-boyce/object/untitled-electric-trees-gma-5015
	public static String extract(String itemUrl) throws IOException{
		Document doc = Jsoup.connect(itemUrl).get();
		//String xmlString = "<?xml version=\"1.0\" encoding=\"utf-8\"?><artwork>";
		String xmlString = "<artwork>";
		Element workMedia = doc.getElementById("work-media");
		String img = workMedia.getElementsByTag("a").first().attr("href");
		Element workTitle = doc.getElementById("work-side");
		String title = workTitle.getElementsByClass("workTitle").first().text();
		String date = workTitle.getElementsByClass("date").first().text();
		xmlString = xmlString + "<image>" + img + "</image>";
		xmlString = xmlString + "<title>" + title + "</title>";
		xmlString = xmlString + "<date>" + date + "</date>";
		
		Element workText = doc.getElementById("work-text");
		String caption = workText.getElementsByClass("woaBodytext").first().text();
		xmlString = xmlString + "<caption>" + caption + "</caption>";
		
		/*Element workGlossary = doc.getElementById("work-glossary");
		String glossary = workGlossary.getElementsByClass("show").first().text();
		xmlString = xmlString + "<glossary>" + glossary + "</glossary>";*/
		
		Element workDetails = doc.getElementById("work-details");
		Elements details = workDetails.getElementsByTag("span");
		String workDetail = "";
		for(Element detail:details)
		{
			String data = detail.text();
			workDetail = workDetail + " " + data;
		}
		xmlString = xmlString + "<details>" + workDetail + "</details>";
		
		Element workBio = doc.getElementById("work-bio");
		if(workBio == null)
		{
			String author = "unknown";
			xmlString = xmlString + "<author>" + author + "</author>";
		}	
		else
		{
			String author = workBio.getElementsByTag("h3").text();
			String authorBio = workBio.getElementsByTag("p").text();
			xmlString = xmlString + "<author>" + author + "</author>";
			xmlString = xmlString + "<authorBio>" + authorBio + "</authorBio>";
		}
		
		xmlString = xmlString + "</artwork>";
		return xmlString;
	}
	public static void writeFile(String xml) throws IOException{
		File file = new File("/users/sharon/desktop/museum/museum.xml");
		// if file doesnt exists, then create it
		if (!file.exists()) {
		   file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(xml);
		bw.close();
	}
}
