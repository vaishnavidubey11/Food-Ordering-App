package hotelmanagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;


public class Orders {

    Scanner sc = new Scanner(System.in);
    Integer key;
    Integer val;

    public void cart(int userId, Map<Integer, Integer> dishInfo) throws ClassNotFoundException, InterruptedException {

        int n = 1;
        Connection connection = Main.connectDb();
        String sql = "SELECT DishName, DishPrice FROM dishes WHERE DishId =?";

        try {
            double sum = 0;
            for (Map.Entry<Integer, Integer> entry : dishInfo.entrySet()) {
                key = entry.getKey();
                val = entry.getValue();

                PreparedStatement preparedStm1 = connection.prepareStatement(sql);
                preparedStm1.setInt(1, key);

                ResultSet rs1 = preparedStm1.executeQuery();

                while (rs1.next()) {
                    String dishName = rs1.getString(1);
                    int dishPrice = rs1.getInt(2);
                    sum += dishPrice * val; // Multiply by quantity
                    System.out.println(n + ". " + dishName + "   Rs." + (dishPrice * val));
                    n++;
                }
            }

            System.out.println("Total: " + sum);

            System.out.println("\nTo place order enter 'Y' else enter 'N'");
            String option = sc.nextLine();
            if (option.equalsIgnoreCase("Y")) {
                placeOrder(userId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void placeOrder(int userId) throws ClassNotFoundException, InterruptedException {

        Connection connection = Main.connectDb();
        Timestamp currentDateTime = new Timestamp(System.currentTimeMillis());
        String sql = "SELECT address FROM users WHERE Id = ?";

        try {
            PreparedStatement preparedStm1 = connection.prepareStatement(sql);
            preparedStm1.setInt(1, userId);
            ResultSet rs = preparedStm1.executeQuery();

            if (rs.next()) {
                String address = rs.getString(1);
                if(address == null){
                	  address(userId, currentDateTime);
                }else {
                	System.out.println("=> Address:\n" + address);
                    System.out.println("Would you like to change the address? Enter 'Y' for Yes or 'N' for No");

                    String opt = sc.nextLine();
                    if (opt.equalsIgnoreCase("Y")) {
                        address(userId, currentDateTime);
                    } else {
                        generateOrdersId(userId, currentDateTime);
                        System.out.println("Order placed successfully.");
                        System.out.println("Your order will be delivered to your address in 30 mins.");
                    }
                }
                
            } else {
                address(userId, currentDateTime);
            }

        } catch (SQLException e) {
        	
        }
    }

    public void address(int userId, Timestamp currentDateTime) throws ClassNotFoundException, InterruptedException {
        Connection connection = Main.connectDb();

        System.out.println("Enter the delivery address");
        String address = sc.nextLine();

        System.out.println("Enter phone number");
        Long phn_number = sc.nextLong();
        
        while (true) {
            String phnStr = String.valueOf(phn_number);

            if (phnStr.length() == 10) {
                break; 
            } else {
                System.out.println("Invalid phone number. Please enter a 10-digit number.");
                phn_number  = sc.nextLong();
            }
        }
        sc.nextLine();

        String sql2 = "UPDATE users SET address = ?, phonenumber = ? WHERE Id = ?";
        try {
            PreparedStatement preparedStm2 = connection.prepareStatement(sql2);
            preparedStm2.setString(1, address);
            preparedStm2.setLong(2, phn_number);
            preparedStm2.setInt(3, userId);

            int rowsAffected = preparedStm2.executeUpdate();
            Thread.sleep(2000);
            if (rowsAffected >= 1) {
                generateOrdersId(userId, currentDateTime);
                System.out.println("Order placed successfully.");
                System.out.println("Your order will be delivered to your address in 30 mins.");
                
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String generateOrdersId(int userId, Timestamp currentTimestamp) throws ClassNotFoundException {
        UUID uuid = UUID.randomUUID();
        String ordersID = "U" + userId + "-order-" + uuid.toString().substring(0, 8); 

        Connection connection = Main.connectDb();

        String sqlInsert = "INSERT INTO orders (order_id, user_id, order_date, status) VALUES(?,?,?,?)";

        try {
           
            while (orderIdExists(connection, ordersID)) {
                uuid = UUID.randomUUID();
                ordersID = "U" + userId + "-order-" + uuid.toString().substring(0, 13);
            }

            PreparedStatement preparedStm = connection.prepareStatement(sqlInsert);
            preparedStm.setString(1, ordersID);
            preparedStm.setInt(2, userId);
            preparedStm.setTimestamp(3, currentTimestamp);
            preparedStm.setString(4, "Pending");
            preparedStm.execute();
        } catch (SQLException e) {
           
        }
        return ordersID;
    }

    private static boolean orderIdExists(Connection connection, String ordersID) throws SQLException {
        String sqlCheck = "SELECT 1 FROM orders WHERE order_id = ?";
        try (PreparedStatement preparedStmCheck = connection.prepareStatement(sqlCheck)) {
            preparedStmCheck.setString(1, ordersID);
            ResultSet rs = preparedStmCheck.executeQuery();
            return rs.next();
        }
    }

}
