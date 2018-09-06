package otimizador;


//TODO arrumar um nome melhor...
public class ParcelaNaOtimizacao {
	private final String nomeVar;
	private final Double pesoNaEquacao;

	public ParcelaNaOtimizacao( String var, Double pes )
	{
		this.nomeVar = var;
		this.pesoNaEquacao = pes;
	}
	public double pegaPesoNaEquacao()
	{
		return pesoNaEquacao;
	}
	public String pegaNomeVariavel(){
		return nomeVar;
	}


}
