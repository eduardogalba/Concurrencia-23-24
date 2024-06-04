package segundoParcial;

import org.jcsp.lang.CSProcess;
import org.jcsp.lang.Channel;
import org.jcsp.lang.ChannelOutput;
import org.jcsp.lang.One2OneChannel;
import org.jcsp.lang.Parallel;

public class Junio2016 {

    static One2OneChannel ch1 = Channel.one2one();
    static One2OneChannel ch2 = Channel.one2one();

    public static void main(String[] args) {
        new Parallel(
                new CSProcess[] {
                        new P1(),
                        new P2(), 
                        new P3()
                }).run();
    }

    public static class P1 implements CSProcess {

        @Override
        public void run() {
            One2OneChannel chResp = Channel.one2one();
            ch1.out().write(chResp.out());
            chResp.in().read();
            System.out.println("A");
        }

    }

    public static class P2 implements CSProcess {

        @Override
        public void run() {
            ch2.out().write(ch1.in().read());
            System.out.println("B");
        }
    }

    public static class P3 implements CSProcess {

        @Override
        public void run() {
            ChannelOutput resp = (ChannelOutput) ch2.in().read();
            resp.write(null);
            System.out.println("C");
        }
    }

}