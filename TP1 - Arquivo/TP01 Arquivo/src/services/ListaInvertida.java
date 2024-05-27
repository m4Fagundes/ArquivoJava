package services;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ListaInvertida {

    RandomAccessFile arquivoIndice;
    RandomAccessFile arquivoListas;
    HashMap<String, Long> indice = new HashMap<>();

    public ListaInvertida() throws Exception {
        arquivoIndice = new RandomAccessFile("TP1 - Arquivo/TP01 Arquivo/src/DataBase/Palavra_EnderecoLinkedList.db", "rw");
        arquivoListas = new RandomAccessFile("TP1 - Arquivo/TP01 Arquivo/src/DataBase/LinkedList_OBJ.db", "rw");

        // Carrega o índice existente dos arquivos
        carregarIndice();
        carregarLista();
    }

    // Método para inserir uma entrada na lista e no índice
    public void inserir(String entrada, int id) throws Exception {
        LinkedList<String> palavras = subdividirEmPalavras(entrada);
        LinkedList<String> combinacoes = gerarCombinacoes(palavras);

        for (String combinacao : combinacoes) {
            if (!indice.containsKey(combinacao)) {
                LinkedList<Integer> listaID = new LinkedList<>();
                listaID.add(id);
                long posicaoLista = salvarListaNoArquivo(listaID);

                indice.put(combinacao, posicaoLista);
            } else {
                long posicaoLista = indice.get(combinacao);
                LinkedList<Integer> listaID = carregarListaDoArquivo(posicaoLista);
                listaID.add(id);
                posicaoLista = salvarListaNoArquivo(listaID);
                indice.put(combinacao, posicaoLista);
            }
        }

        // Salvar o índice e a lista após inserção
        salvarIndice();
        salvarLista();
    }

    // Implementação dos métodos de persistência
    private void salvarIndice() throws IOException {
        arquivoIndice.setLength(0);
        arquivoIndice.seek(0);

        for (Map.Entry<String, Long> entrada : indice.entrySet()) {
            arquivoIndice.writeUTF(entrada.getKey());
            arquivoIndice.writeLong(entrada.getValue());
        }
    }

    private void carregarIndice() throws IOException {
        arquivoIndice.seek(0);
        indice.clear();

        while (arquivoIndice.getFilePointer() < arquivoIndice.length()) {
            String palavra = arquivoIndice.readUTF();
            long posicaoLista = arquivoIndice.readLong();
            indice.put(palavra, posicaoLista);
        }
    }

    private void salvarLista() throws IOException {
        // Percorre o HashMap de índice para salvar todas as listas de endereços no
        // arquivo
        for (Map.Entry<String, Long> entrada : indice.entrySet()) {
            LinkedList<Integer> listaID = carregarListaDoArquivo(entrada.getValue());

            // Atualiza a lista no arquivo com os endereços salvos
            long posicaoLista = salvarListaNoArquivo(listaID);
            indice.put(entrada.getKey(), posicaoLista);
        }
    }

    private void carregarLista() throws IOException {
        // Percorre o arquivo de índice para carregar as entradas e suas respectivas
        // listas de endereços
        arquivoIndice.seek(0);
        indice.clear();

        while (arquivoIndice.getFilePointer() < arquivoIndice.length()) {
            String palavra = arquivoIndice.readUTF();
            long posicaoLista = arquivoIndice.readLong();
            indice.put(palavra, posicaoLista);
        }
    }

    // Métodos auxiliares mantidos
    private LinkedList<String> subdividirEmPalavras(String entrada) {
        LinkedList<String> palavras = new LinkedList<>();

        if (entrada != null && !entrada.isEmpty()) {
            String[] palavrasArray = entrada.toLowerCase().split("\\W+");
            for (String palavra : palavrasArray) {
                palavras.add(palavra);
            }
        }

        return palavras;
    }

    private LinkedList<String> gerarCombinacoes(LinkedList<String> palavras) {
        LinkedList<String> combinacoes = new LinkedList<>();

        int n = palavras.size();
        for (int i = 0; i < n; i++) {
            StringBuilder combinacao = new StringBuilder();
            for (int j = i; j < n; j++) {
                if (j > i) {
                    combinacao.append(" ");
                }
                combinacao.append(palavras.get(j));
                combinacoes.add(combinacao.toString());
            }
        }

        return combinacoes;
    }

    private long salvarListaNoArquivo(LinkedList<Integer> listaID) throws IOException {
        arquivoListas.seek(arquivoListas.length());
        long posicaoLista = arquivoListas.getFilePointer();

        arquivoListas.writeInt(listaID.size());
        for (Integer ID : listaID) {
            arquivoListas.writeInt(ID);
        }

        return posicaoLista;
    }

    private LinkedList<Integer> carregarListaDoArquivo(long posicao) throws IOException {
        LinkedList<Integer> listaID = new LinkedList<>();
    
        arquivoListas.seek(posicao);
        int tamanhoLista = arquivoListas.readInt();
        
        for (int i = 0; i < tamanhoLista; i++) {
            int endereco = arquivoListas.readInt();
            listaID.add(endereco);
        }
    
        return listaID;
    }

}
