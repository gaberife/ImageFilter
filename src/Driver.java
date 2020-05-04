import java.io.*;
import java.util.Scanner;

public class Driver {
    public static void main(String[] args) {
        System.out.println("Welcome user, its time to filter an image. Please enter the file path.");
        Scanner input = new Scanner(System.in);
        String extendedFileName = "Image Files for Project #7/";
        String fileName = extendedFileName + input.nextLine() + ".pgm";
        try {
            checkAndInit(fileName);
            Image.initialData();
            Image.averageFilter();
            Image.medianFilter();
            Image.edgeDetect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void checkAndInit(String fileName) throws IOException {
        File file = new File(fileName); //Creates instance of file object
        String absolute = file.getAbsolutePath(); // Finds absolute path of file object
        if (!file.exists()) {
            System.out.println("Error, does not exist.");
            System.exit(0);
        } else {
            FileInputStream fileInputStream = new FileInputStream(absolute);
            Scanner scan = new Scanner(fileInputStream);
            // P5 128 128 255
            scan.next();
            Image.height = scan.nextInt();
            Image.width = scan.nextInt();
            Image.maxVal = scan.nextInt();
            fileInputStream.close();
            fileInputStream = new FileInputStream(absolute);
            DataInputStream dis = new DataInputStream(fileInputStream);
            Image.data2D = new int[Image.height][Image.width];
            for (int row = 0; row < Image.height; row++) {
                for (int col = 0; col < Image.width; col++) {
                    Image.data2D[row][col] = dis.readUnsignedByte();
                }
            }
        }
    }
}


