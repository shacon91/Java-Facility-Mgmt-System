/**
 * CS4222/CS5052: Project 2
 * Group 46L
 * Author: Shane Conway
 * Student Id: 17170451
 * Due Date: 25/04/2018 13:00
 * */

import java.io.*;
import java.util.*;
import java.time.*;

public class Facility {
	private int id;
	private String name = "";
	private double price = 0.0;
	private boolean decommissioned = false;
	private LocalDate decommisionedUntil;
	
	/**
	 * Facility() is an empty Constructor method needed by the addFacility() method
	 * */
	public Facility() {}
	
	/**
	 * Facility(String name) is a Constructor method for the Facility class. It takes 
	 * one argument in that of the facility's name. This method is instantiated and a 
	 * new facility object is created. it uses getFacilityInfo(name) method to get information
	 * from facility File
	 * */
	public Facility(String name) {
		String[] info = getFacilityInfo(name).split(",");
		if(info.length == 3) {
			this.id = Integer.parseInt(info[0]);
			this.name = info[1];
			this.setPrice(Double.parseDouble(info[2]));
		}else {
			this.id = Integer.parseInt(info[0]);
			this.name = info[1];
			this.setPrice(Double.parseDouble(info[2]));
			this.decommissioned = true;
			this.decommisionedUntil = LocalDate.parse(info[3]);
		}
	}
	
	/**
	 * isDecommissioned() is a method that returns whether a facility is decommissioned
	 * */
	public boolean isDecommissioned() {
		return decommissioned;
	}
	
	/**
	 * decommissionedUntil() is a method that returns when the facility is decommissioned
	 * until
	 * */
	public LocalDate decommissionedUntil() {
		return decommisionedUntil;
	}
	
	/**
	 * addFacility( String name, String price) is a method that adds a new facility. It uses
	 * checkName(name) and writeToFacilityFile(lineToWrite) methods
	 * */
	public int addFacility( String name, String price) {
		if(checkName(name) == true) {
			return -1;
		}else {
			this.id = getId(name);
			this.name = name;
			this.price = Double.parseDouble(price);
			
			String lineToWrite = id+","+name+","+price;
			if(writeToFacilityFile(lineToWrite)) {
				return 1;
			}else {
				return -2;
			}	
		}
	}
	
	/**
	 * makeBooking() method verifies booking and uses availability() & makeBooking() methods
	 * from the Booking Class
	 * */
	public String makeBooking(String email, LocalDate dateReq, int slot, boolean paymentStatus) {
		if(LocalDate.now().isBefore(dateReq)==false) {
			return "You can only book a slot for tomorrow or after";
		}else if(User.getId(email)==-1) {
			return "There is no user with that email";
		}else if(Booking.availability(id, dateReq, slot)==false) {
			return "That slot is currently unavailable";
		}else {
			if(Booking.makeBooking( id, email, dateReq, slot, paymentStatus)) {
				return "true";
			}else {
				return "Could not make Booking";
			}
		}
	}
	
	/**
	 * bookingExists() method  uses availability() method from the Booking Class and checks whether 
	 * the booking is there or not
	 * */
	public boolean bookingExists(LocalDate dateReq, int slot) {
		if(Booking.availability( id, dateReq, slot)==false) {
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * recordPayment() method  uses checkPaymentStatus() and makePayment() methods from the Booking 
	 * Class and checks whether the payment was made
	 * */
	public String recordPayment(LocalDate dateReq, int slot) {
		if(bookingExists(dateReq, slot) ) {
			if(Booking.checkPaymentStatus(id,dateReq, slot)==false ) {
				if(Booking.makePayment( id, dateReq, slot)) {
					return "true";
				}else {
					return "Payment could not be made!";
				}
			}else {
				return "This booking has already being payed for";
			}
		}else {
			return "This booking does not exist";
		}
		
	}

	/**
	 * viewBookings(LocalDate dateReq) method  uses bookings(id,dateReq) method from the Booking 
	 * Class and checks whether there is bookings on that date. It uses the getName() static method 
	 * from the User Class 
	 * */
	public String viewBookings(LocalDate dateReq) {
		String bookings ="";
		ArrayList<Booking> bookingList = Booking.bookings(id,dateReq);
		if (bookingList.size()==0) {
			bookings = "The Facility "+name+" has no bookings for "+dateReq;
		}else {
			bookings += "The Facility "+name+" has the following bookings for "+dateReq+":\n";
			for (int i = 0; i < bookingList.size(); i++) {
					String status = bookingList.get(i).getPayment().equals("Y") ? "Confirmed" : "Not Paid"; 
					String userName = User.getName(bookingList.get(i).getUserId());
					bookings += "Slot "+bookingList.get(i).getSlot()+": Booked by user "+userName+", payment:"+status+", booking id:"+bookingList.get(i).getBookingId()+"\n";
			}
		}
		return bookings;
	}
	
	/**
	 * viewBookings(LocalDate dateFrom,LocalDate dateTo) method  uses bookings(id,dateFrom,dateTo) method from the Booking 
	 * Class and checks whether there is bookings over that period. It uses the getName() static method from the User Class 
	 * */
	public String viewBookings(LocalDate dateFrom,LocalDate dateTo) {
		if(dateFrom.isBefore(dateTo) || dateFrom.equals(dateTo)) {
			String bookings ="";
			ArrayList<Booking> bookingList = Booking.bookings(id,dateFrom,dateTo);
			if (bookingList.size()==0) {
				bookings = "The Facility "+name+" has no bookings from "+dateFrom+" to "+dateTo+".";
			}else {
				bookings += "The Facility "+name+" has the following bookings from "+dateFrom+" to "+dateTo+".\n";
				for (int i = 0; i < bookingList.size(); i++) {
					String status = bookingList.get(i).getPayment().equals("Y") ? "Confirmed" : "Not Paid"; 
					String userName = User.getName(bookingList.get(i).getUserId());
					bookings += "Date: "+bookingList.get(i).getBookingDate()+" for slot "+bookingList.get(i).getSlot()+": Booked by user "+userName+", payment:"+status+", booking id:"+bookingList.get(i).getBookingId()+"\n";			
				}	
			}
			return bookings;
		}else {
			return "Invalid Dates";
		}
	}
	
	/**
	 * viewAvailability(LocalDate dateReq) method uses bookings(id,dateReq) method from the Booking 
	 * Class and checks what slots are available on that date.
	 * */
	public String viewAvailability(LocalDate dateReq) {
		String availability ="";
		ArrayList<Booking> bookingList = Booking.bookings(id,dateReq);
		
		if (bookingList.size()==9) {
			availability = "The Facility "+name+" has no availability on the "+dateReq;
		}else {
			ArrayList<String> slotList = new ArrayList<String>();
			for (int i = 1; i <= 9; i++) {
				slotList.add(""+i+"");
			}
			
			for (int j = 0; j <bookingList.size(); j++) {
				slotList.remove(""+bookingList.get(j).getSlot()+"");
			}
		
			availability += "The Facility "+name+" has availability on the "+dateReq+" at:\n";
			for (int i = 0; i < slotList.size(); i++) {
				availability += "Slot "+slotList.get(i)+"\n";
			}
		}
		return availability;
	}

	/**
	 * viewAvailability(LocalDate dateFrom,LocalDate dateTo) method uses bookings(id,dateFrom,dateTo) method from the Booking 
	 * Class and checks what slots are available over that period.
	 * */
	public String viewAvailability(LocalDate dateFrom,LocalDate dateTo) {
		if(dateFrom.isBefore(dateTo)) {
			String availability ="";
			ArrayList<Booking> bookingList = Booking.bookings(id,dateFrom,dateTo);
			
			if (bookingList.size()==27) {
				availability = "The Facility "+name+" has no availability from "+dateFrom+" to "+dateTo+".";
			}else {
				
				ArrayList<ArrayList<String>> availList = new ArrayList<ArrayList<String>>();
				availList.add(new ArrayList<String>()); //day1
				availList.add(new ArrayList<String>()); //day2
				availList.add(new ArrayList<String>()); //day3
				
				for (int i = 0; i < 3; i++) {
					for (int k = 1; k <= 9; k++) {
						availList.get(i).add(""+k+"");
					}
				}
				
				for (int j = 0; j <bookingList.size(); j++) {
					if (bookingList.get(j).getBookingDate().equals(dateFrom)) {
						availList.get(0).remove(""+bookingList.get(j).getSlot()+"");
					}else if (bookingList.get(j).getBookingDate().equals(dateFrom.plusDays(1))) {
						availList.get(1).remove(""+bookingList.get(j).getSlot()+"");
					}else if (bookingList.get(j).getBookingDate().equals(dateTo)) {
						availList.get(2).remove(""+bookingList.get(j).getSlot()+"");
					}
				}
				
				
				
				
				availability += "The Facility "+name+" has the following availability from "+dateFrom+" to "+dateTo+":\n\n";
				for (int x = 0; x < 3; x++) {
					LocalDate newDate = dateFrom.plusDays(x);
					if(availList.get(x).size()==0) {
						availability +="Date: "+newDate+ " has no slots available\n";
					}else {
						availability +="\n";
						availability +="Date: "+newDate+ " has the following slots available:\n";
						for (int y = 0; y < availList.get(x).size(); y++) {
							availability +="Slot: "+availList.get(x).get(y)+"\n";
						}
					}
					
				}
			}
			return availability;
		}else {
			return "Invalid Dates";
		}
	}
	
	/**
	 * decomissionFacility(LocalDate dateReq) method uses bookings(id) method from the Booking 
	 * Class and checks whether there are no bookings for that facility. If no bookings it 
	 * uses rewriteFacilityFile(newFile) and enters end date of decommissioning into Facility file
	 * */
	public String decomissionFacility(LocalDate dateReq) {
		if(dateReq.isAfter(LocalDate.now())) {
			if (facilityFileExists()) {
				ArrayList<Booking> bookingList = Booking.bookings(id);
				if (bookingList.size()==0) {
					try {
						FileReader readFacilityFile = new FileReader("Facilities.txt");
						Scanner in = new Scanner(readFacilityFile);
						String[] fileItem;
						
						String aLineFromFile;
						ArrayList<String> newFile = new ArrayList<String>();
						
						while(in.hasNext()) {
							aLineFromFile = in.nextLine();
							fileItem = aLineFromFile.split(",");
							
							int facilityIdItem = Integer.parseInt(fileItem[0]);
							
							if(facilityIdItem==id) {
									aLineFromFile = fileItem[0]+","+fileItem[1]+","+fileItem[2]+","+dateReq;
							}
							
							newFile.add(aLineFromFile);
						}
						
						in.close();
						readFacilityFile.close();
						
						if(rewriteFacilityFile(newFile)) {
							return "The Facility "+name+" has been decomissioned until "+dateReq;
						}else {
							return "The Facility "+name+" could not be decomissioned.";
						}	
					}catch(IOException e){
						return "IOException error";
					}
				}else {
					String decomAnswer ="";
					decomAnswer += "The Facility "+name+" cannot be decommissioned becuase the following Bookings have been made:\n";
					for (int i = 0; i < bookingList.size(); i++) {
						decomAnswer += "Date "+bookingList.get(i).getBookingDate()+" for slot "+bookingList.get(i).getSlot()+" booked by user id "+bookingList.get(i).getUserId()+" with booking id "+bookingList.get(i).getBookingId()+"\n";
					}
					return decomAnswer;
				}
			}else {
				return "File Does not exist";
			}
		}else {
			return "Decommissioning Date must be after today";
		}
	}
	
	/**
	 * recomissionFacility() method uses bookings(id) uses rewriteFacilityFile(newFile) 
	 * and removes the decommissioning date from Facility file
	 * */
	public String recomissionFacility() {
		if (facilityFileExists()) {
			try {
				FileReader readFacilityFile = new FileReader("Facilities.txt");
				Scanner in = new Scanner(readFacilityFile);
				String[] fileItem;
				
				String aLineFromFile;
				ArrayList<String> newFile = new ArrayList<String>();
				
				while(in.hasNext()) {
					aLineFromFile = in.nextLine();
					fileItem = aLineFromFile.split(",");
					
					int facilityIdItem = Integer.parseInt(fileItem[0]);
					
					if(facilityIdItem==id) {
							aLineFromFile = fileItem[0]+","+fileItem[1]+","+fileItem[2];
					}
					
					newFile.add(aLineFromFile);
				}
				
				in.close();
				readFacilityFile.close();
				
				if(rewriteFacilityFile(newFile)) {
					return "The Facility "+name+" has been re-comissioned.";
				}else {
					return "The Facility "+name+" could not be re-comissioned.";
				}	
			}catch(IOException e){
				return "IOException error";
			}
		}else {
			return "File Does not exist";
		}
	}
	
	/**
	 * removeFacility() method uses bookings(id) method from the Booking 
	 * Class and checks whether there are no bookings for that facility. If no bookings it 
	 * uses rewriteFacilityFile(newFile) and removes facility info from the Facility file
	 * */
	public String removeFacility() {
		String removeAnswer ="";
		ArrayList<Booking> bookingList = Booking.bookings(id);
		if (bookingList.size()==0) {
			try {
				FileReader readFacilityFile = new FileReader("Facilities.txt");
				Scanner in = new Scanner(readFacilityFile);
				String[] fileItem;
				
				String aLineFromFile;
				ArrayList<String> newFile = new ArrayList<String>();
				
				while(in.hasNext()) {
					aLineFromFile = in.nextLine();
					fileItem = aLineFromFile.split(",");
					
					int facilityIdItem = Integer.parseInt(fileItem[0]);
					
					if(facilityIdItem!=id) {
						newFile.add(aLineFromFile);	
					}
				}
				
				in.close();
				readFacilityFile.close();
				

				if(rewriteFacilityFile(newFile)) {
					removeAnswer = "The Facility "+name+" has been removed";
				}else {
					removeAnswer = "The Facility File could not be overwritten";
				}
			}catch(IOException e){
				return "IOException error";
				}
		}else {
			removeAnswer += "The Facility "+name+" cannot be removed becuase the following Bookings have been made:\n";
			for (int i = 0; i < bookingList.size(); i++) {
				removeAnswer += "Date "+bookingList.get(i).getBookingDate()+" for slot "+bookingList.get(i).getSlot()+" booked by user id "+bookingList.get(i).getUserId()+" with booking id "+bookingList.get(i).getBookingId()+"\n";
			}	
		}
		return removeAnswer;
	}
	
	/**
	 * getFacilityList() is a static method that returns all the facilities on the system
	 * */
	public static ArrayList<String> getFacilityList(){
		ArrayList<String> facilityList = new ArrayList<String>();
		try {
			FileReader readFacilityFile = new FileReader("Facilities.txt");
			Scanner in = new Scanner(readFacilityFile);
			String[] fileItem;
			
			while(in.hasNext()) {
				fileItem = in.nextLine().split(",");
				facilityList.add(fileItem[1]);
			}
			
			in.close();
			readFacilityFile.close();
			
			return facilityList;
			
		}catch(IOException e){
			return facilityList;
		}
	}
	
	/**
	 * getPrice(int facId) is a static method that returns the price that facilities charges for a slot
	 * */
	public static double getPrice(int facId) {
		String fileName = "Facilities.txt";
		File facilityFile = new File(fileName);
		
		if (facilityFile.exists()) {
			try {
				FileReader readFacilityFile = new FileReader("Facilities.txt");
				Scanner in = new Scanner(readFacilityFile);
				String[] fileItem;
				Double facPrice = -1.0;
				
				while(in.hasNext()) {
					
					fileItem = in.nextLine().split(",");
					int id = Integer.parseInt(fileItem[0]);
					
					if(id == facId) {
						facPrice = Double.parseDouble(fileItem[2]);
					}
				}
				
				in.close();
				readFacilityFile.close();
				
				return facPrice;
				
			}catch(IOException e){
				return -1.0;
			}
		}else {
			return -1.0;
		}
	}

	/**
	 * getName(int facId) is a static method that returns the name of the facility
	 * */
	public static String getName(int facId) {
		String fileName = "Facilities.txt";
		File facilityFile = new File(fileName);
		
		if (facilityFile.exists()) {
			try {
				FileReader readFacilityFile = new FileReader("Facilities.txt");
				Scanner in = new Scanner(readFacilityFile);
				String[] fileItem;
				String facName="";
				
				while(in.hasNext()) {
					
					fileItem = in.nextLine().split(",");
					int id = Integer.parseInt(fileItem[0]);
					
					if(id == facId) {
						facName = fileItem[1];
					}
				}
				
				in.close();
				readFacilityFile.close();
				
				return facName;
				
			}catch(IOException e){
				return "false";
			}
		}else {
			return "false";
		}
	}

	
	/**
	 * facilityFileExists() method checks if the Facilities.txt file exists
	 * */
	public boolean facilityFileExists() {
		String fileName = "Facilities.txt";
		File facilityFile = new File(fileName);
		
		if (facilityFile.exists()) {
			return true;
		}else {
			return false;
		}
		
	}
	
	/**
	 *  writeToFacilityFile(String lineToWrite) method writes a line of String to the Facilities.txt file
	 *  It uses facilityFileExists() method.
	 * */
	public boolean writeToFacilityFile(String lineToWrite) {
		try {
			FileWriter readFacilityFile;
			
			if (facilityFileExists()) {
				 readFacilityFile = new FileWriter("Facilities.txt",true);
			}else {
				 readFacilityFile = new FileWriter("Facilities.txt",false);
			}
	
			PrintWriter out = new PrintWriter(readFacilityFile);
			out.println(lineToWrite);
			out.close();
			readFacilityFile.close();
			return true;
		}catch(IOException e){
			return false;	
		}
	}
	
	/**
	 * rewriteFacilityFile(newFile) method re-writes the Facilities.txt file
	 * */
	public static boolean rewriteFacilityFile(ArrayList<String> newFile) {
		try {
			FileWriter readFacilityFile = new FileWriter("Facilities.txt",false);
			PrintWriter out = new PrintWriter(readFacilityFile);
			for (int i = 0; i < newFile.size(); i++) {
				out.println(newFile.get(i));
			}
			
			out.close();
			readFacilityFile.close();
			return true;
		}catch(IOException e){
			return false;	
		}
	}
	
	/**
	 * checkName(String name) method takes the facility's name and returns true if 
	 * that name is in use. This method uses the facilityFileExists() method 
	 * */
	public boolean checkName(String name) {
		if (facilityFileExists()) {
			try {
				FileReader readFacilityFile = new FileReader("Facilities.txt");
				Scanner in = new Scanner(readFacilityFile);
				String[] fileItem;
				boolean exists = false;
				
				while(in.hasNext()) {
					fileItem = in.nextLine().split(",");
					if(fileItem[1].equalsIgnoreCase(name)) {
						exists = true;
						break;
					}else {
						exists = false;
					}
				}
				
				in.close();
				readFacilityFile.close();
				
				if(exists==true) {
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
	 * getId(String name) method takes the facility's name and returns their id. 
	 * This method uses the facilityFileExists() method.
	 * */
	public int getId(String name) {
		if (facilityFileExists()) {
			try {
				FileReader readFacilityFile = new FileReader("Facilities.txt");
				Scanner in = new Scanner(readFacilityFile);
				String[] fileItem;
				int lastId = 0;
				boolean newFacility = true;
				
				while(in.hasNext()) {
					
					fileItem = in.nextLine().split(",");
					lastId = Integer.parseInt(fileItem[0]);
					if(fileItem[1].equalsIgnoreCase(name)) {
						newFacility = false;
						break;
					}
				}
				
				in.close();
				readFacilityFile.close();
				
				if(newFacility == true) {
					return lastId+1;
				}else {
					return lastId;
				}
				
			}catch(IOException e){
				return -1;
			}
		}else {
			 return 1;
		}
	}

	
	/**
	 * getFacilityInfo(String name) method takes the facility's name and returns 
	 * the line of info in file corresponding to them. 
	 * This method uses the facilityFileExists() method.
	 * */
	public String getFacilityInfo(String name) {
		if (facilityFileExists()) {
			try {
				FileReader readFacilityFile = new FileReader("Facilities.txt");
				Scanner in = new Scanner(readFacilityFile);
				String[] fileItem;
				String lineOfInfo="";
				boolean facilityExists = false;
				
				while(in.hasNext()) {
					
					lineOfInfo = in.nextLine();
					fileItem = lineOfInfo.split(",");
					
					if(fileItem[1].equalsIgnoreCase(name)) {
						facilityExists = true;
						break;
					}
				}
				
				in.close();
				readFacilityFile.close();
				
				if(facilityExists == true) {
					return lineOfInfo;
				}else {
					return "Invalid";
				}
				
			}catch(IOException e){
				return "Invalid";
			}
		}else {
			return "Invalid";
		}
	}

	/**
	 * getPrice() is a method that returns the facility's price
	 * */
	public double getPrice() {
		return price;
	}
	
	/**
	 * setPrice(double price) is a method that sets the facility's price
	 * */
	public void setPrice(double price) {
		this.price = price;
	}
	
}
