package performance.throughput.httpserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HTTPServerTest {
    private static final String INPUT_FILE = "./resources/performance/throughput/war_and_peace.txt";
    private static final int NUMBER_OF_THREADS = 4;

    public static void main(String[] args) throws IOException {
        String text = new String(Files.readAllBytes(Paths.get(INPUT_FILE)));

        startServer(text);
    }

    private static void startServer(String text) throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(8000),0);

        httpServer.createContext("/search", new WordCounterHandler(text));

        Executor executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        httpServer.setExecutor(executor);
        httpServer.start();
    }

    private static class WordCounterHandler implements HttpHandler {

        private final String text;

        public WordCounterHandler(String text) {
            this.text = text;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String[] queryParams = exchange.getRequestURI().getQuery().split("=");
            String action = queryParams[0];
            String searchWord = queryParams[1];

            if(Objects.isNull(action) || !action.equalsIgnoreCase("word")){
                exchange.sendResponseHeaders(400, 0);
                return;
            }

            long countWord = countWord(searchWord);

            byte [] response = Long.toString(countWord).getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, response.length);
            OutputStream outputStream = exchange.getResponseBody();
            outputStream.write(response);
            outputStream.close();
        }

        private long countWord( String searchWord){
            long nbrWords = 0;

            int index = 0;

            while (index >=0){
                index = text.indexOf(searchWord,index);

                if(index > 0){
                    nbrWords++;
                    index++;
                }
            }

            return nbrWords;
        }
    }
}
