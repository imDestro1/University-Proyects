import javax.swing.*;
import java.awt.*;

public class vrcMethod extends JFrame {
    private JTextField txtInput;
    private JTextArea txtOutput;
    private JButton btnVerificar;

    public vrcMethod() {
        setTitle("Verificacion de Redundancia Vertical VRC");
        setSize(600,500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        setBackground(Color.darkGray);

        initComponents();
        addComponents();
        addEvents();
    }

    private void initComponents() {
        txtInput = new JTextField();
        txtOutput = new JTextArea();
        btnVerificar = new JButton("Verificar");

        txtInput.setFont(new Font("Monospaced", Font.PLAIN, 14));
        txtOutput.setFont(new Font("Serif", Font.PLAIN, 14));
        txtOutput.setEditable(false);
        txtOutput.setBorder(BorderFactory.createLineBorder(Color.GRAY));
    }

    private void addComponents() {
        JLabel lbl = new JLabel("Ingrese palabra o caracter a verificar (ejemplo WORLD):");
        lbl.setFont(new Font("Serif", Font.BOLD, 16));
        lbl.setBounds(120, 20, 450, 25);
        add(lbl);

        txtInput.setBounds(150, 60, 300, 50);
        add(txtInput);

        btnVerificar.setBounds(250, 130, 100, 35);
        btnVerificar.setFont(new Font("SansSerif", Font.BOLD, 14));
        add(btnVerificar);

        txtOutput.setBounds(100, 180, 400, 200);
        add(txtOutput);
    }

    private void addEvents() {
        btnVerificar.addActionListener(e -> verificar());
    }

    private void verificar() {
        String texto = txtInput.getText().trim();
        if (texto.isEmpty()) {
            txtOutput.setText("⚠ Ingrese algo valido");
            return;
        }

        String bits = convertirABits(texto);
        int paridad = calcularParidad(bits);

        txtOutput.setText("Texto: " + texto +
                "\nBits: " + bits +
                "\nBit de paridad: " + paridad);
    }

    // Convierte texto a binario
    private String convertirABits(String texto) {
        StringBuilder sb = new StringBuilder();
        for (char c : texto.toCharArray()) {
            sb.append(String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0'));
        }
        return sb.toString();
    }

    // Calcula bit de paridad par
    private int calcularParidad(String bits) {
        int ones = 0;
        for (char b : bits.toCharArray()) {
            if (b == '1') ones++;
        }
        return (ones % 2 == 0) ? 0 : 1;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new vrcMethod().setVisible(true));
    }
}
