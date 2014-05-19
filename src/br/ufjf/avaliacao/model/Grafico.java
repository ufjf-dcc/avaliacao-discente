package br.ufjf.avaliacao.model;

public class Grafico {

	String caminho;
	String nome;
	String parametros;
	String tipo;

	public Grafico(){}
	
	public Grafico(String nome,String tipo, String caminho){
		this.nome = nome;
		this.tipo = tipo;
		this.caminho = caminho;
	}
	
	public String getCaminho() {
		return caminho;
	}
	public void setCaminho(String caminho) {
		this.caminho = caminho;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getParametros() {
		return parametros;
	}
	public void setParametros(String parametros) {
		this.parametros = parametros;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getURL(){
		return (this.caminho+this.parametros);
	}
	
	
}
