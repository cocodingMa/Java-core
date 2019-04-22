package test;
public class Test {

    public static void main(String[] args) throws InterruptedException {
        MajusculeABC maj = new MajusculeABC();
        Thread t_a = new Thread(new Thread_ABC(maj , 'A'));
        Thread t_b = new Thread(new Thread_ABC(maj , 'B'));
        Thread t_c = new Thread(new Thread_ABC(maj , 'C'));
        t_a.start();
        Thread.sleep(100);
        t_b.start();
        Thread.sleep(100);
        t_c.start();
    }
}