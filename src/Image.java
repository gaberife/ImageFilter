import javafx.scene.paint.Color;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.*;
import java.util.Vector;

public class Image {
    public static int width;
    public static int height;
    public static int maxVal;
    public static int[][] data2D;
    public static int[][] average;
    public static int[][] median;
    public static int[][] edge;
    public static int[][] accumulator;

    public static void initialData() throws IOException {
        transformBack("data2D", data2D);
    }

    public static void averageFilter() throws IOException {
        average = new int[height][width];
        for (int i = 1; i < height - 1; i++) {
            for (int j = 1; j < width - 1; j++) {
                int sum = 0;
                for (int newI = -1; newI <= 1; newI++) {
                    for (int newJ = -1; newJ <= 1; newJ++) {
                        if ((i + (newJ) >= 0 && i + (newI) >= 0 && j + (newJ) < width && i + (newI) < height)) {
                            int temp = data2D[i + (newI)][j + (newJ)];
                            sum = sum + temp;
                        }
                    }
                }
                int temp = (int) (sum / 9);
                average[i][j] = temp;
            }
        }
        //print(average);
        transformBack("average", average);
    }

    public static void medianFilter() throws IOException {
        median = new int[height][width];
        for (int i = 1; i < height-1; i++) {
            for (int j = 1; j < width-1; j++) {
                int count = 0;
                int array[] = new int[9];
                for (int newI = -1; newI <= 1; newI++) {
                    for (int newJ = -1; newJ <= 1; newJ++) {
                        if ((i + (newJ) >= 0 && i + (newI) >= 0 && j + (newJ) < width && i + (newI) < height)) {
                            int temp = data2D[i + (newI)][j + (newJ)];
                            array[count] = temp;
                            count++;
                        }
                    }
                }
                java.util.Arrays.sort(array);
                median[i][j] = array[count / 2];
            }
        }
        transformBack("median", median);
    }

    public static void edgeDetect() throws IOException {
        edge  = new int[height][width];
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {

                int temp[][] = new int[3][3];

                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        temp[i][j] = average[x - 1 + i][y - 1 + j];
                    }
                }
                int deltaX, deltaY, posX = 0, posY = 0, negX = 0, negY = 0;

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
                int magnitude = adapt((int) Math.sqrt(deltaX * deltaX + deltaY * deltaY));
                edge[x][y] = magnitude;
            }
        }
        //print(edge);
        transformBack("edge", edge);
    }

    public static void hough() throws IOException {
        int maxTheta = 180;
        final double thetaStep = Math.PI / maxTheta;
        int centX = width / 2;
        int centY = height / 2;
        int houghHeight = (int) (Math.sqrt(2) * Math.max(height, width)) / 2;
        int doubleHeight = 2 * houghHeight;
        double[] sinCache = new double[maxTheta];
        double[] cosCache = sinCache.clone();
        for (int t = 0; t < maxTheta; t++) {
            double realTheta = t * thetaStep;
            sinCache[t] = Math.sin(realTheta);
            cosCache[t] = Math.cos(realTheta);
        }

        accumulator = new int[maxTheta][doubleHeight];
        //Initialize all values in accumulator array to 0
        for (int x = 0; x < maxTheta; x++) {
            for (int y = 0; y < doubleHeight; y++) {
                accumulator[x][y] = 0;
            }
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (!checkThreshold(edge[x][y])) {
                    for (int i = 0; i < maxTheta; i++) {
                        int r = (int) (((x - centX) * cosCache[i]) + ((y - centY) * sinCache[i]));
                        r += houghHeight;
                        if (r < 0 || r >= doubleHeight) continue;
                        accumulator[i][r]++;
                    }
                }
            }
        }

        print(accumulator);
        int firstX = 0, secondX = 0, thirdX = 0, firstY = 0, secondY = 0, thirdY = 0, fourthX = 0, fourthY = 0, fifthX = 0, fifthY = 0;
        for (int x = 2; x < maxTheta; x++) {
            for (int y = 0; y < doubleHeight; y++) {
                if (accumulator[x][y] > accumulator[firstX][firstY]) {
                    fifthX = fourthX;
                    fifthY = fourthY;
                    fourthX = thirdX;
                    fourthY = thirdY;
                    thirdX = secondX;
                    thirdY = secondY;
                    secondX = firstX;
                    secondY = firstY;
                    firstX = x;
                    firstY = y;
                } else if (accumulator[x][y] > accumulator[secondX][secondY]) {
                    fifthX = fourthX;
                    fifthY = fourthY;
                    fourthX = thirdX;
                    fourthY = thirdY;
                    thirdX = secondX;
                    thirdY = secondY;
                    secondX = x;
                    secondY = y;
                } else if (accumulator[x][y] > accumulator[thirdX][thirdY]) {
                    fifthX = fourthX;
                    fifthY = fourthY;
                    fourthX = thirdX;
                    fourthY = thirdY;
                    thirdX = x;
                    thirdY = y;
                } else if (accumulator[x][y] > accumulator[fourthX][fourthY]) {
                    fifthX = fourthX;
                    fifthY = fourthY;
                    fourthX = x;
                    fourthY = y;
                }
                else if (accumulator[x][y] > accumulator[fifthX][fifthY]){
                    fifthX = x;
                    fifthY = y;
                }
            }
        }

        System.out.println("First: " + firstX + "," + firstY + " = " + accumulator[firstX][firstY] + "\n" +
                "Second: " + secondX + "," + secondY + " = " + accumulator[secondX][secondY] + "\n" +
                "Third: " + thirdX + "," + thirdY + " = " + accumulator[thirdX][thirdY] + "\n" +
                "Fourth: " + fourthX + "," + fourthY + " = " + accumulator[fourthX][fourthY] + "\n" +
                "Fifth: " + fifthX + "," + fifthY + " = " + accumulator[fifthX][fifthY] + "\n");



        alter(firstX, firstY);
        alter(secondX, secondY);
        alter(thirdX, thirdY);
        alter(fourthX, fourthY);
        alter(fifthX, fifthY);
        //print(edge);
        transformBack("line", accumulator);
    }

    public static void alter(int m, int b){
        if (m > 128) m = m / 2;
        if (b > 128) b = b / 2;
        for(int x = 0; x < height; x++ ){
            int y = ( m * x ) + b;
            if ( y < 180 )
                accumulator[x][y] = 255;
        }
    }

    public static int adapt(int a) {
        if (a < 0 )
            return 0;
        else if (a > 255)
            return 255;
        return a;
    }

    public static boolean checkThreshold(int a){
        if(a == 0)
            return true;
        else if (a == 255)
            return true;
        return false;
    }

    public static void test(){
        System.out.println("test");
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
        for (int row = 0; row < array.length; row++) {
            for (int col = 0; col < array.length; col++)
                System.out.print(array[row][col] + " ");
            System.out.println();
        }
    }
}


