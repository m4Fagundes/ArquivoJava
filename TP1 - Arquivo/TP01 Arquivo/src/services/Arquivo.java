package services;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import models.*;

/**
 * Classe genérica para manipulação de registros de arquivo que implementam a
 * interface Registro.
 * Utiliza serialização e deserialização de objetos do tipo T em um array de
 * bytes para armazenamento em arquivo.
 * 
 * @param <T> Tipo do registro que estende a interface Registro.
 */
public class Arquivo<T extends Registro> {

  protected RandomAccessFile arquivo; // Objeto para leitura e escrita no arquivo.
  protected RandomAccessFile arquivoIndice;
  protected Constructor<T> construtor; // Construtor do tipo T, usado para criar instâncias de T.
  final protected int TAM_CABECALHO = 4; // Tamanho fixo do cabeçalho do arquivo.
  HashMap<Integer, Long> index = new HashMap<>(); // Tabela hash de indexação para pesquisa

  /**
   * Constrói um arquivo que manipula registros do tipo T.
   * O arquivo é criado ou aberto como leitura e escrita ('rw').
   * Um cabeçalho de arquivo é inicializado se o arquivo estiver vazio.
   * 
   * @param c Construtor da classe T.
   */
  public Arquivo(Constructor<T> c) throws Exception {
    this.construtor = c;
    this.arquivo = new RandomAccessFile("pessoas.db", "rw");
    this.arquivoIndice = new RandomAccessFile("indexFile.db", "rw");
    carregarHashMap();

    if (arquivo.length() < TAM_CABECALHO) {
      arquivo.seek(0);
      arquivo.writeInt(0);
    }
  }

  /**
   * Salva o índice atual em um arquivo usando serialização.
   * Este método cria um arquivo se ele não existir ou sobrescreve o existente.
   * Após a escrita, imprime o tamanho do arquivo salvo para fins de verificação.
   */
  private void salvarHashMap() {
    try {
      arquivoIndice.setLength(0);
      arquivoIndice.seek(0);

      for (Map.Entry<Integer, Long> entry : index.entrySet()) {
        arquivoIndice.writeInt(entry.getKey());
        arquivoIndice.writeLong(entry.getValue());
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
   * @param hashtable A Hashtable a ser salva.
   * @param filename  O nome do arquivo em que a Hashtable será salva.
   */
  public class HashTableToFile {
    public static void saveHashtable(Hashtable<Integer, String> hashtable, String filename) {
      try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
        oos.writeObject(hashtable);
      } catch (Exception e) {
        System.out.println("Erro ao salvar a Hashtable: " + e.getMessage());
        e.printStackTrace();
      }
    }
  }

  public void printHashMap() {
    if (index.isEmpty()) {
      System.out.println("O HashMap está vazio.");
    } else {
      for (Map.Entry<Integer, Long> entry : index.entrySet()) {
        System.out.println("Chave: " + entry.getKey() + ", Valor: " + entry.getValue());
      }
    }
  }

  /**
   * Cria um novo registro no arquivo, utilizando índices para otimizar a busca.
   * Salva o novo ID no objeto e no arquivo, e escreve o objeto serializado,
   * reutilizando espaço de registros deletados se possível.
   * 
   * @param obj Instância de T para ser armazenada no arquivo.
   */
  public void Create(T obj) throws Exception {
    arquivo.seek(0);
    int ultimoID = arquivo.readInt();
    ultimoID++;
    arquivo.seek(0);
    arquivo.writeInt(ultimoID);
    obj.setID(ultimoID);

    byte[] registroOBJ = obj.toByteArray();
    short tamanhoRegistroNovo = (short) registroOBJ.length;
    boolean espaçoEncontrado = false;
    Long offset = arquivo.length();

    arquivo.seek(TAM_CABECALHO);

    while (arquivo.getFilePointer() < arquivo.length()) {
      long posiçãoLapide = arquivo.getFilePointer();
      byte valorLapide = arquivo.readByte();
      short tamanhoRegistroAtual = arquivo.readShort();

      if (valorLapide == '*' && tamanhoRegistroAtual >= tamanhoRegistroNovo) {
        arquivo.seek(posiçãoLapide);
        arquivo.writeByte(' ');
        arquivo.writeShort(tamanhoRegistroNovo);
        arquivo.write(registroOBJ);
        espaçoEncontrado = true;
        offset = posiçãoLapide;
        break;
      } else {
        arquivo.skipBytes(tamanhoRegistroAtual);
      }
    }

    if (!espaçoEncontrado) {
      arquivo.seek(arquivo.length());
      arquivo.writeByte(' ');
      arquivo.writeShort(tamanhoRegistroNovo);
      arquivo.write(registroOBJ);
    }

    index.put(ultimoID, offset);
    salvarHashMap();
  }

  /**
   * Apaga um registro pelo ID, marcando seu espaço como deletado (lapide '*')
   * e remove o ID do índice.
   *
   * @param id ID do registro a ser deletado.
   */
  public void delete(int id) {
    try {
      Long offset = index.get(id);
      if (offset == null) {
        System.out.println("Registro com ID " + id + " não encontrado.");
        return;
      }
      arquivo.seek(offset);
      arquivo.writeByte('*');
      index.remove(id);
      salvarHashMap();
    } catch (IOException e) {
      System.out.println("Erro de I/O ao deletar registro: " + e.getMessage());
    }
  }

  /**
   * Atualiza um registro no arquivo substituindo o antigo por um novo
   * se o espaço for suficiente, ou movendo para o final do arquivo se necessário.
   * Atualiza o índice para refletir a mudança.
   *
   * @param obj Registro a ser atualizado.
   */
  public void Update(T obj) throws Exception {
    Long enderecoOBJ = index.get(obj.getID());
    if (enderecoOBJ == null) {
      System.out.println("Objeto não encontrado para atualização.");
      return;
    }

    arquivo.seek(enderecoOBJ);
    arquivo.readByte();
    short tamanhoAtual = arquivo.readShort();
    byte[] novoRegistro = obj.toByteArray();
    short novoTamanho = (short) novoRegistro.length;

    if (tamanhoAtual >= novoTamanho) {
      arquivo.seek(enderecoOBJ);
      arquivo.writeByte(' ');
      arquivo.writeShort(novoTamanho);
      arquivo.write(novoRegistro);
    } else {
      arquivo.seek(enderecoOBJ);
      arquivo.writeByte('*');
      index.remove(obj.getID());
      arquivo.seek(arquivo.length());
      long novoEndereco = arquivo.getFilePointer();
      arquivo.writeByte(' ');
      arquivo.writeShort(novoTamanho);
      arquivo.write(novoRegistro);
      index.put(obj.getID(), novoEndereco);
      salvarHashMap();
    }
  }

  /**
   * Lê e deserializa um registro pelo ID usando índices.
   * Retorna nulo se o registro não for encontrado ou estiver marcado como
   * deletado.
   * 
   * @param id ID do registro a ser lido.
   * @return Uma instância de T se encontrado, nulo caso contrário.
   */
  public T read(int id) throws Exception {
    Long enderecoLeitura = index.get(id);
    if (enderecoLeitura == null) {
      System.out.println("Objeto não encontrado para leitura.");
      return null;
    }

    arquivo.seek(enderecoLeitura);
    byte lapide = arquivo.readByte();
    if (lapide == ' ') {
      short tamanhoRegistro = arquivo.readShort();
      byte[] registro = new byte[tamanhoRegistro];
      arquivo.readFully(registro);
      T obj = construtor.newInstance();
      obj.fromByteArray(registro);
      return obj;
    } else {
      System.out.println("O registro não está presente no acervo.");
      return null;
    }
  }

  /**
   * Fecha o arquivo e garante que todas as modificações sejam salvas.
   * Deve ser chamado ao finalizar o uso do arquivo.
   */
  public void close() {
    try {
      salvarHashMap();
      if (arquivo != null) {
        arquivo.close();
      }
      if (arquivoIndice != null) {
        arquivoIndice.close();
      }
    } catch (IOException e) {
      System.out.println("Erro ao fechar os arquivos: " + e.getMessage());
    }
  }
}
