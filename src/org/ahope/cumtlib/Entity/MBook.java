package org.ahope.cumtlib.Entity;

public class MBook {
	private String Name;
	private String RebookTimes;
	private String BorrowTime;
	private String BackTime;
	private String Barcode;
	private String Location;

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getRebookTimes() {
		return RebookTimes;
	}

	public void setRebookTimes(String rebookTimes) {
		RebookTimes = rebookTimes;
	}

	public String getBorrowTime() {
		return BorrowTime;
	}

	public void setBorrowTime(String borrowTime) {
		BorrowTime = borrowTime;
	}

	public String getBackTime() {
		return BackTime;
	}

	public void setBackTime(String backTime) {
		BackTime = backTime;
	}

	public String getBarcode() {
		return Barcode;
	}

	public void setBarcode(String barcode) {
		Barcode = barcode;
	}

	public String getLocation() {
		return Location;
	}

	public void setLocation(String location) {
		Location = location;
	}

	@Override
	public String toString() {
		return "MBook [Name=" + Name + ", RebookTimes=" + RebookTimes
				+ ", BorrowTime=" + BorrowTime + ", BackTime=" + BackTime
				+ ", Barcode=" + Barcode + ", Location=" + Location + "]";
	}

}
