package cn.elfsoft.assist.util;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PullXml {
    public static List<Map<String, String>> parseGradeXml(InputStream in)
            throws XmlPullParserException, IOException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(in, "utf-8");
        int event = parser.getEventType();
        List<Map<String, String>> list = null;
        Map<String, String> map = null;
        while (event != XmlPullParser.END_DOCUMENT) {
            switch (event) {
                case XmlPullParser.START_DOCUMENT:
                    list = new ArrayList<Map<String, String>>();
                    break;
                case XmlPullParser.START_TAG:
                    if ("class".equals(parser.getName())) {
                        map = new HashMap<String, String>();
                        map.put("class", parser.nextText());
                    } else if ("type".equals(parser.getName())) {
                        map.put("type", parser.nextText());
                    } else if ("type2".equals(parser.getName())) {
                        map.put("type2", parser.nextText());
                    } else if ("exam".equals(parser.getName())) {
                        map.put("exam", parser.nextText());
                    } else if ("condition".equals(parser.getName())) {
                        map.put("condition", parser.nextText());
                    } else if ("grade".equals(parser.getName())) {
                        map.put("grade", parser.nextText());
                    } else if ("point".equals(parser.getName())) {
                        map.put("point", parser.nextText());
                    } else if ("gradepoint".equals(parser.getName())) {
                        map.put("gradepoint", parser.nextText());
                    } else if ("rank".equals(parser.getName())) {
                        map.put("rank", parser.nextText());
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if ("item".equals(parser.getName())) {
                        list.add(map);
                    }
                    break;
            }
            event = parser.next();
        }
        return list;
    }

    public static List<Map<String, String>> parseScheduleXml(InputStream in)
            throws XmlPullParserException, IOException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(in, "utf-8");
        List<Map<String, String>> list = null;
        Map<String, String> map = null;
        int event = parser.getEventType();
        String info = "";
        while (event != XmlPullParser.END_DOCUMENT) {
            switch (event) {
                case XmlPullParser.START_DOCUMENT:
                    list = new ArrayList<Map<String, String>>();
                    break;
                case XmlPullParser.START_TAG:
                    if ("class".equals(parser.getName())) {
                        map = new HashMap<String, String>();
                        String temp = parser.getAttributeValue(0)
                                + parser.getAttributeValue(1);
                        map.put("item", temp);
                        info = "";
                    } else if ("item".equals(parser.getName())) {
                        info += parser.nextText() + "#";
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if ("class".equals(parser.getName())) {
                        map.put("info", info);
                        list.add(map);
                    }
                    break;
                case XmlPullParser.END_DOCUMENT:

                    break;

            }
            event = parser.next();
        }
        return list;
    }

    public static List<Map<String, String>> parseExamXml(InputStream in) throws Exception {
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(in, "utf-8");
        List<Map<String, String>> list = null;
        Map<String, String> map = null;
        int event = parser.getEventType();
        while (event != XmlPullParser.END_DOCUMENT) {
            switch (event) {
                case XmlPullParser.START_DOCUMENT:
                    list = new ArrayList<Map<String, String>>();
                    break;
                case XmlPullParser.START_TAG:
                    if ("class".equals(parser.getName())) {
                        map = new HashMap<String, String>();
                        map.put("class", parser.nextText());
                    } else if ("rank".equals(parser.getName())) {
                        map.put("rank", parser.nextText());
                    } else if ("time".equals(parser.getName())) {
                        map.put("time", parser.nextText());
                    } else if ("location".equals(parser.getName())) {
                        map.put("location", parser.nextText());
                    } else if ("position".equals(parser.getName())) {
                        map.put("position", parser.nextText());
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if ("item".equals(parser.getName())) {
                        list.add(map);
                    }
                    break;
                case XmlPullParser.END_DOCUMENT:

                    break;

            }
            event = parser.next();
        }
        return list;
    }

}
