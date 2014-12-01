package org.sikuli.remoteWebdriver;


import java.awt.Rectangle;
import java.io.IOException;
import java.net.URL;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.sikuli.api.DefaultScreenRegion;
import org.sikuli.api.ImageTarget;
import org.sikuli.api.Relative;
import org.sikuli.api.ScreenLocation;
import org.sikuli.api.ScreenRegion;
import org.sikuli.api.visual.element.ImageElement;
public class SikuliRemoteWebDriver extends RemoteWebDriver
{

    	private static final int DEFAULT_WAIT_TIMEOUT_MSECS = 5000;
    	ScreenRegion webdriverRegion;
    	WebDriverScreen webDriverScreen;
    	/**
    	 * Constructor that takes only a desired capabilities as input. We assume that a remote server is running
    	 * in localhost:4444
    	 * @param dc
    	 */
    	public SikuliRemoteWebDriver(DesiredCapabilities dc){
    		super(dc);
    		try {
    			webDriverScreen = new WebDriverScreen(this);
    		} catch (IOException e1) {
    			e1.printStackTrace();
    			throw new RuntimeException("unable to initialize sikuli remote driver");
    			
    		}
    		webdriverRegion = new DefaultScreenRegion(webDriverScreen);
    	}

    	/**
    	 * Constructor which takes hub and browser capabilities as input
    	 * @param hubURL 
    	 * @param browserCap - Desired capability
    	 */
    	public SikuliRemoteWebDriver(URL hubURL, DesiredCapabilities browserCap){
    		super(hubURL,browserCap);
    		
    		try {
    			webDriverScreen = new WebDriverScreen(this);
    		} catch (IOException e1) {
    			e1.printStackTrace();
    			throw new RuntimeException("unable to initialize sikuli remote driver");
    		}
    		webdriverRegion = new DefaultScreenRegion(webDriverScreen);
    	}

    	/**
    	 * Based on the input provided, find the element in the page using javascript elementFromPoint method.
    	 * @param x
    	 * @param y
    	 * @return {@link WebElement}
    	 */
    	public WebElement findElementByLocation(int x, int y){
    		return (WebElement) ((JavascriptExecutor) this).executeScript("return document.elementFromPoint(" + x + "," + y + ")");
    	}
    	
    	/**
    	 * To get the current height of the document. 
    	 * @return
    	 */
    	private long getDocumentHeight(){
    		return (Long) ((JavascriptExecutor) this).executeScript("return document.body.clientHeight;");
    	}
    	
    	/**
    	 * Scroll the page by using javascript
    	 * @param y
    	 */
    	private void scrollPage(int y){
    		((JavascriptExecutor) this).executeScript("window.scrollBy(0,"+y+");");
    	}
    	
    	/**
    	 * Find the specified image in parameter in the current view port of the web browser. If the input image cannot be found in the current view port
    	 * we will scroll do a page down and check again whether image is in available in view port. This continues until the end of the page.
    	 *  
    	 * @param imageUrl
    	 * @return {@link ImageElement} if element is found. {@link RuntimeException} if element could not be found; 
    	 * @throws ImageNotFoundException 
    	 * 
    	 */
    	public DefaultImageElement findImageElement(URL imageUrl) {
    		ImageTarget target = new ImageTarget(imageUrl);
    		ScreenRegion imageRegion=findImage(target);
    		if(imageRegion!=null){
    			ScreenLocation center = imageRegion.getCenter();
    			WebElement foundWebElement = findElementByLocation(center.getX(), center.getY());
    			Rectangle r = imageRegion.getBounds();
    			return (new DefaultImageElement(this, foundWebElement,r.x, r.y, r.width, r.height));
    		}
    		else{
    			throw new ImageNotFoundException("Element matching the image was not found in the current page");
    		}
    	}
    	
    	
    	/**
    	 * To find the target image within the current page
    	 * @param target
    	 * @return
    	 */
    	private ScreenRegion findImage(ImageTarget target){
    		
    		//Get the entire height of the document to scroll through.
    		int heightOfDoc =(int)getDocumentHeight();
    		
    		//Get the current height of the browser. This is the height we need 
    		int heightOfBrowser=webDriverScreen.getSize().height;
    		
    		//To know the value of height that we are currently at
    		int currentHeight=heightOfBrowser;
    		int y=0;
    		
    		boolean foundImage=false;
    		ScreenRegion imageRegion=null;

    		/**
    		 * Go through the loop until we scroll through the entire page or till we find the target image
    		 */
    		while((currentHeight<heightOfDoc)&&(!foundImage)){
    			//Sikuli method to find the target image in the current region
    			imageRegion = webdriverRegion.wait(target, DEFAULT_WAIT_TIMEOUT_MSECS);
    			
    			if (imageRegion != null){
    				foundImage=true;
    			}
    			/**
    			 * scroll the page. Set the co-ordinates to new height
    			 */
    			else{
    				scrollPage(heightOfBrowser);
    				/**
    				 * Since chrome doesn't provide a full page screenshot we would need to set the x and y positions to be cropped after each scroll 
    				 */
    				if(!this.getCapabilities().getBrowserName().contains("chrome")){
    					currentHeight+=heightOfBrowser;
    					y+=heightOfBrowser;
    					webDriverScreen.setNewHeightForCropping(currentHeight);
    					webDriverScreen.setY(y);
    				}
    			}
    		}
    		return imageRegion;
    	}
    	
    	/**
    	 * Wait for an image to appear in the page until timeout period
    	 * @param imageUrl
    	 * @param timeout
    	 * @return
    	 */
    	public ScreenRegion waitForImage(URL imageUrl,int timeout){
    		ImageTarget target = new ImageTarget(imageUrl);
    		webdriverRegion.wait(target, timeout);
    		return webdriverRegion.find(target);
    	}
    	
    	public DefaultImageElement findImageElementRelativeTo(URL imgToFind, URL relImg, int offsetToMove){
    		ImageTarget target = new ImageTarget(relImg);
    		ScreenRegion relativeImageRegion ;
    		ScreenRegion imageRegion=webdriverRegion;
    		int heightOfDoc =(int)getDocumentHeight();
    		int heightOfBrowser=webDriverScreen.getSize().height-100;
    		int currentHeight=heightOfBrowser;
    		int y=0;
    		boolean foundImage=false;
    		while((currentHeight<heightOfDoc)&&(!foundImage)){
    			relativeImageRegion= webdriverRegion.wait(target,DEFAULT_WAIT_TIMEOUT_MSECS);
    			if(relativeImageRegion!=null)
    				imageRegion =Relative.to(relativeImageRegion).below(offsetToMove).getScreenRegion();
    			else
    				imageRegion=null;
    			
    			if (imageRegion != null){
    				foundImage=true;
    			}else{
    				scrollPage(heightOfBrowser);
    				if(!this.getCapabilities().getBrowserName().contains("chrome")){
    				currentHeight+=heightOfBrowser;
    				y+=heightOfBrowser;
    				webDriverScreen.setNewHeightForCropping(currentHeight);
    				webDriverScreen.setY(y);
    				}
    			}
    			
    		}
    		if(foundImage){
    			ScreenLocation center = imageRegion.getCenter();
    			WebElement foundWebElement = findElementByLocation(center.getX(), center.getY());
    			Rectangle r = imageRegion.getBounds();
    			return new DefaultImageElement(this, foundWebElement, r.x,r.y,r.width,r.height);
    		}
    		
    		else{
    			return null;
    		}
    	}
    	

    	public boolean isImagePresentInPage(URL imageToCheck){
    		ImageTarget target = new ImageTarget(imageToCheck);
    		ScreenRegion outputImage = findImage(target);
    		if(outputImage==null)
    			return false;
    		else
    			return true;
    	}
    	
}
