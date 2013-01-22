package org.ahope.cumtlib.Utilities;


import java.util.ArrayList;
import java.util.List;

import org.ahope.cumtlib.Entity.Book;
import org.ahope.cumtlib.Entity.BookDetail;
import org.ahope.cumtlib.Entity.MBook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

//将提取的网站数据转出成Book类
public class StringToBook {
	// 书目检索转换
	public static List<Book> stringtobook(String s) {
		List<Book> ResultList = new ArrayList<Book>();
		Document doc = Jsoup.parse(s);
		Elements books = doc.select("div.list_books");
		for (Element book : books) {
			Book info = new Book();
			info.setName(book.getElementsByAttribute("href").text());
			info.setNumber(book.getElementsByTag("h3").last().ownText());
			String f = book.getElementsByTag("p").last().ownText();
			int i = f.lastIndexOf(" ");
			int j = f.length();
			if(i!=-1)
			{
				info.setFactory(f.substring(i + 1, j));
				info.setAuthor(f.substring(0, i));
			}
			else
			{
				info.setFactory(f);
				info.setAuthor("无");
			}
			info.setBidNum(book.getElementsByTag("a").attr("href"));
			String n = book.getElementsByTag("span").last().ownText();
			int k = n.lastIndexOf(" ");
			int q = n.length();
			info.setStoreNum(n.substring(0, k));
			info.setSurplusNum(n.substring(k, q));
			ResultList.add(info);
		}
		return ResultList;
	}

	// 我的图书馆转换
	public static List<MBook> StringtoMybook(String s) {
		List<MBook> ResultList = new ArrayList<MBook>();
		Document doc = Jsoup.parse(s);
		Elements mbooks = doc.getElementsByTag("tr");
		for (Element book : mbooks) {
			MBook info = new MBook();
			info.setName(book.getElementsByAttribute("href").text());
			info.setBackTime(book.getElementsByTag("font").text());
			String a = book.getElementsByAttributeValue("width", "11%").text();
			if (a.length() == 0)
				info.setBorrowTime(a);
			else
				info.setBorrowTime(a.substring(0, 10));
			info.setRebookTimes(book.getElementsByAttributeValue("width", "8%")
					.text());
			info.setBarcode(book.getElementsByAttributeValue("width", "10%").text());
			ResultList.add(info);
		}
		return ResultList;
	}

	//
	public static List<BookDetail> StringtoDetail(String s) {
		List<BookDetail> ResultList = new ArrayList<BookDetail>();
		Document doc = Jsoup.parse(s);
		Elements booksdet = doc.getElementsByTag("tr");
		if (booksdet != null) {
			for (Element book : booksdet) {
				BookDetail info = new BookDetail();
				String a = book.getElementsByAttributeValue("width", "25%").text();
				int j = a.length();
				if(j==0)
				{
					info.setLocation(a);
					info.setState(a);
				}
				else
				{
					int i = a.lastIndexOf(" ");
					info.setLocation(a.substring(0,i));
					info.setState(a.substring(i, j));
				}
				ResultList.add(info);
			}
		}
		return ResultList;
	}
}
