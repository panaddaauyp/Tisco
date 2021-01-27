package th.co.d1.digitallending.util;

//import org.apache.log4j.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.h2.tools.Server;
import org.springframework.http.HttpHeaders;

public class ApplicationStartup implements ServletContextListener {

//    public static final Properties scopeProject = new Properties();
//    private static final Logger log4j = Logger.getLogger(ApplicationStartup.class);
    private Server server;
    public static HttpHeaders headersJSON;
    public static HttpHeaders headersTEXT;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try {
            ConstainValue cons = new ConstainValue();
            cons.InitialTiscoLog();
            server = cons.createMemDb();
            cons.setLookUp();
            headersJSON = new HttpHeaders();
            headersJSON.add("Content-Type", "application/json ; charset=UTF-8");

            headersTEXT = new HttpHeaders();
            headersTEXT.add("Content-Type", "text/html ; charset=UTF-8");
        } catch (Exception e) {
//            log4j.error(e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {

            if (server != null) {
                server.shutdown();
                server.stop();
                System.out.println("-----------------------------------------------------------------------");
                System.out.println("H2 " + server.getURL() + " memory database stoped !!");
                System.out.println("-----------------------------------------------------------------------");
            }

        } catch (Exception ex) {
//            log4j.error(ex);
        }
    }
}
