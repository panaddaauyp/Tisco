package th.co.d1.digitallending.util;

//import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.h2.tools.Server;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpHeaders;

@ComponentScan("com.tfglog.*")
public class ApplicationStartup implements ServletContextListener {

//    public static final Properties scopeProject = new Properties();
//    private static final Logger log4j = Logger.getLogger(ApplicationStartup.class.getName());
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
        } catch (ClassNotFoundException e) {
//            log4j.info(e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {

            if (server != null) {
                server.shutdown();
                server.stop();
                //System.out.println("-----------------------------------------------------------------------");
                //System.out.println("H2 " + server.getURL() + " memory database stoped !!");
                //System.out.println("-----------------------------------------------------------------------");
            }

        } catch (Exception ex) {
//            log4j.info(ex);
        }
    }
}
