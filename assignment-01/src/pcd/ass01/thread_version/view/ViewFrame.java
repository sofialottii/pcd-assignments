package pcd.ass01.thread_version.view;

import pcd.ass01.thread_version.model.GameState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * It's the pc window, it extends JFrame. It contains a JPanel. He creates the pixels on the screen.
 * He works when the GameLoop calls the render() method.
 * He draws everything. When he's done, he tells the RenderSynch that he can unlock the GameLoop
 *
 */
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




            //axis
            g2.setColor(Color.LIGHT_GRAY);
            g2.setStroke(new BasicStroke(1));
            g2.drawLine(ox,0,ox,oy*2);
            g2.drawLine(0,oy,ox*2,oy);


            //holes
            for (var h: model.getHoles()){
                g2.setColor(Color.BLACK);
                drawHole(g2, new BallViewInfo(h.pos(), h.radius()), 9);
            }

            //small balls

            for (var b : model.getBalls()) {
                if(!b.inHole()) {
                    if (b.isPlayerTouch()) {
                        g2.setColor(Color.BLUE);
                    } else if (b.isBotTouch()) {
                        g2.setColor(Color.RED);
                    } else {
                        g2.setColor(Color.BLACK);
                    }
                    //new BallViewInfo taken from parameters of Small
                    drawBall(g2, new BallViewInfo(b.pos(), b.radius()), 1);
                }
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



            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(1));
            g2.setFont(new Font("Arial", Font.PLAIN, 14));
            FontMetrics fm = g2.getFontMetrics();

            String ballsText = "Palle in gioco: " + (model.getBalls().size() - model.getBallsInHole());
            String fpsText   = "FPS: " + model.getFramePerSec();

            g2.drawString(ballsText, (getWidth() - fm.stringWidth(ballsText)) / 2, 40);
            g2.drawString(fpsText,   (getWidth() - fm.stringWidth(fpsText))   / 2, 60);

            // score player (blu, in basso a sinistra)
            g2.setColor(Color.BLUE);
            g2.setFont(new Font("Arial", Font.BOLD, 20));
            g2.drawString("Player: " + scoreBoard.getPointPlayer(), 20, getHeight() - 20);

            // score bot (rosso, in basso a destra)
            String botScore = "Bot: " + scoreBoard.getPointBot();
            g2.setColor(Color.RED);
            int botScoreWidth = fm.stringWidth(botScore);
            g2.drawString(botScore, getWidth() - botScoreWidth - 20, getHeight() - 20);


            if(model.isGameOver()){
                g2.clearRect(0, 0, getWidth(), getHeight());
                drawGameOverBox(g2, model.getWinner());
            }

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

        private void drawHole(Graphics2D g2, BallViewInfo b, int stroke) {
            g2.setStroke(new BasicStroke(stroke));

            var p = b.pos();
            int x0 = (int) (ox + p.x() * delta);
            int y0 = (int) (oy - p.y() * delta);
            int radiusX = (int)(b.radius()*delta);
            int radiusY = (int)(b.radius()*delta);

            //draw the circle
            g2.fillOval(x0 - radiusX, y0 - radiusY, radiusX * 2, radiusY * 2);
        }

        /**
         * Screen of end game
         * @param g2
         */
        private void drawGameOverBox(Graphics2D g2, String winner) {
            int boxW = 340;
            int boxH = 160;
            int boxX = (getWidth()  - boxW) / 2;
            int boxY = (getHeight() - boxH) / 2;

            // sfondo semi-trasparente
            g2.setColor(new Color(0, 0, 0, 180));
            g2.fillRoundRect(boxX, boxY, boxW, boxH, 20, 20);

            // bordo bianco
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(boxX, boxY, boxW, boxH, 20, 20);

            // titolo
            g2.setFont(new Font("Arial", Font.BOLD, 26));
            String title = "Fine Partita";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(title, boxX + (boxW - fm.stringWidth(title)) / 2, boxY + 40);

            // punteggio player (blu)
            g2.setColor(Color.BLUE);
            g2.setFont(new Font("Arial", Font.BOLD, 18));
            String playerScore = "Player: " + scoreBoard.getPointPlayer() + " punti";
            fm = g2.getFontMetrics();
            g2.drawString(playerScore, boxX + (boxW - fm.stringWidth(playerScore)) / 2, boxY + 80);

            // punteggio bot (rosso)
            g2.setColor(Color.RED);
            String botScore = "Bot: " + scoreBoard.getPointBot() + " punti";
            fm = g2.getFontMetrics();
            g2.drawString(botScore, boxX + (boxW - fm.stringWidth(botScore)) / 2, boxY + 110);

            g2.setColor(Color.YELLOW);
            g2.setFont(new Font("Arial", Font.BOLD, 20));
            fm = g2.getFontMetrics();
            String winnerText = winner + " ha vinto!";
            g2.drawString(winnerText, boxX + (boxW - fm.stringWidth(winnerText)) / 2, boxY + 148);
        }
    }
}
