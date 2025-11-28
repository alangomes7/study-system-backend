package batistaReviver.studentApi;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Student API application.
 *
 * <p>This class initializes and runs the Spring Boot application, enabling auto-configuration and
 * component scanning.
 */
@SpringBootApplication
public class RestfullestApplication implements CommandLineRunner {

  private static final Logger logger = LoggerFactory.getLogger(RestfullestApplication.class);

  @Value("${server.port:8080}")
  private int serverPort;

  /**
   * Main method to run the application.
   *
   * @param args Command-line arguments.
   */
  public static void main(String[] args) {
    SpringApplication.run(RestfullestApplication.class, args);
  }

  @Override
  public void run(String... args) {
    String ip = getLocalIpAddress();
    logger.info("server is started at [{}:{}]", ip, serverPort);
    System.out.println("server is started at [" + ip + ":" + serverPort + "]");
  }

  /**
   * Retrieves the first non-loopback, site-local IP address.
   *
   * @return The local IP address as a string, or "localhost" if none found.
   */
  private String getLocalIpAddress() {
    try {
      Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
      while (networkInterfaces.hasMoreElements()) {
        NetworkInterface ni = networkInterfaces.nextElement();
        Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();
        while (inetAddresses.hasMoreElements()) {
          InetAddress address = inetAddresses.nextElement();
          if (!address.isLoopbackAddress() && address.isSiteLocalAddress()) {
            return address.getHostAddress();
          }
        }
      }
      return InetAddress.getLocalHost().getHostAddress(); // fallback
    } catch (Exception e) {
      logger.warn("Could not determine local IP address: {}", e.getMessage());
      return "localhost";
    }
  }
}
