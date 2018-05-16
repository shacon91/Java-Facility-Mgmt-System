/**
 * CS4222/CS5052: Project 2
 * Group 46L
 * Author: Shane Conway
 * Student Id: 17170451
 * Due Date: 25/04/2018 13:00
 * */

import java.io.*;
import java.time.*;
import java.util.*;

public class Booking implements Comparable<Booking>{
	private int bookingId;
	private int facilityId;
	private int userId;
	private LocalDate bookingDate;
	private int slot;
	private String payment;
	
	
	/**
	 * Booking(args) is a Constructor method for the Booking class. It takes 
	 * six arguments. This method is instantiated and a new booking object is 
	 * created. 
	 * */
	public Booking(int bookingId,int facilityId,int userId,LocalDate bookingDate,int slot, String payment) {
		this.bookingId = bookingId;
		this.facilityId = facilityId;
		this.userId = userId;
		this.bookingDate = bookingDate;
		this.slot = slot;
		this.payment = payment;
	}
	
	/**
	 * getBookingId() returns the Booking Id
	 * */
	public int getBookingId() {
		return bookingId;
	}
	
	/**
	 * getFacilityId() returns the Bookings Facility Id
	 * */
	public int getFacilityId() {
		return facilityId;
	}
	
	/**
	 * getUserId() returns the Bookings User Id
	 * */
	public int getUserId() {
		return userId;
	}
	
	/**
	 * bookingDate() returns the Booking date
	 * */
	public LocalDate getBookingDate() {
		return bookingDate;
	}
	
	/**
	 * getSlot() returns the Booking slot
	 * */
	public int getSlot() {
		return slot;
	}
	
	/**
	 * getPayment() returns the payment status of the booking
	 * */
	public String getPayment() {
		return payment;
	}
	
	/**
	 * availability(int facilityId, LocalDate dateReq, int slot) method checks whether a slot 
	 * on a date at a Facility is available. It uses the following methods bookingFileExists()
	 * */
	public static boolean availability(int facilityId, LocalDate dateReq, int slot) {
		if (bookingFileExists()) {
			try {
				FileReader readBookingFile = new FileReader("Bookings.txt");
				Scanner in = new Scanner(readBookingFile);
				String[] fileItem;
				boolean availability = true;
				
				while(in.hasNext()) {
					
					fileItem = in.nextLine().split(",");
					
					int facilityIdItem = Integer.parseInt(fileItem[1]);
					LocalDate dateItem = LocalDate.parse(fileItem[3]);
					int slotItem = Integer.parseInt(fileItem[4]);
					
					if(facilityIdItem==facilityId && dateItem.equals(dateReq) && slotItem==slot) {
						availability = false;
						break;
					}
				}
				
				in.close();
				readBookingFile.close();
				
				if(availability == true) {
					return true;
				}else {
					return false;
				}
				
			}catch(IOException e){
				return false;
			}
		}else {
			 return true;
		}
	}

	/**
	 * makeBooking() is a method used on the object (the facility) to carry out the booking
	 * It uses the getId(), getNewId() and writeToBookingsFile() method
	 * */
	public static boolean makeBooking(int facilityId, String email, LocalDate dateReq, int slot, boolean paymentStatus) {
		int userId = User.getId(email);
		int BookingId = 1;
		if(getNewId()==-1) {
			return false;
		}else {
			BookingId = getNewId();
		}
		String payment = paymentStatus==true? "Y" :"N";
		String lineToWrite = BookingId+","+facilityId+","+userId+","+dateReq+","+slot+","+payment;
		if(writeToBookingsFile(lineToWrite)) {
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * checkPaymentStatus() checks whether a booking has being paid for
	 * */
	public static boolean checkPaymentStatus(int facilityId, LocalDate dateReq, int slot) {
		if (bookingFileExists()) {
			try {
				FileReader readBookingFile = new FileReader("Bookings.txt");
				Scanner in = new Scanner(readBookingFile);
				String[] fileItem;
				boolean payment = false;
				
				while(in.hasNext()) {
					
					fileItem = in.nextLine().split(",");
					
					int facilityIdItem = Integer.parseInt(fileItem[1]);
					LocalDate dateItem = LocalDate.parse(fileItem[3]);
					int slotItem = Integer.parseInt(fileItem[4]);
					
					if(facilityIdItem==facilityId && dateItem.equals(dateReq) && slotItem==slot && fileItem[5].equals("Y") ) {
						payment = true;
						break;
					}
				}
				
				in.close();
				readBookingFile.close();
				
				if(payment == true) {
					return true;
				}else {
					return false;
				}
				
			}catch(IOException e){
				return false;
			}
		}else {
			 return false;
		}
	}
	
	/**
	 * makePayment() method is used to make a payment for a booking for the object (the facility). 
	 * */
	public static boolean makePayment(int facilityId, LocalDate dateReq, int slot) {
		if (bookingFileExists()) {
			try {
				FileReader readBookingFile = new FileReader("Bookings.txt");
				Scanner in = new Scanner(readBookingFile);
				String[] fileItem;
				
				String aLineFromFile;
				ArrayList<String> newFile = new ArrayList<String>();
				
				while(in.hasNext()) {
					aLineFromFile = in.nextLine();
					fileItem = aLineFromFile.split(",");
					
					int facilityIdItem = Integer.parseInt(fileItem[1]);
					LocalDate dateItem = LocalDate.parse(fileItem[3]);
					int slotItem = Integer.parseInt(fileItem[4]);
					
					if(facilityIdItem==facilityId && dateItem.equals(dateReq) && slotItem==slot) {
							fileItem[5] = "Y";
							aLineFromFile = fileItem[0]+","+fileItem[1]+","+fileItem[2]+","+fileItem[3]+","+fileItem[4]+","+fileItem[5];
					}
					
					newFile.add(aLineFromFile);
				}
				
				in.close();
				readBookingFile.close();
				
				if(rewriteBookingsFile(newFile)) {
					return true;
				}else {
					return false;
				}	
			}catch(IOException e){
				return false;
			}
		}else {
			 return false;
		}
	}
	
	/**
	 * userBookings(int userId) returns all the bookings for the 
	 * user from now.
	 * */
	public static ArrayList<Booking> userBookings(int userId) {
		ArrayList<Booking> bookingsList = new ArrayList<Booking>();
		
		if (bookingFileExists()) {
			try {
				FileReader readBookingFile = new FileReader("Bookings.txt");
				Scanner in = new Scanner(readBookingFile);
				String[] fileItem;
				
				while(in.hasNext()) {
					fileItem = in.nextLine().split(",");
					
					int userIdItem = Integer.parseInt(fileItem[2]);
					LocalDate dateItem = LocalDate.parse(fileItem[3]);
					
					if(userIdItem==userId && dateItem.isAfter(LocalDate.now())) {
						int bookingIdItem = Integer.parseInt(fileItem[0]);
						int facilityIdItem = Integer.parseInt(fileItem[1]);
						int slotItem = Integer.parseInt(fileItem[4]);
						String payItem = fileItem[5];
						bookingsList.add(new Booking(bookingIdItem,facilityIdItem,userIdItem,dateItem,slotItem,payItem));
					}
				}
				
				in.close();
				readBookingFile.close();
				
				Collections.sort(bookingsList);
				return bookingsList;
				
			}catch(IOException e){
				e.printStackTrace();
				return bookingsList;
			}
		}else {
			 return bookingsList;
		}
	}
	
	/**
	 * userAccount(int userId) returns all the bookings for the user that still need to 
	 * be paid for
	 * */
	public static ArrayList<Booking> userAccount(int userId) {
		ArrayList<Booking> accountList = new ArrayList<Booking>();
		if (bookingFileExists()) {
			try {
				FileReader readBookingFile = new FileReader("Bookings.txt");
				Scanner in = new Scanner(readBookingFile);
				String[] fileItem;
				
				while(in.hasNext()) {
					fileItem = in.nextLine().split(",");
					
					int userIdItem = Integer.parseInt(fileItem[2]);
					
					if(userIdItem==userId && fileItem[5].equals("N")) {
						int bookingIdItem = Integer.parseInt(fileItem[0]);
						int facilityIdItem = Integer.parseInt(fileItem[1]);
						LocalDate dateItem = LocalDate.parse(fileItem[3]);
						int slotItem = Integer.parseInt(fileItem[4]);
						String payItem = fileItem[5];
						accountList.add(new Booking(bookingIdItem,facilityIdItem,userIdItem,dateItem,slotItem,payItem));
					}
				}
				
				in.close();
				readBookingFile.close();
				
				Collections.sort(accountList);
				return accountList;
				
			}catch(IOException e){
				e.printStackTrace();
				return accountList;
			}
		}else {
			 return accountList;
		}
	}
	
	/**
	 * bookings(int facilityId) returns all the bookings for the 
	 * facility from now.
	 * */
	public static ArrayList<Booking> bookings(int facilityId) {
		
		ArrayList<Booking> bookingsList = new ArrayList<Booking>();
		
		if (bookingFileExists()) {
			try {
				FileReader readBookingFile = new FileReader("Bookings.txt");
				Scanner in = new Scanner(readBookingFile);
				String[] fileItem;
				
				while(in.hasNext()) {
					fileItem = in.nextLine().split(",");
					
					int facilityIdItem = Integer.parseInt(fileItem[1]);
					LocalDate dateItem = LocalDate.parse(fileItem[3]);
					
					if(facilityIdItem==facilityId && dateItem.isAfter(LocalDate.now())) {
						int bookingIdItem = Integer.parseInt(fileItem[0]);
						int userIdItem = Integer.parseInt(fileItem[2]);
						int slotItem = Integer.parseInt(fileItem[4]);
						String payItem = fileItem[5];
						bookingsList.add(new Booking(bookingIdItem,facilityIdItem,userIdItem,dateItem,slotItem,payItem));
					}
				}
				
				in.close();
				readBookingFile.close();
				
				Collections.sort(bookingsList);
				return bookingsList;
				
			}catch(IOException e){
				e.printStackTrace();
				return bookingsList;
			}
		}else {
			 return bookingsList;
		}
	}
	
	/**
	 * bookings(int facilityId,LocalDate dateReq) returns all the bookings for the 
	 * facility for a particular date
	 * */
	public static ArrayList<Booking> bookings(int facilityId,LocalDate dateReq) {
		
		ArrayList<Booking> bookingsList = new ArrayList<Booking>();
		
		if (bookingFileExists()) {
			try {
				FileReader readBookingFile = new FileReader("Bookings.txt");
				Scanner in = new Scanner(readBookingFile);
				String[] fileItem;
				
				while(in.hasNext()) {
					fileItem = in.nextLine().split(",");
					
					int facilityIdItem = Integer.parseInt(fileItem[1]);
					LocalDate dateItem = LocalDate.parse(fileItem[3]);
					
					if(facilityIdItem==facilityId && dateItem.equals(dateReq)) {
						int bookingIdItem = Integer.parseInt(fileItem[0]);
						int userIdItem = Integer.parseInt(fileItem[2]);
						int slotItem = Integer.parseInt(fileItem[4]);
						String payItem = fileItem[5];
						bookingsList.add(new Booking(bookingIdItem,facilityIdItem,userIdItem,dateItem,slotItem,payItem));
					}
				}
				
				in.close();
				readBookingFile.close();
				
				Collections.sort(bookingsList);
				return bookingsList;
				
			}catch(IOException e){
				e.printStackTrace();
				return bookingsList;
			}
		}else {
			 return bookingsList;
		}
	}
	
	/**
	 * bookings(int facilityId,LocalDate dateFrom,LocalDate dateTo) returns all the bookings for the 
	 * facility from the period stated in the arguments
	 * */
	public static ArrayList<Booking> bookings(int facilityId,LocalDate dateFrom,LocalDate dateTo) {
		ArrayList<Booking> bookingsList = new ArrayList<Booking>();
		
		if (bookingFileExists()) {
			try {
				FileReader readBookingFile = new FileReader("Bookings.txt");
				Scanner in = new Scanner(readBookingFile);
				String[] fileItem;
				
				while(in.hasNext()) {
					fileItem = in.nextLine().split(",");
					
					int facilityIdItem = Integer.parseInt(fileItem[1]);
					LocalDate dateItem = LocalDate.parse(fileItem[3]);
					
					if(facilityIdItem==facilityId && (dateItem.isBefore(dateFrom)==false) && (dateItem.isAfter(dateTo)==false)){
						int bookingIdItem = Integer.parseInt(fileItem[0]);
						int userIdItem = Integer.parseInt(fileItem[2]);
						int slotItem = Integer.parseInt(fileItem[4]);
						String payItem = fileItem[5];
						bookingsList.add(new Booking(bookingIdItem,facilityIdItem,userIdItem,dateItem,slotItem,payItem));
					}
				}
				
				in.close();
				readBookingFile.close();
				
				Collections.sort(bookingsList);
				return bookingsList;
				
			}catch(IOException e){
				return bookingsList;
			}
		}else {
			 return bookingsList;
		}
	}
	
	/**
	 * getNewId() static method that returns the next available id. This method uses the 
	 * bookingFileExists() method to check if the Bookings.txt file exists
	 * */
	public static int getNewId() {
		if (bookingFileExists()) {
			try {
				FileReader readBookingFile = new FileReader("Bookings.txt");
				Scanner in = new Scanner(readBookingFile);
				String lastFileLine="0,0";
				String[] fileItem;
				
				while(in.hasNext()) {
					lastFileLine = in.nextLine();
				}
				
				in.close();
				readBookingFile.close();
				
				fileItem=lastFileLine.split(",");
				int lastId = Integer.parseInt(fileItem[0]);
				
				return lastId+1;
				
			}catch(IOException e){
				return -1;
			}
		}else {
			 return 1;
		}
	}
	
	/**
	 *  writeToBookingsFile(String lineToWrite) method writes a line of String to the Bookings.txt file
	 *  It uses bookingFileExists() method.
	 * */
	public static boolean writeToBookingsFile(String lineToWrite) {
		try {
			FileWriter readBookingFile;
			
			if (bookingFileExists()) {
				readBookingFile = new FileWriter("Bookings.txt",true);
			}else {
				readBookingFile = new FileWriter("Bookings.txt",false);
			}
	
			PrintWriter out = new PrintWriter(readBookingFile);
			out.println(lineToWrite);
			out.close();
			readBookingFile.close();
			return true;
		}catch(IOException e){
			return false;	
		}
	}
	
	/**
	 * rewriteBookingsFile(newFile) method re-writes the Bookings.txt file
	 * */
	public static boolean rewriteBookingsFile(ArrayList<String> newFile) {
		try {
			FileWriter readBookingFile = new FileWriter("Bookings.txt",false);
			PrintWriter out = new PrintWriter(readBookingFile);
			for (int i = 0; i < newFile.size(); i++) {
				out.println(newFile.get(i));
			}
			
			out.close();
			readBookingFile.close();
			return true;
		}catch(IOException e){
			return false;	
		}
	}
	
	/**
	 * bookingFileExists() method checks if the Bookings.txt file exists
	 * */
	public static boolean bookingFileExists() {
		String fileName = "Bookings.txt";
		File bookingFile = new File(fileName);
		
		if (bookingFile.exists()) {
			return true;
		}else {
			return false;
		}
		
	}

	
	/**
	 * compareTo(Booking o) is used in the Collections.sort objects to sort multiple
	 * fields
	 * */
	@Override
	public int compareTo(Booking o) {
		//return bookingDate.compareTo(o.bookingDate);
		int compare = bookingDate.compareTo(o.bookingDate);
		if (compare==0) {
			compare = Integer.compare(slot, o.slot);
		}
		return compare;
	}
	
}
