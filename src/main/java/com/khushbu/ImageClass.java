package com.khushbu;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by khushbu on 31/5/16.
 */
@Path("imageclass")

public class ImageClass {

    @GET
    @Path("getimage")
    @Produces(MediaType.TEXT_PLAIN)
    public String GetImage(String s)
    {
       return ("hello");
    }


    @GET
    @Path("postimage")
    @Produces(MediaType.TEXT_PLAIN)
    public String PostImage(String s)
    {
        return ("helloworld");
    }



}
