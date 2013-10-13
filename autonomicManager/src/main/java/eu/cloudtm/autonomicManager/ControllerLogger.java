package eu.cloudtm.autonomicManager;

import eu.cloudtm.autonomicManager.commons.ListAppender;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by: Fabio Perfetti E-mail: perfabio87@gmail.com Date: 6/24/13
 */
public class ControllerLogger {

   private static List<String> list = new ArrayList<String>();
   private static ListAppender appender;
   public static Logger log;

   static {
      appender = new ListAppender(new PatternLayout("%d{DATE} - %m"), list);
      appender.setName("ListAppender");
      appender.setThreshold(Level.INFO);
      log = log.getLogger(ControllerLogger.class);
      log.addAppender(appender);
   }

   public static List<String> getAllLogs(){
      return new ArrayList<String>(list);
   }

   public static List<String> getLogsStartingFrom(int id){
      List<String> toRet = new ArrayList<String>();
      for(int i=id+1; i<list.size(); i++){
         toRet.add( (list.get(i)) );
      }
      return toRet;
   }

}
