package grab;

import bean.Book;
import bean.DLTag;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GrabBook {

    private String getBookIndexHtml(String searchText){
        var list = new ArrayList<Book>();
        StringBuilder html = new StringBuilder();
        CloseableHttpClient httpClient = HttpClients.createDefault();

        try {
            URI uri = new URIBuilder()
                    .setScheme("https")
                    .setHost("sou.xanbhx.com")
                    .setPath("/search")
                    .setParameter("siteid", "qula")
                    .setParameter("q", searchText)
                    .build();
            HttpGet httpGet = new HttpGet(uri);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream content = entity.getContent();
            InputStreamReader isr = new InputStreamReader(content);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line=br.readLine())!=null){
                html.append(line);
            }

        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return html.toString();
    }

    public ArrayList<Book> getBookIndex(String searchText){
        var books = new ArrayList<Book>();

        String bookIndexHtml = getBookIndexHtml(searchText);
        Document document = Jsoup.parse(bookIndexHtml);
        Element searchList = document.select("div.search-list").first();
        Elements liBooks = searchList.select("ul").first().children().first().siblingElements();
        if (liBooks == null || liBooks.size() == 0) {
            return null;
        }
        for (Element li: liBooks) {
            String catagoryDecorate = li.child(0).text();
            String catagory = catagoryDecorate.split("\\[")[1].split("]")[0];

            Element aTag1 = li.child(1).select("a").first();
            String bookName = aTag1.text();
            String bookHref = aTag1.attr("href");

            Element aTag2 = li.child(2).select("a").first();
            String lastChapterName = aTag2.text();
            String lastChapterHref = aTag2.attr("href");

            String author = li.child(3).text();

            String lastUpdateDateString = li.child(5).text();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date lastUpdateDate = null;
            try {
                lastUpdateDate = format.parse(lastUpdateDateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String status = li.child(6).text();

            Book book = new Book();
            book.setCategory(catagory);
            book.setName(bookName);
            book.setBookHref(bookHref);
            book.setLastChapter(lastChapterName);
            book.setLastChapterHref(lastChapterHref);
            book.setAuthor(author);
            book.setLastUpdate(lastUpdateDate);
            book.setStatus(status);
            books.add(book);
        }

        return books;
    }

    public String getAllChaptersHtml(Book book){
        StringBuilder html = new StringBuilder();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(book.getBookHref());
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream content = entity.getContent();
            InputStreamReader isr = new InputStreamReader(content);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line=br.readLine())!=null){
                html.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return html.toString();
    }

    public ArrayList<DLTag> proccessChapterHtml(String html){
        ArrayList<DLTag> list = new ArrayList<>();

        String dl = null;
        Pattern pattern = Pattern.compile("<dl>[\\s\\S]*</dl>");
        Matcher matcher = pattern.matcher(html);
        while (matcher.find()) {
            dl  = matcher.group();
        }
        String subDL = dl.split("<dl>")[1].split("</dl>")[0];

        String[] split = subDL.split("<dt>|</dt>");

        DLTag dlTag = null;
        for (int i=1; i<split.length; i = i+2){
            if (i % 2 == 1) {
                dlTag = new DLTag();
            }
            dlTag.setTitle(split[i].trim() + " " +list.size());
            dlTag.setContent(split[i+1].trim());
            list.add(dlTag);

        }
        return list;
    }

    public String getHtml(String url) {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;
        StringBuilder sb = new StringBuilder();
        try {
             response = client.execute(httpGet);
            InputStream content = response.getEntity().getContent();
            InputStreamReader isr = new InputStreamReader(content);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine())!=null) {
                sb.append(line);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public List<String> getAllChapterLinkesAndGenerateFile(List<DLTag> list, String bookName){
        ArrayList<String> chapterLinks = new ArrayList<>();

        File root = new File(bookName);
        root.mkdir();
        for(DLTag dlTag : list) {
            File file = new File(root, dlTag.getTitle());
            file.mkdir();
            Document document = Jsoup.parse(dlTag.getContent());
            Elements links = document.select("a[href]");
            for (Element link : links){
                String href = link.attr("href");
                if (href.contains("book")) {
                    chapterLinks.add("https://www.qu.la" + href);
                }else {
                    chapterLinks.add("https://www.qu.la/book/903/" + href);
                }
                String html = getHtml(chapterLinks.get(chapterLinks.size() - 1));
                Document doc = Jsoup.parse(html);
                String title = doc.select("h1").first().text();
                String content = doc.select("#content").first().text();

                String[] temp = chapterLinks.get(chapterLinks.size() - 1).split("/");
                String fileName = temp[temp.length-1].split("\\.")[0];

                File f = new File(file, fileName);

                try {
                    FileWriter fw = new FileWriter(f);
                    fw.write(title+"\n");
                    fw.write(content);
                    fw.close();
                    TimeUnit.SECONDS.sleep(1);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
        return chapterLinks;
    }




}
