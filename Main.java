package hotelmanagement;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
	Scanner scanner = new Scanner(System.in);
	int userId ;
	int dishId;
	String dishName;
	Integer quantity;
	public static void main(String[] args) throws ClassNotFoundException{
		
		Scanner sc = new Scanner(System.in);
		Main ref  = new Main();
		
		int option ;
		while(true) {
			System.out.println("Enter 1 to SignIn & Enter 2 to Login ");
			 option = ref.getValidIntInput();
			try {
				if (option == 1) {
					ref.signin();
					
				}else if(option ==2) {
					ref.login();
				}else {
					System.out.println("Enter valid option");
					option = sc.nextInt();
				}
				
			} catch (ClassNotFoundException | InterruptedException e) {
				e.printStackTrace();

			} catch (InputMismatchException e) {
				System.out.println("Enter valid option");
				option = sc.nextInt();
			
			}
			
		}
		
		
	}
	
	   private int getValidIntInput() {
	        while (!scanner.hasNextInt()) {
	            System.out.println("Invalid input. Please enter a number.");
	            scanner.next(); // Clear the invalid input
	        }
	        return scanner.nextInt();
	    }
	
	public static Connection connectDb() throws ClassNotFoundException{
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/hotelmanagement" , "root", "RK@137vrk");
			return connection;
			
			
		} catch (SQLException e) {
			
			e.printStackTrace();
			return null;
		}
	}
	
	public void signin() throws ClassNotFoundException, InterruptedException {
	    Connection connection = connectDb();
	    scanner.nextLine();
	    System.out.println("Create new Account");
	    
	    System.out.print("Enter your name: ");
	    String name = scanner.nextLine().trim();
	    
	    if (name.isEmpty()) {
	        System.out.println("Name cannot be empty.");
	        signin();
	        return;
	    }

	    String[] nameArr = name.split(" ");
	    
	    String fname, lname = "";
	    if (nameArr.length == 2) {
	        fname = nameArr[0].substring(0, 1).toUpperCase() + nameArr[0].substring(1).toLowerCase();
	        lname = nameArr[1].substring(0, 1).toUpperCase() + nameArr[1].substring(1).toLowerCase();
	    } else if (nameArr.length == 1) {
	        fname = nameArr[0].substring(0, 1).toUpperCase() + nameArr[0].substring(1).toLowerCase();
	        lname = ""; 
	    } else {
	        System.out.println("Please enter both first and last name.");
	        signin();
	        return;
	    }
	    
	    String fullName = fname + (lname.isEmpty() ? "" : " " + lname);
	    
	    System.out.print("Enter a username: ");
	    String username = scanner.nextLine().trim();
	    
	    System.out.print("Enter a password: ");
	    String password = scanner.next();
	    boolean isValid;
	    do {
	        isValid = isValidPassword(password);
	        if (!isValid) {
	            System.out.println("The password must be at least 8 characters long and must contain numbers and special characters.");
	            System.out.print("Enter a password: ");
	            password = scanner.next();
	        }
	    } while (!isValid); 
	    
	    scanner.nextLine();

	    try {
	        String sql = "INSERT INTO users(Name, Username, Password) VALUES (?, ?, ?)";
	        PreparedStatement preparedstm = connection.prepareStatement(sql);
	        
	        preparedstm.setString(1, fullName);
	        preparedstm.setString(2, username);
	        preparedstm.setString(3, password);

	        int rowsAffected = preparedstm.executeUpdate();
	        
	        if (rowsAffected == 1) {
	            System.out.println("You are a member now");
	        } else {
	            System.out.println("Failed to sign in");
	        }
	        connection.close();
	        
	        System.out.println("Please Login: ");
	        login();
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	
	public boolean isValidPassword(String password) {
		
		if(password.length() <8) {
			return false;
		}
		Pattern specialCharPattern = Pattern.compile("[^a-zA-Z0-9]");
		Pattern numPattern = Pattern.compile("[0-9]");
		
		Matcher specialCharMatcher = specialCharPattern.matcher(password);
		Matcher numMatcher = numPattern.matcher(password);
		
		return specialCharMatcher.find() && numMatcher.find();
	}
	
	
	
	
	public int login() throws ClassNotFoundException, InterruptedException{
		Connection connection = connectDb();
		
		System.out.println("Enter your username");
		String username = scanner.nextLine();
		
		System.out.println("Enter your password");
		String password = scanner.nextLine();
		
		ResultSet result = null; 
		
		try {
			String sql = "select* from users where username = ? and password = ?";
			PreparedStatement preparedStm = connection.prepareStatement(sql);
			preparedStm.setString(1, username);
			preparedStm.setString(2, password);
			
			
			result = preparedStm.executeQuery();
			
			
			if(result.next()) {
				userId = result.getInt(1);
				String[] name = result.getString(2).split(" ");
			
				System.out.println();
				System.out.println("Hi, "+name[0]);
			}else {
				System.out.println("Enter correct username or password");
				login();
			}
			
			menu();
			
		} catch (SQLException e) {
			
			e.printStackTrace();
			
		}
		return userId;
	}
	
  	public void menu() throws ClassNotFoundException, InterruptedException{
		System.out.println("==================================  MENU  =====================================");
		System.out.println("Choose any cuisine by entering it's number");
		System.out.println("1. North Indian");
		System.out.println("2. Continental");
		System.out.println("3. Chinese");
		System.out.println("4. Gujrati");
		System.out.println("5. Beverages");
		System.out.println("6. All");
		
		int option = scanner.nextInt();
		scanner.nextLine();
		
		if(option >6 || option <1 ){
			menu();
		}
		
			switch (option) {
			case 1:
				
				fetch("North Indian");
				break;
				
			case 2:
				fetch("Continental");
				break;
			case 3:
				
				fetch("Chinese");
				break;
				
			case 4: 
				
				fetch("Gujrati");
				break; 
				
			case 5:
				
				fetch("Beverages");
				break;
			
			case 6:
				ResultSet result = null ;
				
				
				try {
					Connection connection = connectDb();
					String sql = "select* from dishes";
					PreparedStatement preparedStm = connection.prepareStatement(sql);
					result = preparedStm.executeQuery();
					System.out.println("Menu");
					System.out.println();
					while(result.next()) {
						
						System.out.println(result.getInt(1)+". "+result.getString(2)+"   --->   "+"Rs."+ result.getInt(3));
						System.out.println();
					}	
				} catch (Exception e) {
					System.err.println("Line 178");
					e.printStackTrace();
					
				}
			default:
				break;
			}
			
			System.out.println("To place the order please enter the id or enter 'E' to exit");
			takeOrder();
			
			
	}
	
	public void takeOrder() throws ClassNotFoundException, InterruptedException {

	    Map<Integer, Integer> dishArray = new HashMap<>(); 
	    Orders orderRef = new Orders();
	    
	    while (true) {

	        System.out.println("Input:");
	        String input = scanner.nextLine();
	        
	        if (input.length() >= 2) {
	            input = input.substring(0, 2);
	        }
	        if (input.equalsIgnoreCase("E")) {
	            orderRef.cart(userId, dishArray);
	            break;
	        }
	        
	        System.out.println("Enter quantity:");
	        Integer quantity = scanner.nextInt();
	        
	        scanner.nextLine();


	        String[] inputArray = input.split(" ");

	        for (String value : inputArray) {
	            try {
	                Integer dishId = Integer.parseInt(value);
	                dishArray.put(dishId, quantity);
	            } catch (NumberFormatException e) {
	                System.out.println("Invalid Dish ID! Please enter a valid number.");
	            }
	        }
	    }
	}

	
	
	
	public ResultSet fetch(String a) throws ClassNotFoundException {
		ResultSet result = null ;
		
		Connection connection = connectDb();
		
		try {
			
			String sql = "select* from dishes where DishType =?";
			PreparedStatement preparedStm = connection.prepareStatement(sql);
			preparedStm.setString(1, a);
			result = preparedStm.executeQuery();
			System.out.println(a+ " Cuisine");
			System.out.println();
			while(result.next()) {
				
				System.out.println(result.getInt(1)+". "+result.getString(2)+"   --->   "+"Rs."+ result.getInt(3));
				System.out.println();
			}
			
		} catch (SQLException e) {
			System.err.println("Line 213");
			e.printStackTrace();
		}
		
		return result;
	}
	


}
