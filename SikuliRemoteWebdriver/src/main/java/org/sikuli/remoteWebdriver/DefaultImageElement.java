package org.sikuli.remoteWebdriver;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class DefaultImageElement implements ImageElement {
	
	private void executeJavaScriptMouseAction(String type, int x, int y){
		((JavascriptExecutor) driver).executeScript("var evt = document.createEvent('MouseEvents');"
				+ " evt.initMouseEvent('" + type + "',true, true, window, 0, 0, 0,"
				+  x + "," + y + ","
				+ " false, false, false, false, 0,null);" + 
				" arguments[0].dispatchEvent(evt);", containerWebElement);
	}
	
	public void click() {
		executeJavaScriptMouseAction("click", x + width/2, y + height/2);
	}
	
	public void doubleClick() {
		executeJavaScriptMouseAction("dblclick", x + width/2, y + height/2);
	}

	final private WebDriver driver;
	final private WebElement containerWebElement;	
	final private int x;
	final private int y;
	final private int width;
	final private int height;
	
	DefaultImageElement(WebDriver driver, WebElement containerWebElement, int x, int y, int width, int height){
		this.driver = driver;
		this.containerWebElement = containerWebElement;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public WebElement getAsWebElement(){
		return containerWebElement;
	}
}
