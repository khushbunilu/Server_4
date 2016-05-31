package com.khushbu;


import sun.misc.BASE64Decoder;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by khushbu on 31/5/16.
 */

@Path("imageclass")

public class ImageClass {

    @POST
    @Path("getimage")
    @Produces(MediaType.TEXT_PLAIN)
    public String GetImage(@FormParam("Image")String s)
    {
        String imageDataBytes = s.substring(s.indexOf(",")+1);

        System.out.println("img is "+s);
       // InputStream stream = new ByteArrayInputStream(Base64.decode(imageDataBytes.getBytes(), Base64.DEFAULT));

        try {
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] decodedBytes = decoder.decodeBuffer(s);
            System.out.println(decodedBytes);
            return ("img is "+s+"\n\n decoded is "+decodedBytes);
        }catch(Exception e)
            {
                e.printStackTrace();
            }

        return ("img is "+s);
    }





    @GET
    @Path("postimage")
    @Produces(MediaType.TEXT_PLAIN)
    public String PostImage(String s)
    {
        return ("helloworld");
    }



}
