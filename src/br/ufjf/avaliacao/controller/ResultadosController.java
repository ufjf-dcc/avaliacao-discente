package br.ufjf.avaliacao.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.PieModel;
import org.zkoss.zul.Row;
import org.zkoss.zul.SimplePieModel;
import org.zkoss.zul.Window;

import br.ufjf.avaliacao.model.Avaliacao;
import br.ufjf.avaliacao.model.Disciplina;
import br.ufjf.avaliacao.model.Pergunta;
import br.ufjf.avaliacao.model.PrazoQuestionario;
import br.ufjf.avaliacao.model.Questionario;
import br.ufjf.avaliacao.model.Resposta;
import br.ufjf.avaliacao.model.RespostaEspecifica;
import br.ufjf.avaliacao.model.Turma;
import br.ufjf.avaliacao.model.Usuario;
import br.ufjf.avaliacao.persistent.impl.AvaliacaoDAO;
import br.ufjf.avaliacao.persistent.impl.PerguntaDAO;
import br.ufjf.avaliacao.persistent.impl.QuestionarioDAO;
import br.ufjf.avaliacao.persistent.impl.RespostaDAO;
import br.ufjf.avaliacao.persistent.impl.TurmaDAO;
import br.ufjf.avaliacao.persistent.impl.UsuarioDAO;

public class ResultadosController extends GenericController implements
		Serializable {

	private static final long serialVersionUID = 6731467107690993996L;

	private String opcao = new String();
	private RespostaDAO respostaDAO = new RespostaDAO();
	private List<String> semestres = respostaDAO.getAllSemestres();
	private String semestre = null;
	private List<Turma> turmas = new ArrayList<Turma>();
	private Turma turma = new Turma();
	private List<Avaliacao> avaliacoes = new ArrayList<Avaliacao>();
	private PrazoQuestionario prazo = new PrazoQuestionario();
	private List<Pergunta> perguntas = new ArrayList<>();
	private Pergunta perguntaSelecionada;
	private List<Usuario> professores = new ArrayList<Usuario>();
	private Usuario professor = null;
	private List<Usuario> coordenadores = new ArrayList<Usuario>();
	private Usuario coordenador = null;
	private List<Questionario> questionarios = new ArrayList<Questionario>();
	private Questionario questionario = null;
	
	
	@Command
	@NotifyChange("turmas") // carregando e filtrando as tumas a serem escolhidas
	public void carregarTurmas() {
		turmas = new ArrayList<Turma>();
		if(professor!=null && semestre!=null){
			TurmaDAO turmaDAO = new TurmaDAO();
			turmas = new ArrayList<Turma>();
			Turma todas = new Turma();
			Disciplina ndisc = new Disciplina();
			ndisc.setNomeDisciplina("Todas");
			ndisc.setCodDisciplina("Sem codigo");
			todas.setDisciplina(ndisc);
			todas.setLetraTurma("Sem letra");
			todas.setSemestre("Avaliação geral");
			turmas.add(todas);
			if(professor.getNome()!="Todos" && semestre!="Todos") // professor e semestre selecionado
				turmas.addAll(turmaDAO.getTurmasUsuarioSemestre(professor, semestre));
			
			if(professor.getNome()!="Todos" && semestre=="Todos") // professor selecionado e semestre=todos
				turmas.addAll(turmaDAO.getTurmasUsuario(professor));
			
			if(professor.getNome()=="Todos" && semestre!="Todos") // professor=todos e semestre selecionado
				turmas.addAll(turmaDAO.getTurmasCursoSemestre(semestre,usuario.getCurso()));
			
			if(professor.getNome()=="Todos" && semestre=="Todos") // professor=todos e semestre selecionado
				turmas.addAll(turmaDAO.getAllTurmasCurso(semestre,usuario.getCurso()));
		}
		questionarios = new ArrayList<Questionario>();
		questionario = null;
		perguntas = new ArrayList<Pergunta>();
	}
	
	@Command
	@NotifyChange("professores")
	public void carregarProfessores() {
	}
	
	@Command
	@NotifyChange("semestres")  // carregando e filtrando os semstres a serem escolhidos
	public void carregarSemestres() {
		semestres = new ArrayList<String>();
		semestres.add("Todos");
		TurmaDAO turmaDAO = new TurmaDAO();
		if(professor != null){
			if(professor.getNome() != "Todos")
				semestres.addAll(turmaDAO.getSemestresUsuario(professor));
			
			else
				semestres.addAll(turmaDAO.getAllSemestres());
		}
		turmas = new ArrayList<Turma>();
		turma = null;
		questionarios = new ArrayList<Questionario>();
		questionario = null;
		perguntas = new ArrayList<Pergunta>();
	}
	
	@Command
	@NotifyChange("questionarios")  // carregando e filtrando os questionarios a serem escolhidos
	public void carregarQuestionarios() {
		questionarios = new ArrayList<Questionario>();
		Questionario todos = new Questionario();
		todos.setTituloQuestionario("Todos");
		questionarios.add(todos);
		QuestionarioDAO questionarioDAO = new QuestionarioDAO();
		if(Integer.parseInt(opcao)==1){
				questionarios.addAll(questionarioDAO.retornaQuestionarioProfessor(usuario.getCurso()));// retorna todos os questionarios de professor
		}	
		perguntas = new ArrayList<Pergunta>();
	}

	@Command
	@NotifyChange("perguntas")  // carregando e filtrando os semstres a serem escolhidos
	public void carregarPerguntas() {
		perguntas = new ArrayList<Pergunta>();
		if(professor != null && questionario!=null && semestre!=null && turma!=null)
			if(questionario.getTituloQuestionario()!="Todos")
				perguntas.addAll(questionario.getPerguntas());
			else{
				QuestionarioDAO questionarioDAO = new QuestionarioDAO();
				questionarios = questionarioDAO.retornaQuestionarioProfessor(usuario.getCurso());
				for(int i=0;i<questionarios.size();i++)
					perguntas.addAll(questionarios.get(i).getPerguntas());
			}
			System.out.println("teste");
	}
		
	@Command
	public void gerarGrafico() {
		getGraficoProfessor();
		session.setAttribute("turma", turma);
		session.setAttribute("pergunta", perguntaSelecionada);
		Window w = (Window) Executions.createComponents("/grafico.zul", null,
				null);
		w.setClosable(true);
		w.setMinimizable(true);
		w.doOverlapped();
	}

	private List<Turma> getLetraDisciplinaTurma() {
		List<Turma> turmas = new ArrayList<Turma>();
		for (Turma t : new TurmaDAO().getAllTurmas(semestre)) {
			turmas.add(t);
		}
		return turmas;
	}

	@Command
	@NotifyChange("perguntas")
	public void verificaTurma() {
		perguntas = new ArrayList<Pergunta>();

		if(professor!=null && semestre!=null && turma!=null){
			PerguntaDAO perguntaDAO = new PerguntaDAO();
			Pergunta teste = new Pergunta();
			teste.setTituloPergunta("Teste");
			if(professor.getNome()!="Todos" && semestre!= "Todos" && turma.getDisciplina().getNomeDisciplina()!="Todos"){
				AvaliacaoDAO avaliacaoDAO = new AvaliacaoDAO();
				List<Avaliacao> avaliacoes = avaliacaoDAO.retornaAvaliacoesUsuarioTurmaSemestre(professor, turma, semestre);


			}
		}
	}

	public void getGraficoProfessor() {
		List<Resposta> respostas;
		System.out.println("Disciplina:");

		System.out.println(turma.getDisciplina().getNomeDisciplina());
		RespostaDAO respostaDAO = new RespostaDAO();
		if(professor.getNome()!="Todos"){
			if(semestre!="Todos"){
				if(turma.getLetraTurma()!="Sem letra"){
					respostas = respostaDAO.getRespostasPerguntaTurmaSemestreAvaliado(perguntaSelecionada, semestre, turma, professor);
				}
				else{
					respostas = respostaDAO.getRespostasPerguntaSemestreAvaliado(perguntaSelecionada, semestre, professor);
					System.out.println("Aqui1");
				}
			}
			else{
				if(turma.getLetraTurma()!="Sem letra"){
					respostas = respostaDAO.getRespostasPerguntaTurmaAvaliado(perguntaSelecionada, turma, professor);
				}
				else{
					respostas = respostaDAO.getRespostasPerguntaAvaliado(perguntaSelecionada, professor);
					System.out.println("Aqui2");
				}
			}
		}
		else{
			if(semestre!="Todos"){
				if(turma.getLetraTurma()!="Sem letra"){
					respostas = respostaDAO.getRespostasPerguntaTurmaSemestre(perguntaSelecionada, semestre, turma);
				}
				else{
					respostas = respostaDAO.getRespostasPerguntaSemestre(perguntaSelecionada, semestre);
					System.out.println("Aqui3");
				}
			}
			else{
				if(turma.getLetraTurma()!="Sem letra"){
					respostas = respostaDAO.getRespostasPerguntaTurma(perguntaSelecionada, turma);
				}
				else{
					System.out.println("Aqui4");
					respostas = respostaDAO.getRespostasPergunta(perguntaSelecionada);
				}
			}
		}
		System.out.println("respostas.size() : ");
		System.out.println(respostas.size());

		List<RespostaEspecifica> alternativas = perguntaSelecionada
				.getRespostasEspecificasBanco();
		Map<String, Integer> contagem = new LinkedHashMap<>();
		PieModel model = new SimplePieModel();
		for (RespostaEspecifica re : alternativas) {
			contagem.put(re.getRespostaEspecifica(), 0);
		}
		for (Resposta r : respostas) {
			for (RespostaEspecifica re : alternativas) {
				if (r.getResposta().equals(re.getRespostaEspecifica())) {
					contagem.put(re.getRespostaEspecifica(),
							(contagem.get(re.getRespostaEspecifica()) + 1));
				}
			}
		}
		Iterator<String> keyIterator = contagem.keySet().iterator();
		while (keyIterator.hasNext()) {
			String key = keyIterator.next();
			model.setValue(key, contagem.get(key));
		}
		session.setAttribute("model", model);
	}

	@Command
	public void avaliacaoEscolhida(@BindingParam("row") Row row,
			@BindingParam("combo") Combobox combo) {

		opcao = combo.getSelectedItem().getValue().toString();
		switch (opcao) {
		case "0":
			row.getNextSibling().setVisible(false);
			row.getNextSibling().getNextSibling().setVisible(true);
			row.getNextSibling().getNextSibling().getNextSibling().setVisible(true);
			row.getNextSibling().getNextSibling().getNextSibling().getNextSibling().setVisible(false);
			row.getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().setVisible(true);
			row.getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().setVisible(true);
			break;
		case "1":
			row.getNextSibling().setVisible(true);
			row.getNextSibling().getNextSibling().setVisible(false);
			row.getNextSibling().getNextSibling().getNextSibling().setVisible(true);
			row.getNextSibling().getNextSibling().getNextSibling().getNextSibling().setVisible(true);
			row.getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().setVisible(true);
			row.getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().setVisible(true);
			break;
		case "2":
			row.getNextSibling().setVisible(false);
			row.getNextSibling().getNextSibling().setVisible(false);
			row.getNextSibling().getNextSibling().getNextSibling().setVisible(true);
			row.getNextSibling().getNextSibling().getNextSibling().getNextSibling().setVisible(false);
			row.getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().setVisible(true);
			row.getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().setVisible(true);
			break;
		case "3":
			row.getNextSibling().setVisible(false);
			row.getNextSibling().getNextSibling().setVisible(false);
			row.getNextSibling().getNextSibling().getNextSibling().setVisible(true);
			row.getNextSibling().getNextSibling().getNextSibling().getNextSibling().setVisible(false);
			row.getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().setVisible(true);
			row.getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().setVisible(true);
			break;
		default:
			;
			break;
		}
		professores = new ArrayList<Usuario>();
		professor = null;
		semestres = new ArrayList<String>();
		semestre = null;
		turmas = new ArrayList<Turma>();
		turma = null;
		questionarios = new ArrayList<Questionario>();
		questionario = null;
		perguntas = new ArrayList<Pergunta>();
		carregarTurmas();
		carregarSemestres();
	}

	public List<String> getSemestres() {
		return semestres;
	}

	public void setSemestres(List<String> semestres) {
		this.semestres = semestres;
	}

	public String getSemestre() {
		return semestre;
	}

	public void setSemestre(String semestre) {
		this.semestre = semestre;
	}

	public List<Turma> getTurmas() {
		return turmas;
	}

	public void setTurmas(List<Turma> turmas) {
		this.turmas = turmas;
	}

	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	public List<Pergunta> getPerguntas() {
		return perguntas;
	}

	public void setPerguntas(List<Pergunta> perguntas) {
		this.perguntas = perguntas;
	}

	public Pergunta getPerguntaSelecionada() {
		return perguntaSelecionada;
	}

	public void setPerguntaSelecionada(Pergunta perguntaSelecionada) {
		this.perguntaSelecionada = perguntaSelecionada;
	}

	public List<Usuario> getProfessores() {
		professores = new ArrayList<Usuario>();
		Usuario todos = new Usuario();
		todos.setNome("Todos");
		professores.add(todos);
		UsuarioDAO usuarioDAO = new UsuarioDAO();
		professores.addAll(usuarioDAO.retornaProfessorCurso(usuario.getCurso()));
		return professores;
	}

	public void setProfessores(List<Usuario> professores) {
		this.professores = professores;
	}

	public Usuario getProfessor() {
		return professor;
	}

	public void setProfessor(Usuario professor) {
		this.professor = professor;
	}
	

	public List<Questionario> getQuestionarios() {
		return questionarios;
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
	public List<Usuario> getCoordenadores() {
		return coordenadores;
	}

	public void setCoordenadores(List<Usuario> coordenadores) {
		this.coordenadores = coordenadores;
	}

	public Usuario getCoordenador() {
		return coordenador;
	}

	public void setCoordenador(Usuario coordenador) {
		this.coordenador = coordenador;
	}

		
}
