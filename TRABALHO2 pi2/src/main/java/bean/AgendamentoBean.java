package bean;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import dao.AgendamentoDAO;
import entidades.Agendamento;
import entidades.Medico;
import entidades.Status;
import util.JPAUtil;

@ManagedBean
@ApplicationScoped

public class AgendamentoBean {

	private Agendamento agendamento;
	private AgendamentoDAO agendamentoDao;
	private List<Agendamento> agendamentos;
	private List<Medico> medicos;
	
	public AgendamentoBean(){
		agendamento = new Agendamento();
		agendamentoDao = new AgendamentoDAO(JPAUtil.getEntityManager());
		medicos = agendamentoDao.listarMedicos();
        atualizarListaAgendamentos();
	}
	
	public Status[] getStatuses() {
        return Status.values();
    }
	
	public List<Medico> getMedicos() {
		return medicos;
	}

	public void setMedicos(List<Medico> medicos) {
		this.medicos = medicos;
	}

	public String editar(Agendamento agendamento) {
    	this.agendamento = agendamento;
		return "index";
	}
	
	public void atualizarListaAgendamentos() {
		agendamentos = agendamentoDao.listar();
	}

	public Agendamento getAgendamento() {
		return agendamento;
	}

	public void setAgendamento(Agendamento agendamento) {
		this.agendamento = agendamento;
	}

	public AgendamentoDAO getAgendamentoDao() {
		return agendamentoDao;
	}

	public void setAgendamentoDao(AgendamentoDAO agendamentoDao) {
		this.agendamentoDao = agendamentoDao;
	}

	public List<Agendamento> getAgendamentos() {
		return agendamentos;
	}

	public void setAgendamentos(List<Agendamento> agendamentos) {
		this.agendamentos = agendamentos;
	}
	
	private boolean agendamentoJaExiste() {
        List<Agendamento> agendamentosExistentes = agendamentoDao.buscarPorDataHoraMedicoClinica(agendamento.getId(), agendamento.getDataHoraAgendamento(), agendamento.getMedico(),agendamento.getClinica());
        return !agendamentosExistentes.isEmpty();
        
        
    }
	
	public void salvar() {
	    try {
	    	
	    	
	        if (agendamento.getId() == null) {
	            agendamentoDao.salvar(agendamento);
	            FacesContext.getCurrentInstance().addMessage(null,
	                    new FacesMessage("agendamento salvo com sucesso."));
	            
	        } else {
	            agendamentoDao.salvar(agendamento); 
	            FacesContext.getCurrentInstance().addMessage(null,
	                    new FacesMessage("agendamento editado com sucesso."));
	        }
	        limparCampos();
	        atualizarListaAgendamentos();
	    } catch (Exception e) {
	    	if (agendamentoJaExiste()) {
	            FacesContext.getCurrentInstance().addMessage(null,
	                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao salvar o agendamento. "
	                    		+ "Já existe um agendamento para a mesma data, hora , médico e clinica.",
	                            ""));
	            return;
	        }
	    }
	}
	
	public void excluir(Agendamento agendamento) {
        try {
        	agendamentoDao.excluir(agendamento);
        	FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("agendamento excluido com sucesso."));
        	atualizarListaAgendamentos();
        } catch (Exception e) {
        }
    }
	
	 public void limparCampos() {
		 agendamento = new Agendamento();
	 }
	 
	 public String editarAgendamentoAdm(Agendamento agendamento) {
	    	this.agendamento = agendamento;
			return "editarAgendamentoAdm";
		}
	 
	 public void cancelarAgendamento(Agendamento agendamento) {
		    try {
		        agendamento.setStatus(Status.CANCELADO);
		        agendamentoDao.salvar(agendamento); // ou método específico para atualizar apenas o status
		        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
		                "Agendamento cancelado com sucesso.", null));
		        atualizarListaAgendamentos();
		    } catch (Exception e) {
		        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
		                "Erro ao cancelar o agendamento.", null));
		    }
		}

}
