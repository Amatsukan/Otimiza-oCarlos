import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
			Map<Mes,Vector<ParcelaNaOtimizacao>> variaveisDoFuncionario = f.geraVariaveis();
//			Vector<ParcelaNaOtimizacao> custoDosFuncionariosNegativo = f.geraVariaveisDosCustos(false);
			Vector<ParcelaNaOtimizacao> custoDosFuncionariosPositivo = f.geraVariaveisDosCustos(true);
			
			int quantidade_de_func = variaveisDoFuncionario.size();
			
			Map<Mes,Vector<ParcelaNaOtimizacao>> mesesEQuantidades = new HashMap<>(), mesesEQuantidadesNegativo = new HashMap<>();
			
			
			for(double k = 1; k<=quantidade_de_func; k++) {
				for(Mes m : Mes.values()) {
					if(!mesesEQuantidades.containsKey(m)) {
						mesesEQuantidades.put(m, new Vector<>());
						mesesEQuantidadesNegativo.put(m, new Vector<>());
					}
					mesesEQuantidades.get(m).addElement(new ParcelaNaOtimizacao(k+"_"+m, k));
					mesesEQuantidadesNegativo.get(m).addElement(new ParcelaNaOtimizacao(k+"_"+m, -1d));
				}
			}
			List<ParcelaNaOtimizacao> funcionarios = pegaParcelas(variaveisDoFuncionario);
			
			otimizador.criarVariaveis(custoDosFuncionariosPositivo);

			otimizador.criarVariaveis(pegaParcelas(mesesEQuantidades));
			
			for(ParcelaNaOtimizacao variavel: funcionarios) {
//				int index = funcionarios.indexOf(variavel);
				
				for(Mes m : Mes.values()) {
					if(variaveisDoFuncionario.containsKey(m)) {
						Vector<ParcelaNaOtimizacao> p = new Vector<>(variaveisDoFuncionario.get(m));
						p.addAll(mesesEQuantidadesNegativo.get(m));
						otimizador.criarRestricao(variavel.pegaNomeVariavel()+"_link_"+m, 0, p, true);
					}
				}
				//custo e funcionario devem ser 1
				
				//no maximo um em cada mes
				otimizador.criarRestricao(variavel.pegaNomeVariavel()+"_no_maximo_uma_vez_cada_mes", 0, 1, variavel);
				
				
				
			}
			
			otimizador.criarRestricao(f.nome+"_em_apenas_um_mes_de_ferias", 1, 1, funcionarios);
			
			Vector<ParcelaNaOtimizacao> parcelasRestricao4 = new Vector<>();
			parcelasRestricao4.addAll(funcionarios);
			parcelasRestricao4.addAll(pegaParcelas(mesesEQuantidadesNegativo));
			
			otimizador.criarRestricao(f.nome+"_numeroIgualdeMesesEFuncionarios", 0, parcelasRestricao4, true);
			otimizador.criarRestricao(f.nome+"_numeroIgualdeMesesEFuncionarios", quantidade_de_func, quantidade_de_func, pegaParcelas(mesesEQuantidades));
		}

		
		otimizador.resolver(true);
		otimizador.logInfo();
	}

	private List<ParcelaNaOtimizacao> pegaParcelas(Map<Mes, Vector<ParcelaNaOtimizacao>> variaveis) {
		List<ParcelaNaOtimizacao> retorno  = new ArrayList<>();
		for(Vector<ParcelaNaOtimizacao> parcelas : variaveis.values()) {
			retorno.addAll(parcelas);
		}
		return retorno;
	}
}
