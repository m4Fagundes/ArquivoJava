public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");

        Arquivo<Pessoa> fileTeste = new Arquivo<>(Pessoa.class.getConstructor());
        
        Pessoa pessoa = new Pessoa();

        pessoa.nome = "Matheus";
        pessoa.sobrenome = "Fagundes";

        Pessoa pessoa2 = new Pessoa();

        pessoa2.nome = "Luca";
        pessoa2.sobrenome = "Lourenco";

        Pessoa pessoa3 = new Pessoa();

        pessoa3.nome = "Mozao";
        pessoa3.sobrenome = "Amor";

        Pessoa pessoa4 = new Pessoa();

        pessoa4.nome = "Gabriel";
        pessoa4.sobrenome = "Martins";

        fileTeste.Create(pessoa);
        fileTeste.Create(pessoa2);
        fileTeste.delete(2);
        fileTeste.Create(pessoa3);
        fileTeste.Create(pessoa4);
        

        pessoa3.nome = "Jesus";
        pessoa3.sobrenome = "C";


        fileTeste.Update(pessoa3);

        fileTeste.close(); 
        
    }
}
