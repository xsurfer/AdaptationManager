package eu.cloudtm.actuators.deltacloud;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/21/13
 */
public class FixedExecutionRunnable<T extends CallableSeveralTimes<Boolean>> implements Runnable {

    private final static Log log = LogFactory.getLog(FixedExecutionRunnable.class);

    private final AtomicInteger runCount = new AtomicInteger();
    private final T delegate;
    private final int maxRunCount;
    private ScheduledExecutorService exec;
    private long period;
    private TimeUnit timeUnit;
    private long maxTimeExecution = 60;
    private CountDownLatch latch;
    private Boolean returnedValue;

    public FixedExecutionRunnable(T delegate, int maxRunCount, long _period, TimeUnit _timeUnit, CountDownLatch _latch) {
        this.delegate = delegate;
        this.maxRunCount = maxRunCount;
        period = _period;
        timeUnit = _timeUnit;
        delegate.setIterations(maxRunCount);
        latch = _latch;
    }

    public FixedExecutionRunnable(T delegate, int maxRunCount, long _period, TimeUnit _timeUnit) {
        this(delegate, maxRunCount, _period, _timeUnit, null);
    }

    @Override
    public void run() {
        ScheduledFuture<Boolean> response;
        try {
            do{
                exec = Executors.newSingleThreadScheduledExecutor();
                response = exec.schedule(delegate, period, timeUnit);
                exec.shutdown();
                while ( !exec.awaitTermination(maxTimeExecution, TimeUnit.SECONDS) ){
                    log.info("Timeout. Waiting next " + maxTimeExecution);
                }

            } while ( runCount.incrementAndGet() < maxRunCount && !response.get() );

            log.info("runCount = " + runCount.get());
            log.info("returnedValue = response = " + response.get());

            returnedValue = response.get();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        if(latch!=null)
            latch.countDown();
    }

    public Boolean isTerminated(){
        return (returnedValue ==null) ? false : true;
    }

    /**
     *
     * @return true if non raggiunto il numero di volte
     *         false if raggiunto il numero di volte
     * @throws FixedExecutionCallableNotTerminated
     */
    public Boolean get() throws FixedExecutionCallableNotTerminated {
        if( returnedValue == null )
            throw new FixedExecutionCallableNotTerminated();
        return returnedValue;
    }


}