package eu.cloudtm.autonomicManager.commons;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

import java.util.Collections;
import java.util.List;


/**
 * Created by: Fabio Perfetti E-mail: perfabio87@gmail.com Date: 10/13/13
 */
public class ListAppender extends AppenderSkeleton {

   private List<String> messageList;

   public ListAppender(Layout layout, List<String> list){
      this.layout = layout;
      this.messageList = list;
   }

   @Override
   protected void append(LoggingEvent loggingEvent) {
      messageList.add(layout.format(loggingEvent));
   }

   @Override
   public void close() {
      messageList = Collections.unmodifiableList(messageList);
   }

   @Override
   public boolean requiresLayout() {
      return true;
   }
}
