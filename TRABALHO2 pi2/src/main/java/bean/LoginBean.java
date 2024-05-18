package bean;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import dao.UsuarioDao;
import entidades.Usuario;
import util.JPAUtil;
import util.SessionUtils;

@ManagedBean
@ApplicationScoped
public class LoginBean {
	
	
	private String login;
    private String senha;
    private UsuarioDao usuariodao;
    private Usuario usuarioLogado; 
    
    private Date dataNascimento;
    
    
    public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public String getNovaSenha() {
		return novaSenha;
	}

	public void setNovaSenha(String novaSenha) {
		this.novaSenha = novaSenha;
	}

	private String novaSenha;

    
    
    public Usuario getUsuarioLogado() {
		return usuarioLogado;
	}

	public void setUsuarioLogado(Usuario usuarioLogado) {
		this.usuarioLogado = usuarioLogado;
	}

	public UsuarioDao getUsuariodao() {
		return usuariodao;
	}

	public void setUsuariodao(UsuarioDao usuariodao) {
		this.usuariodao = usuariodao;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String realizarLogin() {
		usuariodao = new UsuarioDao(JPAUtil.getEntityManager());
        Usuario usuario = usuariodao.buscarPorLoginSenha(login, senha);

        if (usuario != null) {
        	FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
            session.setAttribute("usuarioLogado", usuario);
            
        	if(usuario.getPerfil_id() == 0) {
                return "areaAdm";
        	}else {
        		return "index";
        	}
        	
            
            
            
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Login ou senha incorreto.", null));
            return null;
        }
    }

	public String logout() {
        SessionUtils.logout();
        return "login.xhtml?faces-redirect=true";
    }
	
	public void recuperarOuTrocarSenha() {
        UsuarioDao usuarioDao = new UsuarioDao(JPAUtil.getEntityManager());
        Usuario usuario = usuarioDao.buscarPorLoginEDataNascimento(login, dataNascimento);
        
        if (usuario != null) {
            // Trocar a senha do usuário
            usuario.setSenha(novaSenha);
            usuarioDao.salvar(usuario);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Senha alterada com sucesso!", null));
        } else {
            // Exibir mensagem de erro se o usuário não for encontrado
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Usuário não encontrado ou dados incorretos.", null));
        }
    }

}
