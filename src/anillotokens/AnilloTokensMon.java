package anillotokens;

import java.util.HashMap;
import java.util.Map;

import es.upm.babel.cclib.Monitor;

public class AnilloTokensMon implements AnilloTokens {
    private static final int N = 50;

    /* Declaracion estado del recurso */
    Map<Integer, Boolean> self;

    /* Declaracion elementos para sincronizacion de procesos */
    Monitor mutex;

    Monitor.Cond[] espAdquirir;
    Monitor.Cond[] espSoltar;

    public AnilloTokensMon() {
        self = new HashMap<>();

        for (int i = 0; i < N; i++)
            self.put(i, false);

        mutex = new Monitor();
        espAdquirir = new Monitor.Cond[N];
        espSoltar = new Monitor.Cond[N];

        for (int i = 0; i < N; i++) {
            espAdquirir[i] = mutex.newCond();
            espSoltar[i] = mutex.newCond();
        }

    }

    @Override
    public void adquirir(int i) {
        if (i < 0 || i >= N)
            throw new IllegalArgumentException();

        mutex.enter();
        if (self.get(i) || self.get((i + 1) % N) || espAdquirir[i].waiting() > 0
                || espAdquirir[(i + 1) % N].waiting() > 0)
            espAdquirir[i].await();

        self.put(i, true);
        self.put((i + 1) % N, true);

        desbloqueo();

        mutex.leave();

    }

    @Override
    public void soltar(int i) {
        if (i < 0 || i >= N)
            throw new IllegalArgumentException();

        mutex.enter();
        if (!self.get(i) || !self.get((i + 1) % N) || espSoltar[i].waiting() > 0
                || espSoltar[(i + 1) % N].waiting() > 0)
            espSoltar[i].await();

        self.put(i, false);
        self.put((i + 1) % N, false);

        mutex.leave();
    }

    private void desbloqueo() {
        boolean enc1 = false;
        boolean enc2 = false;
        for (int i = 0; i < N && !enc1 && !enc2; i++) {
            enc1 = !self.get(i) && !self.get((i + 1) % N) && espAdquirir[i].waiting() > 0;
            enc2 = self.get(i) && self.get((i + 1) % N) && espSoltar[i].waiting() > 0;

            if (enc1)
                espAdquirir[i].signal();
            else if (enc2)
                espSoltar[i].signal();
        }
    }

}
