package cn.silen_dev;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Main {

    public static void main(String[] args) throws IOException {
        new DownloadCaptcha().download(1000);
        ImageUtils imageUtils=new ImageUtils();
        File parentFile=new File("captcha/data");
        for (File file:parentFile.listFiles()){
            ImageIO.write(ImageIO.read(file),"gif",new File("captcha/out/"+imageUtils.recognize(file)+".gif"));
           // Files.copy(file.toPath(),new File("captcha/out/"+imageUtils.recognize(file)+".gif").toPath());
        }
    }
}
