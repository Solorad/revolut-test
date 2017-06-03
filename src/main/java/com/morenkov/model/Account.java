package com.morenkov.model;

import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Account {
    private final String id;
    private String name;
    private BigDecimal balance;

    private Lock read;
    private Lock write;

    public Account(String id, String name, BigDecimal balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        initObjectLocks();
    }

    public void initObjectLocks() {
        ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        write = readWriteLock.writeLock();
        read = readWriteLock.readLock();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBalance() {
        read.lock();
        try {
            return balance;
        } finally {
            read.unlock();
        }
    }

    public void withdraw(BigDecimal amount) {
        write.lock();
        try {
            balance = balance.subtract(amount);
        } finally {
            write.unlock();
        }
    }

    public void deposit(BigDecimal amount) {
        write.lock();
        try {
            balance = balance.add(amount);
        } finally {
            write.unlock();
        }
    }

    public Lock getWriteLock() {
        return write;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", balance=" + balance +
                '}';
    }
}
