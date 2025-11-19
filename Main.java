import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.border.*;

public class Main extends JFrame {
    private DrawPanel drawPanel;
    private JButton pencilButton, eraserButton, selectButton, newButton, saveButton, openButton, cropButton;
    private JComboBox<String> colorComboBox;
    private JComboBox<Integer> sizeComboBox;
    private String currentTool = "pencil";
    private Color currentColor = Color.BLACK;
    private int currentSize = 3;

    public Main() {
        setTitle("Простой графический редактор");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Устанавливаем более широкий размер окна
        setSize(1200, 700); // Ширина 1200, высота 700
        setLocationRelativeTo(null);
        
        // Устанавливаем небесно-голубой фон для всего приложения
        getContentPane().setBackground(new Color(135, 206, 235));
        
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
        cropButton = new JButton("Обрезать");
        newButton = new JButton("Новый");
        saveButton = new JButton("Сохранить");
        openButton = new JButton("Открыть");
        
        // Настройка комбобоксов с 24 цветами
        String[] colors = {
            "Черный", "Белый", "Красный", "Зеленый", "Синий", "Желтый",
            "Оранжевый", "Розовый", "Фиолетовый", "Коричневый", "Серый",
            "Голубой", "Лаймовый", "Бирюзовый", "Пурпурный", "Оливковый",
            "Темно-красный", "Темно-зеленый", "Темно-синий", "Темно-фиолетовый",
            "Золотой", "Серебряный", "Бордовый", "Бежевый"
        };
        colorComboBox = new JComboBox<>(colors);
        
        Integer[] sizes = {1, 3, 5, 10, 15, 20};
        sizeComboBox = new JComboBox<>(sizes);
        sizeComboBox.setSelectedItem(3);
    }

    private void setupLayout() {
        // Панель инструментов
        JPanel toolPanel = new JPanel();
        toolPanel.setLayout(new FlowLayout());
        toolPanel.setBackground(new Color(176, 224, 230)); // Светлый голубой для панели инструментов
        toolPanel.setBorder(new CompoundBorder(
            new LineBorder(new Color(70, 130, 180), 1),
            new EmptyBorder(5, 5, 5, 5)
        ));
        
        toolPanel.add(newButton);
        toolPanel.add(openButton);
        toolPanel.add(saveButton);
        toolPanel.add(new JSeparator(SwingConstants.VERTICAL));
        toolPanel.add(pencilButton);
        toolPanel.add(eraserButton);
        toolPanel.add(selectButton);
        toolPanel.add(cropButton);
        toolPanel.add(new JSeparator(SwingConstants.VERTICAL));
        toolPanel.add(new JLabel("Цвет:"));
        toolPanel.add(colorComboBox);
        toolPanel.add(new JLabel("Размер:"));
        toolPanel.add(sizeComboBox);
        
        // Панель для холста с рамкой
        JPanel canvasPanel = new JPanel(new BorderLayout());
        canvasPanel.setBackground(new Color(135, 206, 235)); // Небесно-голубой фон вокруг холста
        canvasPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
            "Холст",
            TitledBorder.CENTER,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12),
            new Color(25, 25, 112)
        ));
        canvasPanel.add(drawPanel, BorderLayout.CENTER);
        
        // Основной layout
        setLayout(new BorderLayout());
        add(toolPanel, BorderLayout.NORTH);
        add(canvasPanel, BorderLayout.CENTER);
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
        
        cropButton.addActionListener(e -> {
            drawPanel.cropSelection();
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
        Color activeColor = new Color(100, 149, 237); // Цвет активной кнопки
        
        pencilButton.setBackground(currentTool.equals("pencil") ? activeColor : new Color(176, 224, 230));
        eraserButton.setBackground(currentTool.equals("eraser") ? activeColor : new Color(176, 224, 230));
        selectButton.setBackground(currentTool.equals("select") ? activeColor : new Color(176, 224, 230));
        cropButton.setBackground(new Color(176, 224, 230));
        
        drawPanel.setCurrentTool(currentTool);
    }

    private void updateColor() {
        String selectedColor = (String) colorComboBox.getSelectedItem();
        switch (selectedColor) {
            case "Черный": currentColor = Color.BLACK; break;
            case "Белый": currentColor = Color.WHITE; break;
            case "Красный": currentColor = Color.RED; break;
            case "Зеленый": currentColor = Color.GREEN; break;
            case "Синий": currentColor = Color.BLUE; break;
            case "Желтый": currentColor = Color.YELLOW; break;
            case "Оранжевый": currentColor = Color.ORANGE; break;
            case "Розовый": currentColor = Color.PINK; break;
            case "Фиолетовый": currentColor = new Color(128, 0, 128); break;
            case "Коричневый": currentColor = new Color(139, 69, 19); break;
            case "Серый": currentColor = Color.GRAY; break;
            case "Голубой": currentColor = Color.CYAN; break;
            case "Лаймовый": currentColor = new Color(50, 205, 50); break;
            case "Бирюзовый": currentColor = new Color(64, 224, 208); break;
            case "Пурпурный": currentColor = Color.MAGENTA; break;
            case "Оливковый": currentColor = new Color(128, 128, 0); break;
            case "Темно-красный": currentColor = new Color(139, 0, 0); break;
            case "Темно-зеленый": currentColor = new Color(0, 100, 0); break;
            case "Темно-синий": currentColor = new Color(0, 0, 139); break;
            case "Темно-фиолетовый": currentColor = new Color(148, 0, 211); break;
            case "Золотой": currentColor = new Color(255, 215, 0); break;
            case "Серебряный": currentColor = new Color(192, 192, 192); break;
            case "Бордовый": currentColor = new Color(128, 0, 0); break;
            case "Бежевый": currentColor = new Color(245, 245, 220); break;
        }
        drawPanel.setCurrentColor(currentColor);
    }

    private void updateSize() {
        currentSize = (Integer) sizeComboBox.getSelectedItem();
        drawPanel.setCurrentSize(currentSize);
    }

    private void createNewCanvas() {
        // Диалог для выбора размера нового холста
        JPanel sizePanel = new JPanel(new GridLayout(2, 2, 5, 5));
        JTextField widthField = new JTextField("1000"); // Увеличил стандартную ширину
        JTextField heightField = new JTextField("600"); // Увеличил стандартную высоту
        
        sizePanel.add(new JLabel("Ширина:"));
        sizePanel.add(widthField);
        sizePanel.add(new JLabel("Высота:"));
        sizePanel.add(heightField);
        
        int result = JOptionPane.showConfirmDialog(this, 
            sizePanel, 
            "Создать новый холст", 
            JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                int width = Integer.parseInt(widthField.getText());
                int height = Integer.parseInt(heightField.getText());
                
                if (width > 0 && height > 0) {
                    drawPanel.createNewCanvas(width, height);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Размеры должны быть положительными числами", 
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Введите корректные числовые значения", 
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
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
        private int startX, startY;
        private Rectangle selectionRect;
        private String currentTool;
        private Color currentColor;
        private int currentSize;

        public DrawPanel() {
            setBackground(Color.WHITE);
            // Увеличил начальный размер холста
            setPreferredSize(new Dimension(1000, 600));
            setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 2));
            
            // Инициализация холста
            createNewCanvas(1000, 600);
            
            // Обработчики мыши
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    startX = e.getX();
                    startY = e.getY();
                    
                    if (currentTool.equals("select")) {
                        selectionRect = new Rectangle(startX, startY, 0, 0);
                    }
                }
                
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (currentTool.equals("select") && selectionRect != null) {
                        // Гарантируем, что выделение имеет положительные размеры
                        if (selectionRect.width > 0 && selectionRect.height > 0) {
                            System.out.println("Выделена область: " + selectionRect);
                        }
                    }
                    repaint();
                }
            });
            
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    int x = e.getX();
                    int y = e.getY();
                    
                    switch (currentTool) {
                        case "pencil":
                            g2d.setColor(currentColor);
                            g2d.setStroke(new BasicStroke(currentSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                            g2d.drawLine(startX, startY, x, y);
                            startX = x;
                            startY = y;
                            break;
                            
                        case "eraser":
                            g2d.setColor(Color.WHITE);
                            g2d.setStroke(new BasicStroke(currentSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                            g2d.drawLine(startX, startY, x, y);
                            startX = x;
                            startY = y;
                            break;
                            
                        case "select":
                            if (selectionRect != null) {
                                selectionRect.setBounds(
                                    Math.min(startX, x),
                                    Math.min(startY, y),
                                    Math.abs(x - startX),
                                    Math.abs(y - startY)
                                );
                            }
                            break;
                    }
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            // Рисуем белый фон для области холста
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, getWidth(), getHeight());
            
            // Рисуем изображение
            if (canvas != null) {
                g.drawImage(canvas, 0, 0, null);
            }
            
            // Рисование выделения с улучшенной видимостью
            if (currentTool.equals("select") && selectionRect != null && 
                selectionRect.width > 0 && selectionRect.height > 0) {
                Graphics2D g2 = (Graphics2D) g.create();
                
                // Полупрозрачная заливка
                g2.setColor(new Color(0, 120, 215, 80));
                g2.fill(selectionRect);
                
                // Пунктирная граница
                g2.setColor(new Color(0, 120, 215));
                g2.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 
                    0, new float[]{5, 5}, 0));
                g2.draw(selectionRect);
                
                // Отображение размеров
                g2.setColor(Color.BLACK);
                g2.setFont(new Font("Arial", Font.BOLD, 12));
                String sizeText = selectionRect.width + " x " + selectionRect.height;
                g2.drawString(sizeText, 
                    selectionRect.x + 5, 
                    selectionRect.y + selectionRect.height - 5);
                
                g2.dispose();
            }
        }

        public void createNewCanvas(int width, int height) {
            canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            g2d = canvas.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            clearCanvas();
            setPreferredSize(new Dimension(width, height));
            revalidate();
            repaint();
        }

        public void clearCanvas() {
            if (g2d != null) {
                g2d.setColor(Color.WHITE);
                g2d.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
                g2d.setColor(currentColor != null ? currentColor : Color.BLACK);
            }
            selectionRect = null;
            repaint();
        }

        public void saveImage(File file) throws IOException {
            ImageIO.write(canvas, "PNG", file);
        }

        public void openImage(File file) throws IOException {
            BufferedImage image = ImageIO.read(file);
            createNewCanvas(image.getWidth(), image.getHeight());
            g2d.drawImage(image, 0, 0, null);
            repaint();
        }

        public void cropSelection() {
            if (selectionRect == null || selectionRect.width <= 0 || selectionRect.height <= 0) {
                JOptionPane.showMessageDialog(Main.this, 
                    "Сначала выделите область для обрезки с помощью инструмента 'Выделение'", 
                    "Нет выделения", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Показываем предпросмотр обрезки
            JDialog previewDialog = new JDialog(Main.this, "Предпросмотр обрезки", true);
            previewDialog.setLayout(new BorderLayout());
            previewDialog.setSize(500, 400); // Увеличил размер окна предпросмотра
            previewDialog.setLocationRelativeTo(Main.this);
            
            JLabel previewLabel = new JLabel();
            previewLabel.setHorizontalAlignment(SwingConstants.CENTER);
            previewLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            
            // Создаем предпросмотр
            BufferedImage previewImage = canvas.getSubimage(
                selectionRect.x, selectionRect.y, selectionRect.width, selectionRect.height);
            ImageIcon icon = new ImageIcon(previewImage.getScaledInstance(
                Math.min(450, previewImage.getWidth()), 
                Math.min(300, previewImage.getHeight()), 
                Image.SCALE_SMOOTH));
            previewLabel.setIcon(icon);
            
            JLabel infoLabel = new JLabel(
                "Новый размер: " + selectionRect.width + " x " + selectionRect.height, 
                SwingConstants.CENTER);
            infoLabel.setFont(new Font("Arial", Font.BOLD, 14));
            
            JPanel buttonPanel = new JPanel();
            JButton confirmButton = new JButton("Подтвердить обрезку");
            JButton cancelButton = new JButton("Отмена");
            
            confirmButton.addActionListener(e -> {
                performCrop();
                previewDialog.dispose();
            });
            
            cancelButton.addActionListener(e -> previewDialog.dispose());
            
            buttonPanel.add(confirmButton);
            buttonPanel.add(cancelButton);
            
            previewDialog.add(infoLabel, BorderLayout.NORTH);
            previewDialog.add(previewLabel, BorderLayout.CENTER);
            previewDialog.add(buttonPanel, BorderLayout.SOUTH);
            previewDialog.setVisible(true);
        }

        private void performCrop() {
            try {
                // Создаем новое изображение размером с выделенную область
                BufferedImage croppedImage = new BufferedImage(
                    selectionRect.width, selectionRect.height, BufferedImage.TYPE_INT_ARGB);
                
                Graphics2D g2dCropped = croppedImage.createGraphics();
                g2dCropped.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Копируем выделенную область в новое изображение
                g2dCropped.drawImage(canvas, 
                    0, 0, selectionRect.width, selectionRect.height,
                    selectionRect.x, selectionRect.y, 
                    selectionRect.x + selectionRect.width, selectionRect.y + selectionRect.height,
                    null);
                g2dCropped.dispose();
                
                // Заменяем текущий холст обрезанным изображением
                canvas = croppedImage;
                g2d = canvas.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Обновляем размер панели
                setPreferredSize(new Dimension(selectionRect.width, selectionRect.height));
                revalidate();
                
                selectionRect = null;
                repaint();
                
                JOptionPane.showMessageDialog(Main.this, 
                    "Изображение успешно обрезано!", 
                    "Обрезка завершена", JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(Main.this, 
                    "Ошибка при обрезке: " + ex.getMessage(), 
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }

        public void setCurrentTool(String tool) {
            this.currentTool = tool;
            if (tool.equals("select")) {
                setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            } else {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
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