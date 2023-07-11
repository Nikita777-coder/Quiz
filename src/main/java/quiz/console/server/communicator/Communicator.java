package quiz.console.server.communicator;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import quiz.console.server.communicator.exception.EmptyResponseException;
import quiz.console.server.communicator.exception.PageNotFoundRE;

import java.io.IOException;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.*;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Slf4j
public class Communicator {
    private int countOfReconnections = 0;
    private HttpRequest httpRequest;
    private final HttpClient HTTP_CLIENT = HttpClient
            .newBuilder()
            .proxy(ProxySelector.getDefault())
            .build();

    @Getter
    private HttpResponse<String> httpResponse;

    @Getter
    private String pathToConnect;
    private String previousPath;
    private void buildRequest() {
        try {
            httpRequest = HttpRequest.newBuilder()
                    .uri(new URI(pathToConnect))
                    .timeout(Duration.of(1, ChronoUnit.SECONDS))
                    .GET()
                    .build();
        } catch (URISyntaxException uriSyntaxException) {
            log.error(String.format("URISyntaxException {%s}", uriSyntaxException.getMessage()));
        }
    }

    private void getResponse() throws HttpTimeoutException, InterruptedException {
        int count = 0;

        while (count < 20) {
            try {
                httpResponse = HTTP_CLIENT.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                log.info("Successful getting of response");
                break;
            } catch (InterruptedException interruptedException) {
                count++;
                log.warn(String.format("The request with page %s is interrupted", pathToConnect));
            } catch (IOException ioException) {
                if (ioException.getClass().getSimpleName().equals("HttpTimeoutException")) {
                    throw (HttpTimeoutException) ioException;
                }

                log.error(String.format("Caught %s, the message from is: %s",
                        ioException.getClass().getSimpleName(),
                        ioException.getMessage())
                );
            }
        }

        if (count == 20) {
            throw new InterruptedException("number of interrupts is equal to 20");
        }
    }
    private void makeRequestAndResponse() throws HttpTimeoutException, InterruptedException, EmptyResponseException {
        if (!previousPath.equals(pathToConnect)) {
            buildRequest();
            log.info("successful request building");
        } else {
            log.info("the previous request will be used for sending");
        }

        getResponse();

        if (httpResponse.body().equals("")) {
            throw new EmptyResponseException(pathToConnect);
        }
    }

    private void checkHttpResponseCode() {
        switch (httpResponse.statusCode()) {
            case 200:
                return;
            case 404:
                log.warn(String.format("Page=%s not found", pathToConnect));
                throw new PageNotFoundRE("page not found. For more info watch logger messages");
            case 500:
                log.error("Server error responses");
            default:
                break;
        }
    }
    public Communicator() {
        this.pathToConnect = "";
        previousPath = pathToConnect;
    }
    public String getQuestionsAndAnswersFromSource()  {
        String ans = null;
        boolean flag = true;

        while (flag && countOfReconnections < 20) {
            try {
                makeRequestAndResponse();
                checkHttpResponseCode();
                ans = httpResponse.body();
                flag = false;
                log.info("Successful getting of response from current page");
            } catch (PageNotFoundRE pageNotFoundRE) {
                ++countOfReconnections;
            } catch (HttpTimeoutException httpTimeoutException) {
                ++countOfReconnections;
                log.warn(String.format("Page = %s, time waiting of sending response > 1 second", pathToConnect));
            } catch (InterruptedException interruptedException) {
                ++countOfReconnections;
                log.warn(String.format("Strange thing for page = %s: %s",
                        pathToConnect, interruptedException.getMessage()));
            } catch (EmptyResponseException emptyResponseException) {
                ++countOfReconnections;
                log.warn(emptyResponseException.getMessage());
            }
        }

        return ans;
    }

    public void setPathToConnect(String pathToConnect) {
        previousPath = this.pathToConnect;
        this.pathToConnect = pathToConnect;
    }
}
