// Painel principal do jogo Bejeweled
// Esta classe representa o tabuleiro do jogo e toda a lógica principal.
// O tabuleiro é composto por uma grelha de botões, cada um representando uma gema.
// Os alunos podem usar esta classe para perceber conceitos de arrays bidimensionais, eventos e interface gráfica em Java.
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.util.HashSet;
import java.util.Set;

public class GameBoard extends JPanel {
    // Constantes para o tamanho do tabuleiro e das gemas
    private static final int ROWS = 8; // Número de linhas do tabuleiro
    private static final int COLS = 8; // Número de colunas do tabuleiro
    private static final int GEM_SIZE = 64; // Tamanho dos ícones das gemas (ajuste conforme as imagens)

    // Matrizes para guardar as gemas e os botões do tabuleiro
    private Gem[][] board = new Gem[ROWS][COLS];
    private JButton[][] buttons = new JButton[ROWS][COLS];
    // Variáveis para guardar a seleção do utilizador
    private int selectedRow = -1, selectedCol = -1;
    private Random random = new Random(); // Gerador de números aleatórios para as gemas
    private int score = 0; // Pontuação do jogador
    private JLabel scoreLabel = new JLabel("Pontuação: 0"); // Label para mostrar a pontuação

    // Construtor: inicializa o painel e o tabuleiro
    public GameBoard() {
        setLayout(new BorderLayout()); // Usamos BorderLayout para ter espaço para a pontuação
        JPanel gridPanel = new JPanel(new GridLayout(ROWS, COLS));
        // Inicializa o tabuleiro e interface
        initBoard();
        // Cria os botões e adiciona ao painel
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                JButton btn = new JButton();
                btn.setIcon(resizeIcon(board[row][col].getIcon(), GEM_SIZE, GEM_SIZE));
                btn.setPreferredSize(new Dimension(GEM_SIZE, GEM_SIZE));
                final int r = row, c = col;
                btn.addActionListener(e -> handleGemClick(r, c));
                buttons[row][col] = btn;
                gridPanel.add(btn);
            }
        }
        add(gridPanel, BorderLayout.CENTER);
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(scoreLabel, BorderLayout.NORTH);
        updateBoard();
    }

    // Inicializa o tabuleiro com gemas aleatórias
    private void initBoard() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                GemType type = GemType.values()[random.nextInt(GemType.values().length)];
                board[row][col] = new Gem(type);
            }
        }
        // Garante que não há combinações iniciais
        while (hasCombination()) {
            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLS; col++) {
                    GemType type = GemType.values()[random.nextInt(GemType.values().length)];
                    board[row][col] = new Gem(type);
                }
            }
        }
    }

    // Atualiza os ícones dos botões para corresponder ao estado do tabuleiro
    private void updateBoard() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                buttons[row][col].setIcon(resizeIcon(board[row][col].getIcon(), GEM_SIZE, GEM_SIZE));
                buttons[row][col].setBorder(null);
            }
        }
        scoreLabel.setText("Pontuação: " + score);
        revalidate();
        repaint();
    }

    // Redimensiona o ícone da gema para o tamanho desejado
    private ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image resized = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resized);
    }

    // Lida com o clique numa gema
    private void handleGemClick(int row, int col) {
        if (selectedRow == -1 && selectedCol == -1) {
            // Primeira seleção: destaca a gema
            selectedRow = row;
            selectedCol = col;
            buttons[row][col].setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
        } else {
            // Segunda seleção: tenta trocar as gemas se forem vizinhas
            if (isAdjacent(selectedRow, selectedCol, row, col)) {
                swapGems(selectedRow, selectedCol, row, col);
                // Só aceita a troca se gerar combinação
                if (hasCombination()) {
                    processCombinations();
                } else {
                    // Se não houver combinação, desfaz a troca
                    swapGems(selectedRow, selectedCol, row, col);
                }
            }
            buttons[selectedRow][selectedCol].setBorder(null); // Remove destaque
            selectedRow = selectedCol = -1; // Limpa seleção
            updateBoard(); // Atualiza o tabuleiro
        }
    }

    // Verifica se duas posições são adjacentes (vizinho direto)
    private boolean isAdjacent(int r1, int c1, int r2, int c2) {
        return (Math.abs(r1 - r2) == 1 && c1 == c2) || (Math.abs(c1 - c2) == 1 && r1 == r2);
    }

    // Troca duas gemas de posição no tabuleiro
    private void swapGems(int r1, int c1, int r2, int c2) {
        Gem temp = board[r1][c1];
        board[r1][c1] = board[r2][c2];
        board[r2][c2] = temp;
    }

    // Função para verificar se existe alguma combinação no tabuleiro
    // Retorna true se houver pelo menos uma linha/coluna de 3 ou mais gemas iguais
    private boolean hasCombination() {
        return !getCombinationPositions().isEmpty();
    }

    // Função que devolve um conjunto de posições (row,col) que fazem parte de combinações
    private Set<Point> getCombinationPositions() {
        Set<Point> toRemove = new HashSet<>();
        // Verifica linhas
        for (int row = 0; row < ROWS; row++) {
            int count = 1;
            for (int col = 1; col < COLS; col++) {
                if (board[row][col].getType() == board[row][col-1].getType()) {
                    count++;
                } else {
                    if (count >= 3) {
                        for (int k = 0; k < count; k++) {
                            toRemove.add(new Point(row, col-1-k));
                        }
                    }
                    count = 1;
                }
            }
            if (count >= 3) {
                for (int k = 0; k < count; k++) {
                    toRemove.add(new Point(row, COLS-1-k));
                }
            }
        }
        // Verifica colunas
        for (int col = 0; col < COLS; col++) {
            int count = 1;
            for (int row = 1; row < ROWS; row++) {
                if (board[row][col].getType() == board[row-1][col].getType()) {
                    count++;
                } else {
                    if (count >= 3) {
                        for (int k = 0; k < count; k++) {
                            toRemove.add(new Point(row-1-k, col));
                        }
                    }
                    count = 1;
                }
            }
            if (count >= 3) {
                for (int k = 0; k < count; k++) {
                    toRemove.add(new Point(ROWS-1-k, col));
                }
            }
        }
        return toRemove;
    }

    // Processa todas as combinações: remove gemas, faz cair, preenche e atualiza pontuação
    private void processCombinations() {
        boolean found;
        do {
            Set<Point> toRemove = getCombinationPositions();
            found = !toRemove.isEmpty();
            if (found) {
                score += toRemove.size() * 10; // 10 pontos por gema removida
                // Remove as gemas (coloca null)
                for (Point p : toRemove) {
                    board[p.x][p.y] = null;
                }
                // Faz as gemas "caírem"
                applyGravity();
                // Preenche espaços vazios no topo
                fillEmptySpaces();
                updateBoard();
                // Pequena pausa para o jogador ver as explosões (opcional)
                try { Thread.sleep(200); } catch (InterruptedException e) { }
            }
        } while (found);
    }

    // Aplica a gravidade: faz as gemas caírem para preencher espaços vazios
    private void applyGravity() {
        for (int col = 0; col < COLS; col++) {
            int empty = ROWS - 1;
            for (int row = ROWS - 1; row >= 0; row--) {
                if (board[row][col] != null) {
                    board[empty][col] = board[row][col];
                    if (empty != row) board[row][col] = null;
                    empty--;
                }
            }
        }
    }

    // Preenche os espaços vazios no topo com novas gemas aleatórias
    private void fillEmptySpaces() {
        for (int col = 0; col < COLS; col++) {
            for (int row = 0; row < ROWS; row++) {
                if (board[row][col] == null) {
                    GemType type = GemType.values()[random.nextInt(GemType.values().length)];
                    board[row][col] = new Gem(type);
                }
            }
        }
    }
}
