package services;

import java.io.*;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ListaInvertida {

    private RandomAccessFile arquivoIndice;
    private RandomAccessFile arquivoListas;
    private HashMap<String, Long> indice = new HashMap<>();

    /**
     * Construtor da classe que inicializa os arquivos e carrega o índice existente.
     */
    public ListaInvertida() throws IOException {
        arquivoIndice = new RandomAccessFile("Palavra_EnderecoLinkedList.db", "rw");
        arquivoListas = new RandomAccessFile("LinkedList_OBJ.db", "rw");
        carregarIndice();
    }

    /**
     * Salva o índice atual no arquivo, escrevendo cada palavra e a posição
     * correspondente no arquivo de listas.
     *
     */
    public void salvarIndice() throws IOException {
        arquivoIndice.setLength(0);
        arquivoIndice.seek(0);

        for (Map.Entry<String, Long> entrada : indice.entrySet()) {
            arquivoIndice.writeUTF(entrada.getKey());
            arquivoIndice.writeLong(entrada.getValue());
        }
    }

    public void adicionarEntrada(String entradaCompleta, long posicao) throws IOException {
        String[] palavras = extrairPalavras(entradaCompleta);
        for (String palavra : palavras) {
            adicionarPalavra(palavra, posicao);
        }
    }

    /**
     * Adiciona uma palavra e sua lista associada de posições ao índice.
     * A lista é armazenada no arquivo de listas e a posição de início da lista é
     * atualizada no índice.
     *
     * @param palavra a palavra chave do índice.
     * @param lista   a lista de posições que será associada à palavra.
     */
    private void adicionarPalavra(String palavra, long posicaoObj) throws IOException {
        LinkedList<Long> lista = buscarPalavra(palavra);
        if (!lista.contains(posicaoObj)) {
            lista.add(posicaoObj);
            long posicaoLista = arquivoListas.length();
            arquivoListas.seek(posicaoLista);

            arquivoListas.writeInt(lista.size());
            for (Long pos : lista) {
                arquivoListas.writeLong(pos);
            }

            indice.put(palavra, posicaoLista);
        }
    }

    private String[] extrairPalavras(String entrada) {
        return entrada.toLowerCase().split("\\W+"); // Divide a string em palavras usando expressões regulares
    }

    /**
     * Carrega o índice à partir do arquivo, reconstruindo o mapeamento de palavras
     * para posições no arquivo de listas.
     */
    private void carregarIndice() throws IOException {
        arquivoIndice.seek(0);
        indice.clear();

        while (arquivoIndice.getFilePointer() < arquivoIndice.length()) {
            String palavra = arquivoIndice.readUTF();
            long posicaoLista = arquivoIndice.readLong();
            indice.put(palavra, posicaoLista);
        }
    }

    /**
     * Busca a lista de posições associada a uma palavra no índice.
     * Retorna a lista de posições se a palavra existir, ou uma lista vazia caso
     * contrário.
     *
     * @param palavra a palavra chave a ser buscada no índice.
     * @return LinkedList<Long> a lista de posições associadas à palavra.
     */
    public LinkedList<Long> buscarPalavra(String palavra) throws IOException {
        if (indice.containsKey(palavra)) {
            long posicaoLista = indice.get(palavra);
            arquivoListas.seek(posicaoLista);
            int tamanhoDaLista = arquivoListas.readInt();
            LinkedList<Long> lista = new LinkedList<>();

            for (int i = 0; i < tamanhoDaLista; i++) {
                lista.add(arquivoListas.readLong());
            }
            return lista;
        }
        return new LinkedList<>(); // Retorna lista vazia se a palavra não for encontrada
    }

    public LinkedList<Long> buscarPorNome(String nome) throws Exception {
        String palavraChave = limparPalavraChave(nome); // Limpa a palavra chave para pesquisa
        return buscarPalavra(palavraChave); // Aproveita a funcionalidade de busca por palavra existente
    }

    private String limparPalavraChave(String palavra) {
        return Normalizer.normalize(palavra, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "") // Remove acentos
                .toLowerCase(); // Converte para minúsculas
    }
}
