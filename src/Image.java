import javafx.scene.paint.Color;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.*;

public class Image {
    public static int width;
    public static int height;
    public static int maxVal;
    public static int[][] data2D;
    public static int[][] average;
    public static int[][] median;
    public static int[][] edge;
    public static int[][] hough;
    public static int[][] accumulator;

    public static void initialData() throws IOException{
        //print(data2D);
        transformBack("data2D", data2D);
    }
    public static void averageFilter() throws IOException {
        average = new int[height][width];
        for (int i = 1; i < height; i++) {
            for (int j = 1; j < width; j++) {
                int sum = 0;
                for (int newI = -1; newI <= 1; newI++) {
                    for (int newJ = -1; newJ <= 1; newJ++) {
                        if((i+(newJ)>=0 && i+(newI)>=0 && j+(newJ)<width && i+(newI)<height)){
                            int temp = data2D[i+(newI)][j+(newJ)];
                            sum = sum + temp;
                        }
                    }
                }
                int temp = (int) (sum/9);
                average[i][j] = temp;
            }
        }
        //print(average);
        transformBack("average", average);
    }

    public static void medianFilter() throws IOException{
        median = new int[height][width];
        for (int i = 1; i < height; i++) {
            for (int j = 1; j < width; j++) {
                int count = 0;
                int array[] = new int[9];
                for (int newI = -1; newI <= 1; newI++) {
                    for (int newJ = -1; newJ <= 1; newJ++) {
                        if((i+(newJ)>=0 && i+(newI)>=0 && j+(newJ)<width && i+(newI)<height)){
                            int temp = data2D[i+(newI)][j+(newJ)];
                            array[count] = temp;
                            count++;
                        }
                    }
                }
                java.util.Arrays.sort(array);
                median[i][j] = array[count/2];
            }
        }
        transformBack("median", median);
    }

    public static void edgeDetect() throws IOException{
        edge = new int[height][width];
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {

                int temp [][] = new int[3][3];

                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        temp[i][j] = average[x-1+i][ y-1+j];
                    }
                }
                int deltaX = 0, deltaY = 0, posX = 0, posY = 0, negX = 0, negY = 0;

                for (int col = 0; col < 3; col++) {
                    negY += temp[2][col];
                    posY += temp[0][col];
                }
                deltaY = posY - negY;

                for (int row = 0; row < 3; row++) {
                    negY += temp[row][2];
                    posY += temp[row][0];
                }
                deltaX = posX - negX;
                int magnitude = threshold((int) Math.sqrt(deltaX*deltaX + deltaY*deltaY));
                edge[x][y] = magnitude;
            }
        }
        //print(edge);
        transformBack("edge", edge);
    }

    public static void hough() throws IOException {
        hough = new int[height][width];
        for(int i = 0; i < height; i++){
            for(int j = 0; j < 2*width; j++){
                accumulator[i][j] = 0;
            }
        }

        for (int x = 1; x < height - 1; x++) {
            for (int y = 1; y < width - 1; y++) {
                if(edge[x][y] == 225){
                    for (int i =0; i< 500; i++){

                    }
                }
            }
        }
        print(hough);
        transformBack("edge", edge);
    }


    public static int threshold(int a) {
        if (a < 0 || a <= 110)
            return 0;
        else if (a > 255 || a >= 140)
            return 255;
        return a;
    }

    public static void transformBack(String newFile, int [][] array) throws IOException {
        BufferedImage image = new BufferedImage(height, width, BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster raster = image.getRaster();
        int[] temp = {0, 0, 0, 255};  //  pixel
        int value;
        for (int y = 1; y < height; y++) {
            for (int x = 1; x < width; x++) {
                value = array[y][x];
                temp[0] = value;
                temp[1] = value;
                temp[2] = value;
                raster.setPixel(x, y, temp);
            }
        }
        String fileName = newFile + ".pgm";
        File output = new File(fileName);
        String absolute = output.getAbsolutePath(); // Finds absolute path of file object
        ImageIO.write(image, "jpg", new File(absolute));
    }

    public static void print(int [][] array) {
        for (int row = 0; row < Image.height; row++) {
            for (int col = 0; col < Image.width; col++)
                System.out.print(array[row][col] + " ");
            System.out.println();
        }
    }
}


