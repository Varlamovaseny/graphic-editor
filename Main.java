import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.border.*;
import java.util.Stack;

public class Main extends JFrame {
    private DrawPanel drawPanel;
    private JButton pencilButton, eraserButton, selectButton, newButton, saveButton, openButton, cropButton;
    private JButton filtersButton, brightnessButton, blurButton, sharpenButton, grayscaleButton, invertButton;
    private JButton sepiaButton, vintageButton, coolButton, warmButton, saturationButton, contrastButton;
    private JButton undoButton, redoButton;
    private JComboBox<String> colorComboBox;
    private JComboBox<Integer> sizeComboBox;
    private String currentTool = "pencil";
    private Color currentColor = Color.BLACK;
    private int currentSize = 3;

    public Main() {
        setTitle("Простой графический редактор с фильтрами");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        getContentPane().setBackground(new Color(135, 206, 235));
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
    }

    private void initializeComponents() {
        drawPanel = new DrawPanel(this); // Передаем ссылку на Main
        
        // Создание кнопок инструментов
        pencilButton = new JButton("Карандаш");
        eraserButton = new JButton("Ластик");
        selectButton = new JButton("Выделение");
        cropButton = new JButton("Обрезать");
        newButton = new JButton("Новый");
        saveButton = new JButton("Сохранить");
        openButton = new JButton("Открыть");
        undoButton = new JButton("Отмена");
        redoButton = new JButton("Повтор");
        
        // Кнопки фильтров
        filtersButton = new JButton("Галерея фильтров");
        brightnessButton = new JButton("Яркость");
        blurButton = new JButton("Размытие");
        sharpenButton = new JButton("Резкость");
        grayscaleButton = new JButton("Ч/Б");
        invertButton = new JButton("Инверсия");
        sepiaButton = new JButton("Сепия");
        vintageButton = new JButton("Винтаж");
        coolButton = new JButton("Холодный");
        warmButton = new JButton("Теплый");
        saturationButton = new JButton("Насыщенность");
        contrastButton = new JButton("Контраст");
        
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
        // Основная панель инструментов
        JPanel toolPanel = new JPanel();
        toolPanel.setLayout(new FlowLayout());
        toolPanel.setBackground(new Color(176, 224, 230));
        toolPanel.setBorder(new CompoundBorder(
            new LineBorder(new Color(70, 130, 180), 1),
            new EmptyBorder(5, 5, 5, 5)
        ));
        
        toolPanel.add(newButton);
        toolPanel.add(openButton);
        toolPanel.add(saveButton);
        toolPanel.add(undoButton);
        toolPanel.add(redoButton);
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
        
        // Панель фильтров - первая строка
        JPanel filtersPanel1 = new JPanel();
        filtersPanel1.setLayout(new FlowLayout());
        filtersPanel1.setBackground(new Color(200, 230, 255));
        
        filtersPanel1.add(filtersButton);
        filtersPanel1.add(brightnessButton);
        filtersPanel1.add(blurButton);
        filtersPanel1.add(sharpenButton);
        filtersPanel1.add(grayscaleButton);
        filtersPanel1.add(invertButton);
        
        // Панель фильтров - вторая строка
        JPanel filtersPanel2 = new JPanel();
        filtersPanel2.setLayout(new FlowLayout());
        filtersPanel2.setBackground(new Color(200, 230, 255));
        
        filtersPanel2.add(sepiaButton);
        filtersPanel2.add(vintageButton);
        filtersPanel2.add(coolButton);
        filtersPanel2.add(warmButton);
        filtersPanel2.add(saturationButton);
        filtersPanel2.add(contrastButton);
        
        // Объединяем панели фильтров
        JPanel filtersPanel = new JPanel(new BorderLayout());
        filtersPanel.setBackground(new Color(200, 230, 255));
        filtersPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180), 1),
            "Фильтры",
            TitledBorder.CENTER,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 11),
            new Color(25, 25, 112)
        ));
        filtersPanel.add(filtersPanel1, BorderLayout.NORTH);
        filtersPanel.add(filtersPanel2, BorderLayout.SOUTH);
        
        // Панель для холста с прокруткой
        JPanel canvasPanel = new JPanel(new BorderLayout());
        canvasPanel.setBackground(new Color(135, 206, 235));
        canvasPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
            "Холст",
            TitledBorder.CENTER,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12),
            new Color(25, 25, 112)
        ));
        
        // Добавляем холст в JScrollPane
        JScrollPane scrollPane = new JScrollPane(drawPanel);
        scrollPane.setPreferredSize(new Dimension(1100, 550));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        canvasPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Основной layout
        setLayout(new BorderLayout());
        add(toolPanel, BorderLayout.NORTH);
        add(filtersPanel, BorderLayout.SOUTH);
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
        
        // Кнопки отмены/повтора
        undoButton.addActionListener(e -> drawPanel.undo());
        redoButton.addActionListener(e -> drawPanel.redo());
        
        // Кнопки фильтров
        filtersButton.addActionListener(e -> showFiltersDialog());
        brightnessButton.addActionListener(e -> showBrightnessDialog());
        blurButton.addActionListener(e -> drawPanel.applyBlurFilter());
        sharpenButton.addActionListener(e -> drawPanel.applySharpenFilter());
        grayscaleButton.addActionListener(e -> drawPanel.applyGrayscaleFilter());
        invertButton.addActionListener(e -> drawPanel.applyInvertFilter());
        sepiaButton.addActionListener(e -> drawPanel.applySepiaFilter());
        vintageButton.addActionListener(e -> drawPanel.applyVintageFilter());
        coolButton.addActionListener(e -> drawPanel.applyCoolFilter());
        warmButton.addActionListener(e -> drawPanel.applyWarmFilter());
        saturationButton.addActionListener(e -> showSaturationDialog());
        contrastButton.addActionListener(e -> showContrastDialog());
        
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
        
        // Инициализируем состояния кнопок
        updateButtonStates();
    }

    public void updateButtonStates() {
        if (undoButton == null || redoButton == null) return; // Защита от NPE
        
        Color activeColor = new Color(100, 149, 237);
        
        pencilButton.setBackground(currentTool.equals("pencil") ? activeColor : new Color(176, 224, 230));
        eraserButton.setBackground(currentTool.equals("eraser") ? activeColor : new Color(176, 224, 230));
        selectButton.setBackground(currentTool.equals("select") ? activeColor : new Color(176, 224, 230));
        cropButton.setBackground(new Color(176, 224, 230));
        
        // Обновляем состояние кнопок отмены/повтора
        undoButton.setEnabled(drawPanel.canUndo());
        redoButton.setEnabled(drawPanel.canRedo());
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

    private void showFiltersDialog() {
        JDialog filtersDialog = new JDialog(this, "Галерея фильтров", true);
        filtersDialog.setLayout(new BorderLayout());
        filtersDialog.setSize(700, 500);
        filtersDialog.setLocationRelativeTo(this);
        
        JPanel previewPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        previewPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        previewPanel.setBackground(new Color(240, 248, 255));
        
        String[] filterNames = {
            "Оригинал", "Ч/Б", "Сепия", "Инверсия", 
            "Размытие", "Резкость", "Винтаж", "Холодный",
            "Теплый", "Высокая насыщенность", "Высокий контраст", "Низкая насыщенность"
        };
        Runnable[] filterActions = {
            () -> drawPanel.applyOriginal(),
            () -> drawPanel.applyGrayscaleFilter(),
            () -> drawPanel.applySepiaFilter(),
            () -> drawPanel.applyInvertFilter(),
            () -> drawPanel.applyBlurFilter(),
            () -> drawPanel.applySharpenFilter(),
            () -> drawPanel.applyVintageFilter(),
            () -> drawPanel.applyCoolFilter(),
            () -> drawPanel.applyWarmFilter(),
            () -> drawPanel.applyHighSaturationFilter(),
            () -> drawPanel.applyHighContrastFilter(),
            () -> drawPanel.applyLowSaturationFilter()
        };
        
        for (int i = 0; i < filterNames.length; i++) {
            JPanel filterPanel = createFilterPreview(filterNames[i], filterActions[i]);
            previewPanel.add(filterPanel);
        }
        
        JButton closeButton = new JButton("Закрыть");
        closeButton.addActionListener(e -> filtersDialog.dispose());
        
        filtersDialog.add(new JLabel("Выберите фильтр:", SwingConstants.CENTER), BorderLayout.NORTH);
        filtersDialog.add(new JScrollPane(previewPanel), BorderLayout.CENTER);
        filtersDialog.add(closeButton, BorderLayout.SOUTH);
        filtersDialog.setVisible(true);
    }
    
    private JPanel createFilterPreview(String filterName, Runnable filterAction) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        
        JLabel previewLabel = new JLabel();
        previewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        previewLabel.setPreferredSize(new Dimension(120, 100));
        
        BufferedImage thumbnail = drawPanel.createThumbnail();
        if (thumbnail != null) {
            BufferedImage filteredThumbnail = applyFilterToImage(thumbnail, filterName);
            previewLabel.setIcon(new ImageIcon(filteredThumbnail));
        }
        
        JButton applyButton = new JButton(filterName);
        applyButton.setFont(new Font("Arial", Font.PLAIN, 10));
        applyButton.addActionListener(e -> {
            filterAction.run();
            ((Window) SwingUtilities.getWindowAncestor(panel)).dispose();
        });
        
        panel.add(previewLabel, BorderLayout.CENTER);
        panel.add(applyButton, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private BufferedImage applyFilterToImage(BufferedImage image, String filterName) {
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        Graphics2D g2d = result.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
        
        switch (filterName) {
            case "Ч/Б":
                return applyGrayscale(result);
            case "Сепия":
                return applySepia(result);
            case "Инверсия":
                return applyInvert(result);
            case "Размытие":
                return applyBlur(result, 3);
            case "Резкость":
                return applySharpen(result);
            case "Винтаж":
                return applyVintage(result);
            case "Холодный":
                return applyCool(result);
            case "Теплый":
                return applyWarm(result);
            case "Высокая насыщенность":
                return adjustSaturation(result, 1.5f);
            case "Низкая насыщенность":
                return adjustSaturation(result, 0.5f);
            case "Высокий контраст":
                return adjustContrast(result, 1.5f);
            default:
                return result;
        }
    }
    
    private void showBrightnessDialog() {
        JSlider brightnessSlider = new JSlider(-100, 100, 0);
        brightnessSlider.setMajorTickSpacing(50);
        brightnessSlider.setMinorTickSpacing(10);
        brightnessSlider.setPaintTicks(true);
        brightnessSlider.setPaintLabels(true);
        
        JLabel valueLabel = new JLabel("Яркость: 0%");
        
        brightnessSlider.addChangeListener(e -> {
            int value = brightnessSlider.getValue();
            valueLabel.setText("Яркость: " + value + "%");
            drawPanel.applyBrightnessPreview(value);
        });
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Регулировка яркости:"), BorderLayout.NORTH);
        panel.add(brightnessSlider, BorderLayout.CENTER);
        panel.add(valueLabel, BorderLayout.SOUTH);
        
        Object[] options = {"Применить", "Отмена"};
        int result = JOptionPane.showOptionDialog(this, panel, "Яркость", 
            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        
        if (result == 0) {
            drawPanel.applyBrightnessFilter(brightnessSlider.getValue());
        } else {
            drawPanel.cancelPreview();
        }
    }
    
    private void showSaturationDialog() {
        JSlider saturationSlider = new JSlider(0, 200, 100);
        saturationSlider.setMajorTickSpacing(50);
        saturationSlider.setMinorTickSpacing(10);
        saturationSlider.setPaintTicks(true);
        saturationSlider.setPaintLabels(true);
        
        JLabel valueLabel = new JLabel("Насыщенность: 100%");
        
        saturationSlider.addChangeListener(e -> {
            int value = saturationSlider.getValue();
            valueLabel.setText("Насыщенность: " + value + "%");
            drawPanel.applySaturationPreview(value / 100.0f);
        });
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Регулировка насыщенности:"), BorderLayout.NORTH);
        panel.add(saturationSlider, BorderLayout.CENTER);
        panel.add(valueLabel, BorderLayout.SOUTH);
        
        Object[] options = {"Применить", "Отмена"};
        int result = JOptionPane.showOptionDialog(this, panel, "Насыщенность", 
            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        
        if (result == 0) {
            drawPanel.applySaturationFilter(saturationSlider.getValue() / 100.0f);
        } else {
            drawPanel.cancelPreview();
        }
    }
    
    private void showContrastDialog() {
        JSlider contrastSlider = new JSlider(50, 200, 100);
        contrastSlider.setMajorTickSpacing(50);
        contrastSlider.setMinorTickSpacing(10);
        contrastSlider.setPaintTicks(true);
        contrastSlider.setPaintLabels(true);
        
        JLabel valueLabel = new JLabel("Контраст: 100%");
        
        contrastSlider.addChangeListener(e -> {
            int value = contrastSlider.getValue();
            valueLabel.setText("Контраст: " + value + "%");
            drawPanel.applyContrastPreview(value / 100.0f);
        });
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Регулировка контраста:"), BorderLayout.NORTH);
        panel.add(contrastSlider, BorderLayout.CENTER);
        panel.add(valueLabel, BorderLayout.SOUTH);
        
        Object[] options = {"Применить", "Отмена"};
        int result = JOptionPane.showOptionDialog(this, panel, "Контраст", 
            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        
        if (result == 0) {
            drawPanel.applyContrastFilter(contrastSlider.getValue() / 100.0f);
        } else {
            drawPanel.cancelPreview();
        }
    }

    private void createNewCanvas() {
        JPanel sizePanel = new JPanel(new GridLayout(2, 2, 5, 5));
        JTextField widthField = new JTextField("1000");
        JTextField heightField = new JTextField("600");
        
        sizePanel.add(new JLabel("Ширина:"));
        sizePanel.add(widthField);
        sizePanel.add(new JLabel("Высота:"));
        sizePanel.add(heightField);
        
        int result = JOptionPane.showConfirmDialog(this, sizePanel, "Создать новый холст", 
            JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                int width = Integer.parseInt(widthField.getText());
                int height = Integer.parseInt(heightField.getText());
                
                if (width > 0 && height > 0) {
                    drawPanel.createNewCanvas(width, height);
                } else {
                    JOptionPane.showMessageDialog(this, "Размеры должны быть положительными числами", 
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Введите корректные числовые значения", 
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

    // Методы применения фильтров к изображениям
    private BufferedImage applyGrayscale(BufferedImage image) {
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color color = new Color(image.getRGB(x, y));
                int gray = (int) (color.getRed() * 0.299 + color.getGreen() * 0.587 + color.getBlue() * 0.114);
                Color grayColor = new Color(gray, gray, gray);
                result.setRGB(x, y, grayColor.getRGB());
            }
        }
        return result;
    }
    
    private BufferedImage applySepia(BufferedImage image) {
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color color = new Color(image.getRGB(x, y));
                int r = color.getRed();
                int g = color.getGreen();
                int b = color.getBlue();
                
                int tr = (int) (0.393 * r + 0.769 * g + 0.189 * b);
                int tg = (int) (0.349 * r + 0.686 * g + 0.168 * b);
                int tb = (int) (0.272 * r + 0.534 * g + 0.131 * b);
                
                tr = Math.min(255, tr);
                tg = Math.min(255, tg);
                tb = Math.min(255, tb);
                
                Color sepiaColor = new Color(tr, tg, tb);
                result.setRGB(x, y, sepiaColor.getRGB());
            }
        }
        return result;
    }
    
    private BufferedImage applyInvert(BufferedImage image) {
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color color = new Color(image.getRGB(x, y));
                Color inverted = new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue());
                result.setRGB(x, y, inverted.getRGB());
            }
        }
        return result;
    }
    
    private BufferedImage applyBlur(BufferedImage image, int radius) {
        int size = radius * 2 + 1;
        float weight = 1.0f / (size * size);
        float[] data = new float[size * size];
        for (int i = 0; i < data.length; i++) {
            data[i] = weight;
        }
        
        Kernel kernel = new Kernel(size, size, data);
        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        return op.filter(image, null);
    }
    
    private BufferedImage applySharpen(BufferedImage image) {
        float[] sharpenMatrix = {
            0, -1, 0,
            -1, 5, -1,
            0, -1, 0
        };
        Kernel kernel = new Kernel(3, 3, sharpenMatrix);
        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        return op.filter(image, null);
    }
    
    private BufferedImage applyVintage(BufferedImage image) {
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color color = new Color(image.getRGB(x, y));
                int r = (int) (color.getRed() * 0.9);
                int g = (int) (color.getGreen() * 0.7);
                int b = (int) (color.getBlue() * 0.4);
                r = Math.min(255, Math.max(0, r + 20));
                g = Math.min(255, Math.max(0, g + 10));
                b = Math.min(255, Math.max(0, b));
                Color vintageColor = new Color(r, g, b);
                result.setRGB(x, y, vintageColor.getRGB());
            }
        }
        return result;
    }
    
    private BufferedImage applyCool(BufferedImage image) {
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color color = new Color(image.getRGB(x, y));
                int r = Math.min(255, color.getRed() - 10);
                int g = Math.min(255, color.getGreen());
                int b = Math.min(255, color.getBlue() + 20);
                Color coolColor = new Color(r, g, b);
                result.setRGB(x, y, coolColor.getRGB());
            }
        }
        return result;
    }
    
    private BufferedImage applyWarm(BufferedImage image) {
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color color = new Color(image.getRGB(x, y));
                int r = Math.min(255, color.getRed() + 20);
                int g = Math.min(255, color.getGreen() + 10);
                int b = Math.min(255, color.getBlue() - 10);
                Color warmColor = new Color(r, g, b);
                result.setRGB(x, y, warmColor.getRGB());
            }
        }
        return result;
    }
    
    private BufferedImage adjustSaturation(BufferedImage image, float saturation) {
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color color = new Color(image.getRGB(x, y));
                float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
                hsb[1] = Math.min(1.0f, hsb[1] * saturation);
                int rgb = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
                result.setRGB(x, y, rgb);
            }
        }
        return result;
    }
    
    private BufferedImage adjustContrast(BufferedImage image, float contrast) {
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color color = new Color(image.getRGB(x, y));
                int r = (int) (((color.getRed() / 255.0 - 0.5) * contrast + 0.5) * 255);
                int g = (int) (((color.getGreen() / 255.0 - 0.5) * contrast + 0.5) * 255);
                int b = (int) (((color.getBlue() / 255.0 - 0.5) * contrast + 0.5) * 255);
                r = Math.min(255, Math.max(0, r));
                g = Math.min(255, Math.max(0, g));
                b = Math.min(255, Math.max(0, b));
                Color contrastColor = new Color(r, g, b);
                result.setRGB(x, y, contrastColor.getRGB());
            }
        }
        return result;
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
        private Main main; // Ссылка на главное окно
        private BufferedImage canvas;
        private BufferedImage originalCanvas;
        private Graphics2D g2d;
        private int startX, startY;
        private Rectangle selectionRect;
        private String currentTool;
        private Color currentColor;
        private int currentSize;
        
        private Stack<BufferedImage> undoStack = new Stack<>();
        private Stack<BufferedImage> redoStack = new Stack<>();
        private static final int MAX_HISTORY = 20;

        public DrawPanel(Main main) {
            this.main = main; // Сохраняем ссылку на главное окно
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(1000, 600));
            setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 2));
            
            createNewCanvas(1000, 600);
            
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
                        if (selectionRect.width > 0 && selectionRect.height > 0) {
                            System.out.println("Выделена область: " + selectionRect);
                        }
                    }
                    // Сохраняем состояние для отмены после рисования
                    if (currentTool.equals("pencil") || currentTool.equals("eraser")) {
                        saveState();
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
            
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, getWidth(), getHeight());
            
            if (canvas != null) {
                g.drawImage(canvas, 0, 0, null);
            }
            
            if (currentTool.equals("select") && selectionRect != null && 
                selectionRect.width > 0 && selectionRect.height > 0) {
                Graphics2D g2 = (Graphics2D) g.create();
                
                g2.setColor(new Color(0, 120, 215, 80));
                g2.fill(selectionRect);
                
                g2.setColor(new Color(0, 120, 215));
                g2.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 
                    0, new float[]{5, 5}, 0));
                g2.draw(selectionRect);
                
                g2.setColor(Color.BLACK);
                g2.setFont(new Font("Arial", Font.BOLD, 12));
                String sizeText = selectionRect.width + " x " + selectionRect.height;
                g2.drawString(sizeText, selectionRect.x + 5, selectionRect.y + selectionRect.height - 5);
                
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
            saveState();
            repaint();
        }

        public void saveImage(File file) throws IOException {
            ImageIO.write(canvas, "PNG", file);
        }

        public void openImage(File file) throws IOException {
            BufferedImage image = ImageIO.read(file);
            createNewCanvas(image.getWidth(), image.getHeight());
            g2d.drawImage(image, 0, 0, null);
            saveState();
            repaint();
        }

        private void saveOriginal() {
            if (canvas != null) {
                originalCanvas = new BufferedImage(canvas.getWidth(), canvas.getHeight(), canvas.getType());
                Graphics2D g = originalCanvas.createGraphics();
                g.drawImage(canvas, 0, 0, null);
                g.dispose();
            }
        }

        private void restoreOriginal() {
            if (originalCanvas != null && canvas != null) {
                g2d.drawImage(originalCanvas, 0, 0, null);
                repaint();
            }
        }

        private void saveState() {
            if (canvas != null) {
                BufferedImage state = new BufferedImage(canvas.getWidth(), canvas.getHeight(), canvas.getType());
                Graphics2D g = state.createGraphics();
                g.drawImage(canvas, 0, 0, null);
                g.dispose();
                
                undoStack.push(state);
                redoStack.clear();
                
                // Ограничиваем размер истории
                if (undoStack.size() > MAX_HISTORY) {
                    undoStack.remove(0);
                }
                
                // Безопасно обновляем кнопки
                if (main != null) {
                    SwingUtilities.invokeLater(() -> main.updateButtonStates());
                }
            }
        }

        public void undo() {
            if (canUndo()) {
                redoStack.push(copyImage(canvas));
                BufferedImage previousState = undoStack.pop();
                g2d.drawImage(previousState, 0, 0, null);
                repaint();
                if (main != null) {
                    main.updateButtonStates();
                }
            }
        }

        public void redo() {
            if (canRedo()) {
                undoStack.push(copyImage(canvas));
                BufferedImage nextState = redoStack.pop();
                g2d.drawImage(nextState, 0, 0, null);
                repaint();
                if (main != null) {
                    main.updateButtonStates();
                }
            }
        }

        public boolean canUndo() {
            return !undoStack.isEmpty();
        }

        public boolean canRedo() {
            return !redoStack.isEmpty();
        }

        private BufferedImage copyImage(BufferedImage source) {
            BufferedImage copy = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
            Graphics2D g = copy.createGraphics();
            g.drawImage(source, 0, 0, null);
            g.dispose();
            return copy;
        }

        public BufferedImage createThumbnail() {
            if (canvas == null) return null;
            int thumbWidth = 80;
            int thumbHeight = 80;
            BufferedImage thumbnail = new BufferedImage(thumbWidth, thumbHeight, canvas.getType());
            Graphics2D g2d = thumbnail.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(canvas, 0, 0, thumbWidth, thumbHeight, null);
            g2d.dispose();
            return thumbnail;
        }

        // Методы применения фильтров
        public void applyBrightnessPreview(int value) {
            if (originalCanvas == null) saveOriginal();
            restoreOriginal();
            applyBrightness(value, false);
        }

        public void applyBrightnessFilter(int value) {
            saveState();
            applyBrightness(value, true);
        }

        private void applyBrightness(int value, boolean permanent) {
            float factor = 1.0f + value / 100.0f;
            
            for (int x = 0; x < canvas.getWidth(); x++) {
                for (int y = 0; y < canvas.getHeight(); y++) {
                    Color color = new Color(canvas.getRGB(x, y), true);
                    int r = Math.min(255, Math.max(0, (int)(color.getRed() * factor)));
                    int g = Math.min(255, Math.max(0, (int)(color.getGreen() * factor)));
                    int b = Math.min(255, Math.max(0, (int)(color.getBlue() * factor)));
                    int a = color.getAlpha();
                    
                    Color newColor = new Color(r, g, b, a);
                    canvas.setRGB(x, y, newColor.getRGB());
                }
            }
            
            if (permanent) {
                saveOriginal();
            }
            repaint();
        }

        public void applySaturationPreview(float saturation) {
            if (originalCanvas == null) saveOriginal();
            restoreOriginal();
            canvas = adjustSaturation(canvas, saturation);
            repaint();
        }

        public void applySaturationFilter(float saturation) {
            saveState();
            canvas = adjustSaturation(canvas, saturation);
            saveOriginal();
            repaint();
        }

        public void applyContrastPreview(float contrast) {
            if (originalCanvas == null) saveOriginal();
            restoreOriginal();
            canvas = adjustContrast(canvas, contrast);
            repaint();
        }

        public void applyContrastFilter(float contrast) {
            saveState();
            canvas = adjustContrast(canvas, contrast);
            saveOriginal();
            repaint();
        }

        public void applyBlurFilter() {
            saveState();
            canvas = applyBlur(canvas, 3);
            saveOriginal();
            repaint();
        }

        public void applySharpenFilter() {
            saveState();
            canvas = applySharpen(canvas);
            saveOriginal();
            repaint();
        }

        public void applyGrayscaleFilter() {
            saveState();
            canvas = applyGrayscale(canvas);
            saveOriginal();
            repaint();
        }

        public void applySepiaFilter() {
            saveState();
            canvas = applySepia(canvas);
            saveOriginal();
            repaint();
        }

        public void applyInvertFilter() {
            saveState();
            canvas = applyInvert(canvas);
            saveOriginal();
            repaint();
        }

        public void applyVintageFilter() {
            saveState();
            canvas = applyVintage(canvas);
            saveOriginal();
            repaint();
        }

        public void applyCoolFilter() {
            saveState();
            canvas = applyCool(canvas);
            saveOriginal();
            repaint();
        }

        public void applyWarmFilter() {
            saveState();
            canvas = applyWarm(canvas);
            saveOriginal();
            repaint();
        }

        public void applyHighSaturationFilter() {
            saveState();
            canvas = adjustSaturation(canvas, 1.5f);
            saveOriginal();
            repaint();
        }

        public void applyLowSaturationFilter() {
            saveState();
            canvas = adjustSaturation(canvas, 0.5f);
            saveOriginal();
            repaint();
        }

        public void applyHighContrastFilter() {
            saveState();
            canvas = adjustContrast(canvas, 1.5f);
            saveOriginal();
            repaint();
        }

        public void applyOriginal() {
            restoreOriginal();
            saveOriginal();
            repaint();
        }

        public void cancelPreview() {
            restoreOriginal();
            repaint();
        }

        public void cropSelection() {
            if (selectionRect == null || selectionRect.width <= 0 || selectionRect.height <= 0) {
                JOptionPane.showMessageDialog(main, 
                    "Сначала выделите область для обрезки с помощью инструмента 'Выделение'", 
                    "Нет выделения", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JDialog previewDialog = new JDialog(main, "Предпросмотр обрезки", true);
            previewDialog.setLayout(new BorderLayout());
            previewDialog.setSize(500, 400);
            previewDialog.setLocationRelativeTo(main);
            
            JLabel previewLabel = new JLabel();
            previewLabel.setHorizontalAlignment(SwingConstants.CENTER);
            previewLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            
            BufferedImage previewImage = canvas.getSubimage(
                selectionRect.x, selectionRect.y, selectionRect.width, selectionRect.height);
            ImageIcon icon = new ImageIcon(previewImage.getScaledInstance(
                Math.min(450, previewImage.getWidth()), 
                Math.min(300, previewImage.getHeight()), 
                Image.SCALE_SMOOTH));
            previewLabel.setIcon(icon);
            
            JLabel infoLabel = new JLabel("Новый размер: " + selectionRect.width + " x " + selectionRect.height, 
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
                BufferedImage croppedImage = new BufferedImage(
                    selectionRect.width, selectionRect.height, BufferedImage.TYPE_INT_ARGB);
                
                Graphics2D g2dCropped = croppedImage.createGraphics();
                g2dCropped.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2dCropped.drawImage(canvas, 
                    0, 0, selectionRect.width, selectionRect.height,
                    selectionRect.x, selectionRect.y, 
                    selectionRect.x + selectionRect.width, selectionRect.y + selectionRect.height,
                    null);
                g2dCropped.dispose();
                
                canvas = croppedImage;
                g2d = canvas.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                setPreferredSize(new Dimension(selectionRect.width, selectionRect.height));
                revalidate();
                
                selectionRect = null;
                saveState();
                repaint();
                
                JOptionPane.showMessageDialog(main, "Изображение успешно обрезано!", 
                    "Обрезка завершена", JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(main, "Ошибка при обрезке: " + ex.getMessage(), 
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
        
        // Методы для применения фильтров к изображениям
        private BufferedImage applyGrayscale(BufferedImage image) {
            BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    Color color = new Color(image.getRGB(x, y));
                    int gray = (int) (color.getRed() * 0.299 + color.getGreen() * 0.587 + color.getBlue() * 0.114);
                    Color grayColor = new Color(gray, gray, gray);
                    result.setRGB(x, y, grayColor.getRGB());
                }
            }
            return result;
        }
        
        private BufferedImage applySepia(BufferedImage image) {
            BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    Color color = new Color(image.getRGB(x, y));
                    int r = color.getRed();
                    int g = color.getGreen();
                    int b = color.getBlue();
                    
                    int tr = (int) (0.393 * r + 0.769 * g + 0.189 * b);
                    int tg = (int) (0.349 * r + 0.686 * g + 0.168 * b);
                    int tb = (int) (0.272 * r + 0.534 * g + 0.131 * b);
                    
                    tr = Math.min(255, tr);
                    tg = Math.min(255, tg);
                    tb = Math.min(255, tb);
                    
                    Color sepiaColor = new Color(tr, tg, tb);
                    result.setRGB(x, y, sepiaColor.getRGB());
                }
            }
            return result;
        }
        
        private BufferedImage applyInvert(BufferedImage image) {
            BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    Color color = new Color(image.getRGB(x, y));
                    Color inverted = new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue());
                    result.setRGB(x, y, inverted.getRGB());
                }
            }
            return result;
        }
        
        private BufferedImage applyBlur(BufferedImage image, int radius) {
            int size = radius * 2 + 1;
            float weight = 1.0f / (size * size);
            float[] data = new float[size * size];
            for (int i = 0; i < data.length; i++) {
                data[i] = weight;
            }
            
            Kernel kernel = new Kernel(size, size, data);
            ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
            return op.filter(image, null);
        }
        
        private BufferedImage applySharpen(BufferedImage image) {
            float[] sharpenMatrix = {
                0, -1, 0,
                -1, 5, -1,
                0, -1, 0
            };
            Kernel kernel = new Kernel(3, 3, sharpenMatrix);
            ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
            return op.filter(image, null);
        }
        
        private BufferedImage applyVintage(BufferedImage image) {
            BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    Color color = new Color(image.getRGB(x, y));
                    int r = (int) (color.getRed() * 0.9);
                    int g = (int) (color.getGreen() * 0.7);
                    int b = (int) (color.getBlue() * 0.4);
                    r = Math.min(255, Math.max(0, r + 20));
                    g = Math.min(255, Math.max(0, g + 10));
                    b = Math.min(255, Math.max(0, b));
                    Color vintageColor = new Color(r, g, b);
                    result.setRGB(x, y, vintageColor.getRGB());
                }
            }
            return result;
        }
        
        private BufferedImage applyCool(BufferedImage image) {
            BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    Color color = new Color(image.getRGB(x, y));
                    int r = Math.min(255, color.getRed() - 10);
                    int g = Math.min(255, color.getGreen());
                    int b = Math.min(255, color.getBlue() + 20);
                    Color coolColor = new Color(r, g, b);
                    result.setRGB(x, y, coolColor.getRGB());
                }
            }
            return result;
        }
        
        private BufferedImage applyWarm(BufferedImage image) {
            BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    Color color = new Color(image.getRGB(x, y));
                    int r = Math.min(255, color.getRed() + 20);
                    int g = Math.min(255, color.getGreen() + 10);
                    int b = Math.min(255, color.getBlue() - 10);
                    Color warmColor = new Color(r, g, b);
                    result.setRGB(x, y, warmColor.getRGB());
                }
            }
            return result;
        }
        
        private BufferedImage adjustSaturation(BufferedImage image, float saturation) {
            BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    Color color = new Color(image.getRGB(x, y));
                    float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
                    hsb[1] = Math.min(1.0f, hsb[1] * saturation);
                    int rgb = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
                    result.setRGB(x, y, rgb);
                }
            }
            return result;
        }
        
        private BufferedImage adjustContrast(BufferedImage image, float contrast) {
            BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    Color color = new Color(image.getRGB(x, y));
                    int r = (int) (((color.getRed() / 255.0 - 0.5) * contrast + 0.5) * 255);
                    int g = (int) (((color.getGreen() / 255.0 - 0.5) * contrast + 0.5) * 255);
                    int b = (int) (((color.getBlue() / 255.0 - 0.5) * contrast + 0.5) * 255);
                    r = Math.min(255, Math.max(0, r));
                    g = Math.min(255, Math.max(0, g));
                    b = Math.min(255, Math.max(0, b));
                    Color contrastColor = new Color(r, g, b);
                    result.setRGB(x, y, contrastColor.getRGB());
                }
            }
            return result;
        }
    }
}