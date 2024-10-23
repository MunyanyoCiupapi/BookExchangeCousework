package coursework.utils;

import coursework.model.BookExchange;
import coursework.model.Client;
import coursework.model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ReadWriteOperation {

    //BookExchange klase sukurta tik laikinai, kol neturime duomenu bazes vien tam, kad kaip objekta irasyt visa objeta

    //Sis metodas parasytas naudojant try catch finally struktura
    public static void writeToFileAsObject(String fileName, BookExchange bookExchange) {
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(new FileOutputStream(fileName));
            objectOutputStream.writeObject(bookExchange);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //Turime metoda su tokiu paciu pavadinimu, kas siaip butu klaida. Taciau metodo parametru skaicius kitas, turime method overloading
    //Cia turime try with resources struktura, trumpesne  + finally yra implicit
    public static void writeToFileAsObject(BookExchange bookExchange) {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("data.txt"))) {
            objectOutputStream.writeObject(bookExchange);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Metodas, kuris nuskaitys duomenis is failo
    public static BookExchange readFromFile() {
        BookExchange bookExchange = null;
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("data.txt"))) {
            //Turim butinai pasakyt koks ten objektas faile, nes nezino programa. Type casting
            bookExchange = (BookExchange) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return bookExchange;
    }


    public static void writeUsersToFile(List<User> userList) {
        try (FileWriter fileWriter = new FileWriter("users.txt")) {

            for (User u : userList) {
                fileWriter.write(u.getId() + ":" + u.getLogin() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

//    public static List<User> getUsersFromFile(){
//        List<User> users = new ArrayList<>();
//        try(Scanner scanner = new Scanner(new File("users.txt"))){
//
//            while ((scanner.hasNext())){
//                String line = scanner.nextLine();
//                String[] info = line.split(":");//Pagal jusu pasirinktus
//                Client client = new Client(info[0], ... ); //Pagal jusu konstruktorius
//                users.add(client);
//            }
//
//        }catch(IOException e) {
//            e.printStackTrace();
//        }
//        return users;
//    }
}
