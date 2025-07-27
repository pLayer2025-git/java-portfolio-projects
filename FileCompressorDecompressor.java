import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.zip.GZIPOutputStream;
import java.util.zip.GZIPInputStream;

public class FileCompressorDecompressor extends JFrame implements ActionListener {

    private JButton compressButton, decompressButton;

    public FileCompressorDecompressor() {
        setTitle("File Compressor & Decompressor");
        setSize(450, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center window
        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 40));

        compressButton = new JButton("📦 Compress File");
        decompressButton = new JButton("📂 Decompress File");

        compressButton.setPreferredSize(new Dimension(180, 40));
        decompressButton.setPreferredSize(new Dimension(180, 40));

        compressButton.addActionListener(this);
        decompressButton.addActionListener(this);

        add(compressButton);
        add(decompressButton);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FileCompressorDecompressor().setVisible(true);
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == compressButton) {
            compressFile();
        } else if (e.getSource() == decompressButton) {
            decompressFile();
        }
    }

    private void compressFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select File to Compress");

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File inputFile = fileChooser.getSelectedFile();
            File outputFile = new File(inputFile.getAbsolutePath() + ".gz");

            try (
                    FileInputStream fis = new FileInputStream(inputFile);
                    FileOutputStream fos = new FileOutputStream(outputFile);
                    GZIPOutputStream gzipOS = new GZIPOutputStream(fos)
            ) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) != -1) {
                    gzipOS.write(buffer, 0, len);
                }
                JOptionPane.showMessageDialog(this, "✅ File compressed successfully!\nSaved as: " + outputFile.getName());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "❌ Error during compression:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void decompressFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select .gz File to Decompress");

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File inputFile = fileChooser.getSelectedFile();

            // Remove .gz extension
            String outputFileName = inputFile.getAbsolutePath().replaceFirst("[.][gG][zZ]$", "");
            File outputFile = new File(outputFileName);

            try (
                    FileInputStream fis = new FileInputStream(inputFile);
                    GZIPInputStream gzipIS = new GZIPInputStream(fis);
                    FileOutputStream fos = new FileOutputStream(outputFile)
            ) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = gzipIS.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }

                // ✅ Delete the original .gz file after successful decompression
                if (inputFile.delete()) {
                    JOptionPane.showMessageDialog(this, "✅ File decompressed successfully!\nDeleted: " + inputFile.getName());
                } else {
                    JOptionPane.showMessageDialog(this, "⚠️ Decompressed successfully but failed to delete: " + inputFile.getName());
                }

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "❌ Error during decompression:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
