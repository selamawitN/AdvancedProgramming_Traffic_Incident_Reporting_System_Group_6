import database.DatabaseConnection;
import gui.LoginScreen;
import gui.SplashScreen;
import network.TrafficServer;
import network.WebServer;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        
        new Thread(() -> TrafficServer.main(null)).start();
        Thread.sleep(2000);
  
        SwingUtilities.invokeLater(() -> new SplashScreen());
        Thread.sleep(3000);
      
        DatabaseConnection.createTables();
        SwingUtilities.invokeLater(() -> new LoginScreen());
    }
}
