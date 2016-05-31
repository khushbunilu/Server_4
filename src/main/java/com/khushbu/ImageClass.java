package com.khushbu;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

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

        byte[] decoded = Base64.getDecoder().decode(s);
        System.out.println(decoded);
        return ("img is "+s+"\n\n decoded is "+decoded);
    }





    @GET
    @Path("postimage")
    @Produces(MediaType.TEXT_PLAIN)
    public String PostImage(String s)
    {
        return ("helloworld");
    }



}
