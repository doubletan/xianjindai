package com.xinhe.cashloan.biz;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;


import android.util.Xml;

import com.xinhe.cashloan.entity.UpdataInfo;

/**
 * 解析从服务端获取的xml文件
 *
 * @author Administrator
 *
 */
public class UpdataInfoParser {

	public static UpdataInfo getUpdataInfo(InputStream is) throws Exception {
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(is, "utf-8");
		int type = parser.getEventType();
		UpdataInfo info = new UpdataInfo();
		while (type != XmlPullParser.END_DOCUMENT) {
			switch (type) {
			case XmlPullParser.START_TAG:
				if ("version".equals(parser.getName())) {
					info.setVersion(parser.nextText());
				} else if ("url".equals(parser.getName())) {
					info.setUrl(parser.nextText());
				} else if ("description".equals(parser.getName())) {
					info.setDescription(parser.nextText());
				}
				break;
			}
			type = parser.next();
		}
		return info;
	}

}
