package university.io;

import java.util.Scanner;

public class ViewProvider {

    public void printMessage(String message) {
        System.out.println(message);
    }

    public String readString() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    public int readNumber() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextInt();
    }
}
