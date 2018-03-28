
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.ebasso.SametimeBot;

import com.ibm.watson.developer_cloud.language_translator.v2.LanguageTranslator;
import com.ibm.watson.developer_cloud.language_translator.v2.model.TranslateOptions;
import com.ibm.watson.developer_cloud.language_translator.v2.model.Translation;
import com.ibm.watson.developer_cloud.language_translator.v2.model.TranslationResult;
import com.ibm.watson.developer_cloud.language_translator.v2.util.Language;
import com.lotus.sametime.im.Im;
import java.util.List;

/**
 *
 * @author ebasso
 */
public class WatsonLanguageTranslatorBot extends SametimeBot {

    private static final String CLASS_NAME = WatsonLanguageTranslatorBot.class.getName();
    private static String INIFILE = CLASS_NAME + ".properties";

    private static Properties config;
    private static boolean debug;

    public static void main(String[] args) {

        try {
            System.out.println(CLASS_NAME);
            WatsonLanguageTranslatorBot bot = new WatsonLanguageTranslatorBot();

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

        if (partnerMessage.length() < 7) {
            return "\nInvalid Message.";
        }

        String modeId = partnerMessage.substring(0, 5).toLowerCase();
        String message = partnerMessage.substring(6);
        //System.out.println("modeId ==> " + modeId);
        //System.out.println("message ==> " + message);
        switch (modeId) {
            case "en-es":
                im.sendText(true, "Translating ...");
                partnerResponse = getTranslation(Language.ENGLISH, Language.SPANISH, message);
                break;
            case "en-ge":
                im.sendText(true, "Translating ...");
                partnerResponse = getTranslation(Language.ENGLISH, Language.GERMAN, message);
                break;
            case "en-pt":
                im.sendText(true, "Translating ...");
                partnerResponse = getTranslation(Language.ENGLISH, Language.PORTUGUESE, message);
                break;
            case "es-en":
                im.sendText(true, "Translating ...");
                partnerResponse = getTranslation(Language.SPANISH, Language.ENGLISH, message);
                break;
            default:
                partnerResponse = "Mode Incorrect";
                break;
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

        String msg = ".\n\nSametime Bot: Watson Language Translator."
                + "\nPlease send as: <mode> <message>"
                + "\n\nExample:"
                + "\nen-es Hello World!"
                + "\n\nAvailable Options"
                + "\nen-es: English to Spanish"
                + "\nen-ge: English to Germany"
                + "\nen-pt: English to Portuguese"
                + "\nes-en: Spanish to Engislh";

        return msg;
    }

    private String getTranslation(String sourceLang, String targetLang, String message) {

        String username = (String) config.getProperty("languagetranslator.username");
        String password = (String) config.getProperty("languagetranslator.password");
        String translationText = "";

        LanguageTranslator service = new LanguageTranslator();
        service.setUsernameAndPassword(username, password);

        TranslateOptions translateOptions = new TranslateOptions.Builder()
                .addText(message)
                .source(sourceLang)
                .target(targetLang)
                .build();

        try {
            TranslationResult translationResult = service.translate(translateOptions).execute();
            List<Translation> translations = translationResult.getTranslations();
            for (Translation t : translations) {
                translationText += t.getTranslation();
            }

        } catch (RuntimeException re) {
            translationText = "Could not contact Translation Services. Please try again later";
            re.printStackTrace();
        }

        return translationText;
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
}
