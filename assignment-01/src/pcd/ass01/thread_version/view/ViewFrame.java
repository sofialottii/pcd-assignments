package pcd.ass01.thread_version.view;

import pcd.ass01.thread_version.model.GameState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ViewFrame extends JFrame {

    private final VisualiserPanel panel;
    private final ViewModel model;
    private final RenderSynch sync;
    private final GameState scoreBoard;

    public ViewFrame(ViewModel model, int w, int h, GameState scoreBoard) {
        this.model = model;
        this.scoreBoard = scoreBoard;
        this.sync = new RenderSynch();

        setTitle("Poool Game");
        setSize(w, h + 25);
        setResizable(false);

        panel = new VisualiserPanel(w, h);
        getContentPane().add(panel);

        //closing window
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev){
                System.exit(-1);
            }
            public void windowClosed(WindowEvent ev){ System.exit(-1); }
        });
    }

    /**
     * asking the sync which frame we are about to draw, making Swing redraw
     * the panel and waiting for the drawing to be finished
     */
    public void render() {
        long nf = sync.nextFrameToRender();

        panel.repaint();

        try {
            sync.waitForFrameRendered(nf);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    private class VisualiserPanel extends JPanel {
        private final int ox, oy, delta;

        public VisualiserPanel(int w, int h) {
            setSize(w, h + 25);
            ox = w / 2;
            oy = h / 2;
            delta = Math.min(ox, oy);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.clearRect(0, 0, getWidth(), getHeight());

            g2.setColor(Color.LIGHT_GRAY);
            g2.setStroke(new BasicStroke(1));
            g2.drawLine(ox,0,ox,oy*2);
            g2.drawLine(0,oy,ox*2,oy);




            //small balls

            for (var b : model.getBalls()) {
                g2.setColor(Color.BLACK);
                if(b.isPlayerTouch())
                    g2.setColor(Color.BLUE);
                if(b.isBotTouch())
                    g2.setColor(Color.RED);
                //new BallViewInfo taken from parameters of Small
                drawBall(g2, new BallViewInfo(b.pos(), b.radius()), 1);
            }

            //player ball
            var pb = model.getPlayerBall();
            if (pb != null) {
                g2.setColor(Color.BLUE);
                g2.setStroke(new BasicStroke(3));
                drawBall(g2, pb, 3);
            }

            //bot ball
            var bot = model.getBotBall();
            if (bot != null) {
                g2.setColor(Color.RED);
                g2.setStroke(new BasicStroke(3));
                drawBall(g2, bot, 3);
            }

            //text
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(1));
            g2.drawString("Palle in gioco: " + model.getBalls().size(), 20, 40);
            g2.drawString("FPS: " + model.getFramePerSec(), 20, 60);

            // score player (blu, in basso a sinistra)
            g2.setColor(Color.BLUE);
            g2.setFont(new Font("Arial", Font.BOLD, 20));
            g2.drawString("Player: " + scoreBoard.getPointPlayer(), 20, getHeight() - 20);

            // score bot (rosso, in basso a destra)
            String botScore = "Bot: " + scoreBoard.getPointBot();
            g2.setColor(Color.RED);
            FontMetrics fm = g2.getFontMetrics();
            int botScoreWidth = fm.stringWidth(botScore);
            g2.drawString(botScore, getWidth() - botScoreWidth - 20, getHeight() - 20);

            //to let gameloop know that view has done
            sync.notifyFrameRendered();
        }

        /**
         *
         * @param stroke spessore del pennello
         */
        private void drawBall(Graphics2D g2, BallViewInfo b, int stroke) {
            g2.setStroke(new BasicStroke(stroke));

            var p = b.pos();
            int x0 = (int) (ox + p.x() * delta);
            int y0 = (int) (oy - p.y() * delta);
            int radiusX = (int)(b.radius()*delta);
            int radiusY = (int)(b.radius()*delta);

            //draw the circle
            g2.drawOval(x0 - radiusX, y0 - radiusY, radiusX * 2, radiusY * 2);
        }
    }
}
