package br.ufjf.avaliacao.controller;

import org.zkoss.zul.PieModel;

import br.ufjf.avaliacao.model.Pergunta;
import br.ufjf.avaliacao.model.Turma;

public class GraficoController extends GenericController{
	
	private Turma turma = new Turma();
	private Pergunta perguntaSelecionada;
	private PieModel model;
	
	public Pergunta getPerguntaSelecionada() {
		return (Pergunta) session.getAttribute("pergunta");
	}
	public void setPerguntaSelecionada(Pergunta perguntaSelecionada) {
		this.perguntaSelecionada = perguntaSelecionada;
	}
	public PieModel getModel() {
		return (PieModel) session.getAttribute("model");
	}
	public void setModel(PieModel model) {
		this.model = model;
	}
	public Turma getTurma() {
		return (Turma) session.getAttribute("turma");
	}
	public void setTurma(Turma turma) {
		this.turma = turma;
	}
	
	
}
