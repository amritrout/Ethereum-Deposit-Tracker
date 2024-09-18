package Tracker;

import java.math.BigInteger;

public class TimePriceConverter {

    // Convert Unix timestamp to a readable date and timee
    public static String convertTimestampToDate(BigInteger timestamp) {
        long timeInMillis = timestamp.longValue() * 1000L;
        java.util.Date date = new java.util.Date(timeInMillis);
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    //wei to Ether
    public static double convertWeiToEther(BigInteger wei) {
        return wei.doubleValue() / Math.pow(10, 18);
    }
}
