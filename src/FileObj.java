import java.io.Serializable;

public class FileObj  implements Serializable  {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String filePath;
<<<<<<< HEAD
    private int fileLength;

    public FileObj(String filep, int len) {
        filePath = filep;
        fileLength = len;

    }

    public int getFileLength() {
=======
    private long fileLength;

    public FileObj(String filep, long len) {
        filePath = filep;
        fileLength = len;

    }

    public long getFileLength() {
>>>>>>> branch 'master' of https://Shaun_de_Jager@bitbucket.org/frankdp1993/cs354_p2p.git
        return fileLength;
    }

    public String getFilePath() {
        return filePath;
    }

}
