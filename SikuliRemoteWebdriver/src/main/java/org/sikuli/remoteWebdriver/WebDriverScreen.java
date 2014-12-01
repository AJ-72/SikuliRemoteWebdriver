package org.sikuli.remoteWebdriver;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.sikuli.api.Screen;

public class WebDriverScreen implements Screen {

	public RemoteWebDriver driver;
	final private Dimension size;
	private int newY=0;
	private int newHeight=0;
	public WebDriverScreen(SikuliRemoteWebDriver inputDriver) throws IOException{
		driver = (RemoteWebDriver)inputDriver;
		driver.manage().window().maximize();
		WebDriver tempDriver = new Augmenter().augment(driver);
		File screenshotFile = ((TakesScreenshot) tempDriver)
				.getScreenshotAs(OutputType.FILE);
		BufferedImage b = ImageIO.read(screenshotFile);
		size = new Dimension(b.getWidth(),b.getHeight());
		newHeight=size.height;
	}
	
	BufferedImage crop(BufferedImage src, int x, int y, int width, int height){
		BufferedImage dest = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		Graphics g = dest.getGraphics();
		g.drawImage(src, 0, 0, width, height, x, y, x + width, y + height, null);
		g.dispose();
		return dest;
	}
	
	public BufferedImage getScreenshot(int x, int y, int width, int height) {
		File screenshotFile = driver.getScreenshotAs(OutputType.FILE);
		try {
			FileUtils.copyFile(screenshotFile,  new File("chrome.jpg"));
			BufferedImage full = ImageIO.read(screenshotFile);
			BufferedImage cropped = crop(full, x,newY, width, newHeight);
			ImageIO.write(cropped, "jpg", new File("chromecrop.jpg"));
			return cropped;
		} catch (IOException e) {
		}
		return null;
	}

	public Dimension getSize() {
		return size;
	}
	
	
	/**
	 * Set the end height for cropping
	 * @param heightToCrop
	 */
	public void setNewHeightForCropping(int heightToCrop){
		newHeight=heightToCrop;
	}
	
	/**
	 * Set the starting height for cropping the image
	 * @param yToCrop
	 */
	public void setY(int yToCrop){
		newY=yToCrop;
	}


}
