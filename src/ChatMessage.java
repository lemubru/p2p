import java.io.*;

public class ChatMessage implements Serializable {

    static final int USERSONLINE = 0, MESSAGE = 1, ERROR = 2, SUCCESS = 3, DOWNLOADREQUEST = 4;
    protected static final long serialVersionUID = 1112122200L;

    private String message;
    private String userList[];
    private int type;

    ChatMessage(int type, String message) {
        this.type = type;
        this.message = message;
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

    public void setType(int type) {
        this.type = type;
    }

    public void setMessage(String msg) {
        this.message = msg;
    }
    public  String getMessage() {
        return message;
    }
}