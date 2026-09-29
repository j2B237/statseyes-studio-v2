package com.statseyes.studio.presentation.navigation;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.URL;

import org.springframework.stereotype.Component;

/**
 * Navigation "interne" : remplace le contenu d'un VBox (pas toute la Scene),
 * avec possibilite de passer un parametre typé au controleur de destination
 * AVANT que son initialize() ne s'execute.
 */

@Component
public class SectionNavigator {

    private final ConfigurableApplicationContext springContext;
    private ViewManager viewManager;

    public SectionNavigator(
            ConfigurableApplicationContext springContext
    ){
        this.springContext = springContext;
    }


    public void setViewManager(ViewManager viewManager){
        this.viewManager = viewManager;
    }

    public void open(VBox container, String fxmlPath){
        open(container, fxmlPath, null, null);
    }


    public <C, P> void open(VBox container, String fxmlPath, Class<C> controllerType, P parameter) {
        try {
            if (controllerType != null && parameter != null) {
                C controller = springContext.getBean(controllerType);
                if (controller instanceof Navigable<?> navigable) {
                    @SuppressWarnings("unchecked")
                    Navigable<P> typed = (Navigable<P>) navigable;
                    typed.onNavigate(parameter);
                }
            }

            URL resource = SectionNavigator.class.getResource(fxmlPath);
            if (resource == null) {
                throw new IllegalStateException("FXML resource not found: " + fxmlPath);
            }

            FXMLLoader loader = new FXMLLoader(resource);
            loader.setControllerFactory(type -> {
                Object bean = springContext.getBean(type);
                if (bean instanceof ViewManagerAware aware) {
                    aware.setViewManager(viewManager);
                }
                return bean;
            });

            Node content = loader.load();
            container.getChildren().setAll(content);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
