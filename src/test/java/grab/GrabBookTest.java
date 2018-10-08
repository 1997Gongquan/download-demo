package grab;

import bean.Book;
import bean.DLTag;
import org.jsoup.Jsoup;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class GrabBookTest {

    // 测试方法 getBookIndex
    @Test
    public void test(){
        GrabBook grabBook = new GrabBook();
        ArrayList<Book> books = grabBook.getBookIndex("唐");
        System.out.println("\n" + books);
    }

    // 测试方法 downloadBook
    @Test
    public void test2(){
        GrabBook grabBook = new GrabBook();
        ArrayList<Book> books = grabBook.getBookIndex("儒道至圣");
        String allChaptersHtml = grabBook.getAllChaptersHtml(books.get(0));
        ArrayList<DLTag> dlTags = grabBook.proccessChapterHtml(allChaptersHtml);
        System.out.println();
    }

    @Test
    public void test3(){
        GrabBook grabBook = new GrabBook();
        String html = grabBook.getHtml("https://www.qu.la/book/903/10619342.html");
    }

    @Test
    public void test4(){
        GrabBook grabBook = new GrabBook();
        ArrayList<Book> books = grabBook.getBookIndex("儒道至圣");
        String allChaptersHtml = grabBook.getAllChaptersHtml(books.get(0));
        ArrayList<DLTag> dlTags = grabBook.proccessChapterHtml(allChaptersHtml);
        List<String> allChapterLinkes = grabBook.getAllChapterLinkesAndGenerateFile(dlTags, "儒道至圣");
    }

}
