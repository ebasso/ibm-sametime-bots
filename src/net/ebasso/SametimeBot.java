package net.ebasso;

import com.lotus.sametime.community.CommunityService;
import com.lotus.sametime.community.LoginEvent;
import com.lotus.sametime.community.LoginListener;
import com.lotus.sametime.core.comparch.DuplicateObjectException;
import com.lotus.sametime.core.comparch.STSession;
import com.lotus.sametime.core.constants.ImTypes;
import com.lotus.sametime.im.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.Calendar;
import java.util.Vector;


public abstract class SametimeBot implements ImListener, ImServiceListener, LoginListener {

    private static final String CLASS_NAME = SametimeBot.class.getName();
    public STSession session;
    public CommunityService communityService;
    public InstantMessagingService imService;
    protected Vector m_imOpened = new Vector();

    private String sametimeServer;
    private String sametimeUsername;
    private String sametimePassword;
    public Calendar dateOfLogin;
    public String chatWelcomeMessage;

    public void login(String sametimeServer, String username, String password) {
        writeToConsole("login","start");
        //Create a new session with a unique name.  If the name is not unique, an exception will be thrown.
        java.util.Date initTime = new java.util.Date();
        try {
            this.sametimeServer = sametimeServer;
            this.sametimeUsername = username;
            this.sametimePassword = password;
            session = new STSession(CLASS_NAME + String.valueOf(initTime.getTime()));
        } catch (DuplicateObjectException doe) {
            doe.printStackTrace();
            return;
        }

        //Load only the non-graphical components for this session.
        session.loadSemanticComponents();

        //Start the session and all it's components
        session.start();
        communityService = (CommunityService) session.getCompApi(CommunityService.COMP_NAME);
        communityService.addLoginListener(this);

        //If the connection is lost, then try to reconnect once a minute for 24 hours.
        communityService.enableAutomaticReconnect(1440, 6000);

        writeToConsole("Logging in to " + this.sametimeServer + " ...");
        communityService.loginByPassword(this.sametimeServer, this.sametimeUsername, this.sametimePassword);
        writeToConsole("login","end");
    }

    /**
     * Logout from the CommunityService, stop the STSession, and unload the
     * session.
     */
    public void logout() {
         writeToConsole("logout","start");
        if (communityService != null) {
            communityService.logout();
        }

        if (session != null) {
            session.stop();
            session.unloadSession();
        }

        writeToConsole("Shutdown complete.");
        writeToConsole("logout","end");
    }

    /**
     * Indicates that we successfully logged in to the Sametime Community.
     *
     * @param event - The event object.
     */
    public void loggedIn(LoginEvent event) {
        writeToConsole("loggedIn","start");
        writeToConsole("Login complete.");

        //Open the InstantMessagingService.
        imService = (InstantMessagingService) session.getCompApi(InstantMessagingService.COMP_NAME);

        //Register the service as a "chat" type only.
        imService.registerImType(ImTypes.IM_TYPE_CHAT);

        //Add a listener for the new InstantMessagingService service.
        imService.addImServiceListener(this);

        //Display information to the console.
        writeToConsole("Waiting for messages...");
        dateOfLogin = Calendar.getInstance();
        System.out.println("Ctrl-C to Kill");
        writeToConsole("loggedIn","end");
    }

    /**
     * Indicates that we successfully logged out of the Sametime Community, or a
     * login request was refused.
     *
     * @param event - The event object.
     */
    public void loggedOut(LoginEvent event) {
        writeToConsole("loggedOut","start");
        writeToConsole("No connection with server, reason: 0x" + Integer.toHexString(event.getReason()));
        System.out.println("reason ]" + event.getReason());
        if (event.getReason() != 0) {

            if (event.getReason() == -2147483119) {
                writeToConsole("Invalid Username and Password");
                System.exit(1);
            } else {
                //Login to the Sametime server with username and password.  This is the Sametime Bot's information.
                writeToConsole("Logging in to " + sametimeServer + " ...");
                communityService.loginByPassword(sametimeServer, sametimeUsername, sametimePassword);
            }

        }
        writeToConsole("loggedOut","end");
    }

    /**
     * The IM session was closed. This code for "imClosed" is from the IBM (ISBN
     * 0738499080) Lotus Instant Messaging/Web Conferencing (): Building
     * Sametime Enabled Applications.
     *
     * @param event - The event object.
     */
    public void imClosed(ImEvent event) {
        writeToConsole("imClosed","start");
        Im im = event.getIm();
        Im currentIm = null;
        for (int i = 0; i < m_imOpened.size(); i++) {
            currentIm = (Im) m_imOpened.elementAt(i);
            if (currentIm.getPartner().getId().equals(im.getPartner().getId())) {
                m_imOpened.removeElement(im);
                im.close(0);
                im.removeImListener(this);
                break;
            }
        }
        writeToConsole("imClosed","end");
    }

    public void imReceived(ImEvent event) {
        writeToConsole("imReceived","start");
        Im im = event.getIm();
        String partnerName = im.getPartner().getDisplayName();
        boolean imExist = false;
        
        Im currentIm = null;
        for (int i = 0; i < m_imOpened.size(); i++) {
            currentIm = (Im) m_imOpened.elementAt(i);
            if (currentIm.getPartner().getId().equals(im.getPartner().getId())) {
                imExist = true;
                im = currentIm;
                break;
            }
        }
        if (!imExist) {
            m_imOpened.addElement(im);
            im.addImListener(this);
            //if (partnerName.indexOf(" ") > 0) {
            //    partnerName = partnerName.substring(0, partnerName.indexOf(" "));
            //}
            String partnerResponse = this.doWelcomeMessage(partnerName);
            im.sendText(true, partnerResponse);
        }
        writeToConsole("imReceived","end");
    }

    public void imOpened(ImEvent event) {
        //writeToConsole("imOpened","todo");
    }

    public void openImFailed(ImEvent event) {
        //writeToConsole("openImFailed","todo");
    }

    public void dataReceived(ImEvent event) {
        //writeToConsole("dataReceived","todo");
    }

    public void textReceived(ImEvent event){
        writeToConsole("textReceived","start");

        Im im = event.getIm();

        String partnerName = im.getPartner().getDisplayName();
        String partnerMessage = event.getText().trim();
        
        String partnerResponse = this.doTextReceived(im,partnerName, partnerMessage);
        
        if (partnerResponse.length() == 0) {
            return;
        }
        
        im.sendText(true, partnerResponse);
        writeToConsole("textReceived","end");
    }
    
    public abstract String doWelcomeMessage(String partnerName);
    
    public abstract String doTextReceived(Im im, String partnerName, String partnerMessage);

    public void writeToConsole(String message) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        System.out.println(now.format(formatter) + " " + message);
    }
    
    public void writeToConsole(String proc, String message) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        //System.out.println(now.format(formatter) + " "  + proc  + " " + message);
    }
}
