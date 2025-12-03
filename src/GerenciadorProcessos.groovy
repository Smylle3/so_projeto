class GerenciadorProcessos {
    int processoId
    int tempoChegada
    int prioridade
    int prioridadeInicial
    int tempoProcessamento
    int tempoRestante
    int blocosDeMemoriaAlocados
    int offsetMemoria = -1

    int tempoEspera
    boolean scannerAlocado
    int impressoraId
    boolean modemAlocado
    int sataId

    GerenciadorProcessos(int processoId, int tempoChegada, int prioridade, int prioridadeInicial, int tempoProcessamento, int tempoRestante, int blocosDeMemoriaAlocados, int impressoraId,  boolean scannerAlocado, boolean modemAlocado, int sataId) {
        this.processoId = processoId
        this.tempoChegada = tempoChegada
        this.prioridade = prioridade
        this.prioridadeInicial = prioridadeInicial
        this.tempoProcessamento = tempoProcessamento
        this.tempoRestante = tempoRestante
        this.blocosDeMemoriaAlocados = blocosDeMemoriaAlocados
        this.scannerAlocado = scannerAlocado
        this.impressoraId = impressoraId
        this.modemAlocado = modemAlocado
        this.sataId = sataId
        this.tempoEspera = 0
    }

    static GerenciadorProcessos extrairProcesso(String linha, int processoId) {
        def elementos = linha.split(',').collect { it.trim() }

        return new GerenciadorProcessos(
                processoId,
                elementos[0] as int,
                elementos[1] as int,
                elementos[1] as int,
                elementos[2] as int,
                elementos[2] as int,
                elementos[3] as int,
                elementos[4] as int,
                (elementos[5] as int) == 1,
                (elementos[6] as int) == 1,
                elementos[7] as int
        )
    }

    static List<GerenciadorProcessos> processarArquivo(File file) {
        List<GerenciadorProcessos> listaProcessos = []
        int numeroProcessos = 0

        file.eachLine { line ->
            if (line.trim()) {
                listaProcessos << extrairProcesso(line, numeroProcessos++)
            }
        }

        return listaProcessos
    }

    void printStart() {
        println("process ${processoId} =>")
        println "P${processoId} STARTED"
    }

    void printInstruction(int step) {
        println "P${processoId} instruction ${step}"
    }

    void printEnd() {
        println "P${processoId} return SIGINT\n"
    }


}
