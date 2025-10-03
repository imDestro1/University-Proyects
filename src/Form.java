import java.awt.*;
import javax.swing.*;

public class Form extends JFrame {

    private JTextField txtAngle, txtTranslateX, txtTranslateY;
    private JButton btnApplyAngle, btnReset, btnZoomIn, btnZoomOut;
    private JButton btnRotateLeft, btnRotateRight, btnApplyTranslate;
    private DrawPanel drawPanel;

    public Form() {
        setSize(1000, 1000);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setTitle("Practica Graficacion");

        drawPanel = new DrawPanel();
        add(drawPanel, BorderLayout.CENTER);

        JLabel lblAngle = new JLabel("Angulo: ");
        txtAngle = new JTextField(5);
        JLabel lblTx = new JLabel("X: ");
        txtTranslateX = new JTextField(4);
        JLabel lblTy = new JLabel("Y: ");
        txtTranslateY = new JTextField(4);

        btnApplyAngle = new JButton("Rotar");
        btnApplyTranslate = new JButton("Trasladar");
        btnReset = new JButton("Reiniciar");
        btnZoomIn = new JButton("Escalar +");
        btnZoomOut = new JButton("Escalar -");
        btnRotateLeft = new JButton("Rotar izq");
        btnRotateRight = new JButton("Rotar der");

        JPanel controlPanel = new JPanel();
        controlPanel.add(lblAngle);
        controlPanel.add(txtAngle);
        controlPanel.add(btnApplyAngle);
        controlPanel.add(lblTx);
        controlPanel.add(txtTranslateX);
        controlPanel.add(lblTy);
        controlPanel.add(txtTranslateY);
        controlPanel.add(btnApplyTranslate);
        controlPanel.add(btnRotateLeft);
        controlPanel.add(btnRotateRight);
        controlPanel.add(btnZoomIn);
        controlPanel.add(btnZoomOut);
        controlPanel.add(btnReset);

        add(controlPanel, BorderLayout.PAGE_END);

        btnApplyAngle.addActionListener(e -> {
            try {
                double ang = Double.parseDouble(txtAngle.getText());
                drawPanel.setAngle(Math.toRadians(ang));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Ingresa un número válido en ángulo");
            }
        });

        btnApplyTranslate.addActionListener(e -> {
            try {
                int x = Integer.parseInt(txtTranslateX.getText());
                int y = Integer.parseInt(txtTranslateY.getText());
                drawPanel.setTranslate(x, y);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Ingresa nmeros en X y Y");
            }
        });

        btnRotateLeft.addActionListener(e -> drawPanel.addAngle(-Math.toRadians(10)));
        btnRotateRight.addActionListener(e -> drawPanel.addAngle(Math.toRadians(10)));

        btnZoomIn.addActionListener(e -> drawPanel.scale(1.2));
        btnZoomOut.addActionListener(e -> drawPanel.scale(0.8));
        btnReset.addActionListener(e -> drawPanel.reset());

    }

    public class DrawPanel extends JPanel {
        double angle = 0;
        int tx = 0, ty = 0;
        double scale = 1.0;

        public void setAngle(double a) {
            angle = a;
            repaint();
        }

        public void addAngle(double da) {
            angle += da;
            repaint();
        }

        public void setTranslate(int x, int y) {
            tx = x;
            ty = y;
            repaint();
        }

        public void scale(double factor) {
            scale *= factor;
            repaint();
        }

        public void reset() {
            angle = 0;
            tx = 0;
            ty = 0;
            scale = 1.0;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            int w = getWidth();
            int h = getHeight();

            // Cruz del centro
            g2.setColor(Color.GRAY);
            g2.drawLine(w / 2, 0, w / 2, h);
            g2.drawLine(0, h / 2, w, h / 2);

            // Traslación, rotación y escala
            g2.translate(w / 2 + tx, h / 2 + ty);
            g2.rotate(angle);
            g2.scale(scale, scale);

            // Dibuj cuadrado
            g2.setColor(Color.ORANGE);
            g2.fillRect(-80, -50, 160, 100);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Form().setVisible(true));
    }
}
