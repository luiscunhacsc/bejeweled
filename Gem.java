// Classe que representa uma gema individual no tabuleiro
// Esta classe mostra como criar um objeto simples em Java com propriedades e métodos.
// Cada gema tem um tipo (cor) e uma imagem associada.
import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.awt.image.BufferedImage;

public class Gem {
    private GemType type; // Tipo da gema (cor)
    private ImageIcon icon; // Imagem associada à gema

    // Construtor: cria uma gema do tipo indicado e carrega a imagem
    public Gem(GemType type) {
        this.type = type;
        this.icon = loadIcon(type);
    }

    // Devolve o tipo da gema
    public GemType getType() {
        return type;
    }

    // Permite alterar o tipo da gema e atualizar a imagem
    public void setType(GemType type) {
        this.type = type;
        this.icon = loadIcon(type);
    }

    // Devolve o ícone (imagem) da gema
    public ImageIcon getIcon() {
        return icon;
    }

    // Método privado para carregar a imagem da gema como recurso
    // Mostra como tratar recursos e criar imagens de fallback
    private ImageIcon loadIcon(GemType type) {
        String path = "/gemsPNG/" + type.getImageFileName(); // Caminho relativo
        URL imgURL = getClass().getResource(path); // Tenta carregar como recurso
        if (imgURL != null) {
            return new ImageIcon(imgURL); // Imagem encontrada
        } else {
            // Se falhar, cria um ícone cinzento para evitar espaços em branco
            Image img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
            Graphics g = img.getGraphics();
            g.setColor(Color.GRAY);
            g.fillRect(0, 0, 64, 64);
            g.dispose();
            return new ImageIcon(img);
        }
    }
}
