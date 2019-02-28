import threading
import queue
import time


def producer():
    for i in range(10):
        q.put("饺子 %s" % i)

    print("开始等待所有的饺子被取走...")
    # 把操作队列的线程join到生产者线程，待这些线程结束后，生产者线程再往下执行。
    q.join()
    print("所有的饺子被取完了...")


def consumer(n):
    while q.qsize() > 0:
        print("%s 取到" % n, q.get())
        q.task_done()
        time.sleep(1)


q = queue.Queue()

p1 = threading.Thread(target=producer, )
p1.start()

p2 = threading.Thread(target=consumer, args=('Allen1',))
p2.start()

p3 = threading.Thread(target=consumer, args=('Allen2',))
p3.start()