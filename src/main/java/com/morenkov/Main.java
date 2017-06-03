package com.morenkov;

import com.google.gson.Gson;
import com.morenkov.exception.InvalidParametersException;
import com.morenkov.model.Account;
import com.morenkov.model.request.AccountUpdateRequest;
import com.morenkov.service.AccountService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static spark.Spark.get;
import static spark.Spark.post;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);


    public static void main(String[] args) throws Exception {
        AccountService accountService = new AccountService();
        Gson gson = new Gson();
        get("/account/:id", (req, res) -> {
            String id = req.params(":id");
            logger.info("Test {}", id);
            return "Test endpoint";
        });
        post("/account/:id", (req, res) -> {
            try {
                String from = req.params(":id");
                AccountUpdateRequest accountUpdateRequest = gson.fromJson(req.body(), AccountUpdateRequest.class);
                Account fromAccount = accountService.updateAccount(from, accountUpdateRequest);
                return "Updated. Current balance is " + fromAccount.getBalance();
            } catch (InvalidParametersException e) {
                res.status(400);
                return e.getMessage();
            } catch (Exception e) {
                res.status(500);
                return "Some exception occurred";
            }
        });
    }
}
