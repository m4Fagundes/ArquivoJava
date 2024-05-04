package services;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class hashMap {

    protected RandomAccessFile arquivoIndice;
    HashMap<Integer, Long> index = new HashMap<>(); // Tabela hash de indexação para pesquisa


    public hashMap() throws Exception {
        this.arquivoIndice = new RandomAccessFile("indexFile.db", "rw");
        carregarHashMap();
    }

    /**
     * Salva o índice atual em um arquivo usando serialização.
     * Este método cria um arquivo se ele não existir ou sobrescreve o existente.
     * Após a escrita, imprime o tamanho do arquivo salvo para fins de verificação.
     */
    void salvarHashMap() {
        try {
            arquivoIndice.setLength(0);
            arquivoIndice.seek(0);

            for (Map.Entry<Integer, Long> entrada : index.entrySet()) {
                arquivoIndice.writeInt(entrada.getKey());
                arquivoIndice.writeLong(entrada.getValue());
            }
        } catch (IOException e) {
            System.out.println("Erro ao salvar HashMap: " + e.getMessage());
        }
    }

    /**
     * Carrega o índice do arquivo, se existente.
     * Se o arquivo de índice não existir, inicia um novo HashMap.
     * Em caso de falha na leitura ou desserialização, inicializa um novo HashMap
     * e imprime o erro ocorrido.
     */
    private void carregarHashMap() {
        try {
            arquivoIndice.seek(0);
            index.clear();

            while (arquivoIndice.getFilePointer() < arquivoIndice.length()) {
                int key = arquivoIndice.readInt();
                long value = arquivoIndice.readLong();
                index.put(key, value);
            }
        } catch (IOException e) {
            System.out.println("Erro ao carregar HashMap: " + e.getMessage());
        }
    }

    /**
     * Classe auxiliar para salvar qualquer Hashtable em um arquivo.
     * Utiliza serialização para salvar os dados de Hashtable em um arquivo
     * especificado.
     * 
     * @param hashtable   A Hashtable a ser salva.
     * @param nomeArquivo O nome do arquivo em que a Hashtable será salva.
     */
    public class HashTableToFile {
        public static void saveHashtable(Hashtable<Integer, String> hashtable, String nomeArquivo) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(nomeArquivo))) {
                oos.writeObject(hashtable);
            } catch (Exception e) {
                System.out.println("Erro ao salvar a Hashtable: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Imprime todos os pares chave-valor do HashMap no console.
     * Utilizado para verificações de integridade e depuração.
     */
    public void printHashMap() {
        if (index.isEmpty()) {
            System.out.println("O HashMap está vazio.");
        } else {
            for (Map.Entry<Integer, Long> entry : index.entrySet()) {
                System.out.println("Chave: " + entry.getKey() + ", Valor: " + entry.getValue());
            }
        }
    }

}
