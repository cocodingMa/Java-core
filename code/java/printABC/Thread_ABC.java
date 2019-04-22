package test;
public class Thread_ABC implements Runnable {
    MajusculeABC majusculeABC;

    Object prev;
    Object self;

    char name;


    Thread_ABC(MajusculeABC majusculeABC, char a){
        this.majusculeABC = majusculeABC;
        this.name = a;
        if(a == 'A'){
            prev = majusculeABC.C;
            self = majusculeABC.A;
        }
        if(a == 'B'){
            prev = majusculeABC.A;
            self = majusculeABC.B;
        }
        if(a == 'C'){
            prev = majusculeABC.B;
            self = majusculeABC.C;
        }
    }

    @Override
    public void run() {
        int count = 10;
        while (count > 0) {
            synchronized (prev) {
                synchronized (self) {
                    System.out.print(name);
                    count--;
                    self.notify();
                }
                try {
                    prev.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
