import java.sql.*;
import java.util.Scanner;

public class HotelBookingApp {
    static final String DB_URL = "jdbc:mysql://localhost:3306/hotel";
    static final String USER = "root";
    static final String PASS = ""; // change if needed

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            int choice;
            do {
                System.out.println("\n--- Hotel Booking System ---");
                System.out.println("1. View Available Rooms");
                System.out.println("2. Book Room");
                System.out.println("3. Cancel Booking");
                System.out.println("4. View All Bookings");
                System.out.println("5. Exit");
                System.out.print("Choose: ");
                choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1:
                        viewAvailableRooms(conn);
                        break;
                    case 2:
                        System.out.print("Enter Name: ");
                        String name = sc.nextLine();
                        System.out.print("Room ID: ");
                        int roomId = sc.nextInt();
                        sc.nextLine();
                        System.out.print("Check-in Date (YYYY-MM-DD): ");
                        String checkIn = sc.nextLine();
                        System.out.print("Check-out Date (YYYY-MM-DD): ");
                        String checkOut = sc.nextLine();
                        bookRoom(conn, name, roomId, checkIn, checkOut);
                        break;
                    case 3:
                        System.out.print("Booking ID to cancel: ");
                        int bookingId = sc.nextInt();
                        cancelBooking(conn, bookingId);
                        break;
                    case 4:
                        viewBookings(conn);
                        break;
                }
            } while (choice != 5);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void viewAvailableRooms(Connection conn) throws SQLException {
        String query = "SELECT * FROM rooms WHERE is_booked = FALSE";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            System.out.println("Available Rooms:");
            while (rs.next()) {
                System.out.println("Room ID: " + rs.getInt("room_id") + ", Type: " + rs.getString("room_type"));
            }
        }
    }

    static void bookRoom(Connection conn, String name, int roomId, String checkIn, String checkOut) throws SQLException {
        String check = "SELECT is_booked FROM rooms WHERE room_id = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(check)) {
            checkStmt.setInt(1, roomId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && !rs.getBoolean("is_booked")) {
                String insert = "INSERT INTO bookings (customer_name, room_id, check_in, check_out) VALUES (?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(insert)) {
                    ps.setString(1, name);
                    ps.setInt(2, roomId);
                    ps.setDate(3, Date.valueOf(checkIn));
                    ps.setDate(4, Date.valueOf(checkOut));
                    ps.executeUpdate();
                }

                String update = "UPDATE rooms SET is_booked = TRUE WHERE room_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(update)) {
                    ps.setInt(1, roomId);
                    ps.executeUpdate();
                }
                System.out.println("Booking successful!");
            } else {
                System.out.println("Room is already booked or not found.");
            }
        }
    }

    static void cancelBooking(Connection conn, int bookingId) throws SQLException {
        String getRoom = "SELECT room_id FROM bookings WHERE booking_id = ?";
        try (PreparedStatement getStmt = conn.prepareStatement(getRoom)) {
            getStmt.setInt(1, bookingId);
            ResultSet rs = getStmt.executeQuery();
            if (rs.next()) {
                int roomId = rs.getInt("room_id");

                String delete = "DELETE FROM bookings WHERE booking_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(delete)) {
                    ps.setInt(1, bookingId);
                    ps.executeUpdate();
                }

                String updateRoom = "UPDATE rooms SET is_booked = FALSE WHERE room_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(updateRoom)) {
                    ps.setInt(1, roomId);
                    ps.executeUpdate();
                }

                System.out.println("Booking cancelled.");
            } else {
                System.out.println("Booking not found.");
            }
        }
    }

    static void viewBookings(Connection conn) throws SQLException {
        String query = "SELECT * FROM bookings";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            System.out.println("All Bookings:");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("booking_id") + ", Name: " + rs.getString("customer_name")
                        + ", Room: " + rs.getInt("room_id") + ", From: " + rs.getDate("check_in")
                        + ", To: " + rs.getDate("check_out"));
            }
        }
    }
}
