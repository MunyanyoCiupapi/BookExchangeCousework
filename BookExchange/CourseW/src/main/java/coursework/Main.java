package coursework;

import coursework.model.BookExchange;
import coursework.utils.MenuOperation;
import coursework.utils.ReadWriteOperation;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        String cmd = "";

        BookExchange bookExchange = ReadWriteOperation.readFromFile();
        if (bookExchange == null) bookExchange = new BookExchange();


        while (!cmd.equals("q")) {

            System.out.println("""
                    Choose an option
                    u - work with users
                    p - work with publications
                    w - write to file as text all users
                    q - quit
                    """);

            cmd = scanner.nextLine();


            switch (cmd) {
                case "u":
                    MenuOperation.generateUserMenu(scanner, bookExchange);
                    break;
                case "w":
                    ReadWriteOperation.writeUsersToFile(bookExchange.getUsers());
                    break;
                case "p":
                    break;
                case "q":
                    ReadWriteOperation.writeToFileAsObject(bookExchange);
                    break;
                default:
                    System.out.println("Learn to read");
            }
        }

    }
}