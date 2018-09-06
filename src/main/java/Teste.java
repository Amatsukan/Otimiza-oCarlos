import java.util.Vector;

public class Teste {
	public static void main(String[] args) {
		Vector<Mes> periodo = new Vector<Mes>();
		periodo.add(Mes.jan);
		periodo.add(Mes.fev);
		
		Funcionario x1 = new Funcionario("x1", 2000, periodo);
		Funcionario x2 = new Funcionario("x3", 3000, periodo);
		
		 Vector<Funcionario> funcionarios = new Vector<Funcionario>();
		 funcionarios.add(x1);
		 funcionarios.add(x2);
		 
		 Otimizacao otimizacao = new Otimizacao(10000, funcionarios);
		 otimizacao.otimizar();
	}
}