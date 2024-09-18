package Tracker;

import java.net.HttpURLConnection;
import java.net.URL;

public class TelegramNotifier {

    private static final String BOT_TOKEN = "GENERATE YOUR BOT TOKEN USING BOTFATHER IN TELEGRAM";
    private static final String CHAT_ID = "PUT YOUR CHAT ID OR GROUP ID HERE";

    public static void sendTelegramMsg(String message) {
        try {
            String urlString = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage";
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            String payload = "chat_id=" + CHAT_ID + "&text=" + message;
            connection.getOutputStream().write(payload.getBytes("UTF-8"));

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Notification sent successfully.");
            } else {
                System.out.println("Failed to send notification. Response Code: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendNotification(Deposit deposit) {
        String message = String.format(
                "New deposit detected:\n\n" +
                        "Block Number: %s\n" +
                        "Timestamp: %s UTC\n" +
                        "Fee: `%f`\n" +
                        "Transaction Hash: %s\n" +
                        "Public Key: %s",
                deposit.getBlockNumber(),
                deposit.getBlockTimestamp(),
                deposit.getFee(),
                deposit.getTransactionHash(),
                deposit.getPubkey()
        );

        sendTelegramMsg(message);
    }


}
