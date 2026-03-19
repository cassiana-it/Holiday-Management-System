/*
Holiday Management System
A console-based Java application for managing holiday bookings.

Author: Cassiana de Oliveira
*/

import java.io.*; 
import java.util.*;

public class HolidayManager 
{
    private static final String FILENAME = "holidays.dat"; //file to save/load/test holiday data
    private static ArrayList<Holiday> holidays = new ArrayList<>(); //list to store holidays
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) 
    {
        loadHolidays(); //load existing holidays from file

        int choice;
        do 
        {
            displayMenu(); //show menu options to user
            System.out.print("Enter your choice: ");
            try 
            {
                choice = scanner.nextInt(); //read the user choice
                scanner.nextLine(); //clear newline character left in input
                
                switch (choice) 
                {
                    case 1:
                        viewAllHolidays(); //display all holidays
                        break;
                    case 2:
                        viewHolidaysByPriceRange(); //filter holidays by price
                        break;
                    case 3:
                        displayCheapestHoliday();//show the cheapest holiday
                        break;
                    case 4:
                        removeHoliday(); //delete a holiday by number
                        break;
                    case 5:
                        updateHolidayDetails(); //edit a holiday
                        break;
                    case 6:
                        addHoliday(); //add a new holiday
                        break;
                    case 0:
                        saveHolidays(); //save before exiting
                        System.out.println("Exiting program... Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } 
            catch (InputMismatchException e) //handle invalid input
            {
                System.out.println("Please enter a valid number.");
                scanner.nextLine(); //clear the invalid input
                choice = -1; //invalid option repeat menu
            }
        } 
        while (choice != 0); //loop until user chooses to exit
    }

    private static void displayMenu() //display the main menu options
    {
        System.out.println("\nHoliday Management System");
        System.out.println("1. View all holidays");
        System.out.println("2. View holiday(s) within a given price range");
        System.out.println("3. Display details of the cheapest holiday");
        System.out.println("4. Remove a holiday");
        System.out.println("5. Update holiday details");
        System.out.println("6. Add a new holiday");
        System.out.println("0. Exit");
        System.out.println();
    }

    private static void loadHolidays() //load holidays from file into the array list
    {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILENAME))) {
            holidays = (ArrayList<Holiday>) ois.readObject();            
        
            int maxHolidayNo = 0;  //update the nextHolidayNo to avoid duplicates
            for (Holiday h : holidays) 
            {
                if (h.getHolidayNo() > maxHolidayNo) 
                {
                    maxHolidayNo = h.getHolidayNo();
                }
            }
            Holiday.setNextHolidayNo(maxHolidayNo + 1); //update the counter
        } 
        catch (FileNotFoundException e) 
        {            
            System.out.println("No existing holiday file found. Starting with empty list."); //if file doesn't exist yet, it will work
        } 
        catch (IOException | ClassNotFoundException e) 
        {
            System.out.println("Error loading holidays: " + e.getMessage()); //handle other file or class loading issues
        }
    }

    private static void saveHolidays() //save holiday list to the file
    {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILENAME))) 
        {
            oos.writeObject(holidays); //serialize the list of holidays
            System.out.println("Holidays saved successfully.");
        } 
        catch (IOException e) 
        {
            System.out.println("Error saving holidays: " + e.getMessage()); //error saving holidays
        }
    }

    private static void viewAllHolidays() //displays all holidays stored in the system
    {
        if (holidays.isEmpty()) 
        {
            System.out.println("No holidays available.");
            return;
        }
        System.out.println("\nAll Holidays:");
        
        for (Holiday h : holidays) {
            System.out.println(h); //print holiday
        }
    }

    private static void viewHolidaysByPriceRange() //show holidays within a specified price range
    {
        if (holidays.isEmpty()) 
        {
            System.out.println("No holidays available.");
            return;
        }
        
        try 
        {
            System.out.print("Enter minimum price: ");
            double min = scanner.nextDouble();
            System.out.print("Enter maximum price: ");
            double max = scanner.nextDouble();
            scanner.nextLine(); //consume newline
            
            if (min > max)  //validate input range
            {
                System.out.println("Minimum price cannot be greater than maximum price.");
                return;
            }
            
            System.out.println("\nHolidays between " + min + " and " + max + ":");
            boolean found = false;
            for (Holiday h : holidays) 
            {
                if (h.getCost() >= min && h.getCost() <= max) 
                {
                    System.out.println(h);
                    found = true;
                }
            }
            
            if (!found) 
            {
                System.out.println("No holidays found in this price range.");
            }
        } catch (InputMismatchException e) 
        {
            System.out.println("Please enter valid numbers for price range.");
            scanner.nextLine(); //clear the invalid input
        }
    }

    private static void displayCheapestHoliday() //find and display the cheapest holiday
    {
        if (holidays.isEmpty()) 
        {
            System.out.println("No holidays available.");
            return;
        }
        
        Holiday cheapest = holidays.get(0); //start with the first as default
        for (Holiday h : holidays) 
        {
            if (h.getCost() < cheapest.getCost()) 
            {
                cheapest = h; //update if a cheaper holiday is found
            }
        }
        
        System.out.println("\nCheapest Holiday:");
        System.out.println(cheapest);
    }

    private static void removeHoliday() //remove a holiday by holidayNo
    {
        if (holidays.isEmpty()) 
        {
            System.out.println("No holidays available to remove.");
            return;
        }
        
        try 
        {
            System.out.print("Enter holiday number to remove: ");
            int holidayNo = scanner.nextInt();
            scanner.nextLine(); //consume newline
            
            boolean removed = false;
            
            for (int i = 0; i < holidays.size(); i++) 
            {
                if (holidays.get(i).getHolidayNo() == holidayNo) 
                {
                    holidays.remove(i); //remove if found
                    removed = true;
                    saveHolidays(); //save
                    System.out.println("Holiday " + holidayNo + " removed successfully.");
                    break;
                }
            }
            
            if (!removed) 
            {
                System.out.println("Holiday with number " + holidayNo + " not found.");
            }
        } 
        catch (InputMismatchException e) 
        {
            System.out.println("Please enter a valid holiday number.");
            scanner.nextLine(); //clear the invalid input
        } 
    }

    private static void updateHolidayDetails() //update the details based on user input
    {
        if (holidays.isEmpty()) 
        {
            System.out.println("No holidays available to update.");
            return;
        }
        
        try 
        {
            System.out.print("Enter holiday number to update: ");
            int holidayNo = scanner.nextInt();
            scanner.nextLine();
            
            Holiday holidayToUpdate = null;
            for (Holiday h : holidays) 
            {
                if (h.getHolidayNo() == holidayNo) 
                {
                    holidayToUpdate = h;
                    break;
                }
            }
            
            if (holidayToUpdate == null) 
            {
                System.out.println("Holiday with number " + holidayNo + " not found.");
                return;
            }
            
            System.out.println("Current details:");
            System.out.println(holidayToUpdate);
            
            System.out.print("Enter new destination (leave blank to keep current): "); //update destination
            String destination = scanner.nextLine();
            if (!destination.isEmpty()) 
            {
                holidayToUpdate.setDestination(destination);
            }
            
            System.out.print("Enter new departure airport (leave blank to keep current): "); //update departure airport
            String deptAirport = scanner.nextLine();
            if (!deptAirport.isEmpty()) 
            {
                holidayToUpdate.setDepartureAirport(deptAirport);
            }
            
            System.out.print("Enter new duration (0 to keep current): ");  //update duration
            String durationInput = scanner.nextLine();
            if (!durationInput.isEmpty()) 
            {
                try 
                {
                    int duration = Integer.parseInt(durationInput);
                    if (duration > 0) 
                    {
                        holidayToUpdate.setDuration(duration);
                    }
                } 
                catch (NumberFormatException e) 
                {
                    System.out.println("Invalid duration. Keeping current value.");
                }
            }
            
            System.out.print("Enter new cost (0 to keep current): "); //update cost
            String costInput = scanner.nextLine();
            if (!costInput.isEmpty()) 
            {
                try 
                {
                    double cost = Double.parseDouble(costInput);
                    if (cost > 0) 
                    {
                        holidayToUpdate.setCost(cost);
                    }
                } catch (NumberFormatException e) 
                {
                    System.out.println("Invalid cost. Keeping current value.");
                }
            }
        } 
        catch (InputMismatchException e) 
        {
            System.out.println("Please enter a valid holiday number.");
            scanner.nextLine();
        }
            saveHolidays(); //save before leaving
    }
    
    private static void addHoliday() //add new holiday
    {
    
    System.out.print("Enter destination: ");
    String destination = scanner.nextLine();

    System.out.print("Enter departure airport: ");
    String airport = scanner.nextLine();

    int duration = 0;
    double cost = 0;

    while (true) 
    {
    System.out.print("Enter duration (days): ");

    try
    {
        duration = scanner.nextInt();

        if (duration > 0) 
        {
            break;
        } 
        else 
        {
            System.out.println("Duration must be greater than 0.");
        }

    } 
    catch (InputMismatchException e)
    {
        System.out.println("Please enter a valid number.");
        scanner.nextLine(); //clear bad input
    }
   }
   
   while (true) 
   {
    System.out.print("Enter cost: ");
    
    try 
    {
        cost = scanner.nextDouble();
        scanner.nextLine(); //clear buffer

        if (cost > 0) 
        {
            break; //valid
        }
        else 
        {
            System.out.println("Cost must be greater than 0.");
        }

    }
    catch (InputMismatchException e) 
    {
        System.out.println("Please enter a valid number.");
        scanner.nextLine(); //clear invalid input
    }
   }

    Holiday newHoliday = new Holiday(destination, airport, duration, cost); //create object

    holidays.add(newHoliday); //add to list
    saveHolidays(); //save before leaving

    System.out.println("Holiday added successfully!");
   }
}