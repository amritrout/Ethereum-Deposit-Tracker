package Tracker;

import com.github.wkennedy.abi.Decoder;
import com.github.wkennedy.abi.models.DecodedLog;
import com.github.wkennedy.abi.models.Log;
import com.github.wkennedy.abi.models.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.core.methods.request.EthFilter;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class EthereumDepositTracker {

    private static final Logger logger = LoggerFactory.getLogger(EthereumDepositTracker.class);
    private static final String INFURA_URL = "https://mainnet.infura.io/v3/PUT YOUR API KEY HERE"; // Replace with your Infura or Alchemy URL
    private static final String DEPOSIT_CONTRACT_ADDRESS = "0x00000000219ab540356cBB839Cbe05303d7705Fa";
    private static final String ABI_FILE_PATH = "contractABI.json"; //REPLACE THE LOCATION WITH YOURS
    private Web3j web3j;
    private Decoder decoder;
    private BigInteger lastProcessedBlock;

    public EthereumDepositTracker() throws IOException {
        web3j = Web3j.build(new HttpService(INFURA_URL));
        String abiJson = new String(Files.readAllBytes(Paths.get(ABI_FILE_PATH)));
        decoder = new Decoder(abiJson);
        lastProcessedBlock = BigInteger.ZERO;
    }

    public void trackDeposits() {
        try {

            EthBlockNumber ethBlockNumber = web3j.ethBlockNumber().send();
            BigInteger latestBlockNumber = ethBlockNumber.getBlockNumber();
            logger.info("Latest block number: {}", latestBlockNumber);

            if(latestBlockNumber!=lastProcessedBlock){
                // Determine the start block and end block to fetch the data from
                BigInteger fromBlock = latestBlockNumber.subtract(BigInteger.valueOf(200));
                BigInteger toBlock = latestBlockNumber;

                //used for Filtering
                EthFilter filter = new EthFilter(
                        DefaultBlockParameter.valueOf(fromBlock),
                        DefaultBlockParameter.valueOf(toBlock),
                        DEPOSIT_CONTRACT_ADDRESS
                );

                // logs( fetch)
                EthLog ethLog = web3j.ethGetLogs(filter).send();

                // logs(process)
                if (ethLog != null && ethLog.getLogs() != null) {
                    if (!ethLog.getLogs().isEmpty()) {
                        logger.info("Found {} logs in blocks {} to {}.", ethLog.getLogs().size(), fromBlock, toBlock);

                        for (EthLog.LogResult<?> logResult : ethLog.getLogs()) {
                            EthLog.LogObject logObject = (EthLog.LogObject) logResult.get();

                            String transactionHash = logObject.getTransactionHash();
                            BigInteger blockNumber = logObject.getBlockNumber();
                            List<String> topics = logObject.getTopics();
                            String data = logObject.getData();

                            decodeLog(data, topics, transactionHash, blockNumber);
                        }
                    } else {
                        logger.info("No logs found in blocks {} to {}.", fromBlock, toBlock);
                    }
                } else {
                    logger.warn("ethLog is null or logs are null.");
                }

                lastProcessedBlock = latestBlockNumber;
            }
            else{
                logger.info("No new Blocks");
            }
        } catch (Exception e) {
            logger.error("Error tracking deposits: {}", e.getMessage(), e);
        }
    }


    private void decodeLog(String data, List<String> topics, String transactionHash, BigInteger blockNumber) {
        try {
            // Create the log to decode
            Log log = new Log(data, topics, DEPOSIT_CONTRACT_ADDRESS);

            // Decode the log
            Log[] logsArray = new Log[]{log};
            List<DecodedLog> decodedLogs = decoder.decodeLogs(logsArray);

            for (DecodedLog decodedLog : decodedLogs) {
                String eventName = decodedLog.getName();
                logger.info("Decoded Event: {}", eventName);

                // Access the decoded parameters
                Map<String, Param> paramsMap = createParamsMap(decodedLog.getEvents());
                String pubkey = getParamValue(paramsMap, "pubkey");


                EthBlock block = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(blockNumber), false).send();
                String timestamp = TimePriceConverter.convertTimestampToDate(block.getBlock().getTimestamp());

                //(gas used * gas price)
                TransactionReceipt receipt = web3j.ethGetTransactionReceipt(transactionHash).send().getTransactionReceipt().orElse(null);
                BigInteger gasUsed = receipt.getGasUsed();
                Transaction transaction = web3j.ethGetTransactionByHash(transactionHash).send().getTransaction().orElse(null);
                BigInteger gasPrice = transaction.getGasPrice();
                Double fee = TimePriceConverter.convertWeiToEther(gasUsed.multiply(gasPrice));

                //Save the Deposit details and log it
                Deposit deposit = new Deposit(blockNumber, timestamp,fee, transactionHash, pubkey);

                if (!DatabaseHelper.depositExists(blockNumber, transactionHash)) {
                    DatabaseHelper.insertDeposit(deposit);
                    logger.info("Deposit Recorded: {}", deposit);
                    TelegramNotifier.sendNotification(deposit);
                } else {
                    logger.info("Deposit already exists in the database: {}", deposit);
                }
            }

        } catch (Exception e) {
            logger.error("Error decoding log data: {}", e.getMessage(), e);
        }
    }

    private Map<String, Param> createParamsMap(List<Param> events) {
        Map<String, Param> paramsMap = new HashMap<>();
        if (events != null) {
            for (Param param : events) {
                paramsMap.put(param.getName(), param);
            }
        }
        return paramsMap;
    }

    private String getParamValue(Map<String, Param> paramsMap, String paramName) {
        Param param = paramsMap.get(paramName);
        return param != null ? param.getValue().toString() : "N/A";
    }

    public static void main(String[] args) {
        try {
            EthereumDepositTracker tracker = new EthereumDepositTracker();
            while (true) {
                tracker.trackDeposits();
                Thread.sleep(60000); // 1 min sleep time
            }
        } catch (IOException e) {
            logger.error("Failed to initialize EthereumDepositTracker: {}", e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Tracking interrupted: {}", e.getMessage(), e);
        }
    }
}
