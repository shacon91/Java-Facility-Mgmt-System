/**
 * CS4222/CS5052: Project 2
 * Group 46L
 * Author: Shane Conway
 * Student Id: 17170451
 * Due Date: 25/04/2018 13:00
 * */

import java.io.*;
import java.util.*;

public class User {
	private int id;
	private String email = "";
	
	/**
	 * User(String email) is the Constructor method for the User class. It takes 
	 * one argument in that of the users email. This method is instantiated and a 
	 * new user object is created 
	 * */
	public User(String email) {
		this.setEmail(email);
		this.id = getId(email);
	}
	
	
	/**
	 * viewBookings() is a non static method that is called by the User object.
	 *  This method retrieves the upcoming bookings for that User. It uses the 
	 *  userBookings() static method from the Booking Class. 
	 * */
	public String viewBookings() {
		String bookingsAnswer ="";
		ArrayList<Booking> bookingList = Booking.userBookings(id);
		if (bookingList.size()==0) {
			bookingsAnswer = "There are currently no bookings for this account";
		}else {
			bookingsAnswer += "Here is the list of the upcoming Bookings for this account:\n";
			for (int i = 0; i < bookingList.size(); i++) {
				String facName = Facility.getName(bookingList.get(i).getFacilityId());
				bookingsAnswer += "Date "+bookingList.get(i).getBookingDate()+" for slot "+bookingList.get(i).getSlot()+" at Facility "+facName+" with booking id "+bookingList.get(i).getBookingId()+"\n";
			}	
		}
		return bookingsAnswer;
	}
	
	
	/**
	 * viewAccount() is a non static method that is called by the User object. This 
	 * method retrieves the outstanding payments for that User. It uses the userAccount() 
	 * static method from the Booking Class. It also uses the static method getPrice() 
	 * from the Facility class to get the price that the Facility charges per slot
	 * */
	public String viewAccount() {
		String accAnswer ="";
		ArrayList<Booking> accountList = Booking.userAccount(id);
		if (accountList.size()==0) {
			accAnswer = "There are currently no outstanding payments for this account";
		}else {
			accAnswer += "Here is the list of outstanding payments for this account:\n";
			Double total = 0.0;
			for (int i = 0; i < accountList.size(); i++) {
				Double facPrice = Facility.getPrice(accountList.get(i).getFacilityId());
				String facName = Facility.getName(accountList.get(i).getFacilityId());
				total += facPrice;
				accAnswer += "Date "+accountList.get(i).getBookingDate()+" for slot "+accountList.get(i).getSlot()+" at Facility "+facName+" with booking id "+accountList.get(i).getBookingId()+" oustanding €"+facPrice+"\n";
			}	
			accAnswer +="\nThe total owed is "+total;
		}
		return accAnswer;
	}
	
	
	/**
	 * register(String email) is a static method for the User class. It takes one argument 
	 * in that of the new users email. This method then calls the getNewId() method to issue 
	 * the user a new Id.  This method also calls the generatePassword() method to issue the 
	 * user a new password. It the uses the writeToUserFile() to insert the new User in the User.txt
	 * */
	public static String register(String email) {
		if(userFileExists()) {
			int userId = getNewId();
			if(userId!=-1) {
				String userPass = generatePassword();
				String alineToWrite = userId+","+email+","+userPass+",0";
				if(writeToUserFile(alineToWrite)) {
					return userPass;
				}else {
					return "false";
				}
			}else {
				return "false";
			}
		}else {
			return "false";
		}
	}
	
	
	/**
	 * userFileExists() method checks if the User.txt file exists
	 * */
	public static boolean userFileExists() {
		String fileName = "Users.txt";
		File userFile = new File(fileName);
		
		if (userFile.exists()) {
			return true;
		}else {
			return false;
		}
		
	}
	
	
	/**
	 * writeToUserFile(String lineToWrite) method writes a line of String to the User.txt file
	 * */
	public static boolean writeToUserFile(String lineToWrite) {
		try {
			FileWriter readUserFile = new FileWriter("Users.txt",true);
			PrintWriter out = new PrintWriter(readUserFile);
			
			out.println(lineToWrite);
			out.close();
			readUserFile.close();
			return true;
		}catch(IOException e){
			return false;	
		}
	}
	
	
	/**
	 * getId(String email) static method takes the users email and returns the users id. 
	 * This method uses the userFileExists() method to check if the User.txt file exists
	 * */
	public static int getId(String email) {
		if (userFileExists()) {
			try {
				FileReader readUserFile = new FileReader("Users.txt");
				Scanner in = new Scanner(readUserFile);
				String[] fileItem;
				int userId = -1;
				boolean exists = false;
				
				while(in.hasNext()) {
					fileItem = in.nextLine().split(",");
					if(fileItem[1].equalsIgnoreCase(email)) {
						exists = true;
						userId = Integer.parseInt(fileItem[0]);
						break;
					}
				}
				
				in.close();
				readUserFile.close();
				
				if(exists==true) {
					return userId;
				}else {
					return -1;
				}
				
			}catch(IOException e){
				e.printStackTrace();
				return -1;
			}
		}else {
			 return -1;
		}
	}
	
	/**
	 * getName(int id) static method takes the users id and returns the users email. 
	 * This method uses the userFileExists() method to check if the User.txt file exists
	 * */
	public static String getName(int id) {
		if (userFileExists()) {
			try {
				FileReader readUserFile = new FileReader("Users.txt");
				Scanner in = new Scanner(readUserFile);
				String[] fileItem;
				String userName = "";
				boolean exists = false;
				
				while(in.hasNext()) {
					fileItem = in.nextLine().split(",");
					int userId = Integer.parseInt(fileItem[0]);
					if(userId==id) {
						exists = true;
						userName = fileItem[1];
						break;
					}
				}
				
				in.close();
				readUserFile.close();
				
				if(exists==true) {
					return userName;
				}else {
					return "false";
				}
				
			}catch(IOException e){
				e.printStackTrace();
				return "false";
			}
		}else {
			return "false";
		}
	}
	

	
	/**
	 * getNewId() static method that returns the next available id. This method uses the 
	 * userFileExists() method to check if the User.txt file exists
	 * */
	public static int getNewId() {
		if (userFileExists()) {
			try {
				FileReader readUserFile = new FileReader("Users.txt");
				Scanner in = new Scanner(readUserFile);
				String[] fileItem;
				int userId = 1;
				
				while(in.hasNext()) {
					fileItem = in.nextLine().split(",");
					userId = Integer.parseInt(fileItem[0])+1;
				}
				
				in.close();
				readUserFile.close();
				
				return userId;
				
				
			}catch(IOException e){
				e.printStackTrace();
				return -1;
			}
		}else {
			 return -1;
		}
	}

	
	/**
	 * emailExists(String email) static method takes the users email and returns true if 
	 * that email is in use. This method uses the userFileExists() method to check if the
	 *  User.txt file exists
	 * */
	public static boolean emailExists(String email) {
		if (userFileExists()) {
			try {
				FileReader readUserFile = new FileReader("Users.txt");
				Scanner in = new Scanner(readUserFile);
				String[] fileItem;
				boolean exists = false;
				
				while(in.hasNext()) {
					fileItem = in.nextLine().split(",");
					if(fileItem[1].equalsIgnoreCase(email)) {
						exists = true;
						break;
					}
				}
				
				in.close();
				readUserFile.close();
				
				if(exists==true) {
					return true;
				}else {
					return false;
				}
				
			}catch(IOException e){
				e.printStackTrace();
				return false;
			}
		}else {
			 return false;
		}
	}
	
	
	/**
	 * generatePassword() static method returns an 8 character string consisting of 
	 * 4 letters, 2 numbers and 2 special characters.
	 * */
	public static String generatePassword() {
		//8 character password
		String password ="";
		String lowerAlpha = "abcdefghijklmnopqrstuvwxyz";
		String capitalAlpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String numbers = "0123456789";
		String specialChar = "_*&^%$£";
		
		password += lowerAlpha.charAt((int) (Math.random()*26));
		password += numbers.charAt((int) (Math.random()*9));
		password += capitalAlpha.charAt((int) (Math.random()*26));
		password += specialChar.charAt((int) (Math.random()*7));
		password += numbers.charAt((int) (Math.random()*9));
		password += lowerAlpha.charAt((int) (Math.random()*26));
		password += specialChar.charAt((int) (Math.random()*7));
		password += capitalAlpha.charAt((int) (Math.random()*26));
		
		return password;
	}
		
	
	/**
	 * getUserList() is a static method that returns all the users on the system
	 * */
	public static ArrayList<String> getUserList(){
		ArrayList<String> userList = new ArrayList<String>();
		try {
			FileReader readUserFile = new FileReader("Users.txt");
			Scanner in = new Scanner(readUserFile);
			String[] fileItem;
			
			while(in.hasNext()) {
				fileItem = in.nextLine().split(",");
				userList.add(fileItem[1]);
			}
			
			in.close();
			readUserFile.close();
			
			return userList;
			
		}catch(IOException e){
			return userList;
		}
	}


	
	/**
	 * getEmail() is a method that returns the users email
	 * */
	public String getEmail() {
		return email;
	}


	
	/**
	 * setEmail(String email) is a method that sets the users email
	 * */
	public void setEmail(String email) {
		this.email = email;
	}
	
}
