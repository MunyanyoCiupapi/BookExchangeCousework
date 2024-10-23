package coursework.utils;

import coursework.model.BookExchange;
import coursework.model.Client;
import coursework.model.User;

import java.time.LocalDate;
import java.util.Scanner;

//Klase naudojam meniu generavimui ir duomenu susirinkimui

public class MenuOperation {
    public static void generateUserMenu(Scanner scanner, BookExchange bookExchange) {
        var userCmd = 0;
        var consoleInput = "";

        while (userCmd != 6) {

            System.out.println("""
                    Choose command
                    1 - create
                    2 - update
                    3 - read
                    4 - read all
                    5 - delete
                    6 - return to main
                    """);
            userCmd = scanner.nextInt();
            //next int neturi next line simbolio, todel reik sios eilutes
            scanner.nextLine();

            switch (userCmd) {
                case 1:
                    //Idealiu atveju turetume patikrinti, ar vedami duomenys geri
                    System.out.println("Which type? C/A");
                    consoleInput = scanner.nextLine();

                    if (consoleInput.equals("C")) {
                        System.out.println("Enter the data: login;psw;name;surname;address;birth date YYYY-MM-DD");
                        consoleInput = scanner.nextLine();
                        String[] info = consoleInput.split(";");
                        //Naudokite Integer, Boolean, Double ir t.t. klases. Aprasytas ne vienas metodas, skirtas lengvesniam duomenu tvarkymui
                        //Integer.parseInt() , Integer.valueOf(), etc.
                        Client client = new Client(info[0], info[1], info[2], info[3], info[4], LocalDate.parse(info[5]));
                        //Cia prisidedame naujai sukurta objekta i useriu sarasa
                        //Taciau BookExchange klaseje turime List<User>. Kodel galime prideti Client ar Admin objekta? Todel, kad ten kur apibrezta tevines klases
                        //struktura pvz List<Users>, galima talpinti vaikiniu klasiu objektus
                        //Tiesiog, jei bandysite is to saraso paimti objekta ir gauti vaikinines klases atributo reiksme - ji bus nepasiekiama be type cast
                        bookExchange.getUsers().add(client);
                    } else {

                    }
                    break;
                case 2:
                    System.out.println("Enter login");
                    consoleInput = scanner.nextLine();
                    for (User u : bookExchange.getUsers())
                        if (u.getLogin().equals(consoleInput)) {
                            System.out.println("Enter name");
                            consoleInput = scanner.nextLine();
                            u.setName(consoleInput);
                        }
                    break;
                case 3:
                    System.out.println("Enter login");
                    consoleInput = scanner.nextLine();
                    for (User u : bookExchange.getUsers())
                        if (u.getLogin().contains(consoleInput))
                            System.out.println(u);
                    break;
                case 4:
                    for (User u : bookExchange.getUsers())
                        System.out.println(u);
                    break;
            }
        }
    }
}
