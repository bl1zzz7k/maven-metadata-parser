package com.company;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class Main {
    static ArrayList getArtifactVersions() {
        ArrayList<String> versionList = new ArrayList<>();

/*        StringBuilder metadataUrl = new StringBuilder(nexusUrl);
        metadataUrl.append("/nexus/content/repositories/");
        metadataUrl.append(repoId);
        metadataUrl.append("/");
        metadataUrl.append(groupId);
        metadataUrl.append("/");
        metadataUrl.append(artifactId);
        metadataUrl.append("maven-metadata.xml");*/


    String metadataUrl = "https://repo1.maven.org/maven2/org/codehaus/mojo/webstart-jnlp-servlet/maven-metadata.xml";
    String xml = getURLContents(metadataUrl .toString());
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xml)));

            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();
            XPathExpression expr = xpath
                    .compile("metadata/versioning//version");
            Object result = expr.evaluate(document, XPathConstants.NODESET);
            NodeList nodes = (NodeList) result;
            for (int i = 0; i < nodes.getLength(); i++) {
                versionList.add(nodes.item(i).getLastChild().getNodeValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionList;
    }

    static String getURLContents(String urlContent) {
        StringBuilder sb = new StringBuilder();
        URLConnection urlConn = null;
        InputStreamReader in = null;
        try {
            URL url = new URL(urlContent);
            urlConn = url.openConnection();
            if (urlConn != null)
                urlConn.setReadTimeout(60 * 1000);
            if (urlConn != null && urlConn.getInputStream() != null) {
                in = new InputStreamReader(urlConn.getInputStream(), Charset.defaultCharset());
                try(BufferedReader bufferedReader = new BufferedReader(in)){
                    if (bufferedReader != null) {
                        int cp;
                        while ((cp = bufferedReader.read()) != -1) {
                            sb.append((char) cp);
                        }
                    }
                }
            }
            in.close();
        } catch (Exception e) {
            throw new RuntimeException("Exception while calling URL:" + urlContent, e);
        }
        return sb.toString();
    }
    public static void main(String[] args) {
        System.out.println(getArtifactVersions());
    }
}
