package services;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import models.*;

/**
 * Classe genérica Arquivo para manipulação de registros de arquivo.
 * 
 * @param <T> Tipo do registro que estende a interface Registro.
 *            Esta classe assume que T pode ser serializado e deserializado
 *            através de um array de bytes.
 */
public class Arquivo<T extends Registro> {

  protected RandomAccessFile arquivo; // Objeto para leitura e escrita no arquivo.
  protected Constructor<T> construtor; // Construtor do tipo T, usado para criar instâncias de T.
  final protected int TAM_CABECALHO = 4; // Tamanho fixo do cabeçalho do arquivo.
  HashMap<Integer, Long> index = new HashMap<>(); // Tabela hash de indexacao para pesquisa

  /**
   * Constrói um arquivo que manipula registros do tipo T.
   * O arquivo é criado ou aberto como leitura e escrita ('rw').
   * Um cabeçalho de arquivo é inicializado se o arquivo estiver vazio.
   *
   * @param c Construtor da classe T.
   */
  public Arquivo(Constructor<T> c) throws Exception {

    this.construtor = c;
    arquivo = new RandomAccessFile("pessoas.db", "rw");
    if (arquivo.length() < TAM_CABECALHO) {
      arquivo.seek(0);
      arquivo.writeInt(0);
    }
  }

  /**
   * Cria um novo registro no arquivo.
   * Incrementa o último ID usado, salva o novo ID no objeto e no arquivo,
   * e escreve o objeto serializado no arquivo, reutilizando espaço de registros
   * deletados se possível.
   *
   * @param obj Instância de para ser armazenada no arquivo.
   */
  public void Create(T obj) throws Exception {
    arquivo.seek(0);
    int ultimoID = arquivo.readInt();
    ultimoID++;
    arquivo.seek(0);
    arquivo.writeInt(ultimoID);
    obj.setID(ultimoID);

    byte[] regitroOBJ = obj.toByteArray();
    short tamanhoRegistroNovo = (short) regitroOBJ.length;

    boolean spaceFound = false;
    Long offset = null;

    arquivo.seek(TAM_CABECALHO);

    while (arquivo.getFilePointer() < arquivo.length()) {
      long lapidePosition = arquivo.getFilePointer();
      byte lapideValor = arquivo.readByte();
      short tamanhoRegistroAtual = arquivo.readShort();

      if (lapideValor == '*' && tamanhoRegistroAtual >= tamanhoRegistroNovo) {
        arquivo.seek(lapidePosition);
        arquivo.writeByte(' ');
        arquivo.writeShort(tamanhoRegistroNovo);
        arquivo.write(regitroOBJ);
        spaceFound = true;
        offset = lapidePosition;
        break;
      } else {
        arquivo.skipBytes(tamanhoRegistroAtual);
      }
    }

    if (!spaceFound) {
      arquivo.seek(arquivo.length());
      arquivo.writeByte(' ');
      arquivo.writeShort(tamanhoRegistroNovo);
      arquivo.write(regitroOBJ);
      offset = arquivo.getFilePointer() - (1 + 2 + tamanhoRegistroNovo);
    }

    index.put(ultimoID, offset);
  }

  /**
   * Apaga um registro pelo ID marcando seu espaço como deletado (lapide '*').
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
      arquivo.writeByte('*'); // Marca o registro como deletado no arquivo.
      index.remove(id); // Remove a referência do índice.
    } catch (IOException e) {
      System.out.println("Erro de I/O ao deletar registro: " + e.getMessage());
    }
  }

  /**
   * Atualiza um registro no arquivo substituindo o antigo por um novo
   * se o espaço for suficiente, ou escrevendo ao final do arquivo se necessário.
   * *
   * 
   * @param obj Registro a ser atualizado.
   * @throws Exception
   */
  public void Update(T obj) throws Exception {
    try {
      arquivo.seek(TAM_CABECALHO);
      Long enderecoOBJ = index.get(obj.getID());

      if (enderecoOBJ == null) {
        System.out.println("Objeto não encontrado para Update");
        return;
      }

      byte[] registroObj = obj.toByteArray();
      short objTam = (short) registroObj.length;

      arquivo.seek(enderecoOBJ);
      arquivo.readByte();
      short tamRegistro = arquivo.readShort();

      if (tamRegistro >= objTam) {
        arquivo.seek(enderecoOBJ);
        arquivo.writeByte(' ');
        arquivo.writeShort(objTam);
        arquivo.write(registroObj);
      } else {
        arquivo.seek(enderecoOBJ);
        arquivo.writeByte('*');
        index.remove(obj.getID());
        arquivo.seek(arquivo.length());
        Long novoEndereco = arquivo.getFilePointer();
        arquivo.writeByte(' ');
        arquivo.writeShort(objTam);
        arquivo.write(registroObj);
        index.put(obj.getID(), novoEndereco);
      }

    } catch (IOException e) {
      System.out.println("Ocorreu uma exceção no Update: " + e.getMessage());
    }
  }

  /**
   * Lê e deserializa um registro pelo ID.
   * Retorna null se o registro não for encontrado ou se estiver marcado como
   * deletado.
   * 
   * @param id ID do registro a ser lido.
   * @return Uma instância de T se encontrado, null caso contrário.
   */
  public T read(int id) throws Exception {
    arquivo.seek(TAM_CABECALHO);
    T obj = null;
    try {
      obj = construtor.newInstance();
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw new Exception("Erro ao instanciar objeto: " + e.getMessage(), e);
    }

    Long enderecoLeitura = index.get(id);
    if (enderecoLeitura == null) {
      System.out.println("Objeto não encontrado para a Leitura");
      return null;
    } else {
      arquivo.seek(enderecoLeitura);
      byte lapide = arquivo.readByte();
      if (lapide == ' ') {
        short tamanhoRegistro = arquivo.readShort();
        byte[] registro = new byte[tamanhoRegistro];
        arquivo.read(registro, 0, tamanhoRegistro);
        obj.fromByteArray(registro);
        return obj;
      } else {
        System.out.println("O registro não está presente no acervo");
        return null;
      }
    }
  }

  /**
   * Fecha o arquivo de registros.
   * Deve ser chamado para garantir que todas as alterações sejam salvas.
   */
  public void close() {
    try {
      if (arquivo != null) {
        arquivo.close();
      }
    } catch (IOException e) {
      System.out.println("Erro ao fechar o arquivo: " + e.getMessage());
    }
  }

}
