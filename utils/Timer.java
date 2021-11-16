package b451_Project.utils;

/**
 * repeatedly executes task in fixed rate
 * */

public class Timer {

    private boolean busyWait = false;
    private Thread timerThread;
    private volatile  boolean threadFlag;

    public Timer(boolean busyWait)
    {
        this.busyWait = busyWait;
    }

    // callback
    public interface Task
    {
        public void run(double deltaT);
    }

    /**
     * start timer
     * @param task runnable task
     * @param interval interval in seconds
     * */
    public void start(Task task, double interval)
    {
        threadFlag = true;
        long desiredWaitTimeNano = (long)(interval * 1000000000);

        timerThread = new Thread(() ->
        {

            //time interval between two frames
            long time0 = System.nanoTime();
            long time1 = time0;
            long deltaTNano = 0;

            long waitTime = 0;
            //refresh scene at constant rate
            while(threadFlag)
            {
                waitTime = System.nanoTime();

                time0 = System.nanoTime();
                deltaTNano = time0 - time1;
                task.run((double)deltaTNano/ 1000000000.0);
                time1 = time0;

                //busy wait or blocking wait
                waitTime = System.nanoTime() - waitTime;
                if(busyWait)
                {
                    waitTime =  desiredWaitTimeNano - waitTime + System.nanoTime();
                    while(waitTime > System.nanoTime()) {};
                }else
                {
                    try {

                        if((desiredWaitTimeNano - waitTime) > 0)
                        {
                            Thread.sleep( (desiredWaitTimeNano - waitTime) / 1000000);
                        }
                    } catch (InterruptedException e) {}
                }
            }


        });

        timerThread.start();
    }

    /**
     * stop timer
     * */
    public void stop()
    {
        threadFlag = false;
    }

}
