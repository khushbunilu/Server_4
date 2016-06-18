package com.khushbu;



import Jama.Matrix;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import javax.print.attribute.HashDocAttributeSet;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.*;

/**
 * Created by khushbu on 31/5/16.
 */

















@Path("imageclass")

public class ImageClass {

    public int gen() {
        Random r = new Random( System.currentTimeMillis() );
        return 10000 + r.nextInt(20000);
    }


    short data[][];



    void storeHashMap(Map<String, String> HashMap) throws IOException {

        Properties properties = new Properties();

        for (Map.Entry<String,String> entry : HashMap.entrySet()) {
            properties.put(entry.getKey(), entry.getValue());
        }

        properties.store(new FileOutputStream("HashMap.properties"), null);

    }

    Map<String, String> getHashMap()  {
        Map<String, String> hmap = new HashMap<String, String>();
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("HashMap.properties"));
        }
        catch (Exception e)
        {
             return null;
        }
        for (String key : properties.stringPropertyNames()) {
            hmap.put(key, properties.get(key).toString());
        }
        return  hmap;
    }

    @POST
    @Path("AddToTrainingSet")
    @Produces("application/json")
    public String AddToTrainingSet(@FormParam("Image")String s,@FormParam("name")String name)
    {
        JSONObject obj= new JSONObject();

        System.out.println("\nAdding to Taining Set");


        HashMap<Integer,String> hm=new HashMap<Integer,String>();

        try {
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] decodedBytes = decoder.decodeBuffer(s);

            obj.put("status", "Success");


            BufferedImage img = ImageIO.read(new ByteArrayInputStream(decodedBytes));
            String ranValue = String.valueOf(gen());

            File f=new File("TrainingSet");

            if(!f.exists())
            {
                f.mkdir();
            }


            ConvertAndSaveToPgm("TrainingSet/image_"+ranValue+".pgm","image_"+ranValue+".txt",img);

            Map<String,String> HashMap=getHashMap();

            if(HashMap==null)
            {
                HashMap= new HashMap<String, String>();
            }

            String s1="image_"+ranValue+".pgm";
            HashMap.put(s1,name);
            System.out.println("\nimage_" + ranValue + ".pgm Saved");

            storeHashMap(HashMap);


            return  String.valueOf(obj);
        }catch(Exception e)
        {
            e.printStackTrace();
        }

        obj.put("status", "failure");
        return  String.valueOf(obj);
    }

    @POST
    @Path("RecogniseImage")
    @Produces("application/json")
    public String RecogniseImage(@FormParam("Image")String s)
    {

        JSONObject obj= new JSONObject();

        System.out.println("\nRecognizing Image");
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] decodedBytes = decoder.decodeBuffer(s);



            BufferedImage img = ImageIO.read(new ByteArrayInputStream(decodedBytes));
            String ranValue=String.valueOf(gen());

            File f=new File("Recognize");

            if(!f.exists())
            {
                f.mkdir();
            }

            try {
                FileUtils.cleanDirectory(new File("Recognize"));
            }catch (Exception e)
            {
                e.printStackTrace();
            }

            ConvertAndSaveToPgm("Recognize/image_" + ranValue + ".pgm","image_" + ranValue + ".txt",img);

            System.out.println("\nimage_" + ranValue + ".pgm Saved");

            int Number_of_Files=new File("TrainingSet").list().length;






            ImageRecognizer recognizer = new ImageRecognizer();
            recognizer.setTrainingSetPath("TrainingSet");
            recognizer.buildUp();

            InputStreamReader isr = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(isr);



            ImageRecognizer recognizer1 = new ImageRecognizer();



            String imageName="Recognize/image_"+ranValue+".pgm";
            Matrix m2=recognizer1.buildUp1(imageName);
            String imname=recognizer.findMostSimilarImages(imageName,Number_of_Files,m2);

            Map<String,String> HashMap=getHashMap();


            obj.put("status", "Success");
            obj.put("name", HashMap.get(imname));
            return  String.valueOf(obj);
        }catch(Exception e)
            {
                e.printStackTrace();
            }

        obj.put("status", "failure");
        return  String.valueOf(obj);
    }


    public void ConvertAndSaveToPgm(String fileName,String shortname,BufferedImage img) throws IOException {


        int width = img.getWidth();
        int height = img.getHeight();
        int alpha, red, green, blue;
        //convert to grayscale

        data = new short[height][width];
        short maxGrayValue = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = img.getRGB(x, y);

                int a = (p >> 24) & 0xff;
                int r = (p >> 16) & 0xff;
                int g = (p >> 8) & 0xff;
                int b = p & 0xff;

                //calculate average
                int avg = (r + g + b) / 3;

                //replace RGB value with avg
                p = (a << 24) | (avg << 16) | (avg << 8) | avg;


                alpha = new Color(img.getRGB(x, y)).getAlpha();
                red = new Color(img.getRGB(x, y)).getRed();
                green = new Color(img.getRGB(x, y)).getGreen();
                blue = new Color(img.getRGB(x, y)).getBlue();


                img.setRGB(x, y, p);
                short gray = (short) (0.299 * red + 0.587 * green + 0.114 * blue);
                if (gray > maxGrayValue) {
                    maxGrayValue = gray;
                }
                data[y][x] = gray;

            }
        }
        File f=new File("ImageTexts");

        if(!f.exists())
        {
            f.mkdir();
        }


        PrintWriter out = new PrintWriter(new File("ImageTexts/"+shortname));

        for(int i = 0; i<height; i++)
        {
            System.out.println();
            out.println();
            for (int j = 0; j<width; j++)
            {
                out.print(data[i][j]+" ");
            }
        }

        out.close();



        File file = new File(fileName);
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter writer = new FileWriter(file);

        writer.append("P2\n");
        writer.append("" + width + " " + height + "\n");
        writer.append("" + maxGrayValue + "\n");

        for (int y = 0; y < data.length; y++) {
            short[] currentLine = data[y];
            for (int x = 0; x < currentLine.length; x++) {
                short value = currentLine[x];
                writer.append("" + value + " ");
            }
            writer.append("\n");
        }
        writer.close();
    }




}
