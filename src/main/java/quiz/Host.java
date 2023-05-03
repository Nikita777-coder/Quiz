package quiz;

import java.io.IOException;
import java.util.Scanner;

public class Host {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server service = new Server();
        Scanner scanner = new Scanner(System.in);

        while (service.hasQuestion()) {
            Formatter.formatQuestion(service.getQuestion());
            Formatter.formatAnswer();
            service.handleAnswer(scanner.nextLine());
            Formatter.formatVerdict(service.getVerdict());
        }

        service.showResults();
    }
}