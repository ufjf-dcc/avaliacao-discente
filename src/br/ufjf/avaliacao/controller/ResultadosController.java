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

	private String opcao = "0";
	Row combobox = null;
	
	private List<Questionario> questionarios = new ArrayList<Questionario>();
	private Questionario questionario = null;
	private List<Questionario> questCoor = null;
	private Usuario aluno = null;
	private List<Usuario> alunos = new ArrayList<Usuario>();
	private List<Avaliacao> avaliacoes = new ArrayList<Avaliacao>();
	private List<Avaliacao> avaCoor = null;
	private List<Avaliacao> avaCoorSelect = null;
	private List<Usuario> coordenadores = new ArrayList<Usuario>();
	private Usuario coordenador = new Usuario();
	private PrazoQuestionario prazo = new PrazoQuestionario();
	private List<Pergunta> perguntas = new ArrayList<>();
	private Pergunta perguntaSelecionada;
	private List<Usuario> professores = new ArrayList<Usuario>();
	private Usuario professor = null;
	private RespostaDAO respostaDAO = new RespostaDAO();
	private List<String> semestres = respostaDAO.getAllSemestres();
	private String semestre = null;
	private List<Turma> turmas = new ArrayList<Turma>();
	private Turma turma = null;

	
	@Command
	@NotifyChange("turmas") // carregando e filtrando as tumas a serem escolhidas
	public void carregarTurmas() {
		turmas = new ArrayList<Turma>();

		if(Integer.parseInt(opcao)==1){
			if(professor!=null && semestre!=null){
				TurmaDAO turmaDAO = new TurmaDAO();
				turmas = new ArrayList<Turma>();
				Turma todas = new Turma();
				Disciplina ndisc = new Disciplina();
				ndisc.setNomeDisciplina("Todas");
				ndisc.setCodDisciplina(" ");
				todas.setDisciplina(ndisc);
				todas.setLetraTurma(" ");
				todas.setSemestre(" ");
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
		}
		
		questionarios = new ArrayList<Questionario>();
		questionario = null;
		perguntas = new ArrayList<Pergunta>();
	}
	
	@Command
	@NotifyChange("professores")
	public void carregarProfessores() {
	}
	
	
	@NotifyChange("coordenadores")
	public void carregarCoordenadores() {
	}
	
	@Command
	@NotifyChange("semestres")  // carregando e filtrando os semestres a serem escolhidos
	public void carregarSemestres() {
		semestres = new ArrayList<String>();
		semestres.add("Todos");
		TurmaDAO turmaDAO = new TurmaDAO();
		
		if(Integer.parseInt(opcao)==0){
			AvaliacaoDAO avaliacaoDAO = new AvaliacaoDAO();
			if(avaCoor!=null){
				avaCoorSelect = new ArrayList<Avaliacao>();
				avaCoorSelect.addAll(avaCoor);
				for(int i=0;i<avaCoor.size();i++){
				if(avaliacaoDAO.getAvaliado(avaCoor.get(i)).getIdUsuario()==coordenador.getIdUsuario()){ // para um coordenador especificio
					if(!semestres.contains(avaCoor.get(i).getRespostas().get(0).getSemestre()))
						semestres.add(avaCoor.get(i).getRespostas().get(0).getSemestre());
						if(!(avaliacaoDAO.getAvaliado(avaCoorSelect.get(i)).getIdUsuario()==coordenador.getIdUsuario())){
							avaCoorSelect.remove(i);
							i--;
						}
					}
					else{
						if(coordenador.getNome()=="Todos"){//para coordenador = todos
							if(!semestres.contains(avaCoor.get(i).getRespostas().get(0).getSemestre()))
								semestres.add(avaCoor.get(i).getRespostas().get(0).getSemestre());
						}
					}
				}
			}
		}
		if(Integer.parseInt(opcao)==1){
			if(professor != null){
				if(professor.getNome() != "Todos")
					semestres.addAll(turmaDAO.getSemestresUsuario(professor));
				
				else
					semestres.addAll(turmaDAO.getAllSemestres()); //MELHORAR
			}

		}
	
		if(Integer.parseInt(opcao)==2){
			if(aluno.getNome()=="Todos"){
				for(int i=0;i<alunos.size();i++)
					if(alunos.get(i).getNome()!="Todos"){
						List<String> semesAux = turmaDAO.getSemestresUsuario(alunos.get(i));
						for(int k=0;k<semesAux.size();k++)
							if(!semestres.contains(semesAux.get(k)))
								semestres.add(semesAux.get(k));
					}
			}
			else{
				semestres.addAll(turmaDAO.getSemestresUsuario(aluno));
			}
		}
	}
	
	@Command
	@NotifyChange("questionarios")  // carregando e filtrando os questionarios a serem escolhidos
	public void carregarQuestionarios() {
		questionarios = new ArrayList<Questionario>();
		Questionario todos = new Questionario();
		todos.setTituloQuestionario("Todos");
		questionarios.add(todos);
		QuestionarioDAO questionarioDAO = new QuestionarioDAO();

		if(Integer.parseInt(opcao)==0)//coordenador
			for(int i=0;i<avaCoor.size();i++)
				questionarios.add(avaCoor.get(i).getPrazoQuestionario().getQuestionario());

		if(Integer.parseInt(opcao)==1)//professor
			questionarios.addAll(questionarioDAO.retornaQuestionariosTipo(usuario.getCurso(),1));// retorna todos os questionarios de professor
		
		if(Integer.parseInt(opcao)==2)//autoavaliação
			questionarios.addAll(questionarioDAO.retornaQuestionariosTipo(usuario.getCurso(),2));// retorna todos os questionarios de autoavaliação

		if(Integer.parseInt(opcao)==3)//infraestrutura
			questionarios.addAll(questionarioDAO.retornaQuestionariosTipo(usuario.getCurso(),3));// retorna todos os questionarios de infraestrutura

	}

	@Command
	@NotifyChange("perguntas")  // carregando e filtrando os semstres a serem escolhidos
	public void carregarPerguntas() {
		perguntas = new ArrayList<Pergunta>();
		
		if(questionario.getTituloQuestionario()!="Todos")// questionario especifico
			perguntas.addAll(questionario.getPerguntas());
		else{//todos os questionarios
			QuestionarioDAO questionarioDAO = new QuestionarioDAO();
			questionarios = questionarioDAO.retornaQuestionariosTipo(usuario.getCurso(),1);
			for(int i=0;i<questionarios.size();i++)
			perguntas.addAll(questionarios.get(i).getPerguntas());
		}
	}
		
	@Command
	public void gerarGrafico() {
		Window w = null;
		if(Integer.parseInt(opcao)==0){//coorednador
			getGraficoCoordenador();
			session.setAttribute("coordenador", coordenador);
			session.setAttribute("semestre", semestre);
			session.setAttribute("pergunta", perguntaSelecionada);
			w = (Window) Executions.createComponents("/graficoCoordenador.zul", null,
				null);
		}
		if(Integer.parseInt(opcao)==1){//professor
			getGraficoProfessor();
			session.setAttribute("turma", turma);
			session.setAttribute("pergunta", perguntaSelecionada);
			w = (Window) Executions.createComponents("/grafico.zul", null,
				null);
		}
		if(Integer.parseInt(opcao)==2){//autoavalição
			getGraficoAutoavaliacao();
			session.setAttribute("aluno", aluno);
			session.setAttribute("semestre", semestre);
			session.setAttribute("pergunta", perguntaSelecionada);
			w = (Window) Executions.createComponents("/graficoAutoavaliacao.zul", null,
				null);
		}
		if(Integer.parseInt(opcao)==3){//infraestrutura
			getGraficoInfraestrutura();
			session.setAttribute("semestre", semestre);
			session.setAttribute("pergunta", perguntaSelecionada);
			w = (Window) Executions.createComponents("/graficoInfraestrutura.zul", null,
				null);
		}
		w.setClosable(true);
		w.setMinimizable(false);
		w.doOverlapped();
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

	public void getGraficoCoordenador() {
		List<Resposta> respostas;

		RespostaDAO respostaDAO = new RespostaDAO();
		if(coordenador.getNome()!="Todos"){
			if(semestre!="Todos"){
				respostas = respostaDAO.getRespostasPerguntaSemestreAvaliado(perguntaSelecionada, semestre, coordenador);
			}
			else{
				respostas = respostaDAO.getRespostasPerguntaAvaliado(perguntaSelecionada, coordenador);
			}
		}
		else{
			if(semestre!="Todos"){
				respostas = respostaDAO.getRespostasPerguntaSemestre(perguntaSelecionada, semestre);
			}
			else{
				respostas = respostaDAO.getRespostasPergunta(perguntaSelecionada);
			}
			
		}

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
	
	public void getGraficoProfessor() {
		List<Resposta> respostas;

		RespostaDAO respostaDAO = new RespostaDAO();
		if(professor.getNome()!="Todos"){
			if(semestre!="Todos"){
				if(turma.getLetraTurma()!=" "){
					respostas = respostaDAO.getRespostasPerguntaTurmaSemestreAvaliado(perguntaSelecionada, semestre, turma, professor);
				}
				else{
					respostas = respostaDAO.getRespostasPerguntaSemestreAvaliado(perguntaSelecionada, semestre, professor);
				}
			}
			else{
				if(turma.getLetraTurma()!=" "){
					respostas = respostaDAO.getRespostasPerguntaTurmaAvaliado(perguntaSelecionada, turma, professor);
				}
				else{
					respostas = respostaDAO.getRespostasPerguntaAvaliado(perguntaSelecionada, professor);
				}
			}
		}
		else{
			if(semestre!="Todos"){
				if(turma.getLetraTurma()!=" "){
					respostas = respostaDAO.getRespostasPerguntaTurmaSemestre(perguntaSelecionada, semestre, turma);
				}
				else{
					respostas = respostaDAO.getRespostasPerguntaSemestre(perguntaSelecionada, semestre);
				}
			}
			else{
				if(turma.getLetraTurma()!=" "){
					respostas = respostaDAO.getRespostasPerguntaTurma(perguntaSelecionada, turma);
				}
				else{
					respostas = respostaDAO.getRespostasPergunta(perguntaSelecionada);
				}
			}
		}

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

	public void getGraficoAutoavaliacao() {
		List<Resposta> respostas;

		RespostaDAO respostaDAO = new RespostaDAO();
		if(aluno.getNome()!="Todos"){
			if(semestre!="Todos"){
				respostas = respostaDAO.getRespostasPerguntaSemestreAvaliado(perguntaSelecionada, semestre, aluno);
			}
			else{
				respostas = respostaDAO.getRespostasPerguntaAvaliado(perguntaSelecionada, aluno);
			}
		}
		else{
			if(semestre!="Todos"){
				respostas = respostaDAO.getRespostasPerguntaSemestre(perguntaSelecionada, semestre);
			}
			else{
				respostas = respostaDAO.getRespostasPergunta(perguntaSelecionada);
			}
			
		}

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

	public void getGraficoInfraestrutura() {
		List<Resposta> respostas;

		RespostaDAO respostaDAO = new RespostaDAO();

		if(semestre!="Todos"){
			respostas = respostaDAO.getRespostasPerguntaSemestre(perguntaSelecionada, semestre);
		}
		else{
			respostas = respostaDAO.getRespostasPergunta(perguntaSelecionada);
		}
	

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
	public void avaliacaoEscolhida(@BindingParam("row") Row row, //vai tornar visivel ou nao o combobox escolhido, dependendo de o que quer visualizar
			@BindingParam("combo") Combobox combo) {

		opcao = combo.getSelectedItem().getValue().toString();
		combobox=row;
		switch (opcao) {
		case "0"://coordenador
			row.getNextSibling().setVisible(false);//professor
			row.getNextSibling().getNextSibling().setVisible(true);//coordenador
			row.getNextSibling().getNextSibling().getNextSibling().setVisible(false);//aluno
			row.getNextSibling().getNextSibling().getNextSibling().getNextSibling().setVisible(true);//semestre para coordenador
			row.getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().setVisible(false);//semestre para professor
			row.getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().setVisible(false);//turma
			row.getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().setVisible(true);//questionario
			row.getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().setVisible(true);//pergunta

			break;
		case "1"://professor
			row.getNextSibling().setVisible(true);//professor
			row.getNextSibling().getNextSibling().setVisible(false);//coordenador
			row.getNextSibling().getNextSibling().getNextSibling().setVisible(false);//aluno
			row.getNextSibling().getNextSibling().getNextSibling().getNextSibling().setVisible(false);//semestre sem turma
			row.getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().setVisible(true);//semestre para professor
			row.getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().setVisible(true);//turma
			row.getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().setVisible(true);//questionario
			row.getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().setVisible(true);//pergunta

			break;
		case "2"://autoavaliação
			row.getNextSibling().setVisible(false);//professor
			row.getNextSibling().getNextSibling().setVisible(false);//coordenador
			row.getNextSibling().getNextSibling().getNextSibling().setVisible(true);//aluno
			row.getNextSibling().getNextSibling().getNextSibling().getNextSibling().setVisible(true);//semestre sem turma
			row.getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().setVisible(false);//semestre para professor
			row.getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().setVisible(false);//turma
			row.getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().setVisible(true);//questionario
			row.getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().setVisible(true);//pergunta

			break;
		case "3"://infraestrutura
			row.getNextSibling().setVisible(false);//professor
			row.getNextSibling().getNextSibling().setVisible(false);//coordenador
			row.getNextSibling().getNextSibling().getNextSibling().setVisible(false);//aluno
			row.getNextSibling().getNextSibling().getNextSibling().getNextSibling().setVisible(true);//semestre sem turma
			row.getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().setVisible(false);//semestre para professor
			row.getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().setVisible(true);//questionario
			row.getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().setVisible(true);//pergunta
			break;
		default:
			;
			break;
		}

		
	}

	public Usuario getAluno() {
		return aluno;
	}

	public void setAluno(Usuario aluno) {
		this.aluno = aluno;
	}

	public List<Usuario> getAlunos() {
		alunos=new ArrayList<Usuario>();
		Usuario todos = new Usuario();
		todos.setNome("Todos");
		alunos.add(todos);
		UsuarioDAO usuarioDAO = new UsuarioDAO();
		alunos.addAll(usuarioDAO.retornaAlunoCurso(usuario.getCurso()));
		return alunos;
	}

	public void setAlunos(List<Usuario> alunos) {
		this.alunos = alunos;
	}

public List<Usuario> getCoordenadores() {
		
		coordenadores = new ArrayList<Usuario>();
		Usuario todos = new Usuario();
		todos.setNome("Todos");
		todos.setIdUsuario(-1);
		coordenadores.add(todos);
		AvaliacaoDAO avaliacaoDAO = new AvaliacaoDAO();
		QuestionarioDAO questionarioDAO = new QuestionarioDAO();
		questCoor = questionarioDAO.retornaQuestinariosCursoTipo(usuario.getCurso(), 0);
		avaCoor = new ArrayList<Avaliacao>();
		for(int i=0;i<questCoor.size();i++){
			List<PrazoQuestionario> prazosAux = questCoor.get(i).getPrazos();
			for(int j=0;j<prazosAux.size();j++){
				List<Avaliacao> avaAux = avaliacaoDAO.getAvaliacoesPrazoQuestionario(prazosAux.get(j));
				avaCoor.addAll(avaAux);
				for(int k=0;k<avaAux.size();k++){
					if(!coordenadores.contains(avaliacaoDAO.getAvaliado(avaAux.get(k))))
						coordenadores.add(avaliacaoDAO.getAvaliado(avaAux.get(k)));
				}
			}
		}

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
	
	public List<String> getSemestres() {
		semestres = new ArrayList<String>();
		semestres.add("Todos");
		TurmaDAO turmaDAO = new TurmaDAO();
		semestres.addAll(turmaDAO.getAllSemestres());

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
		
}
