class FileSystemManager {

    int diskSize
    int[] disk

    // Map: filename -> [start, size, owner]
    Map<String, Map> files = [:]

    void loadInitialState(File f) {
        def lines = f.readLines()

        diskSize = lines[0] as int
        disk = new int[diskSize].collect { 0 }

        int occupied = lines[1] as int

        // carrega blocos existentes
        (0..<occupied).each { idx ->
            def parts = lines[idx + 2].split(',').collect { it.trim() }
            def name = parts[0]
            def start = parts[1] as int
            def size = parts[2] as int

            files[name] = [start: start, size: size, owner: -1]

            (0..<size).each { i -> disk[start + i] = name.charAt(0) as int }
        }
    }

    void executeOperations(File f, List<ProcessManager> processes) {
        def lines = f.readLines()
        int occupied = lines[1] as int
        def ops = lines.drop(occupied + 2)

        ops.eachWithIndex { line, idx ->
            def parts = line.split(',').collect { it.trim() }

            int pid = parts[0] as int
            int op = parts[1] as int
            String name = parts[2]

            ProcessManager p = processes.find { it.pid == pid }

            if (!p) {
                println "Operacao ${idx+1} => Falha"
                println "O processo ${pid} nao existe."
                return
            }

            if (op == 0) {
                int blocks = parts[3] as int
                createFile(p, name, blocks, idx + 1)
            } else {
                deleteFile(p, name, idx + 1)
            }
        }
    }

    void createFile(ProcessManager p, String name, int size, int opNum) {
        int start = firstFit(size)

        if (start < 0) {
            println "Operacao ${opNum} => Falha"
            println "O processo ${p.pid} nao pode criar o arquivo ${name} (falta de espaco)."
            return
        }

        files[name] = [start: start, size: size, owner: p.pid]

        (0..<size).each { i -> disk[start + i] = name.charAt(0) as int }

        println "Operacao ${opNum} => Sucesso"
        println "O processo ${p.pid} criou o arquivo ${name} (blocos ${start} a ${start+size-1})."
    }

    void deleteFile(ProcessManager p, String name, int opNum) {
        if (!files.containsKey(name)) {
            println "Operacao ${opNum} => Falha"
            println "O arquivo ${name} nao existe."
            return
        }

        def meta = files[name]

        boolean allowed = (p.priority == 0) || (meta.owner == p.pid)

        if (!allowed) {
            println "Operacao ${opNum} => Falha"
            println "O processo ${p.pid} nao pode deletar o arquivo ${name}."
            return
        }

        (0..<meta.size).each { i -> disk[meta.start + i] = 0 }

        files.remove(name)

        println "Operacao ${opNum} => Sucesso"
        println "O processo ${p.pid} deletou o arquivo ${name}."
    }

    int firstFit(int size) {
        for (int i = 0; i <= diskSize - size; i++) {
            boolean free = (0..<size).every { disk[i + it] == 0 }
            if (free) return i
        }
        return -1
    }

    void printDiskMap() {
        println '\nMapa de ocupacao do disco:'
        println disk.collect { it == 0 ? '0' : (char)it }.join(' ')
    }

}
