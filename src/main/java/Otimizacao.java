import java.util.Vector;

import otimizador.OtimizadorOjalgo;
import otimizador.ParcelaNaOtimizacao;

public class Otimizacao {
	Vector<Funcionario> funcionarios;
	double custoMaximo;
	OtimizadorOjalgo otimizador;
	
	public Otimizacao(double custoMaximo, Vector<Funcionario> funcionarios) {
		this.funcionarios = funcionarios;
		this.custoMaximo = custoMaximo;
		this.otimizador = new OtimizadorOjalgo();
	}
	
	public void otimizar() {
		for(Funcionario f : funcionarios) {
			Vector<ParcelaNaOtimizacao> variaveisDoFuncionario = f.geraVariaveis();
			otimizador.criarVariaveis(variaveisDoFuncionario);
			
			for(ParcelaNaOtimizacao variavel: variaveisDoFuncionario) {
				otimizador.criarRestricao(variavel.pegaNomeVariavel()+"_no_maximo_uma_vez_cada_mes", 0, 1, variavel);
			}
			otimizador.criarRestricao(f.nome+"_umEApenasUmMes", 1, 1, variaveisDoFuncionario);
		}
	}
}
