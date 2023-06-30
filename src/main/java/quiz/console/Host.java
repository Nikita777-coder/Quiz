package quiz.console;

import quiz.console.server.Server;

import java.util.Scanner;

public class Host {
    public static void main(String[] args) {
        Server server = new Server();
        Scanner scanner = new Scanner(System.in);

        while (server.hasQuestion()) {
            Formatter.formatQuestion(server.getQuestion());
            Formatter.formatAnswer();
            server.handleAnswer(scanner.nextLine());
            Formatter.formatVerdict(server.getVerdict());
        }

        server.showResults();
    }
}