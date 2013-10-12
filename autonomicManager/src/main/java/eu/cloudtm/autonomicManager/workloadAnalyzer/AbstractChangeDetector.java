package eu.cloudtm.autonomicManager.workloadAnalyzer;

import eu.cloudtm.autonomicManager.Paranoid;
import eu.cloudtm.autonomicManager.commons.EvaluatedParam;
import eu.cloudtm.autonomicManager.commons.Param;
import eu.cloudtm.autonomicManager.configs.Config;
import eu.cloudtm.autonomicManager.configs.KeyConfig;
import eu.cloudtm.autonomicManager.statistics.ProcessedSample;
import eu.cloudtm.autonomicManager.statistics.samples.CustomSample;
import eu.cloudtm.autonomicManager.statistics.samples.PBProcessedSample;
import eu.cloudtm.autonomicManager.statistics.samples.TOProcessedSample;
import eu.cloudtm.autonomicManager.statistics.samples.TWOPCProcessedSample;
import org.apache.commons.collections15.Buffer;
import org.apache.commons.collections15.BufferUtils;
import org.apache.commons.collections15.buffer.CircularFifoBuffer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * User: Fabio Perfetti perfabio87 [at] gmail.com Date: 7/19/13 Time: 11:24 AM
 */
public abstract class AbstractChangeDetector {

   private static Log log = LogFactory.getLog(AbstractChangeDetector.class);
   private static Log paranoid = LogFactory.getLog(Paranoid.class);

   protected static final int SLIDE_WINDOW_SIZE = Config.getInstance().getInt(KeyConfig.SLIDE_WINDOW_SIZE.key());

   protected Buffer<ProcessedSample> sampleSlideWindow = BufferUtils.synchronizedBuffer(new CircularFifoBuffer<ProcessedSample>(SLIDE_WINDOW_SIZE));

   private List<WorkloadEventListener> listeners = new ArrayList<WorkloadEventListener>();

   private Map<Param, Double> monitoredParams2delta;
   private Map<EvaluatedParam, Double> monitoredEvaluatedParams2delta;

   private Map<Param, Double> lastAvgParams = new HashMap<Param, Double>();
   private Map<EvaluatedParam, Double> lastAvgEvaluatedParams = new HashMap<EvaluatedParam, Double>();

   private AtomicBoolean enabled = new AtomicBoolean(false);


   public AbstractChangeDetector(Map<Param, Double> monitoredParams2delta,
                                 Map<EvaluatedParam, Double> monitoredEvaluatedParams2delta) {

      this.monitoredParams2delta = monitoredParams2delta;
      this.monitoredEvaluatedParams2delta = monitoredEvaluatedParams2delta;
      init();
   }


   public abstract void samplePerformed(ProcessedSample sample);

   private void init() {

      for (Param param : monitoredParams2delta.keySet()) {
         log.warn("initializing avg structure [" + param + "]");
         lastAvgParams.put(param, 0.0);
      }
      for (EvaluatedParam evaluatedParam : monitoredEvaluatedParams2delta.keySet()) {
         log.warn("initializing avg structure [" + evaluatedParam + "]");
         lastAvgEvaluatedParams.put(evaluatedParam, 0.0);
      }
   }

   protected boolean evaluateParam() {
      for (Param param : monitoredParams2delta.keySet()) {
         log.trace("Analyzing " + param.getKey());
         double sum = 0.0;
         for (ProcessedSample sample : sampleSlideWindow) {
            sum += ((Number) sample.getParam(param)).doubleValue();
         }
         double currentAvg = sum / ((double) sampleSlideWindow.size());
         log.debug("currentAvg: " + currentAvg);
         log.debug("lastAvg: " + lastAvgParams.get(param));

         if (lastAvgParams.get(param) == 0 || lastAvgParams.get(param) == Double.NaN) {
            log.debug("Updating && Skipping lastAvgParams");
            lastAvgParams.put(param, currentAvg);
         } else {
            double ratio = (currentAvg / lastAvgParams.get(param)) * 100;
            log.debug("ratio: " + ratio);

            double variation = Math.abs(ratio - 100);
            log.debug("variation: " + variation);

            if (variation >= monitoredParams2delta.get(param)) {
               log.trace("Updating lastAvgEvaluatedParams for " + param + " to: " + currentAvg);
               lastAvgParams.put(param, currentAvg);
               log.info("BOUND REACHED (" + param.getKey() + ")");
               return true;
            }
         }
      }
      return false;
   }

   protected boolean evaluateEvaluatedParam() {
      for (EvaluatedParam param : monitoredEvaluatedParams2delta.keySet()) {
         log.trace("");
         log.trace("Analyzing " + param.getKey());
         double sum = 0.0;
         for (ProcessedSample sample : sampleSlideWindow) {
            sum += (Double) sample.getEvaluatedParam(param);
         }
         double currentAvg = sum / ((double) sampleSlideWindow.size());
         log.debug("currentAvg: " + currentAvg);
         log.debug("lastAvg: " + lastAvgEvaluatedParams.get(param));

         if (lastAvgEvaluatedParams.get(param) == 0 || lastAvgEvaluatedParams.get(param) == Double.NaN) {
            log.debug("Updating && Skipping lastAvgEvaluatedParams");
            lastAvgEvaluatedParams.put(param, currentAvg);
         } else {
            double ratio = (currentAvg / lastAvgEvaluatedParams.get(param)) * 100;
            log.debug("ratio: " + ratio);

            double variation = Math.abs(ratio - 100);
            log.debug("variation: " + variation);

            if (variation >= monitoredEvaluatedParams2delta.get(param)) {
               log.trace("Updating lastAvgEvaluatedParams for " + param + " to: " + currentAvg);
               lastAvgEvaluatedParams.put(param, currentAvg);
               log.info("BOUND REACHED (" + param.getKey() + ")");
               return true;
            }

         }
      }
      return false;
   }

   protected boolean add(ProcessedSample sample) {
      return sampleSlideWindow.add(sample);
   }

   public synchronized void addEventListener(WorkloadEventListener listener) {
      listeners.add(listener);
   }

   public synchronized void removeEventListener(WorkloadEventListener listener) {
      listeners.remove(listener);
   }

   protected synchronized void fireEvent(WorkloadEvent.WorkloadEventType type, ProcessedSample sample) {
      int avg = Config.getInstance().getAvgWindowSize();
      ProcessedSample sampleToUse = sample;
      if (avg > 1) {
         log.trace("Going to create an AvgProcessedSample with a window of " + avg);
         try {
            sampleToUse = getTimeWindowAverageProcessedSample(avg);
         } catch (Exception e) {
            e.printStackTrace();
            log.error(("I had an exception while averaging the samples, and I do not do anything"));
            log.error(Arrays.toString(e.getStackTrace()));
            return;
         }
      }
      log.trace("Avg sample successfully created");
      log.info("Sending new event...");
      WorkloadEvent event = new WorkloadEvent(this, type, sampleToUse);
      for (WorkloadEventListener listener : listeners) {
         listener.workloadEventPerformed(event);
      }
   }

   //TODO: create a sampleManager which hides the windw buffer and has interface "addSample" "getSample"
   protected ProcessedSample getTimeWindowAverageProcessedSample(int windowSize) {
      //Be sure that you only take samples relevant to the same replication protocol and scale!
      Iterator<ProcessedSample> it = sampleSlideWindow.iterator();
      ProcessedSample lastSample = it.next(), newP;
      int i = 1;
      List<ProcessedSample> toAvg = new ArrayList<ProcessedSample>();
      toAvg.add(lastSample);
      log.trace("Last sample added to the to-avg list");
      while (it.hasNext() && i < windowSize) {
         newP = it.next();
         if (areCompliant(lastSample, newP)) {
            toAvg.add(newP);
            i++;
            log.trace("Sample++ " + i);
         } else {
            break;    //Compliant samples have to be also consecutive!
         }
      }
      //We should always have enough samples, for now I just put a print to tell me if I have less samples than the one required
      if (i < windowSize) {
         log.trace("I wanted " + windowSize + " samples, but I've only got " + i);
      } else {
         log.trace("I selected the last " + windowSize + " samples to create the avg sample from. Now, going to average them");
      }
      return avg(toAvg);

   }

   private boolean areCompliant(ProcessedSample a, ProcessedSample b) {
      boolean sameRD = a.getParam(Param.ReplicationDegree).equals(b.getParam(Param.ReplicationDegree));
      boolean sameRP = a.getClass().equals(b.getClass());
      boolean sameNodes = a.getParam(Param.NumNodes).equals(b.getParam(Param.NumNodes));
      return sameRD && sameRP && sameNodes;
   }

   private ProcessedSample avg(List<ProcessedSample> toAvg) {
      double size = toAvg.size();
      if (size == 1) {
         log.trace("Only one sample found. Going to use it");
         return toAvg.get(1);
      }
      log.trace("I found " + toAvg + " samples to avg!");
      Map<Param, Object> customParam = new HashMap<Param, Object>();
      Map<EvaluatedParam, Object> customEvaluatedParam = new HashMap<EvaluatedParam, Object>();
      Iterator<ProcessedSample> it = toAvg.iterator();
      //Populate the map with the values from the first
      log.trace("Creating the first sample for the avg");
      ProcessedSample referencePS = it.next(), currentPS;
      log.trace("Now the params");
      for (Param p : Param.values()) {
         if (referencePS.getParams().containsKey(p.getKey())) {
            customParam.put(p, Param.sumParams(p, null, referencePS.getParam(p)));
            paranoid.trace("Initing " + p);
         } else {
            paranoid.trace("Skipping " + p);
         }
      }

      log.trace("Now the evaluated params");
      for (
            EvaluatedParam e : EvaluatedParam.values())

      {
         if (referencePS.getEvaluatedParams().containsKey(e)) {
            customEvaluatedParam.put(e, EvaluatedParam.sumEvaluatedParam(e, null, referencePS.getEvaluatedParam(e)));
            paranoid.trace("Initing " + e);
         } else
            paranoid.trace("Skipping " + e);
      }
      //Populate the map with the sum with the others
      log.trace("Populating the others ");
      int I = 2;
      while (it.hasNext())

      {
         currentPS = it.next();
         log.trace(I + " Params...");
         for (Param p : Param.values()) {
            if (referencePS.getParams().containsKey(p.getKey())) {
               customParam.put(p, Param.sumParams(p, customParam.get(p), currentPS.getParam(p)));
               paranoid.trace("Summing " + p);
            } else
               paranoid.trace("Skipping " + p);
         }
         log.trace(I + " EvaluatedParams...");
         for (EvaluatedParam e : EvaluatedParam.values()) {
            if (referencePS.getEvaluatedParams().containsKey(e)) {
               customEvaluatedParam.put(e, EvaluatedParam.sumEvaluatedParam(e, customEvaluatedParam.get(e), currentPS.getEvaluatedParam(e)));
               paranoid.trace("Summing " + e);
            } else
               paranoid.trace("Skipping " + e);
         }
         I++;
      }

      //Average
      double avgFactor = 1.0D / size;
      log.trace("Going to avg to " + avgFactor);
      log.trace("Params...");
      I = 1;
      for (
            Param p
            : Param.values())

      {
         if (customParam.containsKey(p)) {
            customParam.put(p, Param.multiplyParams(p, customParam.get(p), avgFactor));
            paranoid.trace("Avg-ing " + p);
         } else
            paranoid.trace("Skipping " + p);
      }

      log.trace("EvaluatedParams...");

      for (
            EvaluatedParam e
            : EvaluatedParam.values())

      {
         if (customEvaluatedParam.containsKey(e)) {
            customEvaluatedParam.put(e, EvaluatedParam.multiplyEvaluatedParam(e, customEvaluatedParam.get(e), avgFactor));
            paranoid.trace("Avg-ing " + e);
         } else
            paranoid.trace("Skipping " + e);
      }

      return new CustomSample(null, customParam, customEvaluatedParam);

   }

   private ProcessedSample buildProcessedSample(ProcessedSample template) {
      if (template instanceof TWOPCProcessedSample)
         return new TWOPCProcessedSample(template.getInnerSample());
      if (template instanceof PBProcessedSample)
         return new PBProcessedSample(template.getInnerSample());
      return new TOProcessedSample(template.getInnerSample());


   }

}
