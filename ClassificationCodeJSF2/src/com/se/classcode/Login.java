package com.se.classcode;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.primefaces.context.RequestContext;

@ManagedBean
public class Login {

	private boolean show = true;
	private String username;
	private String password;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void login(ActionEvent actionEvent) {
		 RequestContext context = RequestContext.getCurrentInstance();
//       servle
       FacesMessage msg = null;
       boolean loggedIn = false;
       if (getUsername() != null && getUsername().equals("admin") && getPassword() != null && getPassword().equals("admin")) {
           loggedIn = true;
           msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Welcome", getUsername());
           this.setShow(false);
           
       } else {
           loggedIn = false;
           this.setShow(false);
           msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Login Error", "Invalid credentials");
       }
       System.out.println("" + loggedIn);
       FacesContext.getCurrentInstance().addMessage(null, msg);
       context.addCallbackParam("loggedIn", loggedIn);
   }

	/**
	 * @return the show
	 */
	public boolean isShow() {
		return show;
	}

	/**
	 * @param show
	 *            the show to set
	 */
	public void setShow(boolean show) {
		this.show = show;
	}
}
