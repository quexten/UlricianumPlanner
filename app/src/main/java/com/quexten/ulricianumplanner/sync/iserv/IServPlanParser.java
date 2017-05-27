package com.quexten.ulricianumplanner.sync.iserv;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Quexten on 01-Dec-16.
 */

public class IServPlanParser {

    public PlanPage parseResponse(String response) {
        PlanPage planPage = new PlanPage();
        Document document = Jsoup.parse(response);
        planPage.setDate(getDate(document));
        planPage.setEntries(getEntries(document));
        planPage.setNews(getNews(document));
        planPage.setPageNum(getPageNum(document));
        return planPage;
    }

    private Date getDate(Document document) {
        Element body = document.select("body").first();
        Element center = body.select("center").first();
        Elements titleElements = center.getElementsByClass("mon_title");

        String titleElement = titleElements.text();

        String dateString = titleElement.substring(0, titleElement.indexOf(' '));
        int day = Integer.valueOf(dateString.substring(0, dateString.indexOf('.')));
        int month = Integer.valueOf(dateString.substring(dateString.indexOf('.') + 1, dateString.lastIndexOf('.')));
        int year = Integer.valueOf(dateString.substring(dateString.lastIndexOf('.') + 1));

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);

        return calendar.getTime();
    }

    private TableEntry[] getEntries(Document document) {
        Element body = document.select("body").first();
        Element center = body.select("center").first();
        Element list = center.getElementsByClass("mon_list").first().child(0);

        TableEntry[] tableEntries = new TableEntry[list.children().size()];

        for(int i = 0; i < list.children().size(); i++) {
            Element child = list.child(i);
            tableEntries[i] = new TableEntry(child);
        }

        return tableEntries;
    }

    private String getNews(Document document) {
        String news = "";

        Element body = document.select("body").first();
        Element center = body.select("center").first();

        if(center.getElementsByClass("info") != null) {
            Elements info = center.getElementsByClass("info");
            if (info.size() > 0) {
                Element first = info.first();
                Elements subInfo = first.getElementsByClass("info");
                for (int i = 0; i < subInfo.size(); i++) {
                    Element element = subInfo.get(i);
                    for (Node node : element.childNodes())
                        for (Node subNode : node.childNodes())
                            if (subNode instanceof TextNode)
                                news += ((TextNode) subNode).text() + "\n";
                }

            }
        }

        return news;
    }

    /**
     * Gets the total amount of pages
     */
    private int getPageNum(Document document) {
        Element body = document.select("body").first();
        Element center = body.select("center").first();
        Elements titleElements = center.getElementsByClass("mon_title");

        String titleElement = titleElements.text();

        return Character.getNumericValue(titleElement.charAt(titleElement.length() - 2));
    }

    class PlanPage {

        private TableEntry[] entries;
        private int pageNum;
        private String news;
        private Date date;

        public void setEntries(TableEntry[] entries) {
            this.entries = entries;
        }

        public TableEntry[] getEntries() {
            return entries;
        }

        public void setPageNum(int pageNum) {
            this.pageNum = pageNum;
        }

        public int getPageNum() {
            return pageNum;
        }

        public void setNews(String news) {
            this.news = news;
        }

        public String getNews() {
            return this.news;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public Date getDate() {
            return this.date;
        }

    }

}
