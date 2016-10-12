import java.io.Serializable;

public class FileObj  implements Serializable  {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String filePath;

    private long fileLength;

    public FileObj(String filep, long len) {
        filePath = filep;
        fileLength = len;

    }

    public long getFileLength() {

        return fileLength;
    }

    public String getFilePath() {
        return filePath;
    }

}
