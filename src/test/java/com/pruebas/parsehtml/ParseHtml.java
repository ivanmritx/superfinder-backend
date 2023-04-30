package com.pruebas.parsehtml;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseHtml {

    public static void main(String[] args) throws Exception {
        String term = "cacaolat";
        // String url = "https://supermercado.eroski.es/es/search/results/?q=%s&suggestionsFilter=false";
        String url = "https://www.alcampo.es/compra-online/search/?department=&text=bimbo";
        String uriTerm = URLEncoder.encode(term, StandardCharsets.UTF_8.toString());

        final HttpRequest request = HttpRequest.newBuilder().uri(new URI(String.format(url, uriTerm)))
                .timeout(Duration.ofSeconds(10)).GET().build();

        final HttpResponse<String> response = HttpClient.newBuilder().build().send(request, BodyHandlers.ofString());

        String responseStr = response.body();
        System.out.println(response.statusCode()+"BODY: " + responseStr);

        Pattern p = Pattern.compile("product-title\"><a.*>(?s)(.*)<\\/a>");
        Matcher m = p.matcher(responseStr);
        while (m.find()) {
            String tag = m.group(1);
            System.out.println(tag);
        }

    }

}
