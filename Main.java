import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;//для отправки в гит
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
}