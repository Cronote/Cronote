// Class LoginVO convertido pra mapeamento com Annotattion (funcionando)
package com.cronoteSys.model.vo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

// Generated 25/06/2018 22:33:40 by Hibernate Tools 4.3.1

/**
 * TbLogin generated by hbm2java
 */
@Entity
@Table(name = "tb_login")
public class LoginVO implements java.io.Serializable {

	/**
	*
	*/
	private static final long serialVersionUID = 1L;
	private Integer idLogin;
	private UserVO tbUser;
	private String email;
	private String passwd;

	public LoginVO() {
	}

	public LoginVO(UserVO tbUser, String email, String passwd) {
		this.tbUser = tbUser;
		this.email = email;
		this.passwd = passwd;
	}

	@Id
	@Column(name = "id_login")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getIdLogin() {
		return this.idLogin;
	}

	public void setIdLogin(Integer idLogin) {
		this.idLogin = idLogin;
	}

	@ManyToOne(targetEntity = UserVO.class)
	@JoinColumn(name = "id_user")
	@Fetch(FetchMode.SELECT)
	public UserVO getTbUser() {
		return this.tbUser;
	}

	public void setTbUser(UserVO tbUser) {
		this.tbUser = tbUser;
	}

	@Column(name = "email", nullable = false)
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "passwd", nullable = false)
	public String getPasswd() {
		return this.passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;

	}

}
