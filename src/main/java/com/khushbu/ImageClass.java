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
    public void GetImage(String s)
    {
       System.out.println("hello");
    }


    @POST
    @Path("postimage")
    public void PostImage(String s)
    {
        System.out.println("helloworld");
    }



}
