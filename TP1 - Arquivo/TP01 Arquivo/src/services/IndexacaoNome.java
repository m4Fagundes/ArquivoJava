package services;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class IndexacaoNome {

    protected RandomAccessFile arquivoIndiretoNome;
    HashMap<String, Integer> hash = new HashMap<>(); // Tabela hash para indexação e pesquisa rápida.

    public IndexacaoNome() throws Exception {
        this.arquivoIndiretoNome = new RandomAccessFile("Nome_ID.db", "rw");
        carregarHashMap(); // Carregar o hashmap do arquivo no início.
    }

    /**
     * Salva o índice atual no arquivo por meio de escrita direta.
     * Limpa o arquivo existente e escreve os dados do HashMap.
     * Cada par chave-valor é gravado sequencialmente.
     * @throws IOException 
     */
    void salvarHashMapFinal(String nome, int ID) throws Exception {
        arquivoIndiretoNome.seek(arquivoIndiretoNome.length());
        arquivoIndiretoNome.writeUTF(nome);
        arquivoIndiretoNome.writeInt(ID);
    }
    void salvarHashMap(){
        try {
            arquivoIndiretoNome.setLength(0); // Limpa o arquivo para evitar dados obsoletos.
            arquivoIndiretoNome.seek(0); // Inicia no começo do arquivo.

            for (Map.Entry<String, Integer> entrada : hash.entrySet()) {
                arquivoIndiretoNome.writeUTF(entrada.getKey());
                arquivoIndiretoNome.writeInt(entrada.getValue());
            }
        } catch (IOException e) {
            System.out.println("Erro ao salvar HashMap: " + e.getMessage());
        }
    }



    /**
     * Carrega o índice do arquivo ao inicializar o objeto.
     * Lê pares chave-valor do arquivo e os insere no HashMap.
     * Se houver falha na leitura, reinicia o HashMap para garantir consistência.
     */
    private void carregarHashMap() {
        try {
            arquivoIndiretoNome.seek(0);
            hash.clear();

            while (arquivoIndiretoNome.getFilePointer() < arquivoIndiretoNome.length()) {
                String key = arquivoIndiretoNome.readUTF();
                int value = arquivoIndiretoNome.readInt();
                hash.put(key, value);
            }
        } catch (IOException e) {
            System.out.println("Erro ao carregar HashMap: " + e.getMessage());
        }
    }
    /**
     * Salva uma Hashtable em um arquivo usando serialização.
     * Isso é útil para persistência de dados estruturados.
     *
     * @param hashtable   A Hashtable a ser salva.
     * @param nomeArquivo O nome do arquivo-alvo para salvar a Hashtable.
     */
    public static class HashTableToFile {
        public static void saveHashtable(Hashtable<String, String> hashtable, String nomeArquivo) {
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
        if (hash.isEmpty()) {
            System.out.println("O HashMap está vazio.");
        } else {
            for (Map.Entry<String, Integer> entry : hash.entrySet()) {
                System.out.println("Chave: " + entry.getKey() + ", Valor: " + entry.getValue());
            }
        }
    }

}
