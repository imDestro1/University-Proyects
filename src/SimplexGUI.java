import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

/**
 * Programa para resolucion de metodo simplex con interfaz grafica
 * --------------------------------------------------------
 * Este programa implementa el Metodo Simplex para problemas de maximización
 * con restricciones tipo <=.
 *
 * Características:
 * - Permite al usuario ingresar número de variables y restricciones.
 * - Permite ingresar coeficientes de la función objetivo y restricciones.
 * - Muestra paso a paso las iteraciones numeradas en un JTextArea.
 * - Redondea los valores del tableau a 3 decimales para mejor visualización.
 * - Variables en inglés (x1, x2, ...), interfaz simple.
 */
public class SimplexGUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SimplexGUI::createAndShowGUI);
    }

    /**
     * Crea y muestra la interfaz
     */
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Metodo Simplex");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 700);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);
        frame.setBackground(Color.darkGray);


        // ------------------ panel superior ------------------
        JPanel topPanel = new JPanel();
        JLabel varLabel = new JLabel("Numero de variables:");
        JTextField varField = new JTextField(3); // vacio
        JLabel conLabel = new JLabel("Numero de restricciones:");
        JTextField conField = new JTextField(3); // vacio
        JButton generateButton = new JButton("Generar campos");

        topPanel.add(varLabel);
        topPanel.add(varField);
        topPanel.add(conLabel);
        topPanel.add(conField);
        topPanel.add(generateButton);
        frame.add(topPanel, BorderLayout.NORTH);

        // ------------------ Panel central: campos de coeficientes ------------------
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(0, 1));
        JScrollPane scrollPane = new JScrollPane(centerPanel);
        frame.add(scrollPane, BorderLayout.CENTER);
        centerPanel.setBackground(Color.gray);

        // ------------------ Panel inferior: boton resolver y resultados ------------------
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton solveButton = new JButton("Resolver");
        bottomPanel.add(solveButton);
        frame.add(bottomPanel, BorderLayout.SOUTH);
        JTextArea resultArea = new JTextArea(15, 50);
        resultArea.setEditable(false);
        JScrollPane resultScroll = new JScrollPane(resultArea);
        bottomPanel.add(solveButton, BorderLayout.NORTH);
        bottomPanel.add(resultScroll, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);
        bottomPanel.setBackground(Color.gray);
        solveButton.setBounds(320,200, 100, 50);
        // ------------------ Acción botón Generar Campos ------------------
        generateButton.addActionListener(e -> {
            centerPanel.removeAll(); // limpia panel de entradas anteriores
            try {
                int numVar = Integer.parseInt(varField.getText());
                int numCons = Integer.parseInt(conField.getText());

                // ------------------ Función objetivo ------------------
                JPanel objPanel = new JPanel();
                objPanel.add(new JLabel("Función objetivo coef x1:"));
                JTextField[] objFields = new JTextField[numVar];
                for (int i = 0; i < numVar; i++) {
                    objFields[i] = new JTextField(3); // vacío
                    objPanel.add(objFields[i]);
                }
                centerPanel.add(objPanel);

                // ------------------ Restricciones ------------------
                JTextField[][] conFields = new JTextField[numCons][numVar + 1]; // +1 para RHS
                for (int i = 0; i < numCons; i++) {
                    JPanel p = new JPanel();
                    p.add(new JLabel("Restricción " + (i + 1) + " coef x1:"));
                    for (int j = 0; j < numVar + 1; j++) {
                        conFields[i][j] = new JTextField(3); // vacío
                        p.add(conFields[i][j]);
                    }
                    centerPanel.add(p);
                }

                centerPanel.revalidate();
                centerPanel.repaint();

                // ------------------ Accion boton Resolver ------------------
                solveButton.addActionListener(ev -> {
                    try {
                        // Leer datos ingresados
                        double[] objective = new double[numVar];
                        double[][] constraints = new double[numCons][numVar];
                        double[] rhs = new double[numCons];

                        for (int i = 0; i < numVar; i++) objective[i] = Double.parseDouble(objFields[i].getText());
                        for (int i = 0; i < numCons; i++) {
                            for (int j = 0; j < numVar; j++) constraints[i][j] = Double.parseDouble(conFields[i][j].getText());
                            rhs[i] = Double.parseDouble(conFields[i][numVar].getText());
                        }

                        // Construir tableau inicial
                        double[][] tableau = buildTableau(objective, constraints, rhs);
                        resultArea.setText(""); // limpiar área de texto

                        // ------------------ Metodo Simplex ------------------
                        int iteration = 0;
                        while (!isOptimal(tableau)) {
                            iteration++;
                            int pivotCol = findPivotColumn(tableau);
                            int pivotRow = findPivotRow(tableau, pivotCol);

                            // Mostrar iteracipn numerada
                            resultArea.append("=== Iteración " + iteration + " ===\n");
                            resultArea.append("Columna entrante = x" + (pivotCol + 1) +
                                    ", Fila pivote = " + (pivotRow + 1) + "\n");

                            pivotOperation(tableau, pivotRow, pivotCol);
                            printTableau(tableau, resultArea);
                        }

                        // ------------------ Solucion optima ------------------
                        resultArea.append("\nSolución óptima:\n");
                        for (int i = 0; i < numVar; i++) {
                            double value = 0;
                            int countOne = 0;
                            int indexOne = -1;
                            for (int j = 0; j < tableau.length - 1; j++) {
                                if (tableau[j][i] == 1) { countOne++; indexOne = j; }
                                else if (tableau[j][i] != 0) countOne += 10;
                            }
                            if (countOne == 1) value = tableau[indexOne][tableau[0].length - 1];
                            resultArea.append("x" + (i + 1) + " = " + value + "\n");
                        }
                        double zValue = tableau[tableau.length - 1][tableau[0].length - 1];
                        resultArea.append("Z = " + zValue + "\n");

                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, "Error en los datos");
                    }
                });

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Ingrese valores validos para las variables y restricciones");
            }
        });

        frame.setVisible(true);
    }

    // ------------------ METODOS SIMPLEX ------------------

    static double[][] buildTableau(double[] obj, double[][] cons, double[] rhs) {
        int numVar = obj.length;
        int numCons = cons.length;
        double[][] tab = new double[numCons + 1][numVar + numCons + 1];

        for (int i = 0; i < numCons; i++) {
            for (int j = 0; j < numVar; j++) tab[i][j] = cons[i][j];
            tab[i][numVar + i] = 1; // variable de holgura
            tab[i][tab[0].length - 1] = rhs[i]; // RHS
        }

        for (int j = 0; j < numVar; j++) tab[numCons][j] = -obj[j]; // función objetivo
        return tab;
    }

    static boolean isOptimal(double[][] tab) {
        for (int j = 0; j < tab[0].length - 1; j++)
            if (tab[tab.length - 1][j] < 0) return false;
        return true;
    }

    static int findPivotColumn(double[][] tab) {
        int pivotCol = 0;
        double min = tab[tab.length - 1][0];
        for (int j = 1; j < tab[0].length - 1; j++)
            if (tab[tab.length - 1][j] < min) { min = tab[tab.length - 1][j]; pivotCol = j; }
        return pivotCol;
    }

    static int findPivotRow(double[][] tab, int col) {
        int pivotRow = -1;
        double minRatio = Double.MAX_VALUE;
        for (int i = 0; i < tab.length - 1; i++) {
            if (tab[i][col] > 0) {
                double ratio = tab[i][tab[0].length - 1] / tab[i][col];
                if (ratio < minRatio) { minRatio = ratio; pivotRow = i; }
            }
        }
        return pivotRow;
    }

    static void pivotOperation(double[][] tab, int pivotRow, int pivotCol) {
        int rows = tab.length;
        int cols = tab[0].length;
        double pivot = tab[pivotRow][pivotCol];

        for (int j = 0; j < cols; j++) tab[pivotRow][j] /= pivot;

        for (int i = 0; i < rows; i++) {
            if (i != pivotRow) {
                double factor = tab[i][pivotCol];
                for (int j = 0; j < cols; j++) tab[i][j] -= factor * tab[pivotRow][j];
            }
        }
    }

    static void printTableau(double[][] tab, JTextArea area) {
        DecimalFormat df = new DecimalFormat("0.###");
        area.append("Tableau:\n");
        for (int i = 0; i < tab.length; i++) {
            for (int j = 0; j < tab[0].length; j++) area.append(df.format(tab[i][j]) + "\t");
            area.append("\n");
        }
        area.append("\n");
    }
}
