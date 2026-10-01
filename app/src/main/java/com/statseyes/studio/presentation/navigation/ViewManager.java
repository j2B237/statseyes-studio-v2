package com.statseyes.studio.presentation.navigation;

import com.statseyes.studio.domain.config.Error_Type;
import com.statseyes.studio.infrastructure.security.SessionAdapter;
import lombok.Setter;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.net.URL;

import org.springframework.context.ConfigurableApplicationContext;

public class ViewManager{

    private final ConfigurableApplicationContext springContext;
    private final SessionAdapter sessionAdapter;

    @Setter
    private Scene mainScene;

    //
    // Public API
    //

    public ViewManager(
            ConfigurableApplicationContext springContext,
           SessionAdapter sessionAdapter
    ){
        this.springContext = springContext;
        this.sessionAdapter = sessionAdapter;
    }

    public ConfigurableApplicationContext getApplicationContext(){
        return springContext;
    }

    // Navigation simple sans condition
    public void navigateTo(String viewPath){
        loadView(viewPath);
    }


    // Redirection uniquement si utilisateur authentifie
    public void redirectIfAuthenticated(String viewPath){
        if(sessionAdapter.isAuthenticated() == Error_Type.AUTHENTICATION_SUCCESS){
            loadView(viewPath);
        }
    }

    public void redirectIfNotAuthenticated(String viewPath){
        if (sessionAdapter.isAuthenticated() == Error_Type.AUTHENTICATION_FAILED){
            loadView(viewPath);
        }
    }

    private void loadView(String fxmlPath){
        try{
            Parent root = load(fxmlPath, controller -> {});
            mainScene.setRoot(root);

        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de la vue " + fxmlPath);
            e.printStackTrace();
        }
    }

    /*
        Charge un FXML depuis un chemin absolu de classpath, injecte ce
        ViewManager dans tout controleur ViewManagerAware, applique un
        post-traitement optionnel, et retourne le noeud racine.
     */
    private Parent load(String resourcePath, java.util.function.Consumer<Object> postProcess) throws IOException{

        URL resource = ViewManager.class.getResource(resourcePath);
        if(resource == null){
            throw new IllegalStateException("FXML resource not found: " + resourcePath);
        }

        FXMLLoader loader = new FXMLLoader(resource);

        loader.setControllerFactory(type -> {
            Object controller = springContext.getBean(type);
            if(controller instanceof ViewManagerAware aware){
                System.out.println("From viewManager controller set.");
                aware.setViewManager(this);
            }
            postProcess.accept(controller);
            return controller;
        });

        return loader.load();
    }
}
