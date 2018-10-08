import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class Fundamentals {
//   https://sou.xanbhx.com/search
    public static void main(String[] args) throws URISyntaxException, IOException {
        String bookName = "儒道至圣";

        CloseableHttpClient httpClient = HttpClients.createDefault();

        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("sou.xanbhx.com")
                .setPath("/search")
                .setParameter("siteid", "qula")
                .setParameter("q", bookName)
                .build();

        HttpGet httpGet = new HttpGet(uri);

        CloseableHttpResponse response = httpClient.execute(httpGet);

        HttpEntity entity = response.getEntity();
        InputStream inputStream = entity.getContent();

        InputStreamReader isr = new InputStreamReader(inputStream);
        BufferedReader br = new BufferedReader(isr);

        String line = null;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine())!=null){
            sb.append(line);
        }
        System.out.println(sb.toString());

    }
}
