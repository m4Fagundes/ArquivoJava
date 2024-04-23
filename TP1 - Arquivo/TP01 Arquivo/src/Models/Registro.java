/**
 * Interface Registro define as operações necessárias para objetos que podem ser
 * armazenados e recuperados em forma de registros numa estrutura de arquivo.
 * Ela estende Cloneable e é comparável com objetos do tipo Object.
 */
public interface Registro extends Comparable<Object>, Cloneable {
    
    /**
     * Obtém o ID do registro.
     * 
     * @return O ID identificativo do registro.
     */
    public int getID();

    /**
     * Define o ID do registro.
     * 
     * @param id O ID a ser atribuído ao registro.
     */
    public void setID(int id);

    /**
     * Serializa o objeto para um array de bytes para armazenamento.
     * 
     * @return O array de bytes que representa o registro serializado.
     * @throws Exception Se houver um erro durante a serialização.
     */
    public byte[] toByteArray() throws Exception;

    /**
     * Deserializa um array de bytes para obter um objeto Registro.
     * Este método repopula os atributos do objeto usando dados de um array de bytes.
     * 
     * @param ba O array de bytes contendo os dados do registro.
     * @throws Exception Se houver um erro durante a deserialização.
     */
    public void fromByteArray(byte[] ba) throws Exception;

    /**
     * Compara este registro com outro objeto. A implementação desta
     * função deve lançar ClassCastException se o objeto especificado não
     * puder ser comparado com este registro.
     * 
     * @return Um valor inteiro negativo, zero ou positivo quando este objeto
     *         é menor que, igual a, ou maior que o objeto especificado, respectivamente.
     * @throws ClassCastException Se o objeto especificado não puder ser comparado.
     */
    public int compareTo();

}
