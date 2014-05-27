package br.ufjf.avaliacao.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.plaf.basic.BasicBorders.RadioButtonBorder;

import org.hibernate.mapping.Column;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.CategoryModel;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.PieModel;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.SimpleCategoryModel;
import org.zkoss.zul.SimplePieModel;
import org.zkoss.zul.Window;

import br.ufjf.avaliacao.model.Avaliacao;
import br.ufjf.avaliacao.model.Disciplina;
import br.ufjf.avaliacao.model.Grafico;
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
import bsh.org.objectweb.asm.Label;

public class ResultadosController extends GenericController implements
		Serializable {

	private static final long serialVersionUID = 6731467107690993996L;

	private String opcao = "0";
	Row combobox = null;

	private Usuario aluno = null;
	private List<Usuario> alunos = new ArrayList<Usuario>();
	private List<Avaliacao> avaliacoes = new ArrayList<Avaliacao>();
	private List<Avaliacao> avaCoor = null;
	private List<Avaliacao> avaCoorSelect = null;
	private List<Usuario> coordenadores = new ArrayList<Usuario>();
	private Usuario coordenador = new Usuario();
	private List<Radio> focos = new ArrayList<Radio>();
	private Iframe frame = new Iframe();
	private List<Grafico> graficos = new ArrayList<Grafico>();
	private Grafico grafico = new Grafico("","/img/confirm.png");
	private PrazoQuestionario prazo = new PrazoQuestionario();
	private List<Pergunta> perguntas = new ArrayList<>();
	private Pergunta perguntaSelecionada;
	private List<Usuario> professores = new ArrayList<Usuario>();
	private Usuario professor = null;
	private List<Questionario> questionarios = new ArrayList<Questionario>();
	private Questionario questionario = null;
	private List<Questionario> questCoor = null;
	private org.zkoss.zul.Label label1;
	private Radio radioProfessor,radioCoordenador,radioAluno,radioTurma,radioSemestre1,radioSemestre2,radioQuestionario;
	private RespostaDAO respostaDAO = new RespostaDAO();
	private List<String> semestres = respostaDAO.getAllSemestres();
	private String semestre = null;
	private List<Turma> turmas = new ArrayList<Turma>();
	private Turma turma = null;
	private String url;

	
	
	
	@Command
	@NotifyChange("turmas")
	// carregando e filtrando as tumas a serem escolhidas
	public void carregarTurmas() {
		turmas = new ArrayList<Turma>();

		if (Integer.parseInt(opcao) == 1) {
			if (professor != null && semestre != null) {
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
				turma=todas;
				if (professor.getNome() != "Todos" && semestre != "Todos") // professor
																			// e
																			// semestre
																			// selecionado
					turmas.addAll(turmaDAO.getTurmasUsuarioSemestre(professor,
							semestre));

				if (professor.getNome() != "Todos" && semestre == "Todos") // professor
																			// selecionado
																			// e
																			// semestre=todos
					turmas.addAll(turmaDAO.getTurmasUsuario(professor));

				if (professor.getNome() == "Todos" && semestre != "Todos") // professor=todos
																			// e
																			// semestre
																			// selecionado
					turmas.addAll(turmaDAO.getTurmasCursoSemestre(semestre,
							usuario.getCurso()));

				if (professor.getNome() == "Todos" && semestre == "Todos") // professor=todos
																			// e
																			// semestre
																			// selecionado
					turmas.addAll(turmaDAO.getAllTurmasCurso(semestre,
							usuario.getCurso()));
			}
		}

		questionarios = new ArrayList<Questionario>();
		questionario = null;
		perguntas = new ArrayList<Pergunta>();
		if(semestre=="Todos"){
			radioSemestre1.setDisabled(false);
			focos.add(radioSemestre1);
			if(focos.size()==1)
				focos.get(0).setChecked(true);
		}
		else{
			radioSemestre1.setDisabled(true);
			radioSemestre1.setChecked(false);
			focos.remove(radioSemestre1);
			if(focos.size()>0)
				focos.get(0).setChecked(true);
		}
		
	}

	@Command
	@NotifyChange("professores")
	public void carregarProfessores() {
		semestres = new ArrayList<String>();
		questionarios = new ArrayList<Questionario>();
		perguntas = new ArrayList<Pergunta>();
		
	}

	@NotifyChange("coordenadores")
	public void carregarCoordenadores() {
	}

	@Command
	@NotifyChange("semestres")
	// carregando e filtrando os semestres a serem escolhidos
	public void carregarSemestres() {
		semestres = new ArrayList<String>();
		semestres.add("Todos");
		TurmaDAO turmaDAO = new TurmaDAO();

		if (Integer.parseInt(opcao) == 0) {
			AvaliacaoDAO avaliacaoDAO = new AvaliacaoDAO();
			if (avaCoor != null) {
				avaCoorSelect = new ArrayList<Avaliacao>();
				avaCoorSelect.addAll(avaCoor);
				for (int i = 0; i < avaCoor.size(); i++) {
					if (avaliacaoDAO.getAvaliado(avaCoor.get(i)).getIdUsuario() == coordenador
							.getIdUsuario()) { // para um coordenador
												// especificio
						if (!semestres.contains(avaCoor.get(i).getRespostas()
								.get(0).getSemestre()))
							semestres.add(avaCoor.get(i).getRespostas().get(0)
									.getSemestre());
						if (!(avaliacaoDAO.getAvaliado(avaCoorSelect.get(i))
								.getIdUsuario() == coordenador.getIdUsuario())) {
							avaCoorSelect.remove(i);
							i--;
						}
					} else {
						if (coordenador.getNome() == "Todos") {// para
																// coordenador =
																// todos
							if (!semestres.contains(avaCoor.get(i)
									.getRespostas().get(0).getSemestre()))
								semestres.add(avaCoor.get(i).getRespostas()
										.get(0).getSemestre());
						}
					}
				}
			}
			//radiobutton controler
			if(coordenador.getNome()=="Todos"){
				radioCoordenador.setDisabled(false);
				focos.add(radioCoordenador);
				if(focos.size()==1)
					focos.get(0).setChecked(true);
			}
			else{
				radioCoordenador.setDisabled(true);
				radioCoordenador.setChecked(false);
				focos.remove(radioCoordenador);
				if(focos.size()>0)
					focos.get(0).setChecked(true);
			}
			
		}
		if (Integer.parseInt(opcao) == 1) {
			if (professor != null) {
				if (professor.getNome() != "Todos")
					semestres.addAll(turmaDAO.getSemestresUsuario(professor));

				else
					semestres.addAll(turmaDAO.getAllSemestres()); // MELHORAR
			}
			//radiobutton controler
			if(professor.getNome()=="Todos"){
				radioProfessor.setDisabled(false);
				focos.add(radioProfessor);
				if(focos.size()==1)
					focos.get(0).setChecked(true);
			}
			else{
				radioProfessor.setDisabled(true);
				radioProfessor.setChecked(false);
				focos.remove(radioProfessor);
				if(focos.size()>0)
					focos.get(0).setChecked(true);
			}

		}

		if (Integer.parseInt(opcao) == 2) {
			if (aluno.getNome() == "Todos") {
				for (int i = 0; i < alunos.size(); i++)
					if (alunos.get(i).getNome() != "Todos") {
						List<String> semesAux = turmaDAO
								.getSemestresUsuario(alunos.get(i));
						for (int k = 0; k < semesAux.size(); k++)
							if (!semestres.contains(semesAux.get(k)))
								semestres.add(semesAux.get(k));
					}
			} else {
				semestres.addAll(turmaDAO.getSemestresUsuario(aluno));
			}
	
			//radiobutton controler
			if(aluno.getNome()=="Todos"){
				radioAluno.setDisabled(false);
				focos.add(radioAluno);
				if(focos.size()==1)
					focos.get(0).setChecked(true);
			}
			else{
				radioAluno.setDisabled(true);
				radioAluno.setChecked(false);
				focos.remove(radioAluno);
				if(focos.size()>0)
					focos.get(0).setChecked(true);
			}
		}
		
	
		
	}

	

	@Command
	@NotifyChange("questionarios")
	// carregando e filtrando os questionarios a serem escolhidos
	public void carregarQuestionarios() {
		questionarios = new ArrayList<Questionario>();
		Questionario todos = new Questionario();
		todos.setTituloQuestionario("Todos");
		questionarios.add(todos);
		QuestionarioDAO questionarioDAO = new QuestionarioDAO();
		questionario=todos;

		if (Integer.parseInt(opcao) == 0)// coordenador
			for (int i = 0; i < avaCoor.size(); i++)
				if(!questionarios.contains(avaCoor.get(i).getPrazoQuestionario()
						.getQuestionario()))
				questionarios.add(avaCoor.get(i).getPrazoQuestionario()
						.getQuestionario());

		if (Integer.parseInt(opcao) == 1){// professor
			questionarios.addAll(questionarioDAO.retornaQuestionariosTipo(
					usuario.getCurso(), 1));// retorna todos os questionarios de
											// professor
			//radiobutton controler
			if(turma.getDisciplina().getNomeDisciplina()=="Todas"){
				radioTurma.setDisabled(false);
				focos.add(radioTurma);
				if(focos.size()==1)
					focos.get(0).setChecked(true);
			}
			else{
				radioTurma.setDisabled(true);
				radioTurma.setChecked(false);
				focos.remove(radioTurma);
				if(focos.size()>0)
					focos.get(0).setChecked(true);
			}
			
		}

		if (Integer.parseInt(opcao) == 2)// autoavalia��o
			questionarios.addAll(questionarioDAO.retornaQuestionariosTipo(
					usuario.getCurso(), 2));// retorna todos os questionarios de
											// autoavalia��o

		if (Integer.parseInt(opcao) == 3)// infraestrutura
			questionarios.addAll(questionarioDAO.retornaQuestionariosTipo(
					usuario.getCurso(), 3));// retorna todos os questionarios de
											// infraestrutura
		//radiobutton controler
		if(semestre=="Todos"){
			radioSemestre1.setDisabled(false);
			focos.add(radioSemestre1);
			if(focos.size()==1)
				focos.get(0).setChecked(true);
		}
		else{
			radioSemestre1.setDisabled(true);
			radioSemestre1.setChecked(false);
			focos.remove(radioSemestre1);
			if(focos.size()>0)
				focos.get(0).setChecked(true);
		}
		
		
	}

	@Command
	@NotifyChange("perguntas")
	// carregando e filtrando os semstres a serem escolhidos
	public void carregarPerguntas() {
		perguntas = new ArrayList<Pergunta>();
		if(perguntas.size()>0)
			perguntaSelecionada=perguntas.get(0);
		if(questionario!= null){
			if (questionario.getTituloQuestionario() != "Todos")// questionario
																// especifico
				perguntas.addAll(questionario.getPerguntas());
			else {// todos os questionarios
				QuestionarioDAO questionarioDAO = new QuestionarioDAO();
				questionarios = questionarioDAO.retornaQuestionariosTipo(usuario.getCurso(), Integer.parseInt(opcao));
				for (int i = 0; i < questionarios.size(); i++)
					perguntas.addAll(questionarios.get(i).getPerguntas());
			}
		}
		
	}
	
	@Command
	@NotifyChange("graficos")
	// carregando e filtrando os semstres a serem escolhidos
	public void carregarGraficos() {
		graficos = new ArrayList<Grafico>();

		
		Grafico aux = new Grafico("3d-pie","pie","/Highcharts/examples/3d-pie/index.htm");
		graficos.add(aux);
		aux = new Grafico("3d-pie-donut","pie","/Highcharts/examples/3d-pie-donut/index.htm");
		graficos.add(aux);
		aux = new Grafico("pie-basic","pie","/Highcharts/examples/pie-basic/index.htm");
		graficos.add(aux);
		aux = new Grafico("pie-semi-circle","pie","/Highcharts/examples/pie-semi-circle/index.htm");
		graficos.add(aux);
		
//		for(int i=0;i<focos.size();i++){
//			if(focos.get(i).isChecked()){
				aux = new Grafico("areaspline","area","/Highcharts/examples/areaspline/index.htm");
				graficos.add(aux);
				aux = new Grafico("area-stacked-percent","area","/Highcharts/examples/area-stacked-percent/index.htm");
				graficos.add(aux);
				aux = new Grafico("bar-basic","barra","/Highcharts/examples/bar-basic/index.htm");
				graficos.add(aux);
				aux = new Grafico("bar-stacked","barra","/Highcharts/examples/bar-stacked/index.htm");
				graficos.add(aux);
				aux = new Grafico("column-basic","coluna","/Highcharts/examples/column-basic/index.htm");
				graficos.add(aux);
				aux = new Grafico("column-stacked","coluna","/Highcharts/examples/column-stacked/index.htm");
				graficos.add(aux);
				aux = new Grafico("column-stacked-percent","coluna","/Highcharts/examples/column-stacked-percent/index.htm");
				graficos.add(aux);
				aux = new Grafico("line-basic","linha","/Highcharts/examples/line-basic/index.htm");
				graficos.add(aux);
				aux = new Grafico("line-labels","linha","/Highcharts/examples/line-labels/index.htm");
				graficos.add(aux);
//				break;
//			}
//		}
	}

	@Command
	public void gerarGrafico(@BindingParam("frame") Iframe frame) {
	
		if (Integer.parseInt(opcao) == 0) {// coorednador
			getGraficoCoordenador();
			frame.setSrc(getUrl());
			frame.invalidate();
			
		}
		if (Integer.parseInt(opcao) == 1) {// professor
			getGraficoProfessor();
			frame.setSrc(getUrl());
			frame.invalidate();
		
		}
		if (Integer.parseInt(opcao) == 2) {// autoavali��o
			getGraficoAutoavaliacao();
			frame.setSrc(getUrl());
			frame.invalidate();
			
		}
		if (Integer.parseInt(opcao) == 3) {// infraestrutura
			getGraficoInfraestrutura();
			frame.setSrc(getUrl());
			frame.invalidate();
			
		}
		
	}


	@Command
	@NotifyChange("perguntas")
	public void verificaTurma() {
		perguntas = new ArrayList<Pergunta>();

		if (professor != null && semestre != null && turma != null) {
			PerguntaDAO perguntaDAO = new PerguntaDAO();
			Pergunta teste = new Pergunta();
			teste.setTituloPergunta("Teste");
			if (professor.getNome() != "Todos" && semestre != "Todos"
					&& turma.getDisciplina().getNomeDisciplina() != "Todos") {
				AvaliacaoDAO avaliacaoDAO = new AvaliacaoDAO();
				List<Avaliacao> avaliacoes = avaliacaoDAO
						.retornaAvaliacoesUsuarioTurmaSemestre(professor,
								turma, semestre);

			}
		}
	}


	@Command
	public void pegarFrame() {
		System.out.println("aqui");
	}
	
	
	public void getGraficoCoordenador() {
		List<Resposta> respostas;

		RespostaDAO respostaDAO = new RespostaDAO();
		if (coordenador.getNome() != "Todos") {
			if (semestre != "Todos") {
				respostas = respostaDAO.getRespostasPerguntaSemestreAvaliado(
						perguntaSelecionada, semestre, coordenador);
			} else {
				respostas = respostaDAO.getRespostasPerguntaAvaliado(
						perguntaSelecionada, coordenador);
			}
		} else {
			if (semestre != "Todos") {
				respostas = respostaDAO.getRespostasPerguntaSemestre(
						perguntaSelecionada, semestre);
			} else {
				respostas = respostaDAO
						.getRespostasPergunta(perguntaSelecionada);
			}

		}
		
		//cada grafico é tratado de uma maneira diferente
		if(grafico.getTipo()=="pie"){// se o tipo de grafico a ser exibido é do tipo pie

			List<RespostaEspecifica> alternativas = perguntaSelecionada
					.getRespostasEspecificasBanco();
			Map<String, Integer> contagem = new LinkedHashMap<>();
		
			for (RespostaEspecifica re : alternativas) { // seta as alternativas
				contagem.put(re.getRespostaEspecifica(), 0);
			}
	
			for (Resposta r : respostas) { // incementa a quantidade
				for (RespostaEspecifica re : alternativas) {
					if (r.getResposta().equals(re.getRespostaEspecifica())) {
						contagem.put(re.getRespostaEspecifica(),
								(contagem.get(re.getRespostaEspecifica()) + 1));
					}
				}
			}
			Iterator<String> keyIterator = contagem.keySet().iterator();
			String url;// tratar erro pra &
			//nome,quantidade de colunas, quantidade de linhas, tipo_coluna1,tipo_coluna2,...,item 11,item 12, item1..., item21, item 22, item ...
			url = "?" + perguntaSelecionada.getTituloPergunta() + "&"
					+ contagem.size() + "&" + "2" + "&" + "string" + "&" + "float";
			while (keyIterator.hasNext()) {
				String key = keyIterator.next();
				url = url + "&" + key + "&" + contagem.get(key);
			}
			grafico.setParametros(url);
		}
		if(grafico.getTipo()=="area" || grafico.getTipo()=="linha" || grafico.getTipo()=="barra" || grafico.getTipo()=="coluna"){
			
			
			int quantItens=0;
			//tratamento de erro para existencia de foco--------------
			boolean existeFoco=false;
			if(!(radioSemestre1.isChecked() || radioCoordenador.isChecked())
					&& (semestre=="Todos" || professor.getNome()=="Todos"))
				existeFoco=true;
			if(existeFoco){
				if(semestre=="Todos")
					radioSemestre1.setChecked(true);
				else if(coordenador.getNome()=="Todos")
					radioCoordenador.setChecked(true);
			}
				
				
			//--------------------------------------------------------
		
			if(radioSemestre1.isChecked()){
				semestres.remove("Todos");
				quantItens = semestres.size();
			}
			else if(radioCoordenador.isChecked()){
				for(int i=0;i<coordenadores.size();i++)
					if(coordenadores.get(i).getNome()=="Todos")
						coordenadores.remove(i);
				
				quantItens = coordenadores.size();
			}
			else{
				quantItens=1;
			}

			//nome,quantidade semestres, semestre 1,semestre 2,... , quantidade de respostas, quantidade de valores das respostas, resposta 1, valor 1, valor 2,...,resposta 2, valor 2, valor 2...
			url = "";// tratar erro pra &
			url = "?" + perguntaSelecionada.getTituloPergunta() + "&" + quantItens;
			
			for(int i=0;i<quantItens;i++){ //adicionando os objetos
				if(radioSemestre1.isChecked())
					url = url + "&" + semestres.get(i);
				else if(radioCoordenador.isChecked())
					url = url + "&" + coordenadores.get(i).getNome();
				else
					url = url + "&" + semestre;
			
			}
								
			//setando os valores do vetor auxiliar
			List<RespostaEspecifica> resEsp = perguntaSelecionada.getRespostasEspecificasBanco();
			
			url = url + "&" + resEsp.size() + "&" + quantItens;
			
			String[] valores = new String[(quantItens+1)*resEsp.size()];
			String[] equivalente = new String[resEsp.size()];
			
			for(int i=0;i<resEsp.size();i++)
				equivalente[i] = resEsp.get(i).getRespostaEspecifica();
			
			int[] contador = new int[resEsp.size()];
			
			for(int i=0;i<resEsp.size();i++)
				valores[(i*(quantItens+1))] = equivalente[i];
			
			
			//semestres - cada um-------------------------------------------
			for(int i=0;i<quantItens;i++){
				if(radioSemestre1.isChecked()){
					if (coordenador.getNome() != "Todos") {
							respostas = respostaDAO.getRespostasPerguntaSemestreAvaliado(perguntaSelecionada, semestres.get(i), coordenador);
					} else {
						respostas = respostaDAO.getRespostasPerguntaSemestre(perguntaSelecionada, semestres.get(i));
					}
				}
				else if(radioCoordenador.isChecked()){
					if (semestre != "Todos") {
							respostas = respostaDAO.getRespostasPerguntaSemestreAvaliado(perguntaSelecionada, semestre, coordenadores.get(i));
						
					} else {
							respostas = respostaDAO.getRespostasPerguntaAvaliado(perguntaSelecionada, coordenadores.get(i));
					}
				}
				else {
					if (coordenador.getNome() != "Todos") {
						if (semestre != "Todos") {
							respostas = respostaDAO.getRespostasPerguntaSemestreAvaliado(
									perguntaSelecionada, semestre, coordenador);
						} else {
							respostas = respostaDAO.getRespostasPerguntaAvaliado(
									perguntaSelecionada, coordenador);
						}
					} else {
						if (semestre != "Todos") {
							respostas = respostaDAO.getRespostasPerguntaSemestre(
									perguntaSelecionada, semestre);
						} else {
							respostas = respostaDAO
									.getRespostasPergunta(perguntaSelecionada);
						}

					}											
				}
				
				//--------------------------------------------------------------
								
				//setando vetor auxiliar contador
				
				for(int j=0;j<resEsp.size();j++)
					contador[j] = 0;
									

				for(int j=0;j<respostas.size();j++){
					String resp = (String) respostas.get(j).getResposta();
					for(int k=0;k<resEsp.size();k++){
						if(equivalente[k].equals(resp))
							contador[k]++;
					}
				}
													
				//preenchendo o vetor auxiliar valores
				for(int j=0;j<resEsp.size();j++)
					valores[j*(quantItens+1) + 1 + i] =  Integer.toString(contador[j]);
			}

			for(int i=0;i<(quantItens+1)*resEsp.size();i++)
				url = url + "&" + valores[i];
			
			grafico.setParametros(url);
			
		}
		
		session.setAttribute("grafico", grafico);
		System.out.println(grafico.getURL());	
		}

	public void getGraficoProfessor() {
		List<Resposta> respostas;

		RespostaDAO respostaDAO = new RespostaDAO();
		
		if (professor.getNome() != "Todos") {
			if (semestre != "Todos") {
				if (turma.getDisciplina().getNomeDisciplina() != "Todas") {
					respostas = respostaDAO.getRespostasPerguntaTurmaSemestreAvaliado(perguntaSelecionada, semestre, turma,professor);
				} else {
					respostas = respostaDAO.getRespostasPerguntaSemestreAvaliado(perguntaSelecionada, semestre, professor);
				}
			} else {
				if (turma.getDisciplina().getNomeDisciplina() != "Todas") {
					respostas = respostaDAO.getRespostasPerguntaTurmaAvaliado(perguntaSelecionada, turma, professor);
				} else {
					respostas = respostaDAO.getRespostasPerguntaAvaliado(perguntaSelecionada, professor);
				}
			}
		} else {
			if (semestre != "Todos") {
				if (turma.getDisciplina().getNomeDisciplina() != "Todas") {
					respostas = respostaDAO.getRespostasPerguntaTurmaSemestre(perguntaSelecionada, semestre, turma);
				} else {
					respostas = respostaDAO.getRespostasPerguntaSemestre(perguntaSelecionada, semestre);
				}
			} else {
				if (turma.getDisciplina().getNomeDisciplina() != "Todas") {
					respostas = respostaDAO.getRespostasPerguntaTurma(perguntaSelecionada, turma);
				} else {
					respostas = respostaDAO.getRespostasPergunta(perguntaSelecionada);
				}
			}
		}

		//cada grafico é tratado de uma maneira diferente
				if(grafico.getTipo()=="pie"){// se o tipo de grafico a ser exibido é do tipo pie

					List<RespostaEspecifica> alternativas = perguntaSelecionada
							.getRespostasEspecificasBanco();
					Map<String, Integer> contagem = new LinkedHashMap<>();
				
					for (RespostaEspecifica re : alternativas) { // seta as alternativas
						contagem.put(re.getRespostaEspecifica(), 0);
					}
			
					for (Resposta r : respostas) { // incementa a quantidade
						for (RespostaEspecifica re : alternativas) {
							if (r.getResposta().equals(re.getRespostaEspecifica())) {
								contagem.put(re.getRespostaEspecifica(),
										(contagem.get(re.getRespostaEspecifica()) + 1));
							}
						}
					}
					Iterator<String> keyIterator = contagem.keySet().iterator();
					url = "";// tratar erro pra &
					//nome,quantidade de colunas, quantidade de linhas, tipo_coluna1,tipo_coluna2,...,item 11,item 12, item1..., item21, item 22, item ...
					url = "?" + perguntaSelecionada.getTituloPergunta() + "&"
							+ contagem.size() + "&" + "2" + "&" + "string" + "&" + "float";
					while (keyIterator.hasNext()) {
						String key = keyIterator.next();
						url = url + "&" + key + "&" + contagem.get(key);
					}
					grafico.setParametros(url);
				}
				
				
				if(grafico.getTipo()=="area" || grafico.getTipo()=="linha" || grafico.getTipo()=="barra" || grafico.getTipo()=="coluna"){
					
					
					int quantItens=0;
					//tratamento de erro para existencia de foco--------------
					boolean existeFoco=false;
					if(!(radioSemestre1.isChecked() || radioProfessor.isChecked() || radioTurma.isChecked())
							&& (semestre=="Todos" || turma.getDisciplina().getNomeDisciplina()=="Todas" || professor.getNome()=="Todos"))
						existeFoco=true;
					if(existeFoco){
						if(semestre=="Todos")
							radioSemestre1.setChecked(true);
						else if(turma.getDisciplina().getNomeDisciplina()=="Todas")
							radioTurma.setChecked(true);
						else if(professor.getNome()=="Todos")
							radioProfessor.setChecked(true);
					}
						
						
					//--------------------------------------------------------
				
					if(radioSemestre1.isChecked()){
						semestres.remove("Todos");
						quantItens = semestres.size();
					}
					else if(radioProfessor.isChecked()){
						for(int i=0;i<professores.size();i++)
							if(professores.get(i).getNome()=="Todos")
									professores.remove(i);
						
						quantItens = professores.size();
					}
					else if(radioTurma.isChecked()){
						for(int i=0;i<turmas.size();i++)
							if(turmas.get(i).getDisciplina().getNomeDisciplina()=="Todas")
									turmas.remove(i);
						quantItens = turmas.size();
					}
					else{
						quantItens=1;
					}

					//nome,quantidade semestres, semestre 1,semestre 2,... , quantidade de respostas, quantidade de valores das respostas, resposta 1, valor 1, valor 2,...,resposta 2, valor 2, valor 2...
					url = "";// tratar erro pra &
					url = "?" + perguntaSelecionada.getTituloPergunta() + "&" + quantItens;
					
					for(int i=0;i<quantItens;i++){ //adicionando os objetos
						if(radioSemestre1.isChecked())
							url = url + "&" + semestres.get(i);
						else if(radioProfessor.isChecked())
							url = url + "&" + professores.get(i).getNome();
						else if(radioTurma.isChecked())
							url = url + "&" + turmas.get(i).getDisciplinaLetraTurmaSemestre();
						else
							url = url + "&" + semestre;
					
					}
										
					//setando os valores do vetor auxiliar
					List<RespostaEspecifica> resEsp = perguntaSelecionada.getRespostasEspecificasBanco();
					
					url = url + "&" + resEsp.size() + "&" + quantItens;
					
					String[] valores = new String[(quantItens+1)*resEsp.size()];
					String[] equivalente = new String[resEsp.size()];
					
					for(int i=0;i<resEsp.size();i++)
						equivalente[i] = resEsp.get(i).getRespostaEspecifica();
					
					int[] contador = new int[resEsp.size()];
					
					for(int i=0;i<resEsp.size();i++)
						valores[(i*(quantItens+1))] = equivalente[i];
					
					
					//semestres - cada um-------------------------------------------
					for(int i=0;i<quantItens;i++){
						if(radioSemestre1.isChecked()){
							if (professor.getNome() != "Todos") {
									if (turma.getLetraTurma() != " ") {
										respostas = respostaDAO
												.getRespostasPerguntaTurmaSemestreAvaliado(
														perguntaSelecionada, semestres.get(i), turma,
														professor);
									} else {
										respostas = respostaDAO
												.getRespostasPerguntaSemestreAvaliado(
														perguntaSelecionada, semestres.get(i), professor);
									}
								
							} else {
									if (turma.getLetraTurma() != " ") {
										respostas = respostaDAO.getRespostasPerguntaTurmaSemestre(
												perguntaSelecionada, semestres.get(i), turma);
									} else {
										respostas = respostaDAO.getRespostasPerguntaSemestre(
												perguntaSelecionada, semestres.get(i));
									}
							
							}
						}
						else if(radioProfessor.isChecked()){
							if (semestre != "Todos") {
								if (turma.getDisciplina().getNomeDisciplina() != "Todas") {
									respostas = respostaDAO
											.getRespostasPerguntaTurmaSemestreAvaliado(
													perguntaSelecionada, semestre, turma,
													professores.get(i));
								} else {
									respostas = respostaDAO
											.getRespostasPerguntaSemestreAvaliado(
													perguntaSelecionada, semestre, professores.get(i));
								}
							} else {
								if (turma.getDisciplina().getNomeDisciplina() != "Todas") {
									respostas = respostaDAO.getRespostasPerguntaTurmaAvaliado(
											perguntaSelecionada, turma, professores.get(i));
								} else {
									respostas = respostaDAO.getRespostasPerguntaAvaliado(
											perguntaSelecionada, professores.get(i));
								}
							}

						}
						
						else if(radioTurma.isChecked()){
							if (professor.getNome() != "Todos") {
								if (semestre != "Todos") {
										respostas = respostaDAO
												.getRespostasPerguntaTurmaSemestreAvaliado(
														perguntaSelecionada, semestre, turmas.get(i),
														professor);
									
								} else {
										respostas = respostaDAO.getRespostasPerguntaTurmaAvaliado(
												perguntaSelecionada, turmas.get(i), professor);
									
								}
							} else {
								if (semestre != "Todos") {
										respostas = respostaDAO.getRespostasPerguntaTurmaSemestre(
												perguntaSelecionada, semestre, turmas.get(i));
								
								} else {
										respostas = respostaDAO.getRespostasPerguntaTurma(
												perguntaSelecionada, turmas.get(i));
									
								}
							}
						}
						else {
							if (professor.getNome() != "Todos") {
								if (semestre != "Todos") {
									if (turma.getDisciplina().getNomeDisciplina() != "Todas") {
										respostas = respostaDAO.getRespostasPerguntaTurmaSemestreAvaliado(perguntaSelecionada, semestre, turma,professor);
									} else {
										respostas = respostaDAO.getRespostasPerguntaSemestreAvaliado(perguntaSelecionada, semestre, professor);
									}
								} else {
									if (turma.getDisciplina().getNomeDisciplina() != "Todas") {
										respostas = respostaDAO.getRespostasPerguntaTurmaAvaliado(perguntaSelecionada, turma, professor);
									} else {
										respostas = respostaDAO.getRespostasPerguntaAvaliado(perguntaSelecionada, professor);
									}
								}
							} else {
								if (semestre != "Todos") {
									if (turma.getDisciplina().getNomeDisciplina() != "Todas") {
										respostas = respostaDAO.getRespostasPerguntaTurmaSemestre(perguntaSelecionada, semestre, turma);
									} else {
										respostas = respostaDAO.getRespostasPerguntaSemestre(perguntaSelecionada, semestre);
									}
								} else {
									if (turma.getDisciplina().getNomeDisciplina() != "Todas") {
										respostas = respostaDAO.getRespostasPerguntaTurma(perguntaSelecionada, turma);
									} else {
										respostas = respostaDAO.getRespostasPergunta(perguntaSelecionada);
									}
								}
							}
						}
						
						//--------------------------------------------------------------
										
						//setando vetor auxiliar contador
						
						for(int j=0;j<resEsp.size();j++)
							contador[j] = 0;
	
						for(int j=0;j<respostas.size();j++){
							String resp = (String) respostas.get(j).getResposta();
							for(int k=0;k<resEsp.size();k++){
								if(equivalente[k].equals(resp))
									contador[k]++;
							}
						}
															
						//preenchendo o vetor auxiliar valores
						for(int j=0;j<resEsp.size();j++)
							valores[j*(quantItens+1) + 1 + i] =  Integer.toString(contador[j]);
					}

					for(int i=0;i<(quantItens+1)*resEsp.size();i++)
						url = url + "&" + valores[i];
					
					grafico.setParametros(url);
					
				}
				
				session.setAttribute("grafico", grafico);
				System.out.println(grafico.getURL());
	}
	
	public void getGraficoAutoavaliacao() {
		List<Resposta> respostas;
		
		RespostaDAO respostaDAO = new RespostaDAO();

		if (aluno.getNome() != "Todos") {
			if (semestre != "Todos") {
				respostas = respostaDAO.getRespostasPerguntaSemestreAvaliado(
						perguntaSelecionada, semestre, aluno);
			} else {
				respostas = respostaDAO.getRespostasPerguntaAvaliado(
						perguntaSelecionada, aluno);
			}
		} else {
			if (semestre != "Todos") {
				respostas = respostaDAO.getRespostasPerguntaSemestre(
						perguntaSelecionada, semestre);
			} else {
				respostas = respostaDAO
						.getRespostasPergunta(perguntaSelecionada);
			}

		}


				//cada grafico é tratado de uma maneira diferente
		if(grafico.getTipo()=="pie"){// se o tipo de grafico a ser exibido é do tipo pie

			List<RespostaEspecifica> alternativas = perguntaSelecionada
					.getRespostasEspecificasBanco();
			Map<String, Integer> contagem = new LinkedHashMap<>();
		
			for (RespostaEspecifica re : alternativas) { // seta as alternativas
				contagem.put(re.getRespostaEspecifica(), 0);
			}
	
			for (Resposta r : respostas) { // incementa a quantidade
				for (RespostaEspecifica re : alternativas) {
					if (r.getResposta().equals(re.getRespostaEspecifica())) {
						contagem.put(re.getRespostaEspecifica(),
								(contagem.get(re.getRespostaEspecifica()) + 1));
					}
				}
			}
			Iterator<String> keyIterator = contagem.keySet().iterator();
			url = "";// tratar erro pra &
			//nome,quantidade de colunas, quantidade de linhas, tipo_coluna1,tipo_coluna2,...,item 11,item 12, item1..., item21, item 22, item ...
			url = "?" + perguntaSelecionada.getTituloPergunta() + "&"
					+ contagem.size() + "&" + "2" + "&" + "string" + "&" + "float";
			while (keyIterator.hasNext()) {
				String key = keyIterator.next();
				url = url + "&" + key + "&" + contagem.get(key);
			}
			grafico.setParametros(url);
		}
		if(grafico.getTipo()=="area" || grafico.getTipo()=="linha" || grafico.getTipo()=="barra" || grafico.getTipo()=="coluna"){
			
			
			int quantItens=0;
			//tratamento de erro para existencia de foco--------------
			boolean existeFoco=false;
			if(!(radioSemestre1.isChecked() || radioAluno.isChecked())
					&& (semestre=="Todos" || aluno.getNome()=="Todos"))
				existeFoco=true;
			if(existeFoco){
				if(semestre=="Todos")
					radioSemestre1.setChecked(true);
				else if(coordenador.getNome()=="Todos")
					radioAluno.setChecked(true);
			}
				
				
			//--------------------------------------------------------
		
			if(radioSemestre1.isChecked()){
				semestres.remove("Todos");
				quantItens = semestres.size();
			}
			else if(radioAluno.isChecked()){
				for(int i=0;i<alunos.size();i++)
					if(alunos.get(i).getNome()=="Todos")
						alunos.remove(i);
				
				quantItens = alunos.size();
			}
			else{
				quantItens=1;
			}

			//nome,quantidade semestres, semestre 1,semestre 2,... , quantidade de respostas, quantidade de valores das respostas, resposta 1, valor 1, valor 2,...,resposta 2, valor 2, valor 2...
			url = "";// tratar erro pra &
			url = "?" + perguntaSelecionada.getTituloPergunta() + "&" + quantItens;
			
			for(int i=0;i<quantItens;i++){ //adicionando os objetos
				if(radioSemestre1.isChecked())
					url = url + "&" + semestres.get(i);
				else if(radioAluno.isChecked())
					url = url + "&" + alunos.get(i).getNome();
				else
					url = url + "&" + semestre;
			
			}
								
			//setando os valores do vetor auxiliar
			List<RespostaEspecifica> resEsp = perguntaSelecionada.getRespostasEspecificasBanco();
			
			url = url + "&" + resEsp.size() + "&" + quantItens;
			
			String[] valores = new String[(quantItens+1)*resEsp.size()];
			String[] equivalente = new String[resEsp.size()];
			
			for(int i=0;i<resEsp.size();i++)
				equivalente[i] = resEsp.get(i).getRespostaEspecifica();
			
			int[] contador = new int[resEsp.size()];
			
			for(int i=0;i<resEsp.size();i++)
				valores[(i*(quantItens+1))] = equivalente[i];
			
			
			//semestres - cada um-------------------------------------------
			for(int i=0;i<quantItens;i++){
				if(radioSemestre1.isChecked()){
					if (aluno.getNome() != "Todos") {
							respostas = respostaDAO.getRespostasPerguntaSemestreAvaliado(
									perguntaSelecionada, semestres.get(i), aluno);
						
					} else {
							respostas = respostaDAO.getRespostasPerguntaSemestre(
									perguntaSelecionada, semestres.get(i));
						
					}
				}
				else if(radioAluno.isChecked()){

						if (semestre != "Todos") {
							respostas = respostaDAO.getRespostasPerguntaSemestreAvaliado(
									perguntaSelecionada, semestre, alunos.get(i));
						} else {
							respostas = respostaDAO.getRespostasPerguntaAvaliado(
									perguntaSelecionada, alunos.get(i));
						}
					
				}
				else {
					
					if (aluno.getNome() != "Todos") {
						if (semestre != "Todos") {
							respostas = respostaDAO.getRespostasPerguntaSemestreAvaliado(
									perguntaSelecionada, semestre, aluno);
						} else {
							respostas = respostaDAO.getRespostasPerguntaAvaliado(
									perguntaSelecionada, aluno);
						}
					} else {
						if (semestre != "Todos") {
							respostas = respostaDAO.getRespostasPerguntaSemestre(
									perguntaSelecionada, semestre);
						} else {
							respostas = respostaDAO
									.getRespostasPergunta(perguntaSelecionada);
						}

					}
					
				}
				
				//--------------------------------------------------------------
								
				//setando vetor auxiliar contador
				
				for(int j=0;j<resEsp.size();j++)
					contador[j] = 0;
									

				for(int j=0;j<respostas.size();j++){
					String resp = (String) respostas.get(j).getResposta();
					for(int k=0;k<resEsp.size();k++){
						if(equivalente[k].equals(resp))
							contador[k]++;
					}
				}
													
				//preenchendo o vetor auxiliar valores
				for(int j=0;j<resEsp.size();j++)
					valores[j*(quantItens+1) + 1 + i] =  Integer.toString(contador[j]);
			}

			for(int i=0;i<(quantItens+1)*resEsp.size();i++)
				url = url + "&" + valores[i];
			
			grafico.setParametros(url);
			
		}
		
		session.setAttribute("grafico", grafico);
		System.out.println(grafico.getURL());	
	}

	public void getGraficoInfraestrutura() {
		List<Resposta> respostas;

		RespostaDAO respostaDAO = new RespostaDAO();

		if (semestre != "Todos") {
			respostas = respostaDAO.getRespostasPerguntaSemestre(
					perguntaSelecionada, semestre);
		} else {
			respostas = respostaDAO.getRespostasPergunta(perguntaSelecionada);
		}

		//cada grafico é tratado de uma maneira diferente
	if(grafico.getTipo()=="pie"){// se o tipo de grafico a ser exibido é do tipo pie

		List<RespostaEspecifica> alternativas = perguntaSelecionada
				.getRespostasEspecificasBanco();
		Map<String, Integer> contagem = new LinkedHashMap<>();
	
		for (RespostaEspecifica re : alternativas) { // seta as alternativas
			contagem.put(re.getRespostaEspecifica(), 0);
		}

		for (Resposta r : respostas) { // incementa a quantidade
			for (RespostaEspecifica re : alternativas) {
				if (r.getResposta().equals(re.getRespostaEspecifica())) {
					contagem.put(re.getRespostaEspecifica(),
							(contagem.get(re.getRespostaEspecifica()) + 1));
				}
			}
		}
		Iterator<String> keyIterator = contagem.keySet().iterator();
		url = "";// tratar erro pra &
		//nome,quantidade de colunas, quantidade de linhas, tipo_coluna1,tipo_coluna2,...,item 11,item 12, item1..., item21, item 22, item ...
		url = "?" + perguntaSelecionada.getTituloPergunta() + "&"
				+ contagem.size() + "&" + "2" + "&" + "string" + "&" + "float";
		while (keyIterator.hasNext()) {
			String key = keyIterator.next();
			url = url + "&" + key + "&" + contagem.get(key);
		}
		grafico.setParametros(url);
	}
	if(grafico.getTipo()=="area" || grafico.getTipo()=="linha" || grafico.getTipo()=="barra" || grafico.getTipo()=="coluna"){
		
		
		int quantItens=0;
		//tratamento de erro para existencia de foco--------------
		boolean existeFoco=false;
		if(!(radioSemestre1.isChecked()) && (semestre=="Todos"))
			existeFoco=true;
		if(existeFoco){
			if(semestre=="Todos")
				radioSemestre1.setChecked(true);
		}
			
			
		//--------------------------------------------------------
	
		if(radioSemestre1.isChecked()){
			semestres.remove("Todos");
			quantItens = semestres.size();
		}
		else{
			quantItens=1;
		}

		//nome,quantidade semestres, semestre 1,semestre 2,... , quantidade de respostas, quantidade de valores das respostas, resposta 1, valor 1, valor 2,...,resposta 2, valor 2, valor 2...
		url = "";// tratar erro pra &
		url = "?" + perguntaSelecionada.getTituloPergunta() + "&" + quantItens;
		
		for(int i=0;i<quantItens;i++){ //adicionando os objetos
			if(radioSemestre1.isChecked())
				url = url + "&" + semestres.get(i);
			else
				url = url + "&" + semestre;
		
		}
							
		//setando os valores do vetor auxiliar
		List<RespostaEspecifica> resEsp = perguntaSelecionada.getRespostasEspecificasBanco();
		
		url = url + "&" + resEsp.size() + "&" + quantItens;
		
		String[] valores = new String[(quantItens+1)*resEsp.size()];
		String[] equivalente = new String[resEsp.size()];
		
		for(int i=0;i<resEsp.size();i++)
			equivalente[i] = resEsp.get(i).getRespostaEspecifica();
		
		int[] contador = new int[resEsp.size()];
		
		for(int i=0;i<resEsp.size();i++)
			valores[(i*(quantItens+1))] = equivalente[i];
		
		
		//semestres - cada um-------------------------------------------
		for(int i=0;i<quantItens;i++){
			if(radioSemestre1.isChecked()){
					respostas = respostaDAO.getRespostasPerguntaSemestre(
							perguntaSelecionada, semestres.get(i));

			}

			else {
				if (semestre != "Todos") {
					respostas = respostaDAO.getRespostasPerguntaSemestre(
							perguntaSelecionada, semestre);
				} else {
					respostas = respostaDAO.getRespostasPergunta(perguntaSelecionada);
				}
				
			}
			
			//--------------------------------------------------------------
							
			//setando vetor auxiliar contador
			
			for(int j=0;j<resEsp.size();j++)
				contador[j] = 0;
								

			for(int j=0;j<respostas.size();j++){
				String resp = (String) respostas.get(j).getResposta();
				for(int k=0;k<resEsp.size();k++){
					if(equivalente[k].equals(resp))
						contador[k]++;
				}
			}
												
			//preenchendo o vetor auxiliar valores
			for(int j=0;j<resEsp.size();j++)
				valores[j*(quantItens+1) + 1 + i] =  Integer.toString(contador[j]);
		}

		for(int i=0;i<(quantItens+1)*resEsp.size();i++)
			url = url + "&" + valores[i];
		
		grafico.setParametros(url);
		
	}
	
	session.setAttribute("grafico", grafico);
	System.out.println(grafico.getURL());	
	}
	
	
	@Command
	public void avaliacaoEscolhida(@BindingParam("row") Row row, // vai tornar
																	// visivel
																	// ou nao o
																	// combobox
																	// escolhido,
																	// dependendo
																	// de o que
																	// quer
																	// visualizar
			@BindingParam("combo") Combobox combo,
			
			@BindingParam("radioProf") Radio prof,
			@BindingParam("radioCoor") Radio coor,
			@BindingParam("radioAlu") Radio alun,
			@BindingParam("radioSem1") Radio sem1,
			@BindingParam("radioTur") Radio tur,
			@BindingParam("lab1") org.zkoss.zul.Label lab
			
			
			) {

		radioProfessor=prof;
		radioCoordenador=coor;
		radioAluno=alun;
		radioSemestre1=sem1;
		radioTurma=tur;
		label1=lab;
		radioProfessor.setDisabled(false);
		radioCoordenador.setDisabled(false);
		radioAluno.setDisabled(false);


	
		opcao = combo.getSelectedItem().getValue().toString();
		combobox = row;
		switch (opcao) {
		case "0":// coordenador
			row.getNextSibling().setVisible(false);// professor
			row.getNextSibling().getNextSibling().setVisible(true);// coordenador
			row.getNextSibling().getNextSibling().getNextSibling()
					.setVisible(false);// aluno
			row.getNextSibling().getNextSibling().getNextSibling()
					.getNextSibling().setVisible(true);// semestre para
														// coordenador
			row.getNextSibling().getNextSibling().getNextSibling()
					.getNextSibling().getNextSibling().setVisible(false);// semestre
																			// para
																			// professor
			row.getNextSibling().getNextSibling().getNextSibling()
					.getNextSibling().getNextSibling().getNextSibling()
					.setVisible(false);// turma
			row.getNextSibling().getNextSibling().getNextSibling()
					.getNextSibling().getNextSibling().getNextSibling()
					.getNextSibling().setVisible(true);// questionario
			row.getNextSibling().getNextSibling().getNextSibling()
					.getNextSibling().getNextSibling().getNextSibling()
					.getNextSibling().getNextSibling().setVisible(true);// pergunta
			radioProfessor.setVisible(false);
			radioCoordenador.setVisible(true);
			radioAluno.setVisible(false);
			radioSemestre1.setVisible(true);
			radioTurma.setVisible(false);
			label1.setVisible(true);

			break;
		case "1":// professor
			row.getNextSibling().setVisible(true);// professor
			row.getNextSibling().getNextSibling().setVisible(false);// coordenador
			row.getNextSibling().getNextSibling().getNextSibling()
					.setVisible(false);// aluno
			row.getNextSibling().getNextSibling().getNextSibling()
					.getNextSibling().setVisible(false);// semestre sem turma
			row.getNextSibling().getNextSibling().getNextSibling()
					.getNextSibling().getNextSibling().setVisible(true);// semestre
																		// para
																		// professor
			row.getNextSibling().getNextSibling().getNextSibling()
					.getNextSibling().getNextSibling().getNextSibling()
					.setVisible(true);// turma
			row.getNextSibling().getNextSibling().getNextSibling()
					.getNextSibling().getNextSibling().getNextSibling()
					.getNextSibling().setVisible(true);// questionario
			row.getNextSibling().getNextSibling().getNextSibling()
					.getNextSibling().getNextSibling().getNextSibling()
					.getNextSibling().getNextSibling().setVisible(true);// pergunta
			radioProfessor.setVisible(true);
			radioCoordenador.setVisible(false);
			radioAluno.setVisible(false);
			radioSemestre1.setVisible(true);
			radioTurma.setVisible(true);
			label1.setVisible(true);
			
			break;
		case "2":// autoavalia��o
			row.getNextSibling().setVisible(false);// professor
			row.getNextSibling().getNextSibling().setVisible(false);// coordenador
			row.getNextSibling().getNextSibling().getNextSibling()
					.setVisible(true);// aluno
			row.getNextSibling().getNextSibling().getNextSibling()
					.getNextSibling().setVisible(true);// semestre sem turma
			row.getNextSibling().getNextSibling().getNextSibling()
					.getNextSibling().getNextSibling().setVisible(false);// semestre
																			// para
																			// professor
			row.getNextSibling().getNextSibling().getNextSibling()
					.getNextSibling().getNextSibling().getNextSibling()
					.setVisible(false);// turma
			row.getNextSibling().getNextSibling().getNextSibling()
					.getNextSibling().getNextSibling().getNextSibling()
					.getNextSibling().setVisible(true);// questionario
			row.getNextSibling().getNextSibling().getNextSibling()
					.getNextSibling().getNextSibling().getNextSibling()
					.getNextSibling().getNextSibling().setVisible(true);// pergunta
			radioProfessor.setVisible(false);
			radioCoordenador.setVisible(false);
			radioAluno.setVisible(true);
			radioSemestre1.setVisible(true);
			radioTurma.setVisible(false);
			label1.setVisible(true);
			break;
		case "3":// infraestrutura
			row.getNextSibling().setVisible(false);// professor
			row.getNextSibling().getNextSibling().setVisible(false);// coordenador
			row.getNextSibling().getNextSibling().getNextSibling()
					.setVisible(false);// aluno
			row.getNextSibling().getNextSibling().getNextSibling()
					.getNextSibling().setVisible(true);// semestre sem turma
			row.getNextSibling().getNextSibling().getNextSibling()
					.getNextSibling().getNextSibling().setVisible(false);// semestre
																			// para
																			// professor
			row.getNextSibling().getNextSibling().getNextSibling()
					.getNextSibling().getNextSibling().getNextSibling()
					.getNextSibling().setVisible(true);// questionario
			row.getNextSibling().getNextSibling().getNextSibling()
					.getNextSibling().getNextSibling().getNextSibling()
					.getNextSibling().getNextSibling().setVisible(true);// pergunta
			radioProfessor.setVisible(false);
			radioCoordenador.setVisible(false);
			radioAluno.setVisible(false);
			radioSemestre1.setVisible(true);
			radioTurma.setVisible(false);
			label1.setVisible(true);
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
		alunos = new ArrayList<Usuario>();
		Usuario todos = new Usuario();
		todos.setNome("Todos");
		alunos.add(todos);
		UsuarioDAO usuarioDAO = new UsuarioDAO();
		alunos.addAll(usuarioDAO.retornaAlunoCurso(usuario.getCurso()));
		aluno=todos;
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
		questCoor = questionarioDAO.retornaQuestinariosCursoTipo(
				usuario.getCurso(), 0);
		avaCoor = new ArrayList<Avaliacao>();
		for (int i = 0; i < questCoor.size(); i++) {
			List<PrazoQuestionario> prazosAux = questCoor.get(i).getPrazos();
			for (int j = 0; j < prazosAux.size(); j++) {
				List<Avaliacao> avaAux = avaliacaoDAO
						.getAvaliacoesPrazoQuestionario(prazosAux.get(j));
				avaCoor.addAll(avaAux);
				for (int k = 0; k < avaAux.size(); k++) {
					boolean diferente=true;
					if (!coordenadores.contains(avaliacaoDAO.getAvaliado(avaAux.get(k))))
						for(int m=0;m<coordenadores.size();m++){
							if(coordenadores.get(m).getNome().equals(avaliacaoDAO.getAvaliado(avaAux.get(k)).getNome())){
								diferente=false;
							}
						}
					if(diferente)
						coordenadores.add(avaliacaoDAO.getAvaliado(avaAux.get(k)));
				}
			}
		}
		coordenador=todos;
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

	public Iframe getFrame() {
		return frame;
	}

	public void setFrame(Iframe frame) {
		this.frame = frame;
	}
	
	public List<Grafico> getGraficos() {
		return graficos;
	}

	public void setGraficos(List<Grafico> graficos) {
		this.graficos = graficos;
	}

	public Grafico getGrafico() {
		return grafico;
	}

	public void setGrafico(Grafico grafico) {
		this.grafico = grafico;
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
		professores
				.addAll(usuarioDAO.retornaProfessorCurso(usuario.getCurso()));
		professor=todos;
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
	
	public String getUrl() {
		return (String)(grafico.getURL());
	}



}
