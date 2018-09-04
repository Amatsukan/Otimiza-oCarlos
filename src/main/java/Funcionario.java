import java.util.Vector;

import otimizador.ParcelaNaOtimizacao;

public class Funcionario {
	Vector<Mes> periodoPossivelParaFerias;
	double custo;
	String nome;
	static int ID = 0;
	
	public Funcionario(String nome, double custo, Vector<Mes> periodoPossivelParaFerias) {
		this.nome = nome+ID++;//evitando problemas com nomes iguais
		this.custo = custo;
		this.periodoPossivelParaFerias = periodoPossivelParaFerias;
	}
	
	public Vector<ParcelaNaOtimizacao> geraVariaveis(){
		Vector<ParcelaNaOtimizacao> retorno = new Vector<>();
		for(Mes mes : periodoPossivelParaFerias) {
			retorno.add(new ParcelaNaOtimizacao(nome+"_"+mes, custo));
		}
		return retorno;
	}
}
