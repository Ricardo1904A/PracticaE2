import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class AutomovilApp extends JFrame {
    private List<Automovil> automoviles;
    private DefaultListModel<String> listaModel;
    private DefaultListModel<String> listaFiltradaModel;
    private JTextArea sumatoriaTextArea;
    private JPanel graficoPanel;

    public AutomovilApp() {
        automoviles = new ArrayList<>();
        initUI();
    }

    private void initUI() {
        setTitle("Automovil App");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel principal
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Panel de ingreso de datos
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(6, 2));

        JLabel codigoLabel = new JLabel("Código:");
        JTextField codigoField = new JTextField();
        inputPanel.add(codigoLabel);
        inputPanel.add(codigoField);

        JLabel marcaLabel = new JLabel("Marca:");
        JComboBox<String> marcaComboBox = new JComboBox<>(new String[]{"KIA", "BMW", "TOYOTA", "JAC", "HONDA"});
        inputPanel.add(marcaLabel);
        inputPanel.add(marcaComboBox);

        JLabel cilindrajeLabel = new JLabel("Cilindraje:");
        JComboBox<Integer> cilindrajeComboBox = new JComboBox<>(new Integer[]{1300, 1600, 2000, 2400, 2700});
        inputPanel.add(cilindrajeLabel);
        inputPanel.add(cilindrajeComboBox);

        JLabel precioLabel = new JLabel("Precio:");
        JTextField precioField = new JTextField();
        inputPanel.add(precioLabel);
        inputPanel.add(precioField);

        JLabel seguroLabel = new JLabel("Seguro:");
        JComboBox<String> seguroComboBox = new JComboBox<>(new String[]{"SEGURO", "NO SEGURO"});
        inputPanel.add(seguroLabel);
        inputPanel.add(seguroComboBox);

        JButton agregarButton = new JButton("Agregar Automóvil");
        inputPanel.add(new JLabel());
        inputPanel.add(agregarButton);

        panel.add(inputPanel, BorderLayout.NORTH);

        // Panel de listas
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new GridLayout(1, 2));

        listaModel = new DefaultListModel<>();
        JList<String> automovilList = new JList<>(listaModel);
        JScrollPane scrollPane1 = new JScrollPane(automovilList);
        listPanel.add(scrollPane1);

        listaFiltradaModel = new DefaultListModel<>();
        JList<String> filtradaList = new JList<>(listaFiltradaModel);
        JScrollPane scrollPane2 = new JScrollPane(filtradaList);
        listPanel.add(scrollPane2);

        panel.add(listPanel, BorderLayout.CENTER);

        // Panel de sumatoria
        JPanel sumatoriaPanel = new JPanel();
        sumatoriaPanel.setLayout(new BorderLayout());
        sumatoriaTextArea = new JTextArea();
        sumatoriaPanel.add(new JScrollPane(sumatoriaTextArea), BorderLayout.CENTER);
        panel.add(sumatoriaPanel, BorderLayout.SOUTH);

        // Panel de gráficos
        graficoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujarGrafico(g);
            }
        };
        graficoPanel.setPreferredSize(new Dimension(400, 300));
        panel.add(graficoPanel, BorderLayout.EAST);

        add(panel);

        // Acciones
        agregarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int codigo = Integer.parseInt(codigoField.getText());
                String marca = (String) marcaComboBox.getSelectedItem();
                int cilindraje = (int) cilindrajeComboBox.getSelectedItem();
                float precio = Float.parseFloat(precioField.getText());
                String seguro = (String) seguroComboBox.getSelectedItem();

                if (automovilExiste(codigo)) {
                    JOptionPane.showMessageDialog(null, "El código ya ha sido ingresado previamente.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                } else {
                    Automovil automovil = new Automovil(codigo, marca, cilindraje, precio, seguro);
                    automoviles.add(0, automovil);
                    listaModel.add(0, automovil.toString());
                    calcularSumatoriaPorMarca();
                    graficoPanel.repaint();
                }
            }
        });

        automovilList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && automovilList.getSelectedValue() != null) {
                    String marcaSeleccionada = automovilList.getSelectedValue().split(" ")[1];
                    listaFiltradaModel.clear();
                    for (Automovil automovil : automoviles) {
                        if (!automovil.getMarca().equals(marcaSeleccionada)) {
                            listaFiltradaModel.addElement(automovil.toString());
                        }
                    }
                }
            }
        });
    }

    private boolean automovilExiste(int codigo) {
        for (Automovil automovil : automoviles) {
            if (automovil.getCodigo() == codigo) {
                return true;
            }
        }
        return false;
    }

    private void calcularSumatoriaPorMarca() {
        sumatoriaTextArea.setText("");
        String[] marcas = {"KIA", "BMW", "TOYOTA", "JAC", "HONDA"};
        for (String marca : marcas) {
            float sumatoria = calcularSumatoriaRecursiva(marca, 0);
            sumatoriaTextArea.append("Sumatoria de precios para " + marca + ": " + sumatoria + "\n");
        }
    }

    private float calcularSumatoriaRecursiva(String marca, int index) {
        if (index == automoviles.size()) {
            return 0;
        }
        Automovil automovil = automoviles.get(index);
        float precio = automovil.getMarca().equals(marca) ? automovil.getPrecio() : 0;
        return precio + calcularSumatoriaRecursiva(marca, index + 1);
    }

    private void dibujarGrafico(Graphics g) {
        int width = graficoPanel.getWidth();
        int height = graficoPanel.getHeight();
        int barWidth = width / (automoviles.size() + 1);
        int maxBarHeight = height - 50;

        int x = 10;
        for (Automovil automovil : automoviles) {
            if (automovil.getSeguro().equals("SEGURO")) {
                int barHeight = (int) ((automovil.getPrecio() / getMaxPrecio()) * maxBarHeight);
                g.setColor(Color.BLUE);
                g.fillRect(x, height - barHeight - 30, barWidth, barHeight);
                g.setColor(Color.BLACK);
                g.drawRect(x, height - barHeight - 30, barWidth, barHeight);
                g.drawString(automovil.getMarca(), x, height - 10);
                x += barWidth + 10;
            }
        }
    }

    private float getMaxPrecio() {
        float max = 0;
        for (Automovil automovil : automoviles) {
            if (automovil.getSeguro().equals("SEGURO") && automovil.getPrecio() > max) {
                max = automovil.getPrecio();
            }
        }
        return max;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AutomovilApp app = new AutomovilApp();
            app.setVisible(true);
        });
    }

    class Automovil {
        private int codigo;
        private String marca;
        private int cilindraje;
        private float precio;
        private String seguro;

        public Automovil(int codigo, String marca, int cilindraje, float precio, String seguro) {
            this.codigo = codigo;
            this.marca = marca;
            this.cilindraje = cilindraje;
            this.precio = precio;
            this.seguro = seguro;
        }

        public int getCodigo() {
            return codigo;
        }

        public String getMarca() {
            return marca;
        }

        public int getCilindraje() {
            return cilindraje;
        }

        public float getPrecio() {
            return precio;
        }

        public String getSeguro() {
            return seguro;
        }

        @Override
        public String toString() {
            return codigo + " " + marca + " " + cilindraje + " " + precio + " " + seguro;
        }
    }
}
