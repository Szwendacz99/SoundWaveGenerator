package maciej;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainLauncher {

    static final Logger logger = LogManager.getLogger(MainLauncher.class);

    public static void main(String[] args) {
        logger.info("Starting program!");
        logger.info("javafx.runtime.version: " + System.getProperties().get("javafx.runtime.version"));
        MainWindow.main(args);
    }
}
