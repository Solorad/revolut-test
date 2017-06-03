package ut.com.morenkov.service;

import com.morenkov.exception.InvalidParametersException;
import com.morenkov.model.request.AccountOperation;
import com.morenkov.model.request.AccountUpdateRequest;
import com.morenkov.service.AccountService;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class ConcurrentAccountServiceTest {
    private AccountService accountService;

    // number of accounts in aaccounts.json file
    private static final int TOTAL_USER_NUM = 4;
    private Random random = new Random(Runtime.getRuntime().freeMemory());

    @Before
    public void setUp() throws Exception {
        accountService = new AccountService();
    }

    @Test
    public void concurrentTest() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        for (int i = 0; i < 30; i++) {
            Runnable runnable = () -> {
                for (int j = 0; j < 500; j++) {
                    String from = String.valueOf(random.nextInt(TOTAL_USER_NUM) + 1);
                    String to = String.valueOf(random.nextInt(TOTAL_USER_NUM) + 1);
                    String amount = String.valueOf(50 * random.nextDouble());
                    AccountUpdateRequest request = new AccountUpdateRequest(AccountOperation.TRANSFER, to, amount);
                    try {
                        accountService.updateAccount(from, request);
                    } catch (InvalidParametersException e) {
                        // if no money on account - don't print this exception here.
                    }
                }
            };
            executorService.submit(runnable);
        }
        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.MINUTES);
        BigDecimal balance1 = accountService.findAccount("1").getBalance();
        BigDecimal balance2 = accountService.findAccount("2").getBalance();
        BigDecimal balance3 = accountService.findAccount("3").getBalance();
        BigDecimal balance4 = accountService.findAccount("4").getBalance();
        BigDecimal total = balance1.add(balance2).add(balance3).add(balance4);
        assertEquals(0, total.compareTo(new BigDecimal(4000)));
    }


}
