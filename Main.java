// Classe principal para iniciar o jogo Bejeweled
// Esta classe contém o método main, que é o ponto de entrada do programa.
// Aqui criamos a janela principal do jogo e adicionamos o painel do tabuleiro.
// Toda a interface gráfica é criada dentro do método invokeLater para garantir
// que corre na thread correta do Swing (boa prática em Java).
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // O método invokeLater garante que o código da interface gráfica
        // é executado na thread correta do Swing.
        SwingUtilities.invokeLater(() -> {
            // Cria a janela principal do jogo
            JFrame frame = new JFrame("Bejeweled - Projeto Didático");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Fecha o programa ao fechar a janela
            frame.setResizable(false); // Impede o redimensionamento da janela

            // Adiciona o painel do jogo (tabuleiro) à janela
            frame.add(new GameBoard());
            frame.pack(); // Ajusta o tamanho da janela automaticamente
            frame.setLocationRelativeTo(null); // Centraliza a janela no ecrã
            frame.setVisible(true); // Torna a janela visível
        });
    }
}
