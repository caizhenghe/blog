#  Java并发

| tag  | author     | date       | history                                        |
| ---- | ---------- | ---------- | ---------------------------------------------- |
| Java | caizhenghe | 2018-03-03 | create doc                                     |
| java | caizhenghe | 2018-07-31 | Update thread content                          |
| java | caizhenghe | 2018-08-01 | Add Join/Interrupt and Catch exception chapter |
| java | caizhenghe | 2018-08-02 | Add Mutex Chapter                              |
| java | caizhenghe | 2018-08-05 | Add Atomic Chapter                             |
| java | caizhenghe | 2018-08-06 | Interrupt thread in block state                |
| java | caizhenghe | 2018-08-07 | Check interrupt state                          |
| java | caizhenghe | 2018-08-12 | Add synchronized chapter                       |
| java | caizhenghe | 2018-08-21 | fix doc structure                              |
| java | caizhenghe | 2018-08-26 | add CountDownLatch/CyclicBarrier chapter       |
| java | caizhenghe | 2018-08-27 | add new component of java SE5 chapter          |

[TOC]

## 线程的状态

### 五种状态

线程有五种状态：新建状态、可运行状态（就绪状态）、运行状态、阻塞状态、死亡状态。

- 新建状态：使用new创建一个线程时，在start()之前，线程处于新建状态。
- 就绪状态：调用了start()方法，但未真正获取CPU时间之前，线程处于就绪状态。还有些情况会进入就绪状态：
  - 运行状态的线程调用了**yield()**方法后，有可能回到就绪状态（CPU重新选择一个就绪线程执行，这个线程有可能依然是它）。
  - 被挂起的线程调用**notify()**方法回到就绪状态。
  - 睡眠的线程达到时限后回到就绪状态。
- 运行状态：线程真正获取CPU时间之后，即处于运行状态，开始执行任务的run()方法。（Thinking In Java中并没有定义该状态）
- 阻塞状态：进入阻塞状态的情况有很多：
  - 调用**sleep()**进入睡眠状态
  - 调用**wait()**将线程挂起
  - 调用一个在I/O被阻塞的操作
  - 试图得到一个锁且该锁被其它线程持有
- 死亡状态：当run()方法执行完毕后，线程进入死亡态，此时线程对象不一定会被gc（可能还有强引用持有该对象）。可以通过isAlive()方法判断线程的状态（true表示就绪状态/运行/阻塞状态，false表示新建/死亡状态）。

> Tips:线程会注册自己，在任务执行过程中，线程对象不会被gc？

### Sleep和Wait的区别

- Sleep属于Thread的方法，Wait属于Object的方法
- Sleep不会释放锁（**yield**同样如此），当某个持有锁的线程睡眠时，其他线程依旧无法获得这个锁；Wait会释放锁，当某个持有锁的线程挂起时，其他线程可以获得这个锁。

## 多线程的特性

### 原子性

概念：原子性操作一定能在切换到其它线程执行之前执行完毕。

对**除了double和long的基本数据类型**进行简单的读取和赋值操作被认为是安全的原子性操作。JVM会将64位数据（double和long）的读取和写入当作两个分离的32位操作来执行（**volatile关键字可以让long和double也获得原子性**）。

在java中，**递增不是原子性操作**。通过JVM指令可以看到，递增操作会产生get和put，它们之间还有一些其它指令，是有可能被其它任务打断的。

> Notice：在非Volatile域上的原子操作不必刷新到主存中，其它读取该域的任务也不必看到新值，还是会发生线程问题，所以**原子操作必须和volatile结合使用**。

**原子类**

AtomicInteger、AtomicLong、AtomicRefrence等类提供了一系列机器级别的原子性方法，性能比同步操纵要更好。

```java
private AtomicInteger i = new AtomicInteger(0);
i.get(); // 获取值
i.AddAndGet(2); // 自增2
```

### 可见性

概念：对一个域作出的修改能立即被其它任务看到。

**volatile**

volatile可以保证线程的可见性（Java SE5之后，这个关键字才生效）。如果用volatile修饰一个域，对这个域进行写操作，所有的读操作都能看到这个修改（volatile域会被立即写到主存中，而读操作就发生在主存中）。**但是volatile不能保证原子操作**。

当一个域的值依赖于它之前的状态（递增）或者其它域的值，volatile是无效的（FIXME：此处描述的应该是无法保证原子性）。唯一安全的情况是**类中只有一个可变的域**。

volatile关键字会告诉编译器**不要执行移除读写操作的优化**（这些优化的目的是用线程中的局部变量维护对这个域的精确同步）。

**全写和回写机制**

// TODO

## 异步任务的返回值

通过Callable取代Runnable来产生返回值（线程池也可以submit一runnable并返回Future<?>对象，**该对象不包含返回值但可以用于中断线程**）。

```java
ExecutorService exec = Executors.newCachedThreadPool();
Future<String> result = exec.submit(new Callable<String>() {
    @Override
    public String call() {
        return null;
    }
});

// block until completion
result.get();

// check if complete
result.isDone();
```



## 线程优先级

线程调度器会优先选择优先级高的线程。

设置方式：Thread的**setPriority()**方法，设置的时机是在**run()方法的起始位置**。

JDK制定的优先级与操作系统的映射不是很好，通常只使用三种优先级：MAX_PRIORITY、NORM_PRIORITY、MIN_PRIORITY。

## 后台线程

当所有非后台线程结束时，程序就终止了，同时会杀死所有后台线程（立即停止，无法确保后台线程中的finally代码块能够执行完毕）。

设置方式：Thread的**setDaemon()**方法。可以通过**isDaemon()**方法来判断一个线程是否是后台线程。

## 捕获异常

正常情况下，一旦异常逃出任务的run()方法，它就会向外传播到控制台，因此我们不能在run()方法之外捕获异常。

但是我们可以自定义一个ThreadFactory，用于创建某种特定的Thread，我们通过**setUncaughtExceptionHandler()**方法给Thread设置了未捕获异常的监听器，当线程因为异常而即将退出时，会执行该Handler。

```java
ExecutorService exec = Executors.newCachedThreadPool(new ThreadFactory() {
    @Override
    public Thread newThread(@NonNull Runnable r) {
        Thread t = new Thread(r);
        t.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                // do something
            }
        });
        return t;
    }
});
```

当所有的线程在捕获异常时均执行同一操作时，可以使用Thread的静态方法**setDefaultUncaughtExceptionHandler()**设置Handler。

```java
Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        // do something
    }
});
```

> Tips：前者的优先级比后者高。

## ThreadLocal

在不同的线程中存储不同的数据副本。ThreadLocal对象通常当作静态域存储，使用它时不需要加锁。

```java
private static ThreadLocal<Integer> sThreadLocal = new ThreadLocal<Integer>(){
    private Random random = new Random(47);
    @Override
    protected synchronized Integer initialValue() {
        // FIXME：initialValue方法是否有必要添加synchronized关键字？
        // 重写初始化默认值的方法
        return random.nextInt(1000);
    }
};

sThreadLocal.get();  // 获取当前线程对应的数据副本
```



## 线程池

// TODO

## 线程插队

### Join

- 线程A可以在线程B中调用**A.join()**方法，此时线程B被挂起（从内部实现上看，B调用了wait方法，因此会将持有的锁释放），直到线程A结束才恢复。该方法可以捕获**InterruptedException**异常。
- 可以在另一个线程中调用**A.interrupt()**方法，打断线程A，提前恢复线程B。

## 终止任务

### interrupt

使用cancel变量可以使程序在运行状态被终结，但是不能使任务在阻塞状态被终结。此时可以使用之前提到过的Thread的**interrupt()**方法，通过这个方法可以在任务中抛出一个InterruptExeption异常。

> Notice：**sleep()和wait()阻塞**是可以被终结的，但I/O和synchronized块上的阻塞无法被终结。不过我们可以通过**关闭底层资源**（例如InputStream）的方式来终结I/O阻塞的任务。

### lock和lockInterruptibly

interrupt()方法无法中断synchronized块互斥造成的阻塞，**但是可以中断ReentranLock的lockInterruptibly()方法引起的互斥阻塞**。

- lock：优先考虑获取锁，待获取锁成功后，才响应中断。
- lockInterruptibly：优先考虑响应中断，而不是响应锁的普通获取或重入获取。

### 检查中断状态

Thread的interrupt()方法只是将该任务置为打断状态，只能在阻塞或即将进入阻塞状态时（如sleep和wait）抛出异常并退出任务，在正常状态下并不生效。若想在正常状态下退出任务，我们可以使用Thread的**interrupted()**方法检查中断状态并终结任务。检查中断状态有两种方式，分别是静态方法interrupted()和非静态方法isInterrupted()。

- interrupted：检查当前线程的打断状态，并且会重置打断标识位。
- isInterrupted：检查调用该方法的线程对象的打断状态，并且不会重置打断标识位。

### 线程池的终结方式

#### shutdown和shutdownNow

- 如果想通过Executor来打断任务，可以使用**shutdownNow()**方法，该方法会对线程池中所有线程执行interrupt()。
- shutdown()方法允许正在执行的任务执行完毕，但是不允许线程池执行新任务；shutdownNow()方法要求所有任务（包括正在执行的任务）立即停止。

#### cancel

如果想通过Executor打断某个单一的线程。可以使用submit提交一个任务，该方法会返回一个Future对象，可以执行该对象的**cancel(true)**方法来打断线程。

#### awaitTerminate

若所有任务在超时时间之前结束，则返回true，不然返回false。

## 互斥

### synchronized

1. 修饰方法：本质上是锁住了类的this对象，当一个线程调用了某个对象中的synchronized方法时，**这个对象的所有synchronized方法**都不能被其它线程调用。如果修饰的静态方法，这个类的**所有对象中的所有synchronized方法**在同一时间都只能被一个线程独占。

   ```java
   public synchronized void get(int i, String name){}
   ```

   - 将域设置为private很有必要，否则synchronized关键字无法防止其它任务直接访问域。

- 一个任务可以**多次获得对象的锁**（比如在一个synchronized方法中调用同个对象的另一个synchronized方法），JVM会对其计数，当该对象被最终释放，即计数为0时，其它任务才可以获得该对象的锁。

1. 修饰代码块：跟修饰方法类似，新建一个对象，在要求互斥的代码块上锁住这个对象，在同一时间就只有一个线程访问该代码块了。这个新建对象不一定是Object类型，任意类型都可以，在一些对内存要比较抠门的项目中，可以新建一个byte[]类型的对象。

   ```java
   public void get(int i, String name) {
           synchronized (obj) {
               // do something
           }
   }
   ```

修饰静态方法（TODO）

### Lock

优点：

- 当使用synchronized关键字时，若抛出异常，没有机会去做清理工作；而Lock可以在finally子句中进行清理工作。
- 当程序有线程公平性需要时，可以使用这种载入式的加锁方式。（可以解决一些复杂的场景，比如尝试获取锁且最终获取失败；尝试获取一段时间锁然后放弃它）

缺点：相较于synchronized，Lock需要更大的开销，代码量更大，更容易引起错误。

// TODO 了解ReentranLock使用的具体场景

```java
private Lock mLock = new ReentrantLock(); 
public int method() {
    mLock.lock();
    // 尝试获取锁
    // boolean captured = mLock.tryLock();
    // boolean captured = mLock.tryLock(2, TimeUnit.SECONDS);
    try {
        // do something
        // return必须放在try子句中，以确保unlock()不会过早发生，从而将数据暴露给其它任务
        return 0;
    } finally {
        // if(captured)
        // 无论有无interrupt发生，均会执行finally代码块，通常用于清理资源
        mLock.unlock();
    }
}
```

### AQS

#### 公平锁

#### 非公平锁

## 同步

### wait和notify

通过wait()方法将任务挂起并释放锁，此时其它任务可以获取锁并调用synchronized修饰的方法。通过notify()/notifyAll()方法或者**等待指定的时间**可以唤醒挂起的任务。

**wait通常使用while检查而不是if**，避免线程机制自身的不稳定导致异常。

**notify和notifyAll**

- notifyAll可以唤醒对应锁的所有任务，更加安全。
- notify是notifyAll的一种优化，notify只会唤醒**一个**等待对应锁的任务。

> Notice：wait()、notify()、notifyAll()方法必须在**同步控制块**中执行，不然会报IllegalMonitorStateException。

使用wait和notify的例子如下：

```java
class WaitPerson implements Runnable {
    Restaurant res;
    Meal meal;

    WaitPerson(Restaurant res) {
        this.res = res;
        meal = res.meal;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                synchronized (this) {
                    while (meal == null) {
                        wait();
                    }
                }

                // eat meal
                synchronized (res.chef) {
                    meal = null;
                    res.chef.notify();
                }

                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            Log.d("tag", "Interrupted!");
        }
    }
}

class Chef implements Runnable {
    Restaurant res;
    Meal meal;
    int count = 0;

    Chef(Restaurant res) {
        this.res = res;
        meal = res.meal;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                synchronized (this) {
                    while (meal != null) {
                        wait();
                    }
                }

                if(++count == 10){
                    res.service.shutdownNow();
                }

                // create meal
                synchronized (res.person) {
                    meal = new Meal();
                    res.person.notify();
                }

                TimeUnit.MILLISECONDS.sleep(100);
            }
        } catch (InterruptedException e) {
            Log.d("tag", "Interrupted!");
        }
    }
}

class Meal {}

class Restaurant {
    public Meal meal;
    public Chef chef = new Chef(this);
    public WaitPerson person = new WaitPerson(this);
    ExecutorService service = Executors.newCachedThreadPool();
    public Restaurant(){
        service.execute(chef);
        service.execute(person);
    }
}
```

### await和signal

Lock的Condition元素提供了await和signal方法，与wait和notify方法类似，例子如下：

```java
class Car {
    boolean waxOn = false;
    Lock lock = new ReentrantLock();
    Condition condition = lock.newCondition();

    public void waxed() {
        lock.lock();
        try {
            waxOn = true;
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void buffed() {
        lock.lock();
        try {
            waxOn = false;
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }


    public void waitingForWaxing() throws InterruptedException {
        lock.lock();
        try {
            while (!waxOn)
                condition.await();
        } finally {
            lock.unlock();
        }
    }

    public void waitingForBuffing() throws InterruptedException {
        lock.lock();
        try {
            while (waxOn)
                condition.await();
        } finally {
            lock.unlock();
        }
    }
}

class WaxOn implements Runnable {
    Car car = new Car();

    WaxOn(Car car) {
        this.car = car;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                car.waitingForBuffing();
                car.waxed();
                TimeUnit.MILLISECONDS.sleep(100);
            }
        } catch (InterruptedException e) {

        }
    }
}

class WaxOff implements Runnable {
    Car car = new Car();

    WaxOff(Car car) {
        this.car = car;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                car.waitingForWaxing();
                car.buffed();
                TimeUnit.MILLISECONDS.sleep(100);
            }
        } catch (InterruptedException e) {

        }
    }
}
```



### 信号错失

为了防止信号错失导致wait的任务没有被notify唤醒而一直死锁，要将对于条件的判断放在synchronized代码块中。例子如下：

```java
T1:
synchronized(object){
    isWait = false;
    object.notify();
}

T2: 错误范例
while(isWait) {
	synchronized(object) {
		object.wait();
	}
}

T2: 正确范例
synchronized(object) {
	while(isWait) {
		object.wait();
	}
}
```

### 同步队列

wait和notify实现的线程同步非常低级，每次交互都握手。相比之下，同步队列（LinkedBlockingQueue、ArrayBlockingQueue）要简单可靠的多，它将对象的执行串行化了。

**优点：**

- 每次可以消费或生产多个对象。每次只允许一个任务进行写操作。
- 当队列为空时，可以自动挂起消费者任务；当队列有更多元素时，可以恢复消费者任务。
- 消除类与类之间的耦合，每个任务只与它的BlockingQueue通信。

### 管道

管道与BlockingQueue类似（没有更多数据时，管道将自动阻塞），可以通信buffer格式的数据。例子如下：

```java
class Sender implements Runnable {
    private PipedWriter out = new PipedWriter();

    public PipedWriter getPipedWriter() {
        return out;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                for (char c = 'A'; c < 'Z'; c++) {
                    out.write(c);
                    TimeUnit.MILLISECONDS.sleep(500);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Receiver implements Runnable {
    private PipedReader in;

    public Receiver(Sender sender) throws IOException {
        in = new PipedReader(sender.getPipedWriter());
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                TPLog.d("tag", (char) in.read() + ", ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
```

> Notice：**PipedReader是可以被中断的**，这是与其他I/O操作不一致的地方。

### 死锁

// TODO 哲学家就餐问题

## 新类库中的构件

### CountDownLatch

用途：一个或多个任务等待其他一系列任务执行完毕后再开始执行。初始化CountDownLatch时设置一个Size用于计数，调用对象的countDown()方法可以将计数减1，调用对象的await()方法将进入阻塞状态，当计数变成0时所有处于阻塞状态的任务将被唤醒。

缺点：计数只能在初始化时设置一次，没有重置计数的功能。

> Tips：多个任务中同时调用**静态Random对象的Random.nextInt()**是安全的。

### CyclicBarrier

用途：一组任务并行的执行工作，在进行下一个步骤前进行等待，直至所有任务都完成。它使得所有任务可以一致的向前移动。比CountDownLatch更加优秀的是，该控件可以多次重用。

用法：初始化CyclicBarrier时传入两个参数：任务数量和栅栏动作（所有任务到达栅栏前时会触发该动作），任务调用await()方法后进入阻塞状态，当指定数量的任务执行了await()方法后，所有处于阻塞状态的任务被唤醒。

解除阻塞的情况有：

- 最后一个到达栅栏的线程调用了await()方法。
- 当前或其他线程被中断，被打断的线程会抛出InterruptedException异常，其他线程抛出BrokenBarrierException异常。
- 当前或其他正在等待的线程超时（await可以指定超时时间），超时线程会抛出TimeoutException异常，其他线程抛出BrokenBarrierException异常。
- 某个线程调用了该CyclicBarrier对象的reset()方法，将抛出BrokenBarrierException异常。

> 如果当前线程是最后一个到达栅栏的线程，在其他线程解除阻塞之前，在**当前线程**执行栅栏动作。
>
> CyclicBarrier使用了all-or-none的破坏模式，若其中一个线程被中断，所有等待线程都将被中断。

### DelayQueue

是无界的BlockingQueue，有序，队头元素的延迟到期时间最长（最紧急）。队列中的每个元素均为实现了Delayed接口的对象，对象在到期时才能被取走(否则poll()将返回null)。

getDelay()和compareTo()为Delayed接口的两个方法，其中getDelay()表示多长时间后该任务到期（或者任务在多长时间之前已经到期）；compareTo()用于任务的排序，当前任务在队列任务之前执行时返回负数，之后执行返回正数，否则返回0。

**poll和take的区别**

当队头元素没有到期时，poll返回null，take则会阻塞的等待队头元素到期为止。

```java
class DelayedTask implements Runnable, Delayed {
    private static int counter = 0;
    private final int id = counter++;
    private final int delta;
    private final long trigger;

    public DelayedTask(int delayInMilliseconds) {
        delta = delayInMilliseconds;
        trigger = delta + System.currentTimeMillis();
    }

    @Override
    public long getDelay(@NonNull TimeUnit unit) {
        return trigger - System.currentTimeMillis();
    }

    @Override
    public int compareTo(@NonNull Delayed o) {
        DelayedTask that = (DelayedTask) o;
        if (trigger < that.trigger) return -1;
        if (trigger > that.trigger) return 1;
        return 0;
    }

    @Override
    public void run() {
        Log.d("TAG", this.toString());
    }

    @Override
    public String toString() {
        return String.format("[%1$s-4d] ", delta) + " Task " + id;
    }
}

class DelayedTaskConsumer implements Runnable {
    private DelayQueue<DelayedTask> queue;

    public DelayedTaskConsumer(DelayQueue<DelayedTask> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                queue.take().run();
                // queue.poll().run();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Demo {
    public void main() {
        Random rand = new Random(47);
        ExecutorService exec = Executors.newCachedThreadPool();
        DelayQueue<DelayedTask> queue = new DelayQueue<>();
        for (int i = 0; i < 20; i++) {
            queue.put(new DelayedTask(rand.nextInt(5000)));
        }
        exec.execute(new DelayedTaskConsumer(queue));
        exec.shutdown();
    }
}
```



### PriorityBlockingQueue

### Semaphore

### Exechanger

// TODO

## 性能调优

### 加锁机制对比

### CAS

**概念**

Compare-and-Swap，比较并替换，是乐观锁的一种机制。每次完成某项操作时假设没有冲突而不加锁，如果因为冲突失败就重试，直到成功为止。

**特点**

不具备排他性，在冲突不多的情况下，效率更高。比较典型的例子是原子类（如AutomicLong）。

**原理**

当想要修改数据时，会同时传递旧值和新值，如果旧值与当前数据不对应，则说明数据已经被其他线程修改，更新旧值并返回失败，重新尝试修改数据，直到成功为止（自旋）。

### 自旋锁

### 免锁容器

#### 各种Map实现

### ReadWriteLock

当读操作较频繁，写操作不频繁时，使用ReadWriteLock有助于提升并发性能。读锁可以被多个任务持有，而写锁是独占的。如果某个任务持有写锁，其他任务都无法持有读锁。

**获取顺序**

- 非公平模式（默认）：读锁和写锁获取的顺序不确定，主张竞争获取，可能会延缓一个或多个读写线程，但是吞吐量高。

- 公平模式：以队列的顺序获取锁，等待时间最长的线程会分配到锁。// TODO 读写锁是否位于同个队列？若不在同个队列它们的优先级如何？

**特性**

- 可重入：允许读/写锁可重入（读锁中可获取读锁，写锁中可获取写锁），写锁可以获得读锁，读锁不可以获得写锁。
- 锁降级：写锁可以降级为读锁（实现方式是在持有写锁之后持有读锁，然后释放写锁，此时线程依然持有读锁）

**源码分析**

// TODO

## 