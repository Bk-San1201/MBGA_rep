package draw;

import setting.Config;
import setting.Sensor;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class DrawSensor {
    private static int i = 0;
    private static boolean check = false;
    public void draw(ArrayList<Sensor> sensorLists, double[] indi){
        JFrame frame = null;
        for(i=0;i<Config.MAX_LEN;i++){
            if(frame == null){
                frame = new JFrame();
                JPanel panel = new JPanel(){
                    public void paintComponent(Graphics g){
                        Graphics2D g2 = (Graphics2D) g;
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(Color.GREEN);
                        double x, y, r;
                        for (Sensor s : sensorLists) {
                            x = s.getCenter().getX() * 10;
                            y = s.getCenter().getY() * 10;
                            r = (int)(Config.radius)*10;
                            g2.fill(new Arc2D.Double(x-r, Config.H * 10 - y - r, r*2, r*2, 0, 360, Arc2D.OPEN));
                        }
                        g2.setColor(Color.RED);
                        double xCur = Config.X0 * 10;
                        double yCur = Config.Y0 * 10;
                        double phi, xNext, yNext;
                        for (int j = 0; j <= i; j++) {
                            phi = indi[j];
                            xNext = xCur + Math.cos(phi) * Config.DT * Config.VI * 10;
                            yNext = yCur + Math.sin(phi) * Config.DT * Config.VI * 10;
                            if (xNext > Config.W * 10)
                                check = true;
                            g2.draw(new Line2D.Double(xCur, Config.H * 10 - yCur,
                                    xNext, Config.H * 10 - yNext));

                            xCur = xNext;
                            yCur = yNext;
                        }
                    }
                };
                frame.add(panel);
                frame.setSize((int) Config.W *10, (int) Config.H *10);
                frame.setVisible(true);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            }

            for(Sensor s : sensorLists){
                s.move(Config.DT * Config.DS);
            }
            try {
                TimeUnit.MILLISECONDS.sleep(40);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }

    }
}
