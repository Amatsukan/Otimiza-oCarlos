package otimizador;

//import org.ojalgo.netio.BasicLogger;
import org.ojalgo.optimisation.Expression;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Optimisation.Result;

import org.ojalgo.optimisation.Variable;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OtimizadorOjalgo {
	public static int maiorNumeroDeVariaveisGeradasEmUmaOtimizacao = 0;
	
	private List<Variable> variaveis;
    final ExpressionsBasedModel modeloBase = new ExpressionsBasedModel();//modelo para trabalhar com a função objetivo
    private String state;
    private Result ultimoResultado;
    
    public OtimizadorOjalgo() {
    	variaveis = new ArrayList<>();
    	state = "UNEXPLORED";
    	ultimoResultado = null;
    }
    
	//cria as variaveis e adiciona no modelo
	public void criarVariaveis(List<ParcelaNaOtimizacao> listaDeVariaveis) {
		
		for(int i = 0; i < listaDeVariaveis.size() ; i++) {
			String nomeDaVariavel  = listaDeVariaveis.get( i ).pegaNomeVariavel();
			
			if(this.existeVariavel(nomeDaVariavel)) continue;
			
			Double pesoDaVariavel = listaDeVariaveis.get( i ).pegaPesoNaEquacao();
			Variable variavel = new Variable( nomeDaVariavel ).lower(0).weight( pesoDaVariavel ) ;//pesos das variaveis na função objetivo
			
			variaveis.add( variavel );
			modeloBase.addVariable(variavel);
		}
		

    	state = "UNEXPLORED";
	}
	//cria as variaveis e adiciona no modelo
	public void criarVariaveisComValorMinimo(List<ParcelaNaOtimizacao> listaDeVariaveis, int valorMinimo) {
		
		for(int i = 0; i <	 listaDeVariaveis.size() ; i++) {
			String nomeDaVariavel  = listaDeVariaveis.get( i ).pegaNomeVariavel();

			if(this.existeVariavel(nomeDaVariavel)) continue;
			
			
			Double pesoDaVariavel = listaDeVariaveis.get( i ).pegaPesoNaEquacao();
			Variable variavel = new Variable( nomeDaVariavel ).lower(valorMinimo).weight( pesoDaVariavel ) ;//pesos das variaveis na função objetivo
			
			variaveis.add( variavel );
			modeloBase.addVariable(variavel);
		}
		

    	state = "UNEXPLORED";
	}
	
	public int numeroDeVariaveis() {
		return variaveis.size();
	}
	
	//cria restrição usando limite inferior OU superior
	//ex:
	// limiteInferior <= restrição
	//ou
	// restrição <= limiteSuperior
	public void criarRestricao(String nomeDaRestricao, double limite, List<ParcelaNaOtimizacao> listaDeVariaveis, boolean menorQueOLimite) 
	{		
		
		final Expression restricao = modeloBase.addExpression(nomeDaRestricao);  
		
		if( menorQueOLimite ) {
			restricao.upper(limite);
		}
		
		if( !menorQueOLimite ) {
			restricao.lower(limite);
		}
		
		for(int i = 0; i < listaDeVariaveis.size(); i++){
			Variable realVar = pegaVariavelReal(listaDeVariaveis.get(i));
			restricao.set( realVar, listaDeVariaveis.get(i).pegaPesoNaEquacao() );
	    }

    	state = "UNEXPLORED";
	}
	
	private Variable pegaVariavelReal(ParcelaNaOtimizacao parcela)
	{
		Variable retorno = null;
		for(int i = 0; i<variaveis.size();i++) {
			if(variaveis.get(i).getName().equals( parcela.pegaNomeVariavel() )) {
				retorno = variaveis.get(i);
			}
		}
		
		if(retorno == null) throw new InvalidParameterException("Variavel inexistente");
		
		return retorno;
	}
	//cria restrição usando limite inferior E superior
	//ex:
	//	limiteInferior <= restrição <= limiteSuperior 
	public void criarRestricao(String nomeDaRestricao, double limiteInferior, double limiteSuperior, List<ParcelaNaOtimizacao> listaDeVariaveis) 
	{		
		
		final Expression restricao = modeloBase.addExpression(nomeDaRestricao);  
		
		restricao.upper(limiteSuperior);
		
		restricao.lower(limiteInferior);
		
		
		for(int i = 0; i < listaDeVariaveis.size(); i++){
			Variable realVar = pegaVariavelReal(listaDeVariaveis.get(i));
			restricao.set( realVar, listaDeVariaveis.get(i).pegaPesoNaEquacao() );
	    }

    	state = "UNEXPLORED";
	}
	
	//cria restrição usando limite inferior E superior
	//ex:
	//	limiteInferior <= restrição <= limiteSuperior 
	public void criarRestricao(String nomeDaRestricao, double limiteInferior, double limiteSuperior, ParcelaNaOtimizacao variavel) 
	{		
		
		final Expression restricao = modeloBase.addExpression(nomeDaRestricao);  
		
		restricao.upper(limiteSuperior);
		
		restricao.lower(limiteInferior);
		
		Variable realVar = pegaVariavelReal(variavel);
		restricao.set( realVar, variavel.pegaPesoNaEquacao());

    	state = "UNEXPLORED";
	}
	
	public String state() {
		return state;
	}
	
	
	private Optimisation.Result minimize(boolean resultadoDouble){
		ultimoResultado = modeloBase.minimise();
//		System.out.println(ultimoResultado);
//		System.out.println(modeloBase);
		if(!resultadoDouble) {
			for(Variable v : modeloBase.getVariables()){
				v.integer(true);
		    }
			
			ultimoResultado = modeloBase.minimise(); 
		}
		
		state = ultimoResultado.getState().toString();
		
	    return ultimoResultado;
	}
	
	private Optimisation.Result maximize(boolean resultadoDouble){
		ultimoResultado = modeloBase.maximise();
//		System.out.println(ultimoResultado);
//		System.out.println(modeloBase);
		if(!resultadoDouble) {
			for(Variable v : modeloBase.getVariables()){
				v.integer(true);
		    }
			
			ultimoResultado = modeloBase.maximise(); 
		}
		
		
		state = ultimoResultado.getState().toString();
		
	    return ultimoResultado;
	}
	
	public Double resultadoNumericoDaFuncaoObjetivo(boolean minimizar) {
		return ( minimizar ? minimize(false) : maximize(false) ).getValue();
	}
	
	public Map<String, Integer> resolver(boolean minimizar){
		//se minimizar = false, o algoritmio atua como maximizador, senão ele atua como minimizador
		Optimisation.Result result =  ( minimizar ? minimize(false) : maximize(false) );
	    Map<String, Integer> retorno = new HashMap<String,Integer>();
	    
	    for(int j= 0; j<result.size(); j++){
	    	  Variable variable = modeloBase.getVariable(j);
	    	  String chave = variable.getName();
	    	  BigDecimal resultado = result.get(j);
	    	  int resultadoDouble = (int) Math.round(resultado.doubleValue());
	    	  if(resultadoDouble == 0) continue;
	    	  retorno.put(chave,resultadoDouble);
	    }
	    
	    int i = 0;//gambi temporaria
	    for(String name : retorno.keySet()) {
	    	System.out.print(" ("+name+" * "+retorno.get(name)+") ");
	    	
	    	if( i++ <  retorno.size()-1 ) {
	    		System.out.print("+");
	    	}
	    	//TODO logs
	    }
	    
	    if(numeroDeVariaveis() > maiorNumeroDeVariaveisGeradasEmUmaOtimizacao) {
	    	maiorNumeroDeVariaveisGeradasEmUmaOtimizacao = numeroDeVariaveis();
	    }
	    
	    System.out.println("Numero de variaveis utilizadas nessa otimização: "+numeroDeVariaveis());

	    return retorno;
	}
	
	public Map<String, Double> resolverDouble(boolean minimizar){
		//se minimizar = false, o algoritmio atua como maximizador, senão ele atua como minimizador
		Optimisation.Result result =  ( minimizar ? minimize(true) : maximize(true) );
	    Map<String, Double> retorno = new HashMap<String,Double>();
	    
	    for(int j= 0; j<result.size(); j++){
	    	  Variable variable = modeloBase.getVariable(j);
	    	  String chave = variable.getName();
	    	  BigDecimal resultado = result.get(j);
	    	  double resultadoDouble = resultado.doubleValue();
	    	  if(resultadoDouble == 0) continue;
	    	  retorno.put(chave,resultadoDouble);
	    }
	    
	    int i = 0;//gambi temporaria
	    for(String name : retorno.keySet()) {
	    	System.out.print(" ("+name+" * "+retorno.get(name)+") ");
	    	
	    	if( i++ <  retorno.size()-1 ) {
	    		System.out.print("+");
	    	}
	    	//TODO logs
	    }
	    
	    if(numeroDeVariaveis() > maiorNumeroDeVariaveisGeradasEmUmaOtimizacao) {
	    	maiorNumeroDeVariaveisGeradasEmUmaOtimizacao = numeroDeVariaveis();
	    }
	    
	    System.out.println("Numero de variaveis utilizadas nessa otimização: "+numeroDeVariaveis());

	    return retorno;
	}

	public boolean existeVariavel(String variavel) {
		for(Variable var : variaveis) {
			if(var.getName().equals(variavel)) return true;
		}
		return false;
	}
	
	public void logInfo() {
		
		if(ultimoResultado == null) {
			System.out.println("Otimização ainda não gerou resultado");
			return;
		}
		System.out.println("Expressions:");
		System.out.println(modeloBase.getExpressions());
		System.out.println("Vars:");
		System.out.println(modeloBase.getVariables());

		System.out.println("Resultado:");
		System.out.println(ultimoResultado);

		System.out.println("Modelo:");
		System.out.println(modeloBase);
	}

	public void criarVariavel(Variable var, double peso) {
		
			String nomeDaVariavel  = var.getName();
			
			if(this.existeVariavel(nomeDaVariavel)) return;
			
			Variable variavel = new Variable( nomeDaVariavel ).lower(0).upper(Integer.MAX_VALUE).weight( peso ) ;//pesos das variaveis na função objetivo
			
			variaveis.add( variavel );
			modeloBase.addVariable(variavel);
		

    	state = "UNEXPLORED";
		
	}
	
}
