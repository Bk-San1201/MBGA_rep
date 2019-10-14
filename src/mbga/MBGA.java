package mbga;

import draw.DrawSensor;
import setting.Config;
import setting.Sensor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class MBGA {
    final int W = Config.W;
    final int H = Config.H;
    final double VI = Config.VI;
    final double DT = Config.DT;
    final double X0 = Config.X0;
    final double Y0 = Config.Y0;
    final double XN = Config.XN;
    final double YN = Config.YN;
    final int nb = 100; // so nst

    int n;
    double r;
    int k;

    double[] ybest;

    ArrayList<Sensor> sensorLists;
    public double[] xk;
    public double[] yk;

    public void Coordinate() {
        xk = new double[W * 10];
        double t = 0.0;
        for (int i = 0; i < xk.length; i++) {
            xk[i] = t;
            t += 0.1;
        }
        t = 0.0;
        yk = new double[H * 10];
        for (int i = 0; i < yk.length; i++) {
            yk[i] = t;
            t += 0.1;
        }
    }

    public void readData(String fileName) {
        sensorLists = new ArrayList<>();
        FileReader fr;
        try {
            fr = new FileReader(fileName);
            BufferedReader input = new BufferedReader(fr);
            String sf = input.readLine();
            String[] s1 = sf.split(" ");
            n = Integer.parseInt(s1[0]);
            r = Double.parseDouble(s1[1]);
            for (int i = 0; i < n; i++) {
                String temp = input.readLine();
                String[] t = temp.split(" ");
                Sensor Sen = new Sensor((int) Double.parseDouble(t[0]), (int) Double.parseDouble(t[1]),
                        (int) Double.parseDouble(t[2]), (int) Double.parseDouble(t[3]), (int) Double.parseDouble(t[4]),
                        (int) Double.parseDouble(t[5]), (int) Double.parseDouble(t[6]), (int) Double.parseDouble(t[7]),
                        r);
                sensorLists.add(Sen);
            }
            input.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IOE exception!");
            e.printStackTrace();
        }
    }

    public double[] vitri(double[] input, double begin) {
        double[] output = new double[input.length];
        output[0] = begin;
        for (int i = 1; i < input.length; i++) {
            output[i] = output[i - 1] + input[i - 1];
        }
        return output;
    }

    public double[] xSolution() {

        ArrayList<Double> xS = new ArrayList<Double>();
        k = 0;
        double range = 0.0;
        while (range < W) {
            double x = Math.random() * (DT * VI);
            range += x;
            k++;
            xS.add(x);
        }
        double[] xs = new double[xS.size()];
        for (int j = 0; j < xs.length; j++) {
            xs[j] = xS.get(j);
        }
        return xs;
    }

    public double[] xySolution(double[] xs) {
        double S = DT * VI;
        double[] ys = new double[xs.length];
        for (int i = 0; i < ys.length; i++) {
            ys[i] = Math.sqrt(S * S - (xs[i] * xs[i]));
        }
        return ys;
    }

    public void yCoordinate(double[] dy){
        double yt = Config.Y0;
        for(int i = 0; i<dy.length;i++){
            double deltaY = dy[i];
            yt += deltaY;
            if (yt > Config.H){
                yt -= 2 * deltaY;
                dy[i] = -deltaY;
            }
        }
    }

    public static ArrayList<Integer> Random(int n) {
        ArrayList<Integer> intList = new ArrayList<Integer>();
        do {
            int i = (int) (Math.random() * n);
            if (intList.contains(i) == false)
                intList.add(i);
        } while (intList.size() < n);
        return intList;
    }

    public double[] hoanvi(double[] input) {
        ArrayList<Integer> list = Random(input.length);
        double[] output = new double[input.length];
        boolean is = true;
        double value = 0.0;
        do {
            for (int i = 0; i < output.length; i++) {
                output[i] = input[list.get(i)];
                value += output[i];
                if (value > H)
                    is = false;
            }
        } while (is = false);
        return output;
    }

    public double[][] AllSolution(double[] dty) {
        double[][] as = new double[nb][];
        for (int i = 0; i < nb; i++) {
            as[i] = hoanvi(dty);
        }
        return as;
    }

    public ArrayList<Integer> cross(double[] dty1, double[] dty2) {
        double[] yt1 = vitri(dty1, Y0);
        double[] yt2 = vitri(dty2, Y0);
        ArrayList<Integer> dList = new ArrayList<Integer>();
        for (int i = 1; i < dty1.length - 1; i++) {
            if ((yt1[i] > yt2[i] && yt1[i + 1] < yt2[i + 1]) || (yt1[i] < yt2[i] && yt1[i + 1] > yt2[i + 1])) {
                dList.add(i);
            }
        }
        return dList;
    }

    private double[][] copy(double[][] input, int n){
        double[][] output = new double[n + input.length][input[0].length];
        for (int i = 0; i < input.length; i++) {
            output[i] = input[i];
        }
        return output;
    }

    int findMax(double[] input, int begin, int end) {
        int index = end;
        double[] x = xySolution(input);
        x = vitri(x, X0);
        input = vitri(input, Y0);
        double max = -1000000;
        for (int i = begin; i < end; i++) {
            double v = 0;
            for (Sensor s : sensorLists) {
                s.move(i * DT);
                v += s.MEx(x[i], input[i]);
            }
            if (v > max) {
                max = v;
                index = i;
            }
        }
        return index;
    }

    int findMin(double[] input, int begin, int end) {
        int index = end;
        double[] x = xySolution(input);
        x = vitri(x, X0);
        input = vitri(input, Y0);
        double min = Double.MAX_VALUE;
        for (int i = begin; i < end; i++) {
            double value = 0.0;
            for (Sensor s : sensorLists) {
                s.move(i * DT);
                value += s.MEx(x[i], input[i]) * DT;
                s.setDefault();
            }
            if (value < min) {
                min = value;
                index = i;
            }
        }
        return index;
    }

    public boolean Check(double[] input) {
        double value = 0.0;
        boolean is = true;
        for (int i = 0; i < input.length; i++) {
            value += input[i];
            if (value > H) {
                is = false;
                break;
            }
        }
        return is;
    }

    private double[] him_Climming(double[] input) {
        double[] output = new double[input.length];

        int dis = 50;

        do {
            int x, x1, x2;
            x = findMax(input, 0, input.length);

            if (x > dis)
                x1 = findMin(input, x - dis, x);
            else
                x1 = findMin(input, 0, x);
            if (x < input.length - dis)
                x2 = findMin(input, x, x + dis);
            else
                x2 = findMin(input, x, input.length);

            for (int i = 0; i < x1; i++) {
                output[i] = input[i];
            }
            for (int i = x1; i < x2; i++) {
                output[i] = input[x2 + x1 - i];
            }
            for (int i = x2; i < input.length; i++) {
                output[i] = input[i];
            }
        } while (!Check(output));
        return output;
    }

    public double value(ArrayList<Sensor> list, double[] x, double[] y) {
        double value = 0.0;
        // sua closest

        for (Sensor s : list) {
            for (int i = 0; i < x.length; i++) {
                s.move(i * DT);
                value += s.MEx(x[i], y[i]) * DT;
                s.setDefault();
            }
        }
        return value;
    }

    private double[][] Crossover(double[][] input, int nbC) {
        double[][] output = copy(input, nbC);

        Random rand = new Random();
        int count = input.length - 1;
        double[] c1;
        double[] c2;
        int k = 0;
        for (int i = 0; i < nbC / 2; i++) {
            boolean is;
            do {
                is = false;
                int x1 = rand.nextInt(nb);
                int x2 = x1;
                while (x1 == x2) {
                    x2 = rand.nextInt(nb);
                }
                c1 = new double[input[0].length];
                c2 = new double[input[0].length];
                for (int j = 0; j < c1.length; j++) {
                    c1[j] = input[x1][j];
                    c2[j] = input[x2][j];
                }
                ArrayList<Integer> iList = cross(c1, c2);
                if (iList.size() > 1) {
                    is = true;
                    k = rand.nextInt(iList.size() - 1) + 1;
                    k = iList.get(k);
                }
            } while (is = false);

            double[] c3 = new double[input[0].length];
            double[] c4 = new double[input[0].length];
            for (int j = 0; j < k; j++) {
                c3[j] = c1[j];
                c4[j] = c2[j];
            }
            for (int j = k; j < c3.length; j++) {
                c3[j] = c2[j];
                c4[j] = c1[j];
            }
            count++;
            output[count] = c3;
            count++;
            output[count] = c4;

        }

        return output;
    }

    private double[][] Mutation(double[][] input, int nbM) {
        double[][] output = copy(input, nbM);

        Random rand = new Random();
        int count = input.length - 1;
        for (int i = 0; i < nbM; i++) {
            double[] y = him_Climming(input[i]);
            count++;
            output[count] = y;
        }

        return output;
    }

    private double[][] Selection(double[][] input) {
        double[][] output = new double[nb][];
        double[] f = new double[input.length];
        for (int i = 0; i < f.length; i++) {
            double[] yt = vitri(input[i], Y0);
            double[] dtx = xySolution(input[i]);
            double[] xt = vitri(dtx, X0);
            f[i] = value(sensorLists, xt, yt);
        }
        for (int i = 0; i < nb; i++) {
            double min = Double.MAX_VALUE;
            int index = 0;
            for (int j = 0; j < f.length; j++) {
                if (f[j] < min) {
                    min = f[j];
                    index = j;
                }
            }
            output[i] = input[index];
            f[index] = Double.MAX_VALUE;
        }

        return output;
    }

    public double[] searchGA(int iter, double[] dty){
        double[][] init = AllSolution(dty);
        double[] y_best = new double[init[0].length];
        for (int i = 0; i < iter; i++) {
            double[][] cr = Crossover(init, 20);
            double[][] mu = Mutation(cr, 10);
            double[][] se = Selection(mu);
            init = se;
            if (i == iter - 1) {
                for (int j = 0; j < se[0].length; j++) {
                    y_best[j] = se[0][j];
                }
            }
        }
        return y_best;
    }

    public double result(double[] dty) {
        ybest = searchGA(200, dty);
        double[] yt = vitri(ybest, Y0);
        double[] xt = vitri(xySolution(ybest), X0);
        double value = value(sensorLists, xt, yt);
        return value;

    }

    public static void main(String[] args) {
        System.out.println("MBGA program....");
        MBGA mbga = new MBGA();
        mbga.readData("./DATA/50/test10.txt");

        double[] dtx = mbga.xSolution();
        double[] dty = mbga.xySolution(dtx);
        mbga.yCoordinate(dty);

        long begin = Calendar.getInstance().getTimeInMillis();
        double kq = mbga.result(dty);
        long end = Calendar.getInstance().getTimeInMillis();
        double time = (end - begin);

        DrawSensor drawSensor = new DrawSensor();
        drawSensor.draw(mbga.sensorLists, mbga.ybest);

        System.out.println("Time: " + time);
        System.out.println("Value: " + kq);
    }
}
