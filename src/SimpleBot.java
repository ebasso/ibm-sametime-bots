
import com.lotus.sametime.im.Im;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.ebasso.SametimeBot;

/**
 *
 * @author ebasso
 */
public class SimpleBot extends SametimeBot {

    private static final String CLASS_NAME = SimpleBot.class.getName();
    private static String INIFILE = CLASS_NAME + ".properties";
    
    private static Properties config;
    private static boolean debug;

    public static void main(String[] args) {

        try {
            System.out.println(CLASS_NAME);
            SimpleBot bot = new SimpleBot();

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

        if (partnerMessage.length() == 0) {
            return partnerResponse;
        }

        if (partnerMessage.equals("force")) {
            partnerResponse = partnerName + getForceMessage();
        }

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

        String msg = ".\n\nSimple Bot for Sametime. \n\n Please send message: force";

        return msg;
    }

    private String getForceMessage() {

        String msg = ".... May the Force be with you.";//(String) messages.getProperty("simplebot.chat.WelcomeMessage", "");

        return msg;
    }

    private void init(String[] args) throws FileNotFoundException, IOException {

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
}
