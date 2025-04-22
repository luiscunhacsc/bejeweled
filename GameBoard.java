// Painel principal do jogo Bejeweled
// Esta classe representa o tabuleiro do jogo e toda a lógica principal.
// O tabuleiro é composto por uma grelha de botões, cada um representando uma gema.
// Os alunos podem usar esta classe para perceber conceitos de arrays bidimensionais, eventos e interface gráfica em Java.
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.AlphaComposite;
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
    private boolean animating = false; // Estado para bloquear interação durante animações

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
    // Agora é robusto: se board[row][col] == null, mostra um botão vazio (ou cinzento)
    private void updateBoard() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (board[row][col] != null) {
                    buttons[row][col].setIcon(resizeIcon(board[row][col].getIcon(), GEM_SIZE, GEM_SIZE));
                } else {
                    // Mostra um botão vazio (ou um quadrado cinzento)
                    buttons[row][col].setIcon(emptyIcon());
                }
                buttons[row][col].setBorder(null);
            }
        }
        scoreLabel.setText("Pontuação: " + score);
        revalidate();
        repaint();
    }

    // Cria um ícone cinzento para posições vazias
    private ImageIcon emptyIcon() {
        BufferedImage img = new BufferedImage(GEM_SIZE, GEM_SIZE, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRect(0, 0, GEM_SIZE, GEM_SIZE);
        g2.dispose();
        return new ImageIcon(img);
    }

    // Redimensiona o ícone da gema para o tamanho desejado
    private ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image resized = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resized);
    }

    // Lida com o clique numa gema
    private void handleGemClick(int row, int col) {
        if (animating) return; // Bloqueia interação durante animações
        if (selectedRow == -1 && selectedCol == -1) {
            selectedRow = row;
            selectedCol = col;
            buttons[row][col].setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
        } else {
            if (isAdjacent(selectedRow, selectedCol, row, col)) {
                // Guardar os valores antes de limpar a seleção!
                final int prevRow = selectedRow;
                final int prevCol = selectedCol;
                final int targetRow = row;
                final int targetCol = col;
                animating = true;
                animateSlide(prevRow, prevCol, targetRow, targetCol, () -> {
                    swapGems(prevRow, prevCol, targetRow, targetCol);
                    if (hasCombination()) {
                        animateCombinations();
                    } else {
                        animateSlide(targetRow, targetCol, prevRow, prevCol, () -> {
                            swapGems(prevRow, prevCol, targetRow, targetCol);
                            updateBoard();
                            animating = false;
                        });
                    }
                });
            }
            buttons[selectedRow][selectedCol].setBorder(null);
            selectedRow = selectedCol = -1;
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

    // Animação de deslize (slide) entre duas gemas adjacentes
    private void animateSlide(int r1, int c1, int r2, int c2, Runnable onComplete) {
        final int frames = 8;
        final ImageIcon icon1 = (board[r1][c1] != null) ? resizeIcon(board[r1][c1].getIcon(), GEM_SIZE, GEM_SIZE) : emptyIcon();
        final ImageIcon icon2 = (board[r2][c2] != null) ? resizeIcon(board[r2][c2].getIcon(), GEM_SIZE, GEM_SIZE) : emptyIcon();
        Timer timer = new Timer(20, null);
        timer.addActionListener(new ActionListener() {
            int step = 0;
            public void actionPerformed(ActionEvent e) {
                step++;
                // Desenha os ícones "a meio caminho" (simulação simples)
                // Para efeito visual, alterna os ícones entre os botões
                if (step % 2 == 0) {
                    buttons[r1][c1].setIcon(icon2);
                    buttons[r2][c2].setIcon(icon1);
                } else {
                    buttons[r1][c1].setIcon(icon1);
                    buttons[r2][c2].setIcon(icon2);
                }
                if (step >= frames) {
                    timer.stop();
                    onComplete.run();
                }
            }
        });
        timer.start();
    }

    // Explosão (zoom) das gemas em combinação antes do fade-out
    private void animateExplosion(Set<Point> toExplode, Runnable onComplete) {
        final int frames = 6;
        Timer timer = new Timer(30, null);
        timer.addActionListener(new ActionListener() {
            int step = 0;
            public void actionPerformed(ActionEvent e) {
                float scale = 1.0f + 0.3f * (float)Math.sin(Math.PI * step / frames); // efeito "zoom"
                for (Point p : toExplode) {
                    if (board[p.x][p.y] != null)
                        buttons[p.x][p.y].setIcon(scaledIcon(board[p.x][p.y].getIcon(), (int)(GEM_SIZE*scale), (int)(GEM_SIZE*scale)));
                }
                updateBoard();
                step++;
                if (step > frames) {
                    timer.stop();
                    onComplete.run();
                }
            }
        });
        timer.start();
    }

    // Utilitário para criar um ícone redimensionado (para o efeito de zoom)
    private ImageIcon scaledIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image resized = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage bimg = new BufferedImage(GEM_SIZE, GEM_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bimg.createGraphics();
        int x = (GEM_SIZE-width)/2, y = (GEM_SIZE-height)/2;
        g2.drawImage(resized, x, y, width, height, null);
        g2.dispose();
        return new ImageIcon(bimg);
    }

    // MÉTODO PRINCIPAL DE ANIMAÇÃO (substitui processCombinations):
    private void animateCombinations() {
        animating = true;
        Set<Point> toRemove = getCombinationPositions();
        if (toRemove.isEmpty()) {
            animating = false;
            updateBoard();
            return;
        }
        // Atualiza a pontuação ANTES da animação (cada gema removida vale 10 pontos)
        score += toRemove.size() * 10;
        updateBoard(); // Atualiza o label da pontuação imediatamente
        // Explosão (zoom) antes do fade-out
        animateExplosion(toRemove, () -> {
            new FadeOutAnimation(toRemove, () -> {
                for (Point p : toRemove) {
                    board[p.x][p.y] = null;
                }
                // Anima a queda das gemas
                animateGravity(() -> {
                    fillEmptySpaces();
                    updateBoard();
                    // Verifica se há novas combinações
                    if (hasCombination()) {
                        animateCombinations(); // Chama recursivamente para combos
                    } else {
                        animating = false;
                    }
                });
            }).start();
        });
    }

    // CLASSE PARA ANIMAÇÃO DE FADE-OUT
    private class FadeOutAnimation {
        private final Set<Point> points;
        private final Runnable onComplete;
        private int step = 0;
        private final int maxSteps = 7;
        private final Timer timer;

        FadeOutAnimation(Set<Point> points, Runnable onComplete) {
            this.points = points;
            this.onComplete = onComplete;
            this.timer = new Timer(40, null); // 40ms por frame
            this.timer.addActionListener(e -> animateStep());
        }
        void start() { timer.start(); }
        private void animateStep() {
            for (Point p : points) {
                JButton btn = buttons[p.x][p.y];
                btn.setIcon(fadeIcon(board[p.x][p.y].getIcon(), 1.0f - (float)step/maxSteps));
            }
            updateBoard();
            step++;
            if (step > maxSteps) {
                timer.stop();
                onComplete.run();
            }
        }
    }

    // Função utilitária para criar um ícone com opacidade reduzida
    private ImageIcon fadeIcon(ImageIcon icon, float alpha) {
        Image img = icon.getImage();
        BufferedImage faded = new BufferedImage(GEM_SIZE, GEM_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = faded.createGraphics();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2.drawImage(img, 0, 0, GEM_SIZE, GEM_SIZE, null);
        g2.dispose();
        return new ImageIcon(faded);
    }

    // ANIMAÇÃO DA QUEDA DAS GEMAS
    private void animateGravity(Runnable onComplete) {
        final int steps = 8; // Número de frames da animação
        boolean[][] moved = new boolean[ROWS][COLS];
        // Calcula para cada coluna quantas gemas vão cair
        for (int col = 0; col < COLS; col++) {
            int empty = ROWS - 1;
            for (int row = ROWS - 1; row >= 0; row--) {
                if (board[row][col] != null) {
                    if (empty != row) moved[row][col] = true;
                    empty--;
                }
            }
        }
        Timer timer = new Timer(40, null);
        timer.addActionListener(new ActionListener() {
            int frame = 0;
            public void actionPerformed(ActionEvent e) {
                frame++;
                for (int row = ROWS-1; row >= 0; row--) {
                    for (int col = 0; col < COLS; col++) {
                        if (moved[row][col]) {
                            int targetRow = row;
                            while (targetRow+1 < ROWS && board[targetRow+1][col] == null) targetRow++;
                            if (targetRow != row) {
                                buttons[targetRow][col].setIcon(buttons[row][col].getIcon());
                                buttons[row][col].setIcon(null);
                            }
                        }
                    }
                }
                updateBoard();
                if (frame >= steps) {
                    timer.stop();
                    applyGravity();
                    onComplete.run();
                }
            }
        });
        timer.start();
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
