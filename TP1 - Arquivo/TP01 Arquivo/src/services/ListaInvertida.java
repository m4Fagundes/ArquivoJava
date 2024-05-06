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
        arquivoIndice = new RandomAccessFile("Palavra_EnderecoLinkedList.db", "rw");
        arquivoListas = new RandomAccessFile("LinkedList_OBJ.db", "rw");

        // Carrega o índice existente dos arquivos
        carregarIndice();
        carregarLista();
    }

    // Método para inserir uma entrada na lista e no índice
    public void inserir(String entrada, int id) throws Exception {
        LinkedList<String> palavras = subdividirEmPalavras(entrada);

        for (String palavra : palavras) {
            if (!indice.containsKey(palavra)) {
                LinkedList<Integer> listaID = new LinkedList<>();
                System.out.println("ID DENTRO DO ISERIR LINKED LIST: "+id);
                listaID.add(id);
                long posicaoLista = salvarListaNoArquivo(listaID);

                indice.put(palavra, posicaoLista);
            } else {
                System.out.println("ID DENTRO DO ISERIR LINKED LIST: "+id);

                long posicaoLista = indice.get(palavra);
                LinkedList<Integer> listaID = carregarListaDoArquivo(posicaoLista);
                listaID.add(id);
                posicaoLista = salvarListaNoArquivo(listaID);
                indice.put(palavra, posicaoLista);
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
