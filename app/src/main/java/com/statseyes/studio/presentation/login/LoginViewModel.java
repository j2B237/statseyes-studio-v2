package com.statseyes.studio.presentation.login;

import com.statseyes.studio.application.usecase.auth.AuthenticateUseCase;
import com.statseyes.studio.domain.config.Error_Type;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import org.springframework.stereotype.Component;

@Component
public class LoginViewModel {

    // =================
    // PROPERTIES
    // =================

    private final StringProperty username = new SimpleStringProperty();
    private final StringProperty password = new SimpleStringProperty();
    private final StringProperty errorMessage = new SimpleStringProperty();
    private final BooleanProperty errManaged = new SimpleBooleanProperty();
    private final BooleanProperty visible = new SimpleBooleanProperty();

    // ==================
    // INSTANCE VARIABLES
    // ==================

    private final AuthenticateUseCase authenticateUseCase;


    // ==================
    // PUBLIC API
    // ==================

    public LoginViewModel(
            AuthenticateUseCase authenticateUseCase
    ){
        this.authenticateUseCase = authenticateUseCase;
    }

    public void initialize(){

        // Message d'erreur vide, lable masque
        // et n'occupe pas d'espace dans UI.
        setErrorMessage(null);
        setVisible(false);
        setErrManaged(false);
    }


    public Error_Type authenticate(){

        String name = getUsername();
        String rawPwd = getPassword();
        Error_Type result = Error_Type.NONE;

        if(name == null || name.isBlank()){
            return Error_Type.EMPTY_USERNAME;
        }
       if (rawPwd == null || rawPwd.isBlank()){
           return Error_Type.EMPTY_PASSWORD;
       }

        try {
            reset();
            result = authenticateUseCase.execute(name, rawPwd);
        } catch (Exception e) {
            reset();
        }

        return result;
    }

    public void displayErrorMessage(String value){
        setVisible(true);
        setErrorMessage(value);
        setErrManaged(true);
    }

    // =================
    // GETTERS
    // =================

    public StringProperty usernameProperty(){return username;}
    public String getUsername(){return username.get();}
    public StringProperty passwordProperty(){return password;}
    public String getPassword(){return password.get();}
    public StringProperty errorMessageProperty(){return errorMessage;}
    public String getErrorMessage(){return errorMessage.get();}
    public BooleanProperty errorManagedProperty(){return errManaged;}
    public Boolean getErrorMessageManaged(){return errManaged.get();}
    public BooleanProperty visibleProperty(){return visible;}
    public Boolean getVisible(){return visible.get();}

    // ===================
    // SETTERS
    // ===================

    public void setUsername(String value){username.set(value);}
    public void setPassword(String value){password.set(value);}
    public void setErrorMessage(String value){errorMessage.set(value);}
    public void setErrManaged(Boolean value){errManaged.set(value);}
    public void setVisible(Boolean value){visible.set(value);}

    // =====================
    // PRIVATE API
    // =====================

    private void reset(){
        setUsername("");
        setPassword("");
    }
}
