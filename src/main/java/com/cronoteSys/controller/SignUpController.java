package com.cronoteSys.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;

import com.cronoteSys.model.bo.LoginBO;
import com.cronoteSys.model.bo.UserBO;
import com.cronoteSys.model.vo.LoginVO;
import com.cronoteSys.model.vo.UserVO;
import com.cronoteSys.util.EmailUtil;
import com.cronoteSys.util.GenHash;
import com.cronoteSys.util.ScreenUtil;
import com.cronoteSys.util.ScreenUtil.OnChangeScreen;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class SignUpController extends MasterController {

	@FXML
	private TextField txtName;
	@FXML
	private TextField txtBirthday;
	@FXML
	private TextField txtEmail;
	@FXML
	private Button btnProfile;
	@FXML
	private ImageView imgProfile;
	@FXML
	private Hyperlink linkProfile;
	@FXML
	private Button btnSignUp;
	@FXML
	private PasswordField txtPwd;
	@FXML
	private PasswordField txtConfirmPwd;

	private boolean bPasswordOk;
	private LoginVO objLogin;
	@FXML
	private AnchorPane pnlInput;

	@FXML
	protected void initialize() {
		final List<Node> lstPasswordNodes = new ArrayList<Node>();
		lstPasswordNodes.add(txtPwd);
		lstPasswordNodes.add(txtConfirmPwd);
		objLogin = new LoginVO();
		ScreenUtil.addOnChangeScreenListener(new OnChangeScreen() {
			public void onScreenChanged(String newScreen, HashMap<String, Object> hmap) {
				if (hmap.get("previewScene") != null) {
					setsPreviewsScene((String) hmap.get("previewScene"));
				}
			}
		});

		txtPwd.focusedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (!newValue) {

					bPasswordOk = verifyPassFields(txtPwd.getText().trim(), txtConfirmPwd.getText().trim(), lstPasswordNodes);
				}

			}
		});
		txtConfirmPwd.focusedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (!newValue) {
					bPasswordOk = verifyPassFields(txtConfirmPwd.getText().trim(), txtPwd.getText().trim(), lstPasswordNodes);
				}
			}
		});
	}

	public boolean verifyPassFields(String sPass1, String sPass2, List<Node> lstTextFields) {
		if (!sPass1.equals(sPass2)) {
			new ScreenUtil().addORRemoveErrorClass(lstTextFields, true);
			return false;
		} else {
			new ScreenUtil().addORRemoveErrorClass(lstTextFields, false);
			if (!new LoginBO().validatePassword(sPass1)) {
				JOptionPane.showMessageDialog(null, "Mensagem de falha por senhas fora de formato ");
				return false;
			}
			return true;
		}
	}

	@FXML
	public void btnSignUpClicked() {
		if (new ScreenUtil().isFilledFields(getThisStage(), pnlInput)) {
			String sEmail = txtEmail.getText().trim();
			if (new EmailUtil().validateEmail(sEmail)) {
				if (new LoginBO().loginExists(sEmail) == null) {
					String sPass = txtPwd.getText().trim();
					if (bPasswordOk) {
						UserVO objUser = new UserVO();
						objLogin.setTbUser(objUser);
						objLogin.setEmail(sEmail);
						objLogin.setPasswd(new GenHash().hashIt(sPass));
						objUser.setCompleteName(txtName.getText().trim());
						objUser.setStats(Byte.parseByte("1"));
						String[] vetAux = txtBirthday.getText().split("/");
						if (vetAux.length == 3) {

							objUser.setBirthDate(LocalDate.of(Integer.parseInt(vetAux[2]), Integer.parseInt(vetAux[1]),
									Integer.parseInt(vetAux[0])));

						} else {
							return;
						}
						if (new UserBO().save(objUser) && new LoginBO().save(objLogin)) {

							JOptionPane.showMessageDialog(null, "Mensagem de sucesso");
							new ScreenUtil().clearFields(getThisStage(), pnlInput);

						} else {
							JOptionPane.showMessageDialog(null, "Mensagem de falha");
						}
					} else {
						JOptionPane.showMessageDialog(null, "Mensagem de falha por senhas diferentes");
					}
				} else {
					JOptionPane.showMessageDialog(null, "Mensagem de falha por email ja cadastrado");
				}
			} else {
				JOptionPane.showMessageDialog(null, "Mensagem de falha por formato de email");
			}

		}
	}

}
