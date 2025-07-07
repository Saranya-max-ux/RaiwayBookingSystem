import java.sql.*;
import java.util.Scanner;

public class RailwayBooking {

    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/railway";
        String user = "root";
        String password = "2004"; // ← Replace with your actual MySQL password

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, password);
            Scanner sc = new Scanner(System.in);

            // Show available trains with remaining seats
            System.out.println("🚆 Available Trains:");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM trains");

            while (rs.next()) {
                int trainId = rs.getInt("train_id");
                String trainName = rs.getString("train_name");
                int totalSeats = rs.getInt("total_seats");

                // Count booked seats
                PreparedStatement psCount = conn.prepareStatement(
                        "SELECT COUNT(*) FROM bookings WHERE train_id = ?");
                psCount.setInt(1, trainId);
                ResultSet rsCount = psCount.executeQuery();
                rsCount.next();
                int bookedSeats = rsCount.getInt(1);
                int remainingSeats = totalSeats - bookedSeats;

                System.out.printf("Train ID: %d | Name: %s | Seats Left: %d\n", trainId, trainName, remainingSeats);
                psCount.close();
            }

            // Ask user for details
            sc.nextLine(); // Clear buffer
            System.out.print("\nEnter passenger name: ");
            String name = sc.nextLine();

            System.out.print("Enter train ID to book: ");
            int selectedTrainId = sc.nextInt();

            // Check seat availability again for selected train
            PreparedStatement psTrain = conn.prepareStatement("SELECT total_seats FROM trains WHERE train_id = ?");
            psTrain.setInt(1, selectedTrainId);
            ResultSet rsTrain = psTrain.executeQuery();

            if (rsTrain.next()) {
                int totalSeats = rsTrain.getInt("total_seats");

                PreparedStatement psBooked = conn.prepareStatement(
                        "SELECT COUNT(*) FROM bookings WHERE train_id = ?");
                psBooked.setInt(1, selectedTrainId);
                ResultSet rsBooked = psBooked.executeQuery();
                rsBooked.next();
                int bookedSeats = rsBooked.getInt(1);

                if (bookedSeats < totalSeats) {
                    // Book ticket
                    PreparedStatement psInsert = conn.prepareStatement(
                            "INSERT INTO bookings (passenger_name, train_id) VALUES (?, ?)");
                    psInsert.setString(1, name);
                    psInsert.setInt(2, selectedTrainId);
                    psInsert.executeUpdate();
                    psInsert.close();

                    int remainingSeats = totalSeats - (bookedSeats + 1); // One more just booked
                    System.out.println("✅ Ticket booked successfully!");
                    System.out.println("📢 Seats left on Train ID " + selectedTrainId + ": " + remainingSeats);
                } else {
                    System.out.println("❌ No seats available for this train.");
                }

                psBooked.close();
            } else {
                System.out.println("❌ Invalid train ID!");
            }

            psTrain.close();
            stmt.close();
            conn.close();
            sc.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
