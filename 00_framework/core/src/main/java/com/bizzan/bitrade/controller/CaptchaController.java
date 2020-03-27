package com.bizzan.bitrade.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bizzan.bitrade.util.CaptchaUtil;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

@Controller
public class CaptchaController extends BaseController {
	//字符源
	private String text = "0123456789abcdefghijklmnopqrstuvwxyz";
	private int length = 4;
	private int width = 200;
	private int height = 64;
	private Font font = new Font("Arial", Font.ITALIC | Font.BOLD, (int)(height*0.8));
	private boolean crossLine = false;
	private boolean twistImage = true;
	
	public String getText() {
		return text;
	}
	
	public int getLength() {
		return length;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	
	/**
	 * 请求生成图形验证码
	 * @throws IOException
	 */
	@RequestMapping("/captcha")
	public void genCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 在内存中创建图象
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		// 获取图形上下文
		Graphics g = image.getGraphics();
		// 生成随机类
		Random random = new Random();
		// 设定背景色
		g.setColor(new Color(255, 255, 255));
		g.fillRect(0, 0, width, height);
		// 设定字体
		g.setFont(font);
		// 取随机产生的验证码
		String sRand = "";
		//文字X坐标
		int codeX = width/(length+1);
		for (int i = 0; i < length; i++) {
			String rand = String.valueOf(getRandomText());
			sRand += rand;
			//将验证码输出到图象中
			g.setColor(getRandColor(100, 150));// 调用函数出来的颜色相同
			g.drawString(rand, codeX * i + 10, height-20);
		}
		// 生成干扰线
		if (crossLine) {
			for (int i = 0; i < (random.nextInt(5) + 5); i++) {
				g.setColor(new Color(random.nextInt(255) + 1, random.nextInt(255) + 1, random.nextInt(255) + 1));
				g.drawLine(random.nextInt(width), random.nextInt(height), random.nextInt(width),
						random.nextInt(height));
			}
		}
		if(twistImage){
			image = twistImage(image);
		}
		String pageId = request(request,"cid");
		String key = "CAPTCHA_" + pageId;
		// 将验证码存入页面KEY值的SESSION里面
		HttpSession session = request.getSession();
		session.setAttribute(key, sRand);
		session.setAttribute(key + "_time", System.currentTimeMillis());
		// 图象生效
		g.dispose();
		ServletOutputStream responseOutputStream = response.getOutputStream();
		// 输出图象到页面
		ImageIO.write(image, "JPEG", responseOutputStream);
		// 以下关闭输入流！
		response.setHeader("Cache-Control", "no-store");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
		responseOutputStream.flush();
		responseOutputStream.close();
	}
	
	/** 
     *  
     * @Description:正弦曲线Wave扭曲图片
     * @return BufferedImage 
     */ 
    private BufferedImage twistImage(BufferedImage buffImg) {
    	Random random = new Random();
        double dMultValue = random.nextInt(10) + 5;// 波形的幅度倍数，越大扭曲的程序越高，一般为3  
        double dPhase = random.nextInt(6);// 波形的起始相位，取值区间(0-2＊PI)
        BufferedImage destBi = new BufferedImage(buffImg.getWidth(),  
                buffImg.getHeight(), BufferedImage.TYPE_INT_RGB); 
        Graphics g = destBi.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, buffImg.getWidth(), buffImg.getHeight());
        for (int i = 0; i < destBi.getWidth(); i++) {  
            for (int j = 0; j < destBi.getHeight(); j++) {  
                int nOldX = getXPosition4Twist(dPhase, dMultValue,destBi.getHeight(), i, j);  
                int nOldY = j;
                if (nOldX >= 0 && nOldX < destBi.getWidth() && nOldY >= 0 
                        && nOldY < destBi.getHeight()) {
                    destBi.setRGB(nOldX, nOldY, buffImg.getRGB(i, j));  
                }
            }
        }
        return destBi;  
    }
   
    /** 
     *  
     * @Description:获取扭曲后的x轴位置 
     * @param dPhase 
     * @param dMultValue 
     * @param height 
     * @param xPosition 
     * @param yPosition 
     * @return int 
     */ 
    private int getXPosition4Twist(double dPhase, double dMultValue,  
            int height, int xPosition, int yPosition) {  
        double PI = 3.1415926535897932384626433832799; // 此值越大，扭曲程度越大  
        double dx = (double) (PI * yPosition) / height + dPhase;  
        double dy = Math.sin(dx);  
        return xPosition + (int) (dy * dMultValue);  
    }  
    
	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	char getRandomText() {
		Random random = new Random();
		return text.charAt(random.nextInt(text.length()));
	}

	/**
	 * 给定范围获得随机颜色
	 * 
	 * @param fc
	 * @param bc
	 * @return
	 */
	Color getRandColor(int fc, int bc) {
		Random random = new Random();
		if (fc > 255) {
			fc = 255;
		}
		if (bc > 255) {
			bc = 255;
		}
		int r = fc + random.nextInt(bc - fc);
		int g = fc + random.nextInt(bc - fc);
		int b = fc + random.nextInt(bc - fc);
		return new Color(r, g, b);
	}
}
