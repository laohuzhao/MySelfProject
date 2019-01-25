package com.sdk4.biz.aote.controller;

import java.io.OutputStream;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sdk4.common.util.BarcodeUtils;
import com.sdk4.common.util.BarcodeUtils.ImageType;

@Controller
@RequestMapping("")
public class QrcodeController {
	@ResponseBody
	@RequestMapping(value = { "/qrimg" })
	public String qrimg(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			String data = request.getParameter("data");
			if (StringUtils.isNotEmpty(data)) {
				data = URLDecoder.decode(data, "UTF-8");
				response.setContentType("image/png");
				byte[] buff = BarcodeUtils.generateQrCodeByte(data, ImageType.PNG, 230, null, 0, 0);
				if (buff != null) {
					OutputStream out = response.getOutputStream();
					out.write(buff);
					out.flush();
				}
			}
		} catch (Exception e) {
		}
		return null;
	}

}

