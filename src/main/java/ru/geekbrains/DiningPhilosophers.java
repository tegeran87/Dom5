package ru.geekbrains;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class DiningPhilosophers {
    private static final int NUM_PHILOSOPHERS = 5; // Количество философов
    private final Philosopher[] philosophers;
    private final Fork[] forks;
    private final ReentrantLock table = new ReentrantLock();

    public DiningPhilosophers() {
        philosophers = new Philosopher[NUM_PHILOSOPHERS];
        forks = new Fork[NUM_PHILOSOPHERS];

        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            forks[i] = new Fork();
        }

        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            philosophers[i] = new Philosopher(i, forks[i], forks[(i + 1) % NUM_PHILOSOPHERS]);
            new Thread(philosophers[i]).start();
        }
    }

    class Philosopher implements Runnable {
        private final int id;
        private final Fork leftFork;
        private final Fork rightFork;
        private int mealsEaten = 0;

        public Philosopher(int id, Fork leftFork, Fork rightFork) {
            this.id = id;
            this.leftFork = leftFork;
            this.rightFork = rightFork;
        }

        @Override
        public void run() {
            while (mealsEaten < 3) {
                table.lock();
                try {
                    leftFork.pickUp();
                    rightFork.pickUp();
                    eat();
                    rightFork.putDown();
                    leftFork.putDown();
                } finally {
                    table.unlock();
                }
                think();
            }
        }

        private void eat() {
            System.out.println("Философ " + id + " ест.");
            mealsEaten++;
            System.out.println("Философ " + id + " поел " + mealsEaten + " раз.");
        }

        private void think() {
            System.out.println("Философ " + id + " размышляет.");
        }
    }

    class Fork {
        private final Semaphore semaphore = new Semaphore(1);

        public void pickUp() {
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        public void putDown() {
            semaphore.release();
        }
    }
}
