// Enum para os tipos de gemas disponíveis no jogo
// Um enum é uma forma de definir um conjunto fixo de constantes em Java.
// Aqui, cada tipo de gema está associado ao nome do ficheiro da imagem correspondente.
public enum GemType {
    BLUE("blueGem.png"),
    GREEN("greenGem.png"),
    ORANGE("orangeGem.png"),
    PURPLE("purpleGem.png"),
    RED("redGem.png"),
    WHITE("whiteGem.png"),
    YELLOW("yellowGem.png");

    private final String imageFileName; // Nome do ficheiro da imagem

    // Construtor do enum: associa o nome do ficheiro a cada tipo
    GemType(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    // Método para obter o nome do ficheiro da imagem
    public String getImageFileName() {
        return imageFileName;
    }
}
