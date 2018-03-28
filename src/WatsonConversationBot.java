
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.ebasso.SametimeBot;
import com.lotus.sametime.im.Im;

import com.ibm.watson.developer_cloud.conversation.v1.Conversation;
import com.ibm.watson.developer_cloud.conversation.v1.model.Context;
import com.ibm.watson.developer_cloud.conversation.v1.model.InputData;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageOptions;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ebasso
 */
public class WatsonConversationBot extends SametimeBot {

    private static final String CLASS_NAME = WatsonConversationBot.class.getName();
    private static String INIFILE = CLASS_NAME + ".properties";

    private static Properties config;
    private static boolean debug;

    private static Conversation service = new Conversation("2018-02-16");
    private static String conversationWorkspaceId;
    private static Map<String, Object> conversationContexts = new HashMap<String, Object>();

    public static void main(String[] args) {

        try {
            System.out.println(CLASS_NAME);
            WatsonConversationBot bot = new WatsonConversationBot();

            bot.init(args);
            String sametimeHostname = (String) config.getProperty("sametime.hostname");
            String sametimeUsername = (String) config.getProperty("sametime.username");
            String sametimePassword = (String) config.getProperty("sametime.password");

            String conversationUsername = (String) config.getProperty("conversation.username");
            String conversationPassword = (String) config.getProperty("conversation.password");
            service.setUsernameAndPassword(conversationUsername, conversationPassword);
            conversationWorkspaceId = (String) config.getProperty("conversation.workspaceid");

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

        partnerResponse = getConversation(partnerName, partnerMessage);

        writeToConsole("doTextReceived: parterName: \"" + partnerName + "\" partnerResponse: \"" + partnerResponse + "\"");

        writeToConsole("doTextReceived", "end");
        return partnerResponse;
    }

    @Override
    public String doWelcomeMessage(String partnerName) {
        writeToConsole("doWelcomeMessage", "start");

        String partnerResponse = getWelcomeMessage();

        try {

            InputData input = new InputData.Builder("Hi").build();
            MessageOptions options = new MessageOptions.Builder(conversationWorkspaceId)
                    .input(input)
                    .build();
            MessageResponse response = service.message(options).execute();

            Context responseContext = response.getContext();
            conversationContexts.put(partnerName, responseContext);

            //System.out.println("doWelcomeMessage: response -->" + response);
            List<String> texts = response.getOutput().getText();
            for (String t : texts) {
                partnerResponse += t;
            }

        } catch (RuntimeException re) {
            partnerResponse = "Could not contact Translation Services. Please try again later";
            re.printStackTrace();
        }

        writeToConsole("doWelcomeMessage", "end");
        return partnerResponse;
    }

    private String getConversation(String partnerName, String message) {

        String partnerResponse = "";

        try {
            Context context = (Context) conversationContexts.get(partnerName);
            InputData input = new InputData.Builder(message).build();
            MessageOptions options = new MessageOptions.Builder(conversationWorkspaceId)
                    .input(input)
                    .context(context)
                    .build();
            MessageResponse response = service.message(options).execute();
            //System.out.println("getConversation: response -->" + response);
            List<String> texts = response.getOutput().getText();
            for (String t : texts) {
                partnerResponse += t;
            }

        } catch (RuntimeException re) {
            partnerResponse = "Could not contact Translation Services. Please try again later";
            re.printStackTrace();
        }

        return partnerResponse;
    }

    private String getWelcomeMessage() {

        String msg = ".\n\nSametime Bot: Watson Conversation.\n\n";

        return msg;
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
