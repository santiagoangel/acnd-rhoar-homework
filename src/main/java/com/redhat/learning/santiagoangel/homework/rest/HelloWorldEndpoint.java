package com.redhat.learning.santiagoangel.homework.rest;

import java.util.Random;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;

import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Metered;
import org.eclipse.microprofile.metrics.annotation.Timed;

@ApplicationScoped
@Path("/")
public class HelloWorldEndpoint {

	@Timed(name = "hello",
            description = "Monitor the time hello Method takes",
            unit = MetricUnits.MILLISECONDS,
            absolute = true)
	@GET
	@Path("/hello")
	@Produces("text/plain")
	public Response doGet() {
		return Response.ok("Hello from Thorntail!").build();
	}

	@Metered(name = "retry", 
            unit = MetricUnits.MILLISECONDS, 
            description = "Monitor the rate events occured", 
            absolute = true)
	@GET
    @Path("/retry")
    @Retry(maxRetries = 4, retryOn = RuntimeException.class)
    public String getHello () 
    {
         if (new Random().nextFloat() < 0.5f) {
            System.out.println("Error!!!");
            throw new RuntimeException("Resource failure.");
        }
        return "hello world!";
    }
 
	@Metered(name = "fallback", 
            unit = MetricUnits.MILLISECONDS, 
            description = "Monitor the rate events occured", 
            absolute = true)
    @GET
    @Path("/fallback")
    @Produces(MediaType.APPLICATION_JSON)
    @Timeout(250)
    @Fallback(fallbackMethod = "fallbackJSON")
    public SimpleProperty getPropertyJSON () 
    {
        SimpleProperty p = new SimpleProperty("key","value");
        randomSleep();
        return p;
    }
 
 
    public SimpleProperty fallbackJSON() 
    {
        SimpleProperty p = new SimpleProperty("key","fallback");
        return p;
    }
 
	@Counted(unit = MetricUnits.NONE,
            name = "circuit",
            absolute = true,
            displayName = "circuit",
            description = "Monitor how many times ciruit method was called")
    @GET
    @Path("/circuit")
    @Produces(MediaType.APPLICATION_JSON)
    @CircuitBreaker(successThreshold = 5, requestVolumeThreshold = 4, failureRatio=0.75, delay = 1000)
    public SimpleProperty getProperty () 
    {
        SimpleProperty p = buildResponse();      
     
        return p;
    }
 
     private void randomSleep() {
        try {  
          Thread.sleep(new Random().nextInt(500));
        }
        catch (Exception exc) {
          exc.printStackTrace();
        }   
   
    }
 
    private SimpleProperty buildResponse() {
		SimpleProperty p = new SimpleProperty("circuit","closed");
        if (new Random().nextFloat() < 0.5f) {
           p = new SimpleProperty("circuit","open");
        }
        return p;
    }
}
