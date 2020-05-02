import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
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


    public static int[][] filter1 = {{ -1,  0,  1 }, { -2,  0,  2 }, { -1,  0,  1 }};
    public static int[][] filter2 = {{  1,  2,  1 }, {  0,  0,  0 }, { -1, -2, -1 }};

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
                        temp[i][j] = data2D[x-1+i][ y-1+j];
                    }
                }
                int gray1 = 0, gray2 = 0;
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        gray1 += temp[i][j] * filter1[i][j];
                        gray2 += temp[i][j] * filter2[i][j];
                    }
                }
                int magnitude = (int) (255 - Math.sqrt(gray1*gray1 + gray2*gray2));
                edge[x][y] = magnitude;
            }
        }
        print(edge);
        transformBack("edge", edge);
    }

    public static void hough() throws IOException {
        hough = new int[height][width];
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



        public static void transformBack(String newFile, int [][] array) throws IOException {
        BufferedImage image = new BufferedImage(height, width, BufferedImage.TYPE_INT_RGB);
        int value;
        for (int y = 1; y < height; y++) {
            for (int x = 1; x < width; x++) {
                value = array[y][x];
                image.setRGB(y, x, value);
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


