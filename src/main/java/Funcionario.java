import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import otimizador.ParcelaNaOtimizacao;

public class Funcionario {
	Vector<Mes> periodoPossivelParaFerias;
	double custo;
	String nome;
	static int ID = 0;
	
	public Funcionario(String nome, double custo, Vector<Mes> periodoPossivelParaFerias) {
		this.nome = nome;//evitando problemas com nomes iguais
		this.custo = custo;
		this.periodoPossivelParaFerias = periodoPossivelParaFerias;
	}
	
	public Map<Mes,Vector<ParcelaNaOtimizacao>> geraVariaveis(){
		Map<Mes,Vector<ParcelaNaOtimizacao>> retorno = new HashMap<>();
		for(Mes mes : periodoPossivelParaFerias) {
			if(!retorno.containsKey(mes)) {
				retorno.put(mes, new Vector<>());
			}
			retorno.get(mes).add(new ParcelaNaOtimizacao(nome+"_"+mes, 1d));
		}
		return retorno;
	}

	public Vector<ParcelaNaOtimizacao> geraVariaveisDosCustos(boolean positivo) {
		Vector<ParcelaNaOtimizacao> retorno = new Vector<>();
		int umOuZero = positivo ? 0 : 1;
		for(Mes mes : periodoPossivelParaFerias) {
			retorno.add(new ParcelaNaOtimizacao(nome+"_"+mes, (-umOuZero)*custo));
		}
		return retorno;
	}
}
