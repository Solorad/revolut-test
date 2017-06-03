package it.com.morenkov;

import com.morenkov.Main;
import com.morenkov.model.request.AccountOperation;
import com.morenkov.model.request.AccountUpdateRequest;
import io.restassured.RestAssured;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;

/**
 * Without my beloved Spring with its easy-to-write Integration tests,
 * I had to imagine something new and a little bit weird.
 * So, on @BeforeClass stage main thread with Spark server starts.
 */
public class IntegrationTests {
    private static Thread mainThread;
    @BeforeClass
    public static void setup() throws Exception {
        RestAssured.port = 4567;
        RestAssured.basePath = "";
        RestAssured.baseURI = "http://localhost";
        mainThread = new Thread() {
            @Override
            public void run() {
                try {
                    Main.main(new String[]{});
                } catch (Exception e) {
                    System.out.println("Exception occurred");
                }
            }
        };
        mainThread.start();
        // don't want condition, when tests starts before main thread
        Thread.yield();
        Thread.sleep(1000);
    }

    @AfterClass
    public static void cleanUp() {
        mainThread.interrupt();
    }


    @Test
    public void testTransfer() {
        AccountUpdateRequest accountUpdateRequest = new AccountUpdateRequest(AccountOperation.TRANSFER, "2", "100");
        given()
                .contentType("application/json")
                .body(accountUpdateRequest)
                .when().post("/account/1").then()
                .body(containsString("Updated. Current balance is 900.00"));
    }

    @Test
    public void testTransferInvalidAmount() {
        // 5000 - is too much for account.
        AccountUpdateRequest accountUpdateRequest = new AccountUpdateRequest(AccountOperation.TRANSFER, "2", "5000");
        given()
                .contentType("application/json")
                .body(accountUpdateRequest)
                .when().post("/account/1").then()
                .statusCode(equalTo(400));
    }
}
