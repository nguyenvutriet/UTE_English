package com.example.englishapp.utils;


import com.example.englishapp.model.Subtitle;

import org.w3c.dom.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

public class SubtitleParser {

    public static List<Subtitle> parse(InputStream inputStream) {

        List<Subtitle> list = new ArrayList<>();

        try {

            Document doc = DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder()
                    .parse(inputStream);

            NodeList nodes = doc.getElementsByTagName("text");

            for (int i = 0; i < nodes.getLength(); i++) {

                Element element = (Element) nodes.item(i);

                float start =
                        Float.parseFloat(element.getAttribute("start"));

                float dur =
                        Float.parseFloat(element.getAttribute("dur"));

                String text =
                        element.getTextContent()
                                .replace("\n", " ")
                                .replace("&#39;", "'")
                                .replace("&quot;", "\"")
                                .replace("&amp;", "&");

                list.add(new Subtitle(text, start, start + dur));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}