package tech.asmussen.auth.misc;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import tech.asmussen.auth.core.UltraAuthenticator;
import tech.asmussen.auth.util.Utility;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

public class QR extends Utility {
	
	public static void generateCode(String fileName, String value, int size) {
		
		try {
			
			File file = new File(getPlugin().getDataFolder() + "/QRs/", fileName + ".png");
			
			Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<>();
			
			hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
			
			QRCodeWriter qrCodeWriter = new QRCodeWriter();
			
			BitMatrix byteMatrix = qrCodeWriter.encode(value, BarcodeFormat.QR_CODE, size, size, hintMap);
			// Make the BufferedImage that are to hold the QRCode
			int matrixWidth = byteMatrix.getWidth();
			int matrixHeight = byteMatrix.getHeight();
			
			BufferedImage image = new BufferedImage(matrixWidth, matrixHeight, BufferedImage.TYPE_INT_RGB);
			
			image.createGraphics();
			
			Graphics2D graphics = (Graphics2D) image.getGraphics();
			
			graphics.setColor(Color.WHITE);
			graphics.fillRect(0, 0, matrixWidth, matrixHeight);
			// Paint and save the image using the ByteMatrix
			graphics.setColor(Color.BLACK);
			
			for (int i = 0; i < matrixWidth; i++) {
				
				for (int j = 0; j < matrixWidth; j++) {
					
					if (byteMatrix.get(i, j)) {
						
						graphics.fillRect(i, j, 1, 1);
					}
				}
			}
			
			file.mkdirs();
			
			ImageIO.write(image, "PNG", file);
			
		} catch (IOException | WriterException e) {
			
			e.printStackTrace();
		}
	}
}
