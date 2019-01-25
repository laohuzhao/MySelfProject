package com.sdk4.common.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Reader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * 条形码/二维码工具类
 * 
 * @author CnJun
 *
 */
public class BarcodeUtils {
	private static Logger log = LoggerFactory.getLogger(BarcodeUtils.class);
	
	public static final String CHARSET = "UTF-8";
	public static final int IMAGE_WIDTH = 300;
	public static final int IMAGE_HEIGHT = 300;
	public static final int IMAGE_COLOR = 0xff000000;
	public static final int IMAGE_BG_COLOR = 0xfffffff;
	public static final int LOGO_IMAGE_WIDTH = 80;
	public static final int LOGO_IMAGE_HEIGHT = 80;
	public static final int LOGO_FRAME_WIDTH = 1;
	public static final int LOGO_FRAME_COLOR = 0xfffffff;
	
	// 输出图片类型
	public static enum ImageType {
		JPG, PNG, JPEG, BMP
	}
	
	public static byte[] generateQrCodeByte(String content, ImageType type, int width, int height, int color, int bgColor) {
		return generateQrCodeByte(content, type, width, height, new byte[]{}, 0, 0, 0, color, bgColor, 0);
	}
	
	public static byte[] generateQrCodeByte(String content, ImageType type, int size, int color, int bgColor) {
		return generateQrCodeByte(content, type, size, size, new byte[]{}, 0, 0, 0, color, bgColor, 0);
	}
	
	public static byte[] generateQrCodeByte(String content, ImageType type, int size, int color) {
		return generateQrCodeByte(content, type, size, size, new byte[]{}, 0, 0, 0, color, IMAGE_BG_COLOR, 0);
	}
	
	public static byte[] generateQrCodeByte(String content, ImageType type, int size) {
		return generateQrCodeByte(content, type, size, size, new byte[]{}, 0, 0, 0, IMAGE_COLOR, IMAGE_BG_COLOR, 0);
	}
	
	public static byte[] generateQrCodeByte(String content, ImageType type, int size, byte[] logo, int logoWidth, int logoHeight) {
        return generateQrCodeByte(content, type, size, size, logo, logoWidth, logoHeight, 0, IMAGE_COLOR, IMAGE_BG_COLOR, 0);
    }
	
	public static byte[] generateQrCodeByte(String content, ImageType type) {
		return generateQrCodeByte(content, type, IMAGE_WIDTH, IMAGE_HEIGHT, new byte[]{}, 0, 0, 0, IMAGE_COLOR, IMAGE_BG_COLOR, 0);
	}
	
	public static byte[] generateQrCodeByte(String content, int width, int height, int color, int bgColor) {
		return generateQrCodeByte(content, ImageType.PNG, width, height, new byte[]{}, 0, 0, 0, color, bgColor, 0);
	}
	
	public static byte[] generateQrCodeByte(String content, int size, int color, int bgColor) {
		return generateQrCodeByte(content, ImageType.PNG, size, size, new byte[]{}, 0, 0, 0, color, bgColor, 0);
	}
	
	public static byte[] generateQrCodeByte(String content, int size, int color) {
		return generateQrCodeByte(content, ImageType.PNG, size, size, new byte[]{}, 0, 0, 0, color, IMAGE_BG_COLOR, 0);
	}
	
	public static byte[] generateQrCodeByte(String content, int size) {
		return generateQrCodeByte(content, ImageType.PNG, size, size, new byte[]{}, 0, 0, 0, IMAGE_COLOR, IMAGE_BG_COLOR, 0);
	}
	
	public static byte[] generateQrCodeByte(String content) {
		return generateQrCodeByte(content, ImageType.PNG, IMAGE_WIDTH, IMAGE_HEIGHT, new byte[]{}, 0, 0, 0, IMAGE_COLOR, IMAGE_BG_COLOR, 0);
	}
	
	public static byte[] generateQrCodeByte(String content, int size, File logoFile, int logoSize) {
		return generateQrCodeByte(content, ImageType.PNG, size, size, logoFile, logoSize, logoSize, LOGO_FRAME_WIDTH, IMAGE_COLOR, IMAGE_BG_COLOR, LOGO_FRAME_COLOR);
	}
	
	public static byte[] generateQrCodeByte(String content, int size, byte[] logoFile, int logoSize) {
		return generateQrCodeByte(content, ImageType.PNG, size, size, logoFile, logoSize, logoSize, LOGO_FRAME_WIDTH, IMAGE_COLOR, IMAGE_BG_COLOR, LOGO_FRAME_COLOR);
	}
	
	public static byte[] generateQrCodeByte(String content, int size, byte[] logoFile, int logoSize, int color, int bgColor) {
		return generateQrCodeByte(content, ImageType.PNG, size, size, logoFile, logoSize, logoSize, LOGO_FRAME_WIDTH, color, bgColor, bgColor);
	}
	
	/**
	 * 生成二维码
	 * 
	 * @param content 二维码内容
	 * @param type 生成二维码图片类型
	 * @param width 图片宽度
	 * @param height 图片高度
	 * @param logoFile Logo图片
	 * @param logoWidth Logo宽度
	 * @param logoHeight Logo高度
	 * @param logoFrameWidth Logo边框宽度
	 * @param color 二维码颜色
	 * @param bgColor 二维码背景
	 * @param logoFrameColor Logo边框颜色
	 * @return
	 */
	public static byte[] generateQrCodeByte(String content, ImageType type, int width, int height, File logoFile, int logoWidth, int logoHeight, int logoFrameWidth, int color, int bgColor, int logoFrameColor) {
		byte[] buff = null;
		
		try {
			MultiFormatWriter writer = new MultiFormatWriter();
			BufferedImage image = _genQrcode(writer, content, width, height, logoFile, logoWidth, logoHeight, logoFrameWidth, color, bgColor, logoFrameColor);
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			ImageIO.write(image, type.name(), output);
			buff = output.toByteArray();
			output.flush();
			output.close();
		} catch (IOException e) {
			log.error("generate qrcode error: {}", e.getMessage());
		} catch (WriterException e) {
			log.error("generate qrcode error: {}", e.getMessage());
		}
		
		return buff;
	}
	
	public static byte[] generateQrCodeByte(String content, ImageType type, int width, int height, byte[] logoFile, int logoWidth, int logoHeight, int logoFrameWidth, int color, int bgColor, int logoFrameColor) {
		byte[] buff = null;
		
		try {
			MultiFormatWriter writer = new MultiFormatWriter();
			BufferedImage image = _genQrcode(writer, content, width, height, logoFile, logoWidth, logoHeight, logoFrameWidth, color, bgColor, logoFrameColor);
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			ImageIO.write(image, type.name(), output);
			buff = output.toByteArray();
			output.flush();
			output.close();
		} catch (IOException e) {
			log.error("generate qrcode error: {}", e.getMessage());
		} catch (WriterException e) {
			log.error("generate qrcode error: {}", e.getMessage());
		}
		
		return buff;
	}
	
	public static boolean generateQrCode(File outImagefile, String content, int width, int height, int color, int bgColor) {
		return generateQrCode(outImagefile, content, ImageType.PNG, width, height, new byte[]{}, 0, 0, 0, color, bgColor, 0);
	}
	
	public static boolean generateQrCodeByte(File outImagefile, String content, int size, int color, int bgColor) {
		return generateQrCode(outImagefile, content, ImageType.PNG, size, size, new byte[]{}, 0, 0, 0, color, bgColor, 0);
	}
	
	public static boolean generateQrCode(File outImagefile, String content, int size, int color) {
		return generateQrCode(outImagefile, content, ImageType.PNG, size, size, new byte[]{}, 0, 0, 0, color, IMAGE_BG_COLOR, 0);
	}
	
	public static boolean generateQrCode(File outImagefile, String content, int size) {
		return generateQrCode(outImagefile, content, ImageType.PNG, size, size, new byte[]{}, 0, 0, 0, IMAGE_COLOR, IMAGE_BG_COLOR, 0);
	}
	
	public static boolean generateQrCode(File outImagefile, String content) {
		return generateQrCode(outImagefile, content, ImageType.PNG, IMAGE_WIDTH, IMAGE_HEIGHT, new byte[]{}, 0, 0, 0, IMAGE_COLOR, IMAGE_BG_COLOR, 0);
	}
	
	public static boolean generateQrCode(File outImagefile, String content, File logoFile) {
		return generateQrCode(outImagefile, content, ImageType.PNG, IMAGE_WIDTH, IMAGE_HEIGHT, logoFile, 0, 0, 0, IMAGE_COLOR, IMAGE_BG_COLOR, 0);
	}
	
	public static boolean generateQrCode(File outImagefile, String content, int size, File logoFile, int logoSize) {
		return generateQrCode(outImagefile, content, ImageType.PNG, size, size, logoFile, logoSize, logoSize, LOGO_FRAME_WIDTH, IMAGE_COLOR, IMAGE_BG_COLOR, LOGO_FRAME_COLOR);
	}
	
	public static boolean generateQrCode(File outImagefile, String content, byte[] logoFile) {
		return generateQrCode(outImagefile, content, ImageType.PNG, IMAGE_WIDTH, IMAGE_HEIGHT, logoFile, 0, 0, 0, IMAGE_COLOR, IMAGE_BG_COLOR, 0);
	}
	
	public static boolean generateQrCode(File outImagefile, String content, int size, byte[] logoFile, int logoSize) {
		return generateQrCode(outImagefile, content, ImageType.PNG, size, size, logoFile, logoSize, logoSize, LOGO_FRAME_WIDTH, IMAGE_COLOR, IMAGE_BG_COLOR, LOGO_FRAME_COLOR);
	}
	
	public static boolean generateQrCode(File outImagefile, String content, ImageType type, int width, int height, int color, int bgColor) {
		return generateQrCode(outImagefile, content, type, width, height, new byte[]{}, 0, 0, 0, color, bgColor, 0);
	}
	
	public static boolean generateQrCode(File outImagefile, String content, ImageType type, int size, int color, int bgColor) {
		return generateQrCode(outImagefile, content, type, size, size, new byte[]{}, 0, 0, 0, color, bgColor, 0);
	}
	
	public static boolean generateQrCodeByte(File outImagefile, String content, ImageType type, int size, int color) {
		return generateQrCode(outImagefile, content, type, size, size, new byte[]{}, 0, 0, 0, color, IMAGE_BG_COLOR, 0);
	}
	
	public static boolean generateQrCode(File outImagefile, String content, ImageType type, int size) {
		return generateQrCode(outImagefile, content, type, size, size, new byte[]{}, 0, 0, 0, IMAGE_COLOR, IMAGE_BG_COLOR, 0);
	}
	
	public static boolean generateQrCode(File outImagefile, String content, ImageType type) {
		return generateQrCode(outImagefile, content, type, IMAGE_WIDTH, IMAGE_HEIGHT, new byte[]{}, 0, 0, 0, IMAGE_COLOR, IMAGE_BG_COLOR, 0);
	}
	
	public static boolean generateQrCode(File outImagefile, String content, ImageType type, int size, File logoFile, int logoSize, int logoFrameWidth, int color, int bgColor, int logoFrameColor) {
		return generateQrCode(outImagefile, content, type, size, size, logoFile, logoSize, logoSize, logoFrameWidth, color, bgColor, logoFrameColor);
	}
	
	public static boolean generateQrCode(File outImagefile, String content, ImageType type, int size, byte[] logoFile, int logoSize, int logoFrameWidth, int color, int bgColor, int logoFrameColor) {
		return generateQrCode(outImagefile, content, type, size, size, logoFile, logoSize, logoSize, logoFrameWidth, color, bgColor, logoFrameColor);
	}
	
	/**
	 * 生成二维码
	 * 
	 * @param outImagefile 输出二维码图片文件
	 * @param content 二维码内容
	 * @param type 生成二维码图片类型
	 * @param width 图片宽度
	 * @param height 图片高度
	 * @param logoFile Logo图片
	 * @param logoWidth Logo宽度
	 * @param logoHeight Logo高度
	 * @param logoFrameWidth Logo边框宽度
	 * @param color 二维码颜色
	 * @param bgColor 二维码背景
	 * @param logoFrameColor Logo边框颜色
	 * @return
	 */
	public static boolean generateQrCode(File outImagefile, String content, ImageType type, int width, int height, File logoFile, int logoWidth, int logoHeight, int logoFrameWidth, int color, int bgColor, int logoFrameColor) {
		boolean ret = false;
		
		try {
			MultiFormatWriter writer = new MultiFormatWriter();
			BufferedImage image = _genQrcode(writer, content, width, height, logoFile, logoWidth, logoHeight, logoFrameWidth, color, bgColor, logoFrameColor);
			ImageIO.write(image, _image_type(outImagefile.getName()).name(), outImagefile);
			if (outImagefile.exists()) {
				ret = true;
			}
		} catch (IOException e) {
			log.error("generate qrcode error: {}", e.getMessage());
		} catch (WriterException e) {
			log.error("generate qrcode error: {}", e.getMessage());
		}
		
		return ret;
	}
	
	/**
	 * 生成二维码
	 * 
	 * @param outImagefile 输出二维码图片文件
	 * @param content 二维码内容
	 * @param type 生成二维码图片类型
	 * @param width 图片宽度
	 * @param height 图片高度
	 * @param logoFile Logo图片
	 * @param logoWidth Logo宽度
	 * @param logoHeight Logo高度
	 * @param logoFrameWidth Logo边框宽度
	 * @param color 二维码颜色
	 * @param bgColor 二维码背景
	 * @param logoFrameColor Logo边框颜色
	 * @return
	 */
	public static boolean generateQrCode(File outImagefile, String content, ImageType type, int width, int height, byte[] logoFile, int logoWidth, int logoHeight, int logoFrameWidth, int color, int bgColor, int logoFrameColor) {
		boolean ret = false;
		
		try {
			MultiFormatWriter writer = new MultiFormatWriter();
			BufferedImage image = _genQrcode(writer, content, width, height, logoFile, logoWidth, logoHeight, logoFrameWidth, color, bgColor, logoFrameColor);
			ImageIO.write(image, _image_type(outImagefile.getName()).name(), outImagefile);
			if (outImagefile.exists()) {
				ret = true;
			}
		} catch (IOException e) {
			log.error("generate qrcode error: {}", e.getMessage());
		} catch (WriterException e) {
			log.error("generate qrcode error: {}", e.getMessage());
		}
		
		return ret;
	}
	
	private static ImageType _image_type(String name) {
		int n = name.lastIndexOf('.');
		if (n > 0) {
			name = name.substring(n + 1);
		}
		ImageType type = ImageType.valueOf(name.toUpperCase());
		if (type == null) {
			type = ImageType.PNG;
		}
		return type;
	}
	
	private static BufferedImage _genQrcode(MultiFormatWriter writer, String content, int width, int height, 
			File logoImage, int logoWidth, int logoHeight, int logoFrameWidth, int color, int bgColor, int frameColor) throws WriterException, IOException {
		BufferedImage logoScaleImage = _scale(logoImage, logoWidth, logoHeight, true);
		
		return _genQrcode(writer, content, width, height, logoScaleImage, logoWidth, logoHeight, logoFrameWidth, color, bgColor, frameColor);
	}
	
	private static BufferedImage _genQrcode(MultiFormatWriter writer, String content, int width, int height, 
			byte[] logoImage, int logoWidth, int logoHeight, int logoFrameWidth, int color, int bgColor, int frameColor) throws WriterException, IOException {
		BufferedImage logoScaleImage = null;
		
		if (logoImage != null && logoImage.length > 0) {
		    logoScaleImage = _scale(logoImage, logoWidth, logoHeight, true);
		}
		
        return _genQrcode(writer, content, width, height, logoScaleImage, logoWidth, logoHeight, logoFrameWidth, color, bgColor, frameColor);
	}
	
	
	private static BufferedImage _genQrcode(MultiFormatWriter writer, String content, int width, int height, 
			BufferedImage logoImage, int logoWidth, int logoHeight, int logoFrameWidth, int color, int bgColor, int frameColor) throws WriterException, IOException {
		// Logo
		int[][] logoPixels = null;
		if (logoImage != null) {
			if (logoImage != null) {
				logoPixels = new int[logoWidth][logoHeight];
				for (int i = 0; i < logoImage.getWidth(); i++) {
					for (int j = 0; j < logoImage.getHeight(); j++) {
						logoPixels[i][j] = logoImage.getRGB(i, j);
					}
				}
			}
		}
		
		Map<EncodeHintType, Object> hint = new HashMap<EncodeHintType, Object>();
		hint.put(EncodeHintType.CHARACTER_SET, CHARSET);
		hint.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
		hint.put(EncodeHintType.MARGIN, 0);//白边
		
		BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height, hint);
		
		// 二维矩阵转为一维像素数组
		int halfW = matrix.getWidth() / 2;
		int halfH = matrix.getHeight() / 2;
		int[] pixels = new int[width * height];
		
		int halfW_logo = logoWidth / 2;
		int halfH_logo = logoHeight / 2;
		
		for (int y = 0; y < matrix.getHeight(); y++) {
			for (int x = 0; x < matrix.getWidth(); x++) {
				if (logoPixels == null) {
					// 此处可以修改二维码的颜色，可以分别制定二维码和背景的颜色
					pixels[y * width + x] = matrix.get(x, y) ? color : bgColor;
				} else {
					if (x > halfW - halfW_logo && x < halfW + halfW_logo && y > halfH - halfH_logo && y < halfH + halfH_logo) {
						// Logo
						pixels[y * width + x] = logoPixels[x - halfW + halfW_logo][y - halfH + halfH_logo];
					}
					// 在图片四周形成边框
					else if ((x > halfW - halfW_logo - logoFrameWidth && x < halfW - halfW_logo + logoFrameWidth && y > halfH - halfH_logo - logoFrameWidth && y < halfH + halfH_logo + logoFrameWidth)
							|| (x > halfW + halfW_logo - logoFrameWidth && x < halfW + halfW_logo + logoFrameWidth && y > halfH - halfH_logo - logoFrameWidth && y < halfH + halfH_logo + logoFrameWidth)
							|| (x > halfW - halfW_logo - logoFrameWidth && x < halfW + halfW_logo + logoFrameWidth && y > halfH - halfH_logo - logoFrameWidth && y < halfH - halfH_logo + logoFrameWidth)
							|| (x > halfW - halfW_logo - logoFrameWidth && x < halfW + halfW_logo + logoFrameWidth && y > halfH + halfH_logo - logoFrameWidth && y < halfH + halfH_logo + logoFrameWidth)) {
						pixels[y * width + x] = frameColor;
					} else {
						// 此处可以修改二维码的颜色，可以分别制定二维码和背景的颜色
						pixels[y * width + x] = matrix.get(x, y) ? color : bgColor;
					}
				}
			}
		}
		
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		image.getRaster().setDataElements(0, 0, width, height, pixels);
		
		return image;
	}
	
	/**
	 * 把传入的原始图像按高度和宽度进行缩放，生成符合要求的图标
	 * 
	 * @param srcImageFile 源文件地址
	 * @param height 目标高度
	 * @param width 目标宽度
	 * @param hasFiller 比例不对时是否需要补白：true为补白; false为不补白;
	 * 
	 * @throws IOException
	 */
	private static BufferedImage _scale(File srcImageFile, int height, int width, boolean hasFiller) throws IOException {
		if (!srcImageFile.exists()) {
			return null;
		}
		BufferedImage srcImage = _read_image(srcImageFile);
		return _scale(srcImage, height, width, hasFiller);
	}
	
	private static BufferedImage _scale(byte[] buff, int height, int width, boolean hasFiller) throws IOException {
		BufferedImage srcImage = _read_image(buff);
		return _scale(srcImage, height, width, hasFiller);
	}
	
	private static BufferedImage _scale(BufferedImage srcImage, int height, int width, boolean hasFiller) throws IOException {
		// 缩放比例
		double ratio = 0.0;
		Image destImage = srcImage.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH);
		// 计算比例
		if (srcImage.getHeight() > height || srcImage.getWidth() > width) {
		    //缩小
			if (srcImage.getHeight() > srcImage.getWidth()) {
				ratio = (new Integer(height)).doubleValue() / srcImage.getHeight();
			} else {
				ratio = (new Integer(width)).doubleValue() / srcImage.getWidth();
			}
			AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(ratio, ratio), null);
			destImage = op.filter(srcImage, null);
		}
		// 补白
		if (hasFiller) {
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D graphic = image.createGraphics();
			graphic.setColor(Color.white);
			graphic.fillRect(0, 0, width, height);
			if (width == destImage.getWidth(null)) {
				graphic.drawImage(destImage, 0, (height - destImage.getHeight(null)) / 2, destImage.getWidth(null), destImage.getHeight(null), Color.white, null);
			} else {
				graphic.drawImage(destImage, (width - destImage.getWidth(null)) / 2, 0, destImage.getWidth(null), destImage.getHeight(null), Color.white, null);
			}
			graphic.dispose();
			destImage = image;
		}
		return (BufferedImage) destImage;
	}
	
	private static BufferedImage _read_image(byte[] buff) throws IOException {
		if (buff == null || buff.length == 0) {
			return null;
		}
		return ImageIO.read(new ByteArrayInputStream(buff));
	}
	
	private static BufferedImage _read_image(File file) throws IOException {
		if (file == null || !file.exists()) {
			return null;
		}
		return ImageIO.read(file);
	}
	
	public static String decode(String imgPath) {
		return decode(imgPath, "UTF-8");
	}
	
    // 解码  
	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	public static String decode(String imgPath, String charset) {
		String msg = "";
		try {
			Reader reader = new MultiFormatReader();
			File file = new File(imgPath);
			BufferedImage image;
			try {
				image = ImageIO.read(file);
				if (image == null) {
					log.error("二维码图像不存在: " + imgPath);
				} else {
					LuminanceSource source = new BufferedImageLuminanceSource(image);
					BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
					Result result;
					Hashtable hints = new Hashtable();
					hints.put(DecodeHintType.CHARACTER_SET, charset);
					result = new MultiFormatReader().decode(bitmap, hints);
					msg = result.getText();
				}
			} catch (IOException e) {
				log.error("二维码解码失败: " + imgPath + "\r\n" + e.getMessage());
			} catch (ReaderException e) {
				log.error("二维码解码失败: " + imgPath + "\r\n" + e.getMessage());
			}
		} catch (Exception e) {
			log.error("二维码解码失败: " + imgPath + "\r\n" + e.getMessage());
		}

		return msg;
	}
	
	public static class LicenseReqInfo {
		private String type;
		private String name;//应用系统名称
		private String khName;//客户名称
		private String khContact;//联系人
		private String khMobile;//联系电话
		private String serverId;//服务器信息
		private String fromDate;//开始日期
		private String endDate;//到期日期
		private int clientLink;//连接数
		private String owner;//公司负责人
		private String mobile;//联系电话
		private String email;//授权文件接收邮箱

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getKhName() {
			return khName;
		}

		public void setKhName(String khName) {
			this.khName = khName;
		}

		public String getKhContact() {
			return khContact;
		}

		public void setKhContact(String khContact) {
			this.khContact = khContact;
		}

		public String getKhMobile() {
			return khMobile;
		}

		public void setKhMobile(String khMobile) {
			this.khMobile = khMobile;
		}

		public String getServerId() {
			return serverId;
		}

		public void setServerId(String serverId) {
			this.serverId = serverId;
		}

		public String getFromDate() {
			return fromDate;
		}

		public void setFromDate(String fromDate) {
			this.fromDate = fromDate;
		}

		public String getEndDate() {
			return endDate;
		}

		public void setEndDate(String endDate) {
			this.endDate = endDate;
		}

		public int getClientLink() {
			return clientLink;
		}

		public void setClientLink(int clientLink) {
			this.clientLink = clientLink;
		}

		public String getOwner() {
			return owner;
		}

		public void setOwner(String owner) {
			this.owner = owner;
		}

		public String getMobile() {
			return mobile;
		}

		public void setMobile(String mobile) {
			this.mobile = mobile;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

    }
    
	public static void main1(String[] args) throws IOException {
	    byte[] buf = BarcodeUtils.generateQrCodeByte("weixin://wxpay/bizpayurl?pr=Z2GFOXY", ImageType.PNG, 235);
        FileUtils.writeByteArrayToFile(new File("/Users/9dsoft/Downloads/a.jpeg"), buf);
        //String a = "33b135a72a08bb457c3547f435b452886e9ed181e5008258b9e39391386f3a6cae6c5cee4ea3dde7b19c63d3ba823e80";
        
	}
	
	public static void main(String[] args) throws IOException {
	    
		//generateQrCode("CNJUN沈", 300, 300, "c:\\1.jpg", "c:\\2.jpg");
		/*
		byte[] buff = generateQrCodeByte("CNJUN沈" + new Date(), 500, new File("c:\\1.jpg"), 100);
		FileUtils.writeByteArrayToFile(new File("c:\\2.png"), buff);
		
		//generateQrCode("CNJUN沈", "c:\\2.jpg");
		
		JFrame jf = new JFrame("二维码");
		jf.setSize(400, 300);// 设置组件大小
		jf.setLocationRelativeTo(null);// 设置该组件位置(null则为屏幕居中)
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// 点击窗口关闭后触发的事件
		
		Image img = ImageIO.read(new File("c:\\2.png"));
		JLabel label = new JLabel(new ImageIcon(img));
		jf.add(label);
		
		jf.setVisible(true);// 是否显式该组件*/
		
		
		String s = "http://192.168.0.133:8080/zt/wx/index.html";
		
		LicenseReqInfo req = new LicenseReqInfo();
		req.setName("九鼎软件邮件管理系统");
		req.setKhName("山西省检察字");
		req.setKhContact("张二三");
		req.setKhMobile("13412345678");
		req.setServerId("1234-5678-ABCD-EFGH");
		req.setFromDate("2016-01-01");
		req.setEndDate("2016-12-01");
		req.setClientLink(100);
		req.setOwner("李四一");
		req.setMobile("15012345678");
		req.setEmail("94582826@qq.com");
		
		s = JSON.toJSONString(req);
		
		s = "weixin://wxpay/bizpayurl?pr=iBPLZgU";
		
		generateQrCode(new File("/Users/9dsoft/temp/logo/a.png"), s, 300, new File("/Users/9dsoft/temp/logo/weixin.png"), 60);
		//System.out.println(">>" + decode("c:\\sq.jpg"));
		
		JFrame jf = new JFrame("二维码");
		jf.setSize(650, 650);// 设置组件大小
		jf.setLocationRelativeTo(null);// 设置该组件位置(null则为屏幕居中)
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// 点击窗口关闭后触发的事件
		
		Image img = ImageIO.read(new File("/Users/9dsoft/temp/logo/a.png"));
		JLabel label = new JLabel(new ImageIcon(img));
		jf.add(label);
		
		jf.setVisible(true);
	}
	
}
