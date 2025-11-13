import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Main extends JFrame {
    private DrawPanel drawPanel;
    private JButton pencilButton, eraserButton, selectButton, newButton, saveButton, openButton;
    private JComboBox<String> colorComboBox;
    private JComboBox<Integer> sizeComboBox;
    private String currentTool = "pencil";
    private Color currentColor = Color.BLACK;
    private int currentSize = 3;

    public Main() {
        setTitle("Простой графический редактор");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
    }

    private void initializeComponents() {
        drawPanel = new DrawPanel();
        
        // Создание кнопок инструментов
        pencilButton = new JButton("Карандаш");
        eraserButton = new JButton("Ластик");
        selectButton = new JButton("Выделение");
        newButton = new JButton("Новый");
        saveButton = new JButton("Сохранить");
        openButton = new JButton("Открыть");
        
        // Настройка комбобоксов
        String[] colors = {"Черный", "Красный", "Зеленый", "Синий", "Желтый"};
        colorComboBox = new JComboBox<>(colors);
        
        Integer[] sizes = {1, 3, 5, 10, 15, 20};
        sizeComboBox = new JComboBox<>(sizes);
        sizeComboBox.setSelectedItem(3);
    }

    private void setupLayout() {
        // Панель инструментов
        JPanel toolPanel = new JPanel();
        toolPanel.setLayout(new FlowLayout());
        
        toolPanel.add(newButton);
        toolPanel.add(openButton);
        toolPanel.add(saveButton);
        toolPanel.add(new JSeparator(SwingConstants.VERTICAL));
        toolPanel.add(pencilButton);
        toolPanel.add(eraserButton);
        toolPanel.add(selectButton);
        toolPanel.add(new JSeparator(SwingConstants.VERTICAL));
        toolPanel.add(new JLabel("Цвет:"));
        toolPanel.add(colorComboBox);
        toolPanel.add(new JLabel("Размер:"));
        toolPanel.add(sizeComboBox);
        
        // Основной layout
        setLayout(new BorderLayout());
        add(toolPanel, BorderLayout.NORTH);
        add(drawPanel, BorderLayout.CENTER);
    }

    private void setupEventListeners() {
        // Кнопки инструментов
        pencilButton.addActionListener(e -> {
            currentTool = "pencil";
            updateButtonStates();
        });
        
        eraserButton.addActionListener(e -> {
            currentTool = "eraser";
            updateButtonStates();
        });
        
        selectButton.addActionListener(e -> {
            currentTool = "select";
            updateButtonStates();
        });
        
        // Кнопки файловых операций
        newButton.addActionListener(e -> createNewCanvas());
        saveButton.addActionListener(e -> saveImage());
        openButton.addActionListener(e -> openImage());
        
        // Комбобоксы
        colorComboBox.addActionListener(e -> updateColor());
        sizeComboBox.addActionListener(e -> updateSize());
        
        // Передача текущего инструмента на панель рисования
        drawPanel.setCurrentTool(currentTool);
        drawPanel.setCurrentColor(currentColor);
        drawPanel.setCurrentSize(currentSize);
    }

    private void updateButtonStates() {
        pencilButton.setBackground(currentTool.equals("pencil") ? Color.LIGHT_GRAY : null);
        eraserButton.setBackground(currentTool.equals("eraser") ? Color.LIGHT_GRAY : null);
        selectButton.setBackground(currentTool.equals("select") ? Color.LIGHT_GRAY : null);
        
        drawPanel.setCurrentTool(currentTool);
    }

    private void updateColor() {
        String selectedColor = (String) colorComboBox.getSelectedItem();
        switch (selectedColor) {
            case "Черный": currentColor = Color.BLACK; break;
            case "Красный": currentColor = Color.RED; break;
            case "Зеленый": currentColor = Color.GREEN; break;
            case "Синий": currentColor = Color.BLUE; break;
            case "Желтый": currentColor = Color.YELLOW; break;
        }
        drawPanel.setCurrentColor(currentColor);
    }

    private void updateSize() {
        currentSize = (Integer) sizeComboBox.getSelectedItem();
        drawPanel.setCurrentSize(currentSize);
    }

    private void createNewCanvas() {
        int result = JOptionPane.showConfirmDialog(this, 
            "Создать новый холст? Несохраненные изменения будут потеряны.", 
            "Новый файл", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            drawPanel.clearCanvas();
        }
    }

    private void saveImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Сохранить изображение");
        fileChooser.setFileFilter(new FileNameExtensionFilter("PNG images", "png"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".png")) {
                file = new File(file.getAbsolutePath() + ".png");
            }
            
            try {
                drawPanel.saveImage(file);
                JOptionPane.showMessageDialog(this, "Изображение сохранено успешно!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Ошибка при сохранении: " + ex.getMessage(), 
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Открыть изображение");
        fileChooser.setFileFilter(new FileNameExtensionFilter("PNG images", "png"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                drawPanel.openImage(file);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Ошибка при открытии: " + ex.getMessage(), 
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                try {
                    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            
            Main app = new Main();
            app.setVisible(true);
        });
    }

    class DrawPanel extends JPanel {
        private BufferedImage canvas;
        private Graphics2D g2d;
        private String currentTool;
        private Color currentColor;
        private int currentSize;

        public DrawPanel() {
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(800, 500));
            
            // Инициализация холста
            canvas = new BufferedImage(800, 500, BufferedImage.TYPE_INT_ARGB);
            g2d = canvas.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            clearCanvas();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(canvas, 0, 0, null);
        }

        public void clearCanvas() {
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            g2d.setColor(currentColor != null ? currentColor : Color.BLACK);
            repaint();
        }

        public void saveImage(File file) throws IOException {
            ImageIO.write(canvas, "PNG", file);
        }

        public void openImage(File file) throws IOException {
            BufferedImage image = ImageIO.read(file);
            g2d.drawImage(image, 0, 0, getWidth(), getHeight(), null);
            repaint();
        }

        public void setCurrentTool(String tool) {
            this.currentTool = tool;
        }

        public void setCurrentColor(Color color) {
            this.currentColor = color;
            if (g2d != null && !currentTool.equals("eraser")) {
                g2d.setColor(color);
            }
        }

        public void setCurrentSize(int size) {
            this.currentSize = size;
        }
    }
}