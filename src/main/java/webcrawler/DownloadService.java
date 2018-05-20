package webcrawler;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

public class DownloadService {

    private static final Logger LOG = Logger.getLogger(DownloadService.class.getName());
    private static DownloadService dr = null;

    public DownloadService() {}

    public static DownloadService getService() {
        if (dr == null) {
            dr = new DownloadService();
        }
        return dr;
    }

    private void download(WebElement webElement, String destination) {
        InputStream inputStream;
        String elem = webElement.getElementName();
        int i = elem.lastIndexOf('/', elem.length());
        String subString = elem.substring(i + 1, elem.length());

        try {
            URL url = new URL(elem);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Crawlerbot/1.0");
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = connection.getInputStream();

                FileOutputStream fos = new FileOutputStream(destination + subString);
                OutputStream outputStream = new BufferedOutputStream(fos);

                int reader;
                while ((reader = inputStream.read()) != -1) {
                    outputStream.write(reader);
                }

                outputStream.close();
                inputStream.close();
            }

        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    /**
     * Download a collection of web elements to the specified destination.
     *
     * @param webElements
     * @param destination
     */
    public void downloadElements(List<WebElement> webElements, String destination) {
        webElements.parallelStream().forEach(webElement -> download(webElement, destination));
    }
}
