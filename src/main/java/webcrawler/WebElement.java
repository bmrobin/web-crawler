package webcrawler;

public class WebElement {

    private String elementName;

    public WebElement(String name) {
        this.elementName = name;
    }

    public String getElementName() {
        return elementName;
    }
}
