
import com.google.gson.Gson;
import com.lotus.sametime.im.Im;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.ebasso.SametimeBot;

/**
 *
 * @author ebasso
 */
public class SimpleRestBot extends SametimeBot {

    private static final String CLASS_NAME = SimpleRestBot.class.getName();
    private static String INIFILE = CLASS_NAME + ".properties";

    private static Properties config;
    private static boolean debug;

    public static void main(String[] args) {

        try {
            System.out.println(CLASS_NAME);
            SimpleRestBot bot = new SimpleRestBot();

            bot.init(args);
            String sametimeHostname = (String) config.getProperty("sametime.hostname");
            String sametimeUsername = (String) config.getProperty("sametime.username");
            String sametimePassword = (String) config.getProperty("sametime.password");

            bot.login(sametimeHostname, sametimeUsername, sametimePassword);
        } catch (IOException ex) {
            Logger.getLogger(SimpleBot.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String doTextReceived(Im im, String partnerName, String partnerMessage) {
        writeToConsole("doTextReceived", "start");

        String partnerResponse = getWelcomeMessage();

        writeToConsole("doTextReceived: parterName: \"" + partnerName + "\" partnerMessage: \"" + partnerMessage + "\"");

        partnerMessage = partnerMessage.trim();

        if (partnerMessage.length() == 0) {
            return "\nInvalid Message.";
        }

        if (partnerMessage.equals("GET REST")) {
            im.sendText(true, "Get Rest Data ...");
            partnerResponse = getRest();
        }

        writeToConsole("doTextReceived: parterName: \"" + partnerName + "\" partnerResponse: \"" + partnerResponse + "\"");

        writeToConsole("doTextReceived", "end");
        return partnerResponse;
    }

    @Override
    public String doWelcomeMessage(String partnerName) {
        writeToConsole("doWelcomeMessage", "start");

        String partnerResponse = "Hello " + partnerName + getWelcomeMessage();

        writeToConsole("doWelcomeMessage", "end");
        return partnerResponse;
    }

    private String getWelcomeMessage() {

        String msg = ".\n\nSametime Bot: Simple Rest Bot."
                + "\nPlease send as: GET REST";

        return msg;
    }

    private String getRest() {

        String partnerResponse = "";
        try {

            URL url = new URL("https://httpbin.org/get?color=red&shape=oval");
            InputStreamReader reader = new InputStreamReader(url.openStream());
            MyDto dto = new Gson().fromJson(reader, MyDto.class);
            partnerResponse = dto.toString();

        } catch (RuntimeException | IOException ex) {
            partnerResponse = "Could not contact Rest Service. Please try again later";
            ex.printStackTrace();
        }

        return partnerResponse;
    }

    public void init(String[] args) throws FileNotFoundException, IOException {

        if (args.length != 0) {

            for (String arg : args) {
                if (arg.equals("-help")) {
                    System.out.println("-help  : Help de ajuda");
                    System.out.println("-debug : Habilita debug da aplicação");
                }
                if (arg.equals("-debug")) {
                    debug = true;
                }
                if (arg.startsWith("-inifile=")) {
                    INIFILE = arg.substring(9);
                } else {
                    System.out.println("inifile not informed");
                }
            }
        }
        config = new Properties();
        FileInputStream file = new FileInputStream(INIFILE);
        config.load(file);
    }

    private class MyDto {

        Map<String, String> headers;
        Map<String, String> args;
        String origin;
        String url;

        @Override
        public String toString() {
            return "MyDto{" + "headers=" + headers + ", args=" + args + ", origin=" + origin + ", url=" + url + '}';
        }

    }
}
