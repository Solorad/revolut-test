package ut.com.morenkov.service;

import com.morenkov.exception.InvalidParametersException;
import com.morenkov.model.Account;
import com.morenkov.model.request.AccountOperation;
import com.morenkov.model.request.AccountUpdateRequest;
import com.morenkov.service.AccountService;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.Assert.*;

public class AccountServiceTest {
    private AccountService accountService;

    @Before
    public void setUp() throws Exception {
        accountService = new AccountService();
    }

    @Test(expected = InvalidParametersException.class)
    public void findAccountNull() throws Exception {
        accountService.findAccount(null);
    }

    @Test
    public void findAccount() throws Exception {
        Account account = accountService.findAccount("1");
        assertNotNull(account);
    }

    @Test
    public void findAccountNotFound() throws Exception {
        Account account = accountService.findAccount("100");
        assertNull(account);
    }

    @Test(expected = InvalidParametersException.class)
    public void updateAccountTransferNull() throws Exception {
        accountService.updateAccount(null, null);
    }

    @Test(expected = InvalidParametersException.class)
    public void updateAccountTransferNotFound() throws Exception {
        accountService.updateAccount("100", null);
    }


    @Test(expected = InvalidParametersException.class)
    public void updateAccountTransferNoOperation() throws Exception {
        AccountUpdateRequest accountUpdateRequest = new AccountUpdateRequest();
        accountService.updateAccount("1", accountUpdateRequest);
    }

    @Test(expected = InvalidParametersException.class)
    public void updateAccountTransferNotEnoughMoney() throws Exception {
        AccountUpdateRequest accountUpdateRequest = new AccountUpdateRequest();
        accountUpdateRequest.setOperation(AccountOperation.TRANSFER);
        accountUpdateRequest.setAmount("9000");
        accountUpdateRequest.setTo("2");
        accountService.updateAccount("1", accountUpdateRequest);
    }

    @Test
    public void updateAccountTransfer() throws Exception {
        AccountUpdateRequest accountUpdateRequest = new AccountUpdateRequest();
        accountUpdateRequest.setOperation(AccountOperation.TRANSFER);
        accountUpdateRequest.setAmount("300");
        accountUpdateRequest.setTo("2");
        Account from = accountService.updateAccount("1", accountUpdateRequest);
        assertEquals(new BigInteger("700"), from.getBalance().toBigInteger());
        Account to = accountService.findAccount("2");
        assertEquals(new BigInteger("1300"), to.getBalance().toBigInteger());
    }

    @Test
    public void updateAccountTransferWithDecimal() throws Exception {
        AccountUpdateRequest accountUpdateRequest = new AccountUpdateRequest();
        accountUpdateRequest.setOperation(AccountOperation.TRANSFER);
        accountUpdateRequest.setAmount("248.34");
        accountUpdateRequest.setTo("2");
        Account from = accountService.updateAccount("1", accountUpdateRequest);
        assertEquals(0, from.getBalance().compareTo(new BigDecimal("751.66")));
        Account to = accountService.findAccount("2");
        assertEquals(0, to.getBalance().compareTo(new BigDecimal("1248.34")));
    }
}