//package eu.cloudtm.RESTcontroller;
//
//import com.sun.jersey.api.json.JSONConfiguration;
//import com.sun.jersey.api.json.JSONJAXBContext;
//import eu.cloudtm.controller.model.Scale;
//
//
//import javax.ws.rs.ext.ContextResolver;
//import javax.ws.rs.ext.Provider;
//import javax.xml.bind.JAXBContext;
//
//
///**
// * Created by: Fabio Perfetti
// * E-mail: perfabio87@gmail.com
// * Date: 5/27/13
// */
//@Provider
//public class MyJAXBContextResolver implements ContextResolver<JAXBContext> {
//
//    private JAXBContext context;
//    private Class[] types = {Scale.class};
//
//    public MyJAXBContextResolver() throws Exception {
//        this.context = new JSONJAXBContext(
//                JSONConfiguration.mapped()
//                        .rootUnwrapping(true)
//                        //.arrays("configuration")
//                        .nonStrings("small", "medium","large")
//                        .build(),
//                types);
//
//    }
//
//    public JAXBContext getContext(Class<?> objectType) {
//        return (types[0].equals(objectType)) ? context : null;
//    }
//}