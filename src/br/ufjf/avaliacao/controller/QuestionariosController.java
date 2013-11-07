package br.ufjf.avaliacao.controller;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Window;

import br.ufjf.avaliacao.model.Pergunta;
import br.ufjf.avaliacao.model.Questionario;
import br.ufjf.avaliacao.persistent.impl.PerguntaDAO;
import br.ufjf.avaliacao.persistent.impl.QuestionarioDAO;


public class QuestionariosController extends GenericController{
	
	private QuestionarioDAO questionarioDAO = new QuestionarioDAO();
	private PerguntaDAO perguntaDAO = new PerguntaDAO();
	private List<Questionario> questionarios;
	private List<Questionario> questionariosCoord = questionarioDAO.retornaQuestinariosCursoTipo(usuario.getCurso(),0);
	private List<Questionario> questionariosProf = questionarioDAO.retornaQuestinariosCursoTipo(usuario.getCurso(),1);
	private List<Questionario> questionariosAuto = questionarioDAO.retornaQuestinariosCursoTipo(usuario.getCurso(),2);
	private List<Questionario> questionariosInfra = questionarioDAO.retornaQuestinariosCursoTipo(usuario.getCurso(),3);
	private boolean ativo;
	private List<Pergunta> perguntas = new ArrayList<Pergunta>();
	private Pergunta pergunta = new Pergunta();
	private Questionario questionario = new Questionario();
	
	@Init
	public void init() throws HibernateException, Exception {
		if(testaLogado())
			testaPermissao(0);
	}
	
	@Command
	public void abreJanela(){
		Window window = (Window) Executions.createComponents(
                "/criarQuestionario.zul", null, null);
		window.doModal();
	}

	@Command
	@NotifyChange({"perguntas","pergunta"})
	public void adicionaPergunta(){
		perguntas.add(pergunta);
		pergunta = new Pergunta();
	}
	
	@Command
	@NotifyChange({"perguntas","pergunta"})
	public void excluiPergunta(@BindingParam("pergunta")Pergunta pergunta){
		perguntas.remove(pergunta);
	}
	
	@Command
	@NotifyChange({"questionariosCoord","questionariosProf","questionariosAuto","questionariosInfra","questionario"})
	public void exclui(@BindingParam("questionario")Questionario questionario){
			perguntas = questionario.getPerguntas();
			perguntaDAO	.excluiLista(perguntas);	
			questionarioDAO.exclui(questionario);
			listaQuestionarios(questionario.getTipoQuestionario()).remove(questionario);
			
	}
	
	@Command
	@NotifyChange({"perguntas","questionario"})
	public void cria(){
		questionario.setCurso(usuario.getCurso());
		if (isAtivo()){
			for (Questionario q : listaQuestionarios(questionario.getTipoQuestionario())){
				q.setAtivo(false);
				questionarioDAO.editar(q);
			}
			questionario.setAtivo(true);
		}
		questionarioDAO.salvar(questionario);
		for (Pergunta pergunta : perguntas){
			pergunta.setQuestionario(questionario);
		}
		perguntaDAO.salvarLista(perguntas);
		questionario = new Questionario();
		pergunta = new Pergunta();
		perguntas = new ArrayList<Pergunta>();
	}
	
	@Command
	@NotifyChange({"perguntas","pergunta"})
	public void top(@BindingParam("pergunta")Pergunta pergunta){
		int index = perguntas.indexOf(pergunta);
		Pergunta aux = perguntas.get(0);
		perguntas.set(0, pergunta);
		perguntas.set(index,aux);
	}
	
	@Command
	@NotifyChange("perguntas")
	public void down(@BindingParam("pergunta")Pergunta pergunta){
		int index = perguntas.indexOf(pergunta);
		Pergunta aux = perguntas.get(index+1);
		perguntas.set(index+1, pergunta);
		perguntas.set(index,aux);
	}
	
	@Command
	@NotifyChange({"perguntas","pergunta"})
	public void up(@BindingParam("pergunta")Pergunta pergunta){
		int index = perguntas.indexOf(pergunta);
		Pergunta aux = perguntas.get(index-1);
		perguntas.set(index-1, pergunta);
		perguntas.set(index,aux);
	}
	
	@Command
	@NotifyChange({"perguntas","pergunta"})
	public void bottom(@BindingParam("pergunta")Pergunta pergunta){
		int index = perguntas.indexOf(pergunta);
		Pergunta aux = perguntas.get(perguntas.size()-1);
		perguntas.set(perguntas.size()-1, pergunta);
		perguntas.set(index,aux);
	}
	
	@Command
	@NotifyChange({"questionariosCoord","questionariosProf","questionariosAuto","questionariosInfra","questionario"})
	public void ativa(@BindingParam("questionario")Questionario questionario){
		for (Questionario q : listaQuestionarios(questionario.getTipoQuestionario())){
				if (q.equals(questionario))
					q.setAtivo(true);
				else q.setAtivo(false);
				questionarioDAO.editar(q);
		}
	}
	
	@Command
	public void enviarQuest() {
		
	}
	
	public List<Pergunta> getPerguntas() {
		return perguntas;
	}
	
	public void setPerguntas(List<Pergunta> perguntas) {
		this.perguntas = perguntas;
	}
	
	public Pergunta getPergunta() {
		return pergunta;
	}
	
	public void setPergunta(Pergunta pergunta) {
		this.pergunta = pergunta;
	}
	
	public QuestionarioDAO getQuestionarioDAO() {
		return questionarioDAO;
	}
	
	public void setQuestionarioDAO(QuestionarioDAO questionarioDAO) {
		this.questionarioDAO = questionarioDAO;
	}
	
	public List<Questionario> listaQuestionarios(Integer tipoQuestionario) {
		if (tipoQuestionario == 0)
			return questionariosCoord;
		else if (tipoQuestionario == 1)
			return questionariosProf;
		else if (tipoQuestionario == 2)
			return questionariosAuto;
		else
			return questionariosInfra;
	}
	
	public void setQuestionarios(List<Questionario> questionarios) {
		this.questionarios = questionarios;
	}

	public Questionario getQuestionario() {
		return questionario;
	}

	public void setQuestionario(Questionario questionario) {
		this.questionario = questionario;
	}

	public List<Questionario> getQuestionariosCoord() {
		return questionariosCoord;
	}

	public void setQuestionariosCoord(List<Questionario> questionariosCoord) {
		this.questionariosCoord = questionariosCoord;
	}

	public List<Questionario> getQuestionariosProf() {
		return questionariosProf;
	}

	public void setQuestionariosProf(List<Questionario> questionariosProf) {
		this.questionariosProf = questionariosProf;
	}

	public List<Questionario> getQuestionariosAuto() {
		return questionariosAuto;
	}

	public void setQuestionariosAuto(List<Questionario> questionariosAuto) {
		this.questionariosAuto = questionariosAuto;
	}

	public List<Questionario> getQuestionariosInfra() {
		return questionariosInfra;
	}

	public void setQuestionariosInfra(List<Questionario> questionariosInfra) {
		this.questionariosInfra = questionariosInfra;
	}

	public List<Questionario> getQuestionarios() {
		return questionarios;
	}

	public boolean isAtivo() {
		return ativo;
	}
	
	@Command
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	
}
