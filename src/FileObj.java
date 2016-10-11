import java.io.Serializable;

public class FileObj  implements Serializable  {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String filePath;
    private int fileLength;

    public FileObj(String filep, int len) {
        filePath = filep;
        fileLength = len;

    }

    public int getFileLength() {
        return fileLength;
    }

    public String getFilePath() {
        return filePath;
    }

}
