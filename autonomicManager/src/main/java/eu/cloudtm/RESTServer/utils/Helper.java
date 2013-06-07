package eu.cloudtm.RESTServer.utils;


import javax.ws.rs.core.Response;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/1/13
 */
public class Helper {

    public static Response createResponse(String content){
        Response.ResponseBuilder builder = Response.ok(content);
        builder.header("Access-Control-Allow-Origin", "*");
        //builder.header("Access-Control-Max-Age", "3600");
        builder.header("Access-Control-Allow-Methods", "GET");
        //builder.header("Access-Control-Allow-Headers", "X-Requested-With,Host,User-Agent,Accept,Accept-Language,Accept-Encoding,Accept-Charset,Keep-Alive,Connection,Referer,Origin");

        return builder.build();
    }
    
    public static Response.ResponseBuilder createResponsePUT(String content){
        Response.ResponseBuilder builder = Response.ok(content);

        //builder.header("Access-Control-Max-Age", "3600");
        //builder.header("Access-Control-Allow-Methods", "PUT");
        //builder.header("Access-Control-Allow-Headers", "X-Requested-With,Host,User-Agent,Accept,Accept-Language,Accept-Encoding,Accept-Charset,Keep-Alive,Connection,Referer,Origin");

        return builder;
    }
}
