package Tracker;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseHelper {

    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/ethereum_deposits";
    private static final String DATABASE_USER = "amrit";
    private static final String DATABASE_PASSWORD = "amrit@123";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
    }

    // Check if the deposit already exists in DB
    public static boolean depositExists(BigInteger blockNumber, String transactionHash) {
        String query = "SELECT COUNT(*) FROM deposits WHERE block_number = ? AND transaction_hash = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setBigDecimal(1, new BigDecimal(blockNumber)); // Convert BigInteger to BigDecimal
            stmt.setString(2, transactionHash);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Insert a new deposit to DB
    public static void insertDeposit(Deposit deposit) {
        String query = "INSERT INTO deposits (block_number,block_timestamp,fee,transaction_hash,pubkey) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setBigDecimal(1, new BigDecimal(deposit.getBlockNumber()));
            stmt.setString(2, deposit.getBlockTimestamp());
            stmt.setDouble(3, deposit.getFee());
            stmt.setString(4, deposit.getTransactionHash());
            stmt.setString(5, deposit.getPubkey());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
