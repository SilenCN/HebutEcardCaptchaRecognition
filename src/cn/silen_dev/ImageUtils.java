package cn.silen_dev;

import cn.silen_dev.Model.RecognitionWeight;
import cn.silen_dev.Model.Seed;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Created by silen on 16-12-11.
 */
public class ImageUtils {
    private List<Seed> seedList;
    private static int CHAR_IMAGE_INDEX = 0;
    private static int CHAR_IMAGE_1_INDEX = 0;
    private static int CHAR_IMAGE_2_INDEX = 0;
    private static int CHAR_IMAGE_3_INDEX = 0;
    private static int CHAR_IMAGE_4_INDEX = 0;
    private static int CHAR_IMAGE_5_INDEX = 0;
    private static int CHAR_IMAGE_6_INDEX = 0;
    private static int CHAR_IMAGE_7_INDEX = 0;
    private static int CHAR_IMAGE_8_INDEX = 0;
    private static int CHAR_IMAGE_9_INDEX = 0;

    public ImageUtils() throws IOException {
        loadSeed();
    }


    public boolean isWhite(int colorInt) {

        Color color = new Color(colorInt);
        if (color.getBlue() > 160 && color.getGreen() > 160 && color.getRed() > 160) {
            return true;
        } else {
            return false;
        }
    }

    public BufferedImage removeBackgroudColor(File file) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(file);
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (isWhite(bufferedImage.getRGB(x, y))) {
                    bufferedImage.setRGB(x, y, Color.BLACK.getRGB());
                } else {
                    bufferedImage.setRGB(x, y, Color.WHITE.getRGB());
                }
            }
        }
        return bufferedImage;
    }

    public List<BufferedImage> spiltImage(BufferedImage image) {
        List<BufferedImage> list = new ArrayList<>();
        List<Integer> widthInt = new ArrayList<>();
        int width = image.getWidth();
        int height = image.getHeight();
        for (int x = 0; x < width; x++) {
            int count = 0;
            for (int y = 0; y < height; y++) {
                if (!isWhite(image.getRGB(x, y))) {
                    count++;
                }
            }
            widthInt.add(count);
        }

        for (int x = 0; x < width; x++) {
            if (widthInt.get(x) > 0) {
                for (int temp = x + 1; temp < width; temp++) {
                    if (widthInt.get(temp) < 1) {
                        list.add(image.getSubimage(x, 0, temp - x, height));
                        x = temp;
                        break;
                    }
                }
            }
        }

        for (int index = 0; index < list.size(); index++) {
            List<Integer> heightInt = new ArrayList<>();
            BufferedImage bufferedImage = list.get(index);
            int widthT = bufferedImage.getWidth();
            int heightT = bufferedImage.getHeight();
            for (int y = 0; y < heightT; y++) {
                int count = 0;
                int g = 0;//消除重复代码提示
                for (int x = 0; x < widthT; x++) {
                    if (!isWhite(bufferedImage.getRGB(x, y))) {
                        count++;
                    }
                }
                heightInt.add(count);
            }
            for (int y = 0; y < heightT; y++) {
                if (heightInt.get(y) > 0) {
                    for (int lastY = heightT - 1; lastY > y; lastY--) {
                        if (heightInt.get(lastY) > 0) {
                            list.remove(index);
                            list.add(index, bufferedImage.getSubimage(0, y, widthT, lastY - y));

                            break;
                        }
                    }
                    break;
                }
            }
        }

        return list;
    }

    public void whiteImage(BufferedImage bufferedImage, File fileP) throws IOException {
        File file = new File(fileP.getParentFile().getParent() + "/out/" + fileP.getName());
        System.out.println(file);
        ImageIO.write(bufferedImage, "gif", file);
    }

    public void whiteImage(List<BufferedImage> bufferedImageList, File fileP) throws IOException {
        for (BufferedImage bufferedImage : bufferedImageList) {
            File file = new File(fileP.getParentFile().getParent() + "/charImage/" + (CHAR_IMAGE_INDEX++) + ".gif");
            System.out.println(file);
            ImageIO.write(bufferedImage, "gif", file);
        }
    }

    public void whiteImage(List<BufferedImage> bufferedImageList) throws IOException {
        for (BufferedImage bufferedImage : bufferedImageList) {
            File file = null;
            switch (recognize(bufferedImage)) {
                case '1':
                    file = new File("captcha/out/1_" + (CHAR_IMAGE_1_INDEX++) + ".gif");
                    break;
                case '2':
                    file = new File("captcha/out/2_" + (CHAR_IMAGE_2_INDEX++) + ".gif");
                    break;
                case '3':
                    file = new File("captcha/out/3_" + (CHAR_IMAGE_3_INDEX++) + ".gif");
                    break;
                case '4':
                    file = new File("captcha/out/4_" + (CHAR_IMAGE_4_INDEX++) + ".gif");
                    break;
                case '5':
                    file = new File("captcha/out/5_" + (CHAR_IMAGE_5_INDEX++) + ".gif");
                    break;
                case '6':
                    file = new File("captcha/out/6_" + (CHAR_IMAGE_6_INDEX++) + ".gif");
                    break;
                case '7':
                    file = new File("captcha/out/7_" + (CHAR_IMAGE_7_INDEX++) + ".gif");
                    break;
                case '8':
                    file = new File("captcha/out/8_" + (CHAR_IMAGE_8_INDEX++) + ".gif");
                    break;
                case '9':
                    file = new File("captcha/out/9_" + (CHAR_IMAGE_9_INDEX++) + ".gif");
                    break;
                default:
                    return;
            }

            System.out.println(file);
            ImageIO.write(bufferedImage, "gif", file);
        }
    }


    private void loadSeed() throws IOException {
        seedList = new ArrayList<>();
        File file = new File("captcha/charImage/src");
        for (File file1 : file.listFiles()) {
            Map<BufferedImage, Integer> map = new HashMap<>();
            Seed seed = new Seed();
            seed.setBufferedImage(ImageIO.read(file1));
            seed.setValue(file1.getName().charAt(0));
            seedList.add(seed);
        }
    }


    private char recognize(BufferedImage bufferedImage) {
        List<RecognitionWeight> weightList = new ArrayList<>();
        for (Seed seed : seedList) {
            RecognitionWeight recognitionWeight = new RecognitionWeight();
            BufferedImage seedImage = seed.getBufferedImage();
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();
            float widthTimes = ((float) seedImage.getWidth()) / width;
            float heightTimes = ((float) seedImage.getHeight()) / height;
            int count = 0;
            for (int x = 0; x < width; x++) {
                for (int y=0; y < height; y++) {
                    int seedX=(int) Math.rint(widthTimes * x);
                    int seedY= (int) Math.rint(heightTimes * y);
                    seedX=seedX>(seedImage.getWidth()-1)?seedImage.getWidth()-1:seedX;
                    seedY=seedY>(seedImage.getHeight()-1)?seedImage.getHeight()-1:seedY;

                    if (bufferedImage.getRGB(x, y) == seedImage.getRGB(seedX,seedY)) {
                        count++;
                    }
                }
            }
            recognitionWeight.setValue(seed.getValue());
            recognitionWeight.setWeight(count);
            weightList.add(recognitionWeight);
        }
        RecognitionWeight recognitionWeight = new RecognitionWeight();
        for (RecognitionWeight weight : weightList) {
            if (weight.getWeight() > recognitionWeight.getWeight()) {
                recognitionWeight = weight;
            }
        }
        return recognitionWeight.getValue();
    }

    public String recognize(File file) throws IOException {
        StringBuilder builder=new StringBuilder();
        for (BufferedImage bufferedImage:spiltImage(removeBackgroudColor(file))){
            builder.append(recognize(bufferedImage));
        }
        return builder.toString();
    }
}
