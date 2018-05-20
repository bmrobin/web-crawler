package webcrawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

public class WebPage {

    private static final Logger LOG = Logger.getLogger(WebPage.class.getName());
    public static final List<WebPage> visitedWebsites = new ArrayList<>();
    public static final List<WebElement> images = new ArrayList<>();
    public static final List<WebElement> files = new ArrayList<>();

    private String baseUrl;

    public WebPage(String newPage) {
        baseUrl = newPage;
        LOG.info("Crawling/Saving this Site - " + this.baseUrl);
    }

    public List<WebElement> getImages() {
        return images;
    }

    public List<WebElement> getFiles() {
        return files;
    }

    public void crawl(int maxCrawlDepth) throws IOException {
        // don't want to execute if maxCrawlDepth is zero
        if (maxCrawlDepth == 0) {
            return;
        }

        URL url = new URL(this.baseUrl);
        BufferedReader buffer;
        try {
            buffer = new BufferedReader(new InputStreamReader(url.openStream()));
        } catch (Exception ex) {
            System.out.println("Can't open the stream to buffer...");
            return;
        }

        String strings = buffer.readLine();
        String line = "";

        while (strings != null) {

            if (strings.contains(" href=")) {
                String subString = strings.substring(strings.indexOf("href="), strings.length());
                Scanner parser = new Scanner(subString);
                parser.useDelimiter("=\"");

                if (!parser.hasNext()) {
                    return;
                }

                try {
                    parser.next(); // skip first token
                    line = parser.next();
                } catch (Exception e) {
                    LOG.warning("No further input from Scanner...");
                }

                //create another Scanner to search for final " in the html line
                Scanner parse = new Scanner(line);
                parse.useDelimiter("\"");
                String fileName = " ";
                if (parse.hasNext()) {
                    fileName = parse.next();
                }

                if (fileName.startsWith("http")) {
                    // ensure that we haven't already searched the current site
                    if (!visitedWebsites.contains(fileName)) {
                        if (fileName.matches(".x?html?") || fileName.endsWith(".php") || fileName.endsWith(".asp") ||
                                fileName.endsWith(".net") ||
                                fileName.endsWith(".com") ||
                                fileName.endsWith(".edu") ||
                                fileName.endsWith(".org")) {
                            visitedWebsites.add(new WebPage(fileName));
                        }

                        //images can also be stored under 'href' tags
                        if (fileName.endsWith(".gif") || fileName.matches(".jpe?g") || fileName.endsWith(".png")) {
                            images.add(new WebElement(fileName));
                        }
                    }
                } else {
                    files.add(new WebElement(url.getProtocol() + ":" + fileName));
                }
            }

            if (strings.contains(" src=")) {
                String subString;
                if (strings.contains("href=")) {
                    subString = strings.substring(strings.indexOf("href="), strings.length());
                } else {
                    subString = strings.substring(strings.indexOf("src="), strings.length());
                }

                Scanner parser = new Scanner(subString);
                parser.useDelimiter("=\"");

                if (!parser.hasNext()) {
                    return;
                }

                try {
                    parser.next(); // skip first token
                    line = parser.next();
                } catch(Exception e) {
                    LOG.warning("No further input from Scanner...");
                }

                // search for final " in the html line
                Scanner parse = new Scanner(line);
                parse.useDelimiter("\"");
                String fileName = " ";
                if (parse.hasNext()) {
                    fileName = parse.next();
                }

                if (fileName.startsWith("http")) {
                    if (fileName.matches(".jpe?g") || fileName.endsWith(".gif") || fileName.endsWith(".png")) {
                        images.add(new WebElement(fileName));
                    }
                    if (fileName.matches(".x?html?") || fileName.endsWith(".php") || fileName.endsWith(".asp") ||
                            fileName.endsWith(".net") ||
                            fileName.endsWith(".com") ||
                            fileName.endsWith(".edu") ||
                            fileName.endsWith(".org")) {
                        visitedWebsites.add(new WebPage(fileName));
                    }

                    else {
                        files.add(new WebElement(fileName));
                    }
                }

                else {
                    files.add(new WebElement(url.getProtocol() + ":" + fileName));
                }
            }

            // attempt to read more into the buffer
            strings = buffer.readLine();
        }

        buffer.close();

        // recursion:
        // loop over all pages in 'visitedWebsites' and perform crawl on them
        // make sure to de-increment maxCrawlDepth each time
        for (WebPage visitedWebsite : visitedWebsites) {
            visitedWebsite.crawl(maxCrawlDepth - 1);
        }
    }

}
