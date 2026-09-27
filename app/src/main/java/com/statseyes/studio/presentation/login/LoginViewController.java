package com.statseyes.studio.presentation.login;

import com.statseyes.studio.domain.config.Error_Type;
import com.statseyes.studio.presentation.navigation.ViewManager;
import com.statseyes.studio.presentation.navigation.ViewManagerAware;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;

import javafx.fxml.FXML;

@Controller
public class LoginViewController implements ViewManagerAware {

    // ===============
    // FXML ENTITIES
    // ===============

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;


    // ====================
    // INSTANCE VARIABLES
    // ====================

    private final LoginViewModel viewModel;
    private ViewManager viewManager;


    public LoginViewController(LoginViewModel viewModel){
        this.viewModel = viewModel;
    }

    public void initialize(){
        viewModel.initialize();
        bindViewModel();
    }

    @Override
    public void setViewManager(ViewManager viewManager){
        this.viewManager = viewManager;
    }

    // ==================
    // PROTECTED API
    // ==================

    @FXML
    protected void handleLogin(ActionEvent event){
        Error_Type result = viewModel.authenticate();
        System.out.println("Result :" + result.getMessage());

        if(
            (result == Error_Type.EMPTY_USERNAME)
                    ||
            (result == Error_Type.EMPTY_PASSWORD)
                    ||
            (result == Error_Type.AUTHENTICATION_FAILED)
                    ||
            (result == Error_Type.SESSION_LOGGING_FAILED)
        ){
            viewModel.displayErrorMessage(result.getMessage());
        }
        else if( (result == Error_Type.NONE)){
            viewModel.displayErrorMessage(Error_Type.AUTHENTICATION_FAILED.getMessage());
        }
        else{
            viewManager.navigateTo("home/HomeView.fxml");
        }
    }


    // ===================
    // PRIVATE API
    // ===================


    private void bindViewModel(){
        usernameField.textProperty().bindBidirectional(viewModel.usernameProperty());
        passwordField.textProperty().bindBidirectional(viewModel.passwordProperty());
        errorLabel.textProperty().bindBidirectional(viewModel.errorMessageProperty());
        errorLabel.managedProperty().bindBidirectional(viewModel.errorManagedProperty());
        errorLabel.visibleProperty().bindBidirectional(viewModel.visibleProperty());
    }
}
