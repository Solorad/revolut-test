package com.morenkov.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.morenkov.exception.InvalidParametersException;
import com.morenkov.model.Account;
import com.morenkov.model.request.AccountUpdateRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

/**
 * Service class for work with account
 */
public class AccountService {
    private static final Logger logger = LogManager.getLogger(AccountService.class);

    private final ConcurrentHashMap<String, Account> accountRepository;

    public AccountService() throws Exception {
        try(InputStream accountsInput = new FileInputStream("accounts.json")) {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(accountsInput));

            Type type = new TypeToken<List<Account>>() {
            }.getType();
            List<Account> result = new Gson().fromJson(reader, type);
            // during json deserialization no constructor is called. Between custom JsonDeserializer and
            // extra call for init locks methods I choosed second.
            result.forEach(Account::initObjectLocks);
            accountRepository = result.stream().collect(Collectors.toMap(Account::getId, a -> a,
                    (account, account2) -> account, ConcurrentHashMap::new));
            logger.info("We've loaded accounts '{}'", accountRepository);
        }
    }

    public Account findAccount(String accountId) throws InvalidParametersException {
        if (accountId == null) {
            logger.error("account id must not be null!");
            throw new InvalidParametersException("account id must not be null!");
        }
        return accountRepository.get(accountId);
    }

    public Account updateAccount(String from, AccountUpdateRequest accountUpdateRequest)
            throws InvalidParametersException {
        if (accountUpdateRequest == null || accountUpdateRequest.getOperation() == null) {
            logger.error("Invalid update account request");
           throw new InvalidParametersException("Invalid update account request");
        }
        switch (accountUpdateRequest.getOperation()) {
            case TRANSFER:
                return validateAndTransferMoney(from, accountUpdateRequest.getTo(), accountUpdateRequest.getAmount());
        }
        return null;
    }

    private Account validateAndTransferMoney(String fromAccountId, String toAccountId, String value)
            throws InvalidParametersException {
        if (fromAccountId == null || toAccountId == null || value == null) {
            logger.error("Invalid parameters");
            throw new InvalidParametersException("Invalid parameters");
        }
        BigDecimal amount = new BigDecimal(value);
        Account from = findAccount(fromAccountId);
        Account to = findAccount(toAccountId);
        if (from == null || to == null) {
            logger.error("Accounts ['" + fromAccountId + "', '" + toAccountId + "'] were not found");
            throw new InvalidParametersException(
                    "Accounts ['" + fromAccountId + "', '" + toAccountId + "'] were not found");
        }
        return transferMoney(fromAccountId, toAccountId, amount, from, to);
    }

    private Account transferMoney(String fromAccountId, String toAccountId, BigDecimal amount, Account from, Account to)
            throws InvalidParametersException {
        // for protection from deadlock and make same lock ordering, take locks in order by accounts id
        Lock firstLock;
        Lock secondLock;
        if (fromAccountId.compareTo(toAccountId) < 0) {
            firstLock = from.getWriteLock();
            secondLock = to.getWriteLock();
        } else {
            firstLock = to.getWriteLock();
            secondLock = from.getWriteLock();
        }

        firstLock.lock();
        secondLock.lock();
        try {
            // no credit is allowed
            if (from.getBalance().compareTo(amount) < 0) {
                logger.error("Current user has " + from.getBalance() + " balance. Amount to transfer " + amount +
                        " is greater");
                throw new InvalidParametersException(
                        "Current user has " + from.getBalance() + " balance. Amount to transfer " + amount +
                                " is greater");
            }
            from.withdraw(amount);
            to.deposit(amount);
        } finally {
            secondLock.unlock();
            firstLock.unlock();
        }
        logger.info("Money were transferred. Balance of sender now is {}, balance of receiver is {}", from.getBalance(),
                to.getBalance());
        return from;
    }
}
