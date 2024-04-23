import models.*;
import services.*;


public class App {
    public static void main(String[] args) throws Exception {

        Arquivo<Pessoa> fileTeste = new Arquivo<>(Pessoa.class.getConstructor());
        
        Pessoa pessoa = new Pessoa();

        pessoa.nome = "Matheus";
        pessoa.sobrenome = "Fagundes";

        Pessoa pessoa2 = new Pessoa();

        pessoa2.nome = "Luca";
        pessoa2.sobrenome = "Lourenco";

        Pessoa pessoa3 = new Pessoa();

        pessoa3.nome = "Liz";
        pessoa3.sobrenome = "Usa";

        Pessoa pessoa4 = new Pessoa();

        pessoa4.nome = "Iam";
        pessoa4.sobrenome = "Chevrand";

        fileTeste.Create(pessoa);
        fileTeste.Create(pessoa2);
        fileTeste.delete(2);
        fileTeste.Create(pessoa3);
        fileTeste.Create(pessoa4);
        

        pessoa3.nome = "Luana";
        pessoa3.sobrenome = "Cunha";

        Pessoa pessoaRead = new Pessoa();

        fileTeste.Update(pessoa3);
        pessoaRead = fileTeste.read(1);

        fileTeste.Create(pessoaRead);
        System.out.println("O nome e : " + pessoaRead.nome + pessoaRead.sobrenome);


        fileTeste.close();
        
    }
}
