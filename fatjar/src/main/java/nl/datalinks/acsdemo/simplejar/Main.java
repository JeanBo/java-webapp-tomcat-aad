package nl.datalinks.acsdemo.simplejar;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        while(true) {
            System.out.println("Hello WORLD...excellent demo nr:" + randomAlphanumeric(5));
            SECONDS.sleep(1);
        }
    }

}
