package org.ahope.cumtlib.Entity;

public class Book {
	private String Name;
	private String Number;
	private String Author;
	private String Factory;
	private String Location;
	private String Series;
	private String StoreNum;
	private String SurplusNum;
	private String BidNum;

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getNumber() {
		return Number;
	}

	public void setNumber(String number) {
		Number = number;
	}

	public String getAuthor() {
		return Author;
	}

	public void setAuthor(String author) {
		Author = author;
	}

	public String getFactory() {
		return Factory;
	}

	public void setFactory(String factory) {
		Factory = factory;
	}

	public String getLocation() {
		return Location;
	}

	public void setLocation(String location) {
		Location = location;
	}

	public String getSeries() {
		return Series;
	}

	public void setSeries(String series) {
		Series = series;
	}

	public String getStoreNum() {
		return StoreNum;
	}

	public void setStoreNum(String storeNum) {
		StoreNum = storeNum;
	}

	public String getSurplusNum() {
		return SurplusNum;
	}

	public void setSurplusNum(String surplusNum) {
		SurplusNum = surplusNum;
	}

	public String getBidNum() {
		return BidNum;
	}

	public void setBidNum(String bidNum) {
		BidNum = bidNum;
	}

	@Override
	public String toString() {
		return "Book [Name=" + Name + ", Number=" + Number + ", Author="
				+ Author + ", Factory=" + Factory + ", Location=" + Location
				+ ", Series=" + Series + ", StoreNum=" + StoreNum
				+ ", SurplusNum=" + SurplusNum + ", BidNum=" + BidNum + "]";
	}

}
