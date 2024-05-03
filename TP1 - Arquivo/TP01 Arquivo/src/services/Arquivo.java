package services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Hashtable;

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
    carregarHashMap();
    
    if (arquivo.length() < TAM_CABECALHO) {
        arquivo.seek(0);
        arquivo.writeInt(0);
    }
}

private void salvarHashMap() {
    File arquivoIndice = new File("indexFile.db");
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(arquivoIndice))) {
        oos.writeObject(index);
        oos.flush();  // Força o buffer a escrever no arquivo
        System.out.println("HashMap salvo com sucesso. Tamanho do arquivo: " + arquivoIndice.length() + " bytes.");
    } catch (IOException e) {
        System.out.println("Erro ao salvar HashMap: " + e.getMessage());
    }
}

@SuppressWarnings("unchecked")
private void carregarHashMap() {
    File arquivoIndice = new File("indexFile.db");
    if (arquivoIndice.exists()) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arquivoIndice))) {
            index = (HashMap<Integer, Long>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Erro ao carregar HashMap: " + e.getMessage());
            index = new HashMap<>();
        }
    } else {
        System.out.println("Arquivo de índice não encontrado. Criando um novo HashMap.");
        index = new HashMap<>();
    }
}


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
    byte lapide = arquivo.readByte();
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
      if (arquivo != null) {
        arquivo.close();
      }
      salvarHashMap();
    } catch (IOException e) {
      System.out.println("Erro ao fechar o arquivo: " + e.getMessage());
    }
  }
}
