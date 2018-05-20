package webcrawler;

public class WebCrawler {

	public static void main(String args[]) throws Exception {

		WebPage crawler = new WebPage(args[0]);
		int maxCrawlDepth = Integer.parseInt(args[1]);
		String destinationFolder = args[2];

		crawler.crawl(maxCrawlDepth);

		DownloadService.getService().downloadElements(crawler.getFiles(), destinationFolder);
        DownloadService.getService().downloadElements(crawler.getImages(), destinationFolder);
	}
}
