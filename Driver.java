/**
 * CS4222/CS5052: Project 2
 * Group 46L
 * Author: Shane Conway
 * Student Id: 17170451
 * Due Date: 25/04/2018 13:00
 * */

import java.io.*;
import java.time.*;
import javax.swing.*;
import java.util.*;
import java.text.*;

public class Driver {
	
	public static Facility pickedFacility;
	public static String facilityName;

	/**
	 * main() method is the method that starts the application. It displays the Login Screen.
	 * The user then enters their credentials and is verified by the method if it is successful 
	 * it determines whether the user is a user or administrator and displays the UserPanel() or 
	 * AdminPanel() depending on who it is.
	 * */
	public static void main(String[] args)throws IOException {
		boolean administrator = false;
		boolean loggedIn = false;
		
		JPanel login = new JPanel();
		JLabel emailLabel = new JLabel("Username:");
		JTextField emailInput = new JTextField(25);
		
		JLabel passLabel = new JLabel ("Password:");
		JTextField passInput = new JTextField(25);
		
		login.add(emailLabel);
		login.add(emailInput);
		login.add(passLabel);
		login.add(passInput);
		String email="";
		while(loggedIn == false) {
			int selection = JOptionPane.showConfirmDialog(null, login,"Log In",JOptionPane.OK_CANCEL_OPTION);
			
			if (selection == JOptionPane.OK_CANCEL_OPTION) {
				System.exit(0);
			}else if(selection == JOptionPane.OK_OPTION) {
				
				email = (emailInput.getText()).trim();
				String password = (passInput.getText()).trim();
				
				if (email.isEmpty() || password.isEmpty()) {
					JOptionPane.showMessageDialog(null, "Please enter a username and password", "Invalid Entry", 2);
				}else {
					
					String fileName = "Users.txt";
					File userFile = new File(fileName);
					
					if (!userFile.exists()) {
						JOptionPane.showMessageDialog(null, "The administration file cannot be found!", "File not Found", 3);
					}else {
						Scanner in = new Scanner(userFile);
						String[] fileItem;
							
						while(in.hasNext()) {
							fileItem = in.nextLine().split(",");
							if (fileItem[1].equalsIgnoreCase(email) && fileItem[2].equals(password)) {
								loggedIn = true;
								if (fileItem[3].equals("1")) {
									administrator = true;
									break;
								}
								
							}
						}
						
						if(loggedIn==false) {
							JOptionPane.showMessageDialog(null, "Incorrect Login credentials", "Incorrect Login",2);
							
						}
						
						in.close();	
					}
					
				}
				
			}
		}
		
		if(administrator == true) {
			AdminPanel();
		}else {
			UserPanel(email);
		}
		
	}
	
	/**
	 * AdminPanel() method displays different options to the admin. It uses the following methods 
	 * registerUser(), addFacility(), curFacility(), viewAccounts()
	 * */
	public static void AdminPanel() {
		
		String[] options = {"Register User", "Add Facility", "Current Facilities","View Accounts","Log out"};
		
		int optionPicked = JOptionPane.showOptionDialog(null, "Please select an option","Facility Application",JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);	
		
		if (optionPicked==0) {
			registerUser();
		}else if(optionPicked==1){
			addFacility();
		}else if(optionPicked==2){
			curFacility();
		}else if(optionPicked==3){
			viewAccounts();
		}else if(optionPicked==4){
			JOptionPane.showMessageDialog(null,"Exiting...");
			System.exit(0);
		}
	}
	
	/**
	 * addFacility() method allows the admin to add a new facility. It instantiates a Facility 
	 * from the Facility Class and then performs the addFacility(facilityname, facilityprice)
	 * method on the object.
	 * */
	public static void addFacility() {
		JPanel facility = new JPanel();

		JLabel facilityLabel = new JLabel("Facility Name:");
		JTextField facilityInput = new JTextField(25);

		JLabel priceLabel = new JLabel("Price per Slot:");
		JTextField priceInput = new JTextField(25); 

		facility.add(facilityLabel);
		facility.add(facilityInput);
		facility.add(priceLabel);
		facility.add(priceInput);
		
		boolean issues = false;
		do{	
			int selection = JOptionPane.showConfirmDialog(null,facility, "Add Facility",JOptionPane.OK_CANCEL_OPTION);

			if (selection == JOptionPane.OK_OPTION) {
				
				String facilityname = (facilityInput.getText()).trim();
				String facilityprice = (priceInput.getText()).trim();

				if (facilityname.equals("") || facilityprice.equals("") ) {
					JOptionPane.showMessageDialog(null,"All fields must be filled in");
					issues = true;
				}else{
					Facility add = new Facility();
					
					if(add.addFacility(facilityname, facilityprice)==-1) {
						JOptionPane.showMessageDialog(null,"A Facilty already uses that name");
						issues = true;
					}else if(add.addFacility(facilityname, facilityprice)==-2) {
						JOptionPane.showMessageDialog(null,"There was an issue writing to file");
						issues = true;
					}else{
						issues = false;
						JOptionPane.showMessageDialog(null,"You have successfully added "+facilityname,"Registration Success",1);
					}
				}

			}else if(selection == JOptionPane.OK_CANCEL_OPTION){
				issues = false;
			}	
		}while(issues);
		
		AdminPanel();
	}
	
	/**
	 * curFacility() method uses the getFacilityList() from the Facility Class and 
	 * displays each facility as an option to the admin. It then calls the facilityOptions(selection)
	 * method.
	 * */
	public static void curFacility() {
		String aFile = "Facilities.txt";
		File facilityFile = new File(aFile);
		if (facilityFile.exists()) {
			ArrayList<String> optionsArray = Facility.getFacilityList();
			
			if(optionsArray.size()==0) {
				JOptionPane.showMessageDialog(null,"You currently have no facilities setup");
				AdminPanel();
			}else {
				String[] options = new String[optionsArray.size()];
	
				for(int i =0;i<optionsArray.size();i++){
					options[i] = optionsArray.get(i);
				}
				String selection = (String) JOptionPane.showInputDialog(null,"Select a Facility","Facilities",1,null,options,options[0]);
				
	
				if (selection == null) {
					AdminPanel();
				}else{
					facilityOptions(selection);
				}
			}
		}else{
			JOptionPane.showMessageDialog(null,"You currently have no facilities setup");
			AdminPanel();
		}
	}
	
	/**
	 * facilityOptions(String name) method displays the options that the admin can choose for a particular Facility.
	 * It instantiates a new Facility and uses the following static methods, makeBooking(), viewBookings(), viewAvailability()
	 * recordPayment(), decommission(), recommission(), removeFacility().
	 * */
	public static void facilityOptions(String name) {
		facilityName = name;
		pickedFacility = new Facility(facilityName);
		
		String[] options = {"Make Booking", "View Bookings", "View availability","Record payment","Decomission","Recomission","Remove Facility","Main Menu"};
		
		int optionPicked = JOptionPane.showOptionDialog(null, "Please select an option","Facility "+name,JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);	
		
		if (optionPicked==0) {
			makeBooking();
		}else if(optionPicked==1){
			viewBookings();	
		}else if(optionPicked==2){
			viewAvailability();	
		}else if(optionPicked==3){
			recordPayment();
		}else if(optionPicked==4){
			decommission();
		}else if(optionPicked==5){
			recommission();
		}else if(optionPicked==6){
			removeFacility();
		}else if(optionPicked==7){
			AdminPanel();
		}
		
	}
	
	/**
	 * makeBooking() method allows the admin to make a booking for a facility on behalf of a user
	 * it uses the methods makeBooking( email, dateReq, timeSlot, paymentStatus) from the Facility 
	 * class on the facility object.
	 * */
	public static void makeBooking() {
		if (pickedFacility.isDecommissioned()==false) {
			boolean setupAchieved=false;
			JPanel bookingSetup = new JPanel();
			bookingSetup.setLayout(new BoxLayout(bookingSetup, BoxLayout.Y_AXIS));		

			JLabel emailLabel = new JLabel("Enter users email:");
			JTextField emailInput = new JTextField(25);

			JLabel dateLabel = new JLabel("Enter the date for the booking in format 'YYYY-MM-DD'");
			JTextField dateInput = new JTextField(3);

			JLabel slotLabel = new JLabel("Enter a number in the range 1-9 to signify which slot");
			JTextField slotInput = new JTextField(3);

			JLabel payLabel = new JLabel("Enter Yes/No if payment was made");
			JTextField payInput = new JTextField(3);


			bookingSetup.add(emailLabel);
			bookingSetup.add(emailInput);
			bookingSetup.add(dateLabel);
			bookingSetup.add(dateInput);
			bookingSetup.add(slotLabel);
			bookingSetup.add(slotInput);
			bookingSetup.add(payLabel);
			bookingSetup.add(payInput);

			do{

				int selection = JOptionPane.showConfirmDialog(null,bookingSetup, "Make a Booking",JOptionPane.OK_CANCEL_OPTION);
	
				if (selection == JOptionPane.OK_OPTION) {
	
					String email = (emailInput.getText()).trim();
					String date = (dateInput.getText()).trim();
					String slot = (slotInput.getText()).trim();
					String payment = (payInput.getText()).trim();
		 
					String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
					//String datePattern = "[1-9]{4}+[0-9]{2}+[0-9]{2}";
					String slotPattern = "[1-9]{1}";
	
					if (email.equals("") || date.equals("") || slot.equals("") || payment.equals("")) {
						JOptionPane.showMessageDialog(null,"All fields must be filled in");
					}else{
						if(!(email.matches(emailPattern))){
							JOptionPane.showMessageDialog(null,"Please enter a valid email");
						}else if(!(isValidDate(date))){
							JOptionPane.showMessageDialog(null,"Please enter a valid date in the format 'YYYY-MM-DD'");
						}else if(!(slot.matches(slotPattern))){
							JOptionPane.showMessageDialog(null,"Please enter ONLY one number in the range 1-9");
						}else if(!payment.equalsIgnoreCase("Yes") && !payment.equalsIgnoreCase("No") ){
							JOptionPane.showMessageDialog(null,"Please enter ONLY Yes OR No for payment made");
						}else{
							int timeSlot = Integer.parseInt(slot);
							LocalDate dateReq = LocalDate.parse(date);
							boolean paymentStatus =false;
							if(payment.equalsIgnoreCase("Yes")) {
								paymentStatus = true;
							}
						
							String madeBooking = pickedFacility.makeBooking( email, dateReq, timeSlot, paymentStatus);
							
							if(madeBooking.equals("true")) {
								setupAchieved=true;
								JOptionPane.showMessageDialog(null,"Booking was successful");
								facilityOptions(facilityName);
							}else {
								JOptionPane.showMessageDialog(null,madeBooking);
							}	
						}
					}
					
				}else if(selection == JOptionPane.OK_CANCEL_OPTION){
					facilityOptions(facilityName);
				}
			}while(setupAchieved==false);
		}else {
			JOptionPane.showMessageDialog(null,"This Facility is decommissioned and cannot take bookings");
			facilityOptions(facilityName);
		}
	}
	
	/**
	 * viewBookings() method displays the different option of how the admin can view bookings for a
	 * facility. It uses methods such as dateBooking() and periodBooking().
	 * */
	public static void viewBookings() {
		String[] bookingOptions = {"View Bookings on a date", "View Bookings over period", "Back"};
		int bookingOption = JOptionPane.showOptionDialog(null, "Please select an option","Facility "+facilityName,JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, bookingOptions, bookingOptions[0]);	
		
		if (bookingOption==0) {
			dateBooking();
		}else if(bookingOption==1){
			periodBooking();
		}else if(bookingOption==2){
			facilityOptions(facilityName);
		}
	}
	
	/**
	 * dateBooking() method allows the admin to view bookings for a facility on a particular date 
	 * it uses the methods viewBookings(dateReq) from the Facility class on 
	 * the facility object.
	 * */
	public static void dateBooking() {
		boolean setupAchieved=false;
		JPanel bookingOneDate = new JPanel();
		bookingOneDate.setLayout(new BoxLayout(bookingOneDate, BoxLayout.Y_AXIS));		


		JLabel bookingDateLabel = new JLabel("Enter the date in format 'YYYY-MM-DD' to view the bookings");
		JTextField dateInput = new JTextField(3);

		bookingOneDate.add(bookingDateLabel);
		bookingOneDate.add(dateInput);

		do{

			int selection = JOptionPane.showConfirmDialog(null,bookingOneDate, "View Bookings",JOptionPane.OK_CANCEL_OPTION);

			if (selection == JOptionPane.OK_OPTION) {

				String date = (dateInput.getText()).trim();
	 
				if (date.equals("")) {
					JOptionPane.showMessageDialog(null,"Please enter a date");
				}else{
					if(!(isValidDate(date))){
						JOptionPane.showMessageDialog(null,"Please enter a valid date in the format 'YYYY-MM-DD'");
					}else{
						setupAchieved=true;
						LocalDate dateReq = LocalDate.parse(date);
						
						String listOfBookings = pickedFacility.viewBookings(dateReq);
						
						JOptionPane.showMessageDialog(null,listOfBookings);
						facilityOptions(facilityName);
					}
				}
				
			}else if(selection == JOptionPane.OK_CANCEL_OPTION){
				facilityOptions(facilityName);
			}
		}while(setupAchieved==false);
	}
	
	/**
	 * periodBooking() method allows the admin to view bookings for a facility over a period 
	 * of time it uses the methods viewBookings(dateReqFrom,dateReqTo) from the Facility class on 
	 * the facility object.
	 * */
	public static void periodBooking() {
		boolean setupAchieved=false;
		JPanel bookingbetweenDates = new JPanel();
		bookingbetweenDates.setLayout(new BoxLayout(bookingbetweenDates, BoxLayout.Y_AXIS));		


		JLabel bookingFromDateLabel = new JLabel("Enter the Start date in format 'YYYY-MM-DD'");
		JTextField dateFromInput = new JTextField(3);
		JLabel bookingToDateLabel = new JLabel("Enter the End date in format 'YYYY-MM-DD'");
		JTextField dateToInput = new JTextField(3);
		
		bookingbetweenDates.add(bookingFromDateLabel);
		bookingbetweenDates.add(dateFromInput);
		bookingbetweenDates.add(bookingToDateLabel);
		bookingbetweenDates.add(dateToInput);

		do{

			int selection = JOptionPane.showConfirmDialog(null,bookingbetweenDates, "View Bookings",JOptionPane.OK_CANCEL_OPTION);

			if (selection == JOptionPane.OK_OPTION) {

				String dateFrom = (dateFromInput.getText()).trim();
				String dateTo = (dateToInput.getText()).trim();
	 
				if (dateFrom.equals("") || dateTo.equals("")) {
					JOptionPane.showMessageDialog(null,"Please enter a date");
				}else{
					LocalDate dateReqFrom = LocalDate.parse(dateFrom);
					LocalDate dateReqTo = LocalDate.parse(dateTo);
					if(!(isValidDate(dateFrom)) || !(isValidDate(dateTo))){
						JOptionPane.showMessageDialog(null,"Please enter a valid date in the format 'YYYY-MM-DD'");
					}else if(dateReqTo.isBefore(dateReqFrom) || dateReqTo.equals(dateReqFrom)){
						JOptionPane.showMessageDialog(null,"To date cannot be before or equal to 'From date'");
					}else{
						setupAchieved=true;
						
						String listOfBookings = pickedFacility.viewBookings(dateReqFrom,dateReqTo);
						
						JOptionPane.showMessageDialog(null,listOfBookings);
						facilityOptions(facilityName);
					}
				}
				
			}else if(selection == JOptionPane.OK_CANCEL_OPTION){
				facilityOptions(facilityName);
			}
		}while(setupAchieved==false);
	}
	
	
	/**
	 * viewAvailability() method displays the different option of how the admin can view availabilities for a
	 * facility. It uses methods such as dateBooking() and periodBooking().
	 * */
	public static void viewAvailability() {
		if (pickedFacility.isDecommissioned()==false) {
			String[] availOptions = {"View Availabilities on a date", "View Availabilities over period", "Back"};
			int availOption = JOptionPane.showOptionDialog(null, "Please select an option","Facility "+facilityName,JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, availOptions, availOptions[0]);	
			
			if (availOption==0) {
				dateAvailability();
			}else if(availOption==1){
				periodAvailability();
			}else if(availOption==2){
				facilityOptions(facilityName);
			}
		}else {
			JOptionPane.showMessageDialog(null, "You cannot view Availabilities for his Facility as it is decommissioned untill "+pickedFacility.decommissionedUntil(), "Remove Facility", 2);
			facilityOptions(facilityName);
		}
	}
	
	/**
	 * dateAvailability() method allows the admin to view availabilities for a facility on a particular date 
	 * it uses the methods viewBookings(dateReq) from the Facility class on 
	 * the facility object and then sees what's available.
	 * */
	public static void dateAvailability() {
		boolean setupAchieved=false;
		JPanel availOnDate = new JPanel();
		availOnDate.setLayout(new BoxLayout(availOnDate, BoxLayout.Y_AXIS));		


		JLabel availDateLabel = new JLabel("Enter the date in format 'YYYY-MM-DD' to view the availabilities");
		JTextField dateInput = new JTextField(3);

		availOnDate.add(availDateLabel);
		availOnDate.add(dateInput);

		do{

			int selection = JOptionPane.showConfirmDialog(null,availOnDate, "View Availabilities",JOptionPane.OK_CANCEL_OPTION);

			if (selection == JOptionPane.OK_OPTION) {

				String date = (dateInput.getText()).trim();
	 
				if (date.equals("")) {
					JOptionPane.showMessageDialog(null,"Please enter a date");
				}else{
					if(!(isValidDate(date))){
						JOptionPane.showMessageDialog(null,"Please enter a valid date in the format 'YYYY-MM-DD'");
					}else{
						setupAchieved=true;
						LocalDate dateReq = LocalDate.parse(date);
						
						String listOfAvailabilities = pickedFacility.viewAvailability(dateReq);
						
						JOptionPane.showMessageDialog(null,listOfAvailabilities);
						facilityOptions(facilityName);
					}
				}
				
			}else if(selection == JOptionPane.OK_CANCEL_OPTION){
				facilityOptions(facilityName);
			}
		}while(setupAchieved==false);
	}
	
	/**
	 * periodBooking() method allows the admin to view availabilities for a facility over a period 
	 * of time it uses the methods viewBookings(dateReqFrom,dateReqTo) from the Facility class on 
	 * the facility object and then sees what's available.
	 * */
	public static void periodAvailability() {
		boolean setupAchieved=false;
		JPanel availbetweenDates = new JPanel();
		availbetweenDates.setLayout(new BoxLayout(availbetweenDates, BoxLayout.Y_AXIS));		


		JLabel availFromDateLabel = new JLabel("Enter the Start date in format 'YYYY-MM-DD'");
		JTextField dateFromInput = new JTextField(3);
		JLabel availToDateLabel = new JLabel("Enter the End date in format 'YYYY-MM-DD'");
		JTextField dateToInput = new JTextField(3);
		
		availbetweenDates.add(availFromDateLabel);
		availbetweenDates.add(dateFromInput);
		availbetweenDates.add(availToDateLabel);
		availbetweenDates.add(dateToInput);

		do{

			int selection = JOptionPane.showConfirmDialog(null,availbetweenDates, "View Availabilities",JOptionPane.OK_CANCEL_OPTION);

			if (selection == JOptionPane.OK_OPTION) {

				String dateFrom = (dateFromInput.getText()).trim();
				String dateTo = (dateToInput.getText()).trim();
	 
				if (dateFrom.equals("") || dateTo.equals("")) {
					JOptionPane.showMessageDialog(null,"Please enter a date");
				}else{
					LocalDate dateReqFrom = LocalDate.parse(dateFrom);
					LocalDate dateReqTo = LocalDate.parse(dateTo);
					
					if(!(isValidDate(dateFrom)) || !(isValidDate(dateTo))){
						JOptionPane.showMessageDialog(null,"Please enter a valid date in the format 'YYYY-MM-DD'");
					}else if(dateReqTo.isBefore(dateReqFrom) || dateReqTo.equals(dateReqFrom)){
						JOptionPane.showMessageDialog(null,"To date cannot be before or equal to 'From date'");
					}else if(dateReqTo.isAfter(dateReqFrom.plusDays(2))){
						JOptionPane.showMessageDialog(null,"You can only view availabilities for a range of 3 days");
					}else{
						setupAchieved=true;
						String listOfAvailabilities = pickedFacility.viewAvailability(dateReqFrom,dateReqTo);
						JOptionPane.showMessageDialog(null,listOfAvailabilities);
						facilityOptions(facilityName);
					}
				}
				
			}else if(selection == JOptionPane.OK_CANCEL_OPTION){
				facilityOptions(facilityName);
			}
		}while(setupAchieved==false);
	}
	
	/**
	 * recordPayment(dateReq,timeSlot) method allows the admin to record a payment to a facility on behalf of a user
	 * it uses the methods recordPayment() from the Facility class on the facility object.
	 * */
	public static void recordPayment() {
		boolean setupAchieved=false;
		JPanel recordPay = new JPanel();
		recordPay.setLayout(new BoxLayout(recordPay, BoxLayout.Y_AXIS));		


		JLabel dateLabel = new JLabel("Enter the date for the booking in format 'YYYY-MM-DD'");
		JTextField dateInput = new JTextField(3);

		JLabel slotLabel = new JLabel("Enter the slot number");
		JTextField slotInput = new JTextField(3);
		
		recordPay.add(dateLabel);
		recordPay.add(dateInput);
		recordPay.add(slotLabel);
		recordPay.add(slotInput);

		do{

			int selection = JOptionPane.showConfirmDialog(null,recordPay, "View Bookings",JOptionPane.OK_CANCEL_OPTION);

			if (selection == JOptionPane.OK_OPTION) {

				String date = (dateInput.getText()).trim();
				String slot = (slotInput.getText()).trim();
				
				String slotPattern = "[1-9]{1}";
				
				if (date.equals("") || slot.equals("")) {
					JOptionPane.showMessageDialog(null,"All fields must be filled in");
				}else{
					if(!(isValidDate(date))){
						JOptionPane.showMessageDialog(null,"Please enter a valid date in the format 'YYYY-MM-DD'");
					}else if(!(slot.matches(slotPattern))){
						JOptionPane.showMessageDialog(null,"Please enter ONLY one number in the range 1-9");
					}else{
						LocalDate dateReq = LocalDate.parse(date);
						int timeSlot = Integer.parseInt(slot);
						
						String payResponse = pickedFacility.recordPayment(dateReq,timeSlot);
						
						if(payResponse.equals("true")) {
							setupAchieved=true;
							JOptionPane.showMessageDialog(null,"Payment confirmed!");
							facilityOptions(facilityName);
						}
						
						JOptionPane.showMessageDialog(null,payResponse);
					}
				}
				
			}else if(selection == JOptionPane.OK_CANCEL_OPTION){
				facilityOptions(facilityName);
			}
		}while(setupAchieved==false);
	}
	
	/**
	 * decommission() method allows the admin to decommission a facility it uses the methods decomissionFacility(dateReq) 
	 * from the Facility class on the facility object.
	 * */
	public static void decommission(){
		boolean setupAchieved=false;
		JPanel decomPanel = new JPanel();
		decomPanel.setLayout(new BoxLayout(decomPanel, BoxLayout.Y_AXIS));		


		JLabel dateLabel = new JLabel("Enter the date in format 'YYYY-MM-DD' for Facility to be decommisioned until");
		JTextField dateInput = new JTextField(3);
		
		decomPanel.add(dateLabel);
		decomPanel.add(dateInput);

		do{

			int selection = JOptionPane.showConfirmDialog(null,decomPanel, "Decommissioning",JOptionPane.OK_CANCEL_OPTION);

			if (selection == JOptionPane.OK_OPTION) {

				String date = (dateInput.getText()).trim();
				
				
				if (date.equals("")) {
					JOptionPane.showMessageDialog(null,"Please enter a date");
				}else{
					if(!(isValidDate(date))){
						JOptionPane.showMessageDialog(null,"Please enter a valid date in the format 'YYYY-MM-DD'");
					}else{
						LocalDate dateReq = LocalDate.parse(date);
						
						String decomResponse = pickedFacility.decomissionFacility(dateReq);
						
						setupAchieved=true;
						JOptionPane.showMessageDialog(null,decomResponse);
						facilityOptions(facilityName);
					}
				}
				
			}else if(selection == JOptionPane.OK_CANCEL_OPTION){
				facilityOptions(facilityName);
			}
		}while(setupAchieved==false);
	}
	
	/**
	 * recommission() method allows the admin to recommision a facility it uses the methods isDecommissioned() 
	 * from the Facility class on the facility object to check whether the facility 
	 * has being decommissioned first. If not then it uses recomissionFacility() method on the object from the Facility 
	 * Class.
	 * */
	public static void recommission(){
		if (pickedFacility.isDecommissioned()==true) {
			String recommissionAns = pickedFacility.recomissionFacility();
			JOptionPane.showMessageDialog(null, recommissionAns, "Re-commission Facility", 2);
			facilityOptions(facilityName);
		}else {
			JOptionPane.showMessageDialog(null, "This Facility has not been decommissioned", "Re-commission Facility", 2);
			facilityOptions(facilityName);
		}
	}
	
	/**
	 * removeFacility() method allows the admin remove a facility it uses the methods isDecommissioned() 
	 * and decommissionedUntil() from the Facility class on the facility object to check whether the facility 
	 * has being decommissioned first. If not then it uses removeFacility() method on the object from the Facility 
	 * Class.
	 * */
	public static void removeFacility(){
		if (pickedFacility.isDecommissioned()==false) {
			String removeAnswer = pickedFacility.removeFacility();
			JOptionPane.showMessageDialog(null, removeAnswer, "Remove Facility", 2);
			AdminPanel();
		}else {
			JOptionPane.showMessageDialog(null, "This Facility cannot be removed as it is currently decommissioned untill "+pickedFacility.decommissionedUntil(), "Remove Facility", 2);
			facilityOptions(facilityName);
		}
	}
	
	/**
	 * registerUser() method allows the admin to add a new user and uses the methods emailExists(email) 
	 * and register(email) from the User Class to check email and register the user
	 * */
	public static void registerUser() {
		boolean setupAchieved=false;
		JPanel registerPanel = new JPanel();
		registerPanel.setLayout(new BoxLayout(registerPanel, BoxLayout.Y_AXIS));		

		JLabel emailLabel = new JLabel("Enter New Users email:");
		JTextField emailInput = new JTextField(25);



		registerPanel.add(emailLabel);
		registerPanel.add(emailInput);

		do{

			int selection = JOptionPane.showConfirmDialog(null,registerPanel, "Register a User",JOptionPane.OK_CANCEL_OPTION);

			if (selection == JOptionPane.OK_OPTION) {

				String email = (emailInput.getText()).trim();
	 
				String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
				

				if (email.equals("") ) {
					JOptionPane.showMessageDialog(null,"Please enter users email");
				}else{
					if(!(email.matches(emailPattern))){
						JOptionPane.showMessageDialog(null,"Please enter a valid email");
					}else if(User.emailExists(email)){
						JOptionPane.showMessageDialog(null,"That email is already in use");
					}else {
						String result = User.register(email);
						if(result.equals("false")) {
							JOptionPane.showMessageDialog(null,"Could not register user at this time");
						}else {
							setupAchieved=true;
							JOptionPane.showMessageDialog(null,"User successfully Registered.\n\n Here is their password: "+result);	
							AdminPanel();
						}	
					}
				}
				
			}else if(selection == JOptionPane.OK_CANCEL_OPTION){
				AdminPanel();
			}
		}while(setupAchieved==false);
	}
	
	/**
	 * viewAccounts() method uses the getUserList() static method from the User Class 
	 * to retrieve each of the users and displays them as an option to the admin. This 
	 * method then instantiates the picked user and uses the viewAccount() method from 
	 * the User Class to show the users outstanding payments
	 * */
	public static void viewAccounts() {
		ArrayList<String> optionsArray = User.getUserList();
		
		if(optionsArray.size()==0) {
			JOptionPane.showMessageDialog(null,"You currently have no Users setup");
			AdminPanel();
		}else {
			String[] options = new String[optionsArray.size()];

			for(int i =0;i<optionsArray.size();i++){
				options[i] = optionsArray.get(i);
			}
			String selection = (String) JOptionPane.showInputDialog(null,"Select a user","View User Accounts",1,null,options,options[0]);
			
			if (selection == null) {
				AdminPanel();
			}else{
				User pickedUser = new User(selection);
				String accounts = pickedUser.viewAccount();
				JOptionPane.showMessageDialog(null,accounts);
				viewAccounts();
			}
		}
		
		
	}
	
	/**
	 * UserPanel(String email) method takes the user who logged in and instantiates a User. This 
	 * method displays the users options and uses the viewBookings() method from the User Class to 
	 * to retrieve the users upcoming bookings. It uses the viewAccount() method from the User Class 
	 * to show the users outstanding payments.
	 * */
	public static void UserPanel(String email) {
		User loggedInUser = new User(email);
		String[] options = {"View Bookings", "View Account", "Log Out"};
		
		int optionPicked = JOptionPane.showOptionDialog(null, "Please select an option","View Accounts",JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);	
		
		if (optionPicked==0) {
			String listOfBookings = loggedInUser.viewBookings();
			JOptionPane.showMessageDialog(null,listOfBookings);
			UserPanel(email);
		}else if (optionPicked==1) {
			String accounts = loggedInUser.viewAccount();
			JOptionPane.showMessageDialog(null,accounts);
			UserPanel(email);
		}else if (optionPicked==2) {
			System.exit(0);
		}
	}
	
	/**
	 * isValidDate(String date) is a static method that determines if a date is in a correct 
	 * pattern i.e YYYY-MM-DD and whether it is a correct date i.e not 2018-02-31
	 * */
	public static boolean isValidDate(String date) {
		  if (date == null || !date.matches("\\d{4}-[01]\\d-[0-3]\\d"))
		        return false;
		    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		    df.setLenient(false);
		    try {
		        df.parse(date);
		        return true;
		    } catch (ParseException ex) {
		        return false;
		    }
	}
			
}
