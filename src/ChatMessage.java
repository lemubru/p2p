import java.io.*;

/**
 * This class represent a chatmessage sent between clients.
 * @author frank
 *
 */
public class ChatMessage implements Serializable {

    static final int USERSONLINE = 0, MESSAGE = 1, ERROR = 2, SUCCESS = 3, DOWNLOADREQUEST = 4, DOWNLOADFILE = 10;

    static final int SEARCH = 5, SEARCH_RESULT = 6, CORRECTKEY = 90, UNKNOWNKEY = 444;
    protected static final long serialVersionUID = 1112122200L;

    private String message;
    private String userList[];
    private String searchResults[];
    private int type;
    private int port;
    private byte[] encryptedMsg;


    ChatMessage(int type, String message, int port) {
        this.type = type;
        this.message = message;
        this.port = port;
    }

    ChatMessage(int type, String[] searchResults, int port) {
        this.type = type;
        this.searchResults = searchResults;
        this.port = port;
    }


    ChatMessage(int type, String message) {
        this.type = type;
        this.message = message;
    }
    ChatMessage(int type, String message, byte[] enc) {
        this.type = type;
        this.encryptedMsg = enc;
        this.message = message;
    }

    public byte[] getEncryptedMsg() {
        return encryptedMsg;
    }

    ChatMessage(int type, String[] userList) {
        this.type = type;
        this.userList = userList;
    }
    public String[] getUserList() {
        return userList;
    }

    int getType() {
        return type;
    }

    public String[] getSearchResults() {
        return searchResults;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setMessage(String msg) {
        this.message = msg;
    }
    public  String getMessage() {
        return message;
    }

    int getPort() {
        return port;

    }
}