package com.quexten.ulricianumplanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.Calendar;

/**
 * Created by Quexten on 01-Dec-16.
 */

public class IServPlanParser {

    public PlanPage parseResponse(String response, boolean today) {
        PlanPage planPage = new PlanPage();

        Document doc = Jsoup.parse(response);
        Element body = doc.select("body").first();
        Element center = body.select("center").first();
        Elements titleElements = center.getElementsByClass("mon_title");

        if(center.getElementsByClass("info") != null) {
            Elements info = center.getElementsByClass("info");
            if(info.size() > 0) {
                Element first = info.first();
                Elements subInfo = first.getElementsByClass("info");
                Element element = subInfo.get(3);
                planPage.news = "";
                for(Node node : element.childNodes().get(0).childNodes()) {
                    if(node instanceof TextNode)
                        planPage.news += ((TextNode) node).text() + "\n";
                }
            }
        }

        String titleElement = titleElements.text();

        String dayString = titleElement.substring(titleElement.indexOf(' ') + 1);

        dayString = dayString.substring(0, dayString.indexOf(','));
        Day thisDay = "Montag".equals(dayString) ? Day.MON : "Dienstag".equals(dayString) ? Day.TUE : "Mittwoch".equals(dayString) ? Day.WED : "Donnerstag".equals(dayString) ? Day.THU : "Freitag".equals(dayString) ? Day.FRI : Day.MON;

        planPage.day = thisDay;


        planPage.pageNum = Character.getNumericValue(titleElement.charAt(titleElement.length() - 2));

        String dateString = titleElement.substring(0, titleElement.indexOf(' '));
        int day = Integer.valueOf(dateString.substring(0, dateString.indexOf('.')));
        int month = Integer.valueOf(dateString.substring(dateString.indexOf('.') + 1, dateString.lastIndexOf('.')));
        int year = Integer.valueOf(dateString.substring(dateString.lastIndexOf('.') + 1));

        Calendar c = Calendar.getInstance();
        c.set(year, month - 1, day);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

        Day scannedDay = dayOfWeek == 2 ? Day.MON : dayOfWeek == 3 ? Day.TUE : dayOfWeek == 4 ? Day.WED : dayOfWeek == 5 ? Day.THU : dayOfWeek == 6 ? Day.FRI : Day.MON;
        planPage.day = scannedDay;

        Element list = center.getElementsByClass("mon_list").first().child(0);

        planPage.setEntries(new TableEntry[list.children().size()]);

        for(int i = 0; i < list.children().size(); i++) {
            Element child = list.child(i);
            planPage.getEntries()[i] = new TableEntry(child);
        }

        return planPage;
    }

    class PlanPage {

        private TableEntry[] entries;
        private Day day;
        private int pageNum;
        private String news;


        public void setEntries(TableEntry[] entries) {
            this.entries = entries;
        }

        public TableEntry[] getEntries() {
            return entries;
        }

        public void setDay(Day day) {
            this.day = day;
        }

        public Day getDay() {
            return day;
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

    }

}
