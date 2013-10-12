package eu.cloudtm.autonomicManager.commons;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created with IntelliJ IDEA. User: fabio Date: 7/9/13 Time: 12:33 PM To change this template use File | Settings |
 * File Templates.
 */
public enum EvaluatedParam {

   ACF(0, "ACF"),
   CORE_PER_CPU(1, "CORE_PER_CPU"),
   MAX_ACTIVE_THREADS(2, "MAX_ACTIVE_THREADS"),
   SYSTEM_TYPE(3, "SYSTEM_TYPE"),
   DATA_ACCESS_FREQUENCIES(4, "DATA_ACCESS_FREQUENCIES"),
   TX_INVOKER_FREQUENCY(5, "TX_INVOKER_FREQUENCY"),
   TX_RESPONSE_TIME(6, "TX_RESPONSE_TIME"),
   ISOLATION_LEVEL(7, "ISOLATION_LEVEL"),;


   private final int id;
   private final String key;
   private final static Log log = LogFactory.getLog(EvaluatedParam.class);

   private EvaluatedParam(int id, String name) {
      this.id = id;
      this.key = name;
   }

   public int getId() {
      return id;
   }

   public String getKey() {
      return key;
   }

   public static EvaluatedParam getById(int id) {
      for (EvaluatedParam param : values()) {
         if (param.getId() == id)
            return param;
      }
      return null;
   }

   public static EvaluatedParam getByName(String name) {
      for (EvaluatedParam param : values()) {
         if (param.getKey().equals(name))
            return param;
      }
      return null;
   }


   public static Object sumEvaluatedParam(EvaluatedParam e, Object a, Object b) {
      if (b == null)
         log.trace("B is not supposed to be null!");
      Object ret = null;
      log.trace("Going to sum EvaluatedParam " + e + " : " + a + " + " + b);
      if (e.equals(ISOLATION_LEVEL) || e.equals(SYSTEM_TYPE))
         ret = b;
      else {
         if (b instanceof Long) {
            log.trace("Summing long");
            if (a == null)
               ret = new Long(((Long) b).longValue());
            else
               ret = new Long((Long) a + (Long) b);
         }
         //if (p.getClazz() == Double.class) {
         if (b instanceof Double) {
            log.trace("Summing double");
            if (a == null)
               ret = new Double(((Double) b).doubleValue());
            else
               ret = new Double((Double) a + (Double) b);
         }

         //if (p.getClazz() == Integer.class) {
         if (b instanceof Integer) {
            log.trace("Summing int");
            if (a == null)
               ret = new Integer(((Integer) b).intValue());
            else
               ret = new Integer((Integer) a + (Integer) b);
         }

         if (ret == null)
            throw new IllegalArgumentException("EvaluatedParam " + e + " is not Double, Long or Integer and thus cannot be averaged");
         log.trace("Returning " + ret);
         return ret;
      }

      log.trace("Returning " + ret);
      return ret;
   }

   public static Object multiplyEvaluatedParam(EvaluatedParam e, Object a, double b) {
      if (e.equals(ISOLATION_LEVEL) || e.equals(SYSTEM_TYPE))
         return a;
      if (a instanceof Long) {
         double d = (Long) a * b;
         long dd = (long) d;
         return new Long(dd);
      }
      if (a instanceof Double) {
         return new Double((Double) a * b);
      }

      if (a instanceof Integer) {
         double d = (Integer) a * b;
         int dd = (int) d;
         return new Integer(dd);
      }
      throw new IllegalArgumentException("EvaluatedParam " + e + " is not Double, Long or Integer and thus cannot be averaged");
   }

}
