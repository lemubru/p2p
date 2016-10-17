import java.io.Serializable;

public class FileObj  implements Serializable  {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String filePath;

    private long fileLength;
    private String key;

    public FileObj(String filep, long len, String k) {
        filePath = filep;
        fileLength = len;
        key = k;

    }

    public String getKey() {
        return key;
    }

    public long getFileLength() {

        return fileLength;
    }

    public String getFilePath() {
        return filePath;
    }

}
