import time, random
import queue, threading

# 生产者和消费者动态生成或者消费
#   知识点： 临界区（加锁解锁）
#           缓冲区（本例为阻塞队列）
#           生产者，消费者同步

# 缓冲区用阻塞队列实现
q = queue.Queue()
# 临界区指的是一个访问共用资源（例如：共用设备或是共用存储器）的程序片段，而这些共用资源又无法同时被多个线程访问的特性
# 生成全局锁，对缓冲区这个共享资源（临界资源）进行加锁解锁操作
lock = threading.Lock()


def Producer(name):
    """
        生产者在0-3秒内动态生产饺子
    """
    count = 1
    global lock

    while True:
        time.sleep(random.randrange(3))
        # 生产者线程进入临界区
        # 修改缓冲区前加锁
        lock.acquire()
        # 缓冲区满，生产者线程阻塞，虽然此处的缓冲区（队列）没有设置maxsize
        q.put(count, block=True)
        print('Producer %s produced 1 jiaozi, has produced %s jiaozi...%i jiaozi left' % (name, count, q.qsize()))
        count += 1
        lock.release()
        # 生产者线程退出临界区


def Consumer(name):
    """
        消费者在0-4秒内动态消费饺子
    """
    count = 1
    global lock

    while True:
        time.sleep(random.randrange(2))
        # 消费者线程进入临界区
        lock.acquire()

        if not q.empty():
            # 缓冲区为空，消费者线程阻塞,虽然此处的缓冲区（队列）没有设置maxsize
            q.get(block=True)

            print('\033[32;1mConsumer %s took 1 jiaozi, has taken %s jiaozi...%i jiaozi left\033[0m' % (
            name, count, q.qsize()))
            count += 1
        lock.release()
        # 消费者线程退出临界区


if __name__ == '__main__':
    p1 = threading.Thread(target=Producer, args=('p1',))
    p2 = threading.Thread(target=Producer, args=('p2',))
    c1 = threading.Thread(target=Consumer, args=('c1',))
    c2 = threading.Thread(target=Consumer, args=('c2',))
    p1.start()
    p2.start()
    c1.start()
    c2.start()